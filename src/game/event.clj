(ns game.event)

(def time_precision 10)

(def get_timestamp (fn []
  (quot (System/currentTimeMillis) time_precision)
  ))

(defprotocol ev_func
  (process [this])
  (cleanup [this]))

(defrecord event [ref_id timestamp complete_f cleanup_f]
  java.lang.Comparable
    (compareTo [this other] (compare (:timestamp this) (:timestamp other)))
  ev_func
    (process [this] (when-not (nil? complete_f) (complete_f this)))
    (cleanup [this] (when-not (nil? cleanup_f) (cleanup_f this)))
  )

(def ^{:private true} _events (ref (sorted-set)))

(def ^{:private true} _worker (ref nil))

(def set_worker (fn [arg]
  (dosync
    (let [value (if (fn? arg) (arg @_worker) arg)]
      (ref-set _worker value)))
  ))

(def with_events (fn [func & args]
  (dosync
    (let [e @_events,
      result (apply func e args),
      [changed_events & ret] (if-not (list? result) (list result) result)]

      (ref-set _events changed_events)
      ret))
  ))

(def events? (fn []
  (with_events (fn [events] [events, (empty events)]))
  ))

(def shift (fn []
  (with_events (fn [events]
    (let [f (first events),
      e (disj events f)]

      (list e f))))
  ))

(def add (fn [ref_id timestamp complete_f cleanup_f]
  (let [e (->event ref_id timestamp complete_f cleanup_f)]
    (with_events (fn [events] (conj events e))))
  ))

(def del (fn [ev]
  (with_events (fn [events] (disj events ev)))
  ))

(def event_worker (fn [s]
  (let [maxs (max 0 s), f (future
    (Thread/sleep (* time_precision maxs))
    (set_worker nil)
    (when-let [e (first (shift))]
      (process e)
      (cleanup e)))]

    (set_worker f))
  ))

(def register_watch (fn []
  (add-watch _events nil (fn [_key, _ref, _old, _new]
    (when-not (= (first _old) (first _new))
      (set_worker (fn [worker]
        (when-not (nil? worker)
          (future-cancel worker))
        nil))
      (when-let [e (first _new)]
        (event_worker (- (:timestamp e) (get_timestamp)))))))
  ))

(def unregister_watch (fn []
  (remove-watch _events nil)
  ))
