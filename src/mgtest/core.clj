(ns mgtest.core
  (:require [game.world] [objects.object]))

(def l (fn [w]
  (game.world/next_id w)
  ))

(def -main (fn []
  (let [ship (objects.object/ship {
    :name "Falcon", :size 10, :wgt 100, :mass 15, :qty 1})]

    (println game.world/_world)
    (println ship)
    (println (game.world/from_world game.world/by_id 1))
    (game.world/with_world game.world/del_obj ship)
    (println game.world/_world))
  (println (game.world/with_world l))
  (println (game.world/with_world l))
  ))
