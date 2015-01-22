(ns mgtest.core
  (:require [game.world] [objects.object]))

(def -main (fn []
  (let [world (game.world/->world 0 {}),
    [world ship] (objects.object/ship world {
      :name "Falcon", :size 10, :wgt 100, :mass 15, :qty 1})]
    (println world)
    (println ship)
    (println (game.world/by_id world 1))
    (let [world (game.world/del_obj world ship)]
      (println world)))
  ))
