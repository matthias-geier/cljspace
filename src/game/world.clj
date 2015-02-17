(ns game.world)

(defprotocol obj_fns
  (by_id [this id]))

(defrecord world [max_id objects]
  obj_fns (by_id [this id] (get (:objects this) id)))

(def _world (ref (->world 0 {})))

(def from_world (fn [func & args]
  (apply func @_world args)
  ))

(def with_world (fn [func & args]
  (dosync (let [w @_world,
    result (apply func w args),
    [new_world & ret] (if-not (list? result) (list result) result)]

    (ref-set _world new_world)
    (if (< (count ret) 2) (first ret) ret)))
  ))

(def next_id (fn [world]
  (let [id (inc (:max_id world))]
    (list (assoc world :max_id id) id))
  ))

(def add_obj (fn [world obj]
  (let [objects (assoc (:objects world) (:id obj) obj)]
    (list (assoc world :objects objects) obj))
  ))

(def del_obj (fn [world obj]
  (let [objects (dissoc (:objects world) (:id obj))]
    (list (assoc world :objects objects) obj))
  ))
