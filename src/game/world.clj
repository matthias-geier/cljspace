(ns game.world)

(defprotocol obj_fns
  (by_id [this id]))

(defrecord world [max_id objects]
  obj_fns (by_id [this id] (get (:objects this) id)))

(def next_id (fn [world]
  (let [id (inc (:max_id world))]
    (list (assoc world :max_id id) id))
  ))

(def add_obj (fn [world obj]
  (let [objects (assoc (:objects world) (:id obj) obj)]
    (assoc world :objects objects))
  ))

(def del_obj (fn [world obj]
  (let [objects (dissoc (:objects world) (:id obj))]
    (assoc world :objects objects))
  ))

(def _world (ref (->world 0 {})))

(defmacro with_world [func & args]
  `(let [w# @_world,
    new_l# (~func w# ~@args),
    [new_w# & ret#] (if-not (list? new_l#) (list new_l#) new_l#)]
    (dosync (ref-set _world new_w#))
    (if (< (count ret#) 2) (first ret#) ret#))
  )
