(ns objects.object
  (:require [game.world])
  (:use [objects.ftl]))

(defrecord obj [id name size wgt mass qty caps])

(def ship (fn [world args]
  (let [[world next_id] (game.world/next_id world),
    caps [(->ftl)],
    args (merge args {:id next_id, :caps caps}),
    ship_obj (map->obj args)]

    [(game.world/add_obj world ship_obj) ship_obj])
  ))
