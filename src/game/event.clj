(ns game.event
  (:use [game.world :only [from by-id] :rename { from from-world }]))

(def ^{:private true} time-precision 10)

(def get-timestamp (fn []
  (quot (System/currentTimeMillis) time-precision)
  ))

(defprotocol ev-func
  (process [this])
  (cleanup [this])
  )

(defprotocol resolver
  (resolve-ref [this])
  )

(defrecord event [ref_id props timestamp complete_f cleanup_f]
  java.lang.Comparable
    (compareTo [this other] (compare (:timestamp this) (:timestamp other)))
  ev-func
    (process [this] (when-not (nil? complete_f) (complete_f this)))
    (cleanup [this] (when-not (nil? cleanup_f) (cleanup_f this)))
  resolver
    (resolve-ref [this] (from-world by-id (:ref_id this)))
  )

(def ^{:private true} _events (ref (sorted-set)))

(def ^{:private true} _worker (ref nil))

(def ^{:private true} set_worker (fn [arg]
  (dosync
    (let [value (if (fn? arg) (arg @_worker) arg)]
      (ref-set _worker value)))
  ))

(def with (fn [func & args]
  (dosync
    (let [e @_events,
      result (apply func e args),
      [changed_events & ret] (if-not (list? result) (list result) result)]

      (ref-set _events changed_events)
      ret))
  ))

(def ^{:private true} events? (fn []
  (with (fn [events] [events, (empty events)]))
  ))

(def ^{:private true} shift (fn []
  (with (fn [events]
    (let [f (first events),
      e (disj events f)]

      (list e f))))
  ))

(def add (fn [ref_id props timestamp complete_f cleanup_f]
  (let [e (->event ref_id props timestamp complete_f cleanup_f)]
    (with (fn [events] (conj events e))))
  ))

(def del (fn [ev]
  (with (fn [events] (disj events ev)))
  ))

(def ^{:private true} event_worker (fn [s]
  (let [maxs (max 0 s), f (future
    (Thread/sleep (* time-precision maxs))
    (set_worker nil)
    (when-let [e (first (shift))]
      (process e)
      (cleanup e)))]

    (set_worker f))
  ))

(def register-watch (fn []
  (add-watch _events nil (fn [_key, _ref, _old, _new]
    (when-not (= (first _old) (first _new))
      (set_worker (fn [worker]
        (when-not (nil? worker)
          (future-cancel worker))
        nil))
      (when-let [e (first _new)]
        (event_worker (- (:timestamp e) (get-timestamp)))))))
  ))

(def unregister-watch (fn []
  (remove-watch _events nil)
  ))
