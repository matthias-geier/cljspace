(ns game.world)

(defprotocol obj_fns
  (by_id [this id]))

(defrecord world [max_id objects]
  obj_fns (by_id [this id] (get (:objects this) id)))

(def next_id (fn [world]
  (let [id (inc (:max_id world))]
    [(assoc world :max_id id) id])
  ))

(def add_obj (fn [world obj]
  (let [objects (assoc (:objects world) (:id obj) obj)]
    (assoc world :objects objects))
  ))

(def del_obj (fn [world obj]
  (let [objects (dissoc (:objects world) (:id obj))]
    (assoc world :objects objects))
  ))
