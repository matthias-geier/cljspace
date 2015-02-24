(ns mgtest.core
  (:require [game.world] [objects.object] [game.event]))

(def l (fn [w]
  (game.world/next_id w)
  ))

(def event_setup (fn []
  (game.event/register_watch)
  (game.event/add 100 (+ 10 (game.event/get_timestamp))
    (fn [_] (println "m1")) nil)
  (game.event/add 101 (+ 5 (game.event/get_timestamp)) (fn [_] (println "m2"))
    (fn [_] (println "m3")))
  (println "main thread sleeping for 10s")
  (Thread/sleep 11000)
  (println "main thread wake")
  (game.event/unregister_watch)
  (shutdown-agents)
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
  (event_setup)
  ))
