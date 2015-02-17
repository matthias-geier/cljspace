(ns objects.object
  (:require [game.world])
  (:use [objects.ftl]))

(defrecord obj [id name size wgt mass qty caps])

(def ship (fn [args]
  (let [next_id (game.world/with_world game.world/next_id),
    caps [(->ftl)],
    args (merge args {:id next_id, :caps caps}),
    ship_obj (map->obj args)]

    (game.world/with_world game.world/add_obj ship_obj))
  ))
