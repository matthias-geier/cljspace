(ns game.world)

(defprotocol obj-fns
  (by-id [this id])
  )

(defrecord world [max_id objects]
  obj-fns
    (by-id [this id] (get (:objects this) id))
  )

(def ^{:private true} _world (ref (->world 0 {})))

(def from (fn [func & args]
  (apply func @_world args)
  ))

(def with (fn [func & args]
  (dosync (let [w @_world,
    result (apply func w args),
    [new_world & ret] (if-not (list? result) (list result) result)]

    (ref-set _world new_world)
    (if (< (count ret) 2) (first ret) ret)))
  ))

(def ^{:private true} next-id (fn [world]
  (let [id (inc (:max_id world))]
    (list (assoc world :max_id id) id))
  ))

(def ^{:private true} assoc-id (fn [obj]
  (if (:id obj)
    obj
    (assoc obj :id (with next-id)))
  ))

(def add (fn [obj]
  (let [obj (assoc-id obj)]
    (with (fn [w]
      (list (assoc w :objects (assoc (:objects w) (:id obj) obj)) obj))
      ))
  ))

(def del (fn [obj]
  (with (fn [w]
    (list (assoc w :objects (dissoc (:objects w) (:id obj))) obj)
    ))
  ))

