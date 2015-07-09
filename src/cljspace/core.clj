(ns cljspace.core
  (:require [request.crud :as crud])
  (:require [game.event :only [unregister-watch register-watch]])
  (:require compojure.route)
  (:require compojure.handler)
  (:use compojure.core)
  (:use ring.middleware.reload)
  (:use ring.middleware.json)
  (:use ring.adapter.jetty)
  (:use ring.util.response))

(defroutes my-routes
  (GET "/object/:id{[0-9]+}" [id] crud/object)
  (PUT "/ship" [] crud/ship)
  (PUT "/asteroid" [] crud/asteroid)
  (POST "/object/:id/mine" [id] crud/mine)
  (compojure.route/not-found "Not found!")
  )

(def app (wrap-reload (wrap-json-response (wrap-json-body my-routes))))

(def init (fn []
  (println "Register event watch")
  (game.event/register-watch)
  ))

(def destroy (fn []
  (println "Unregister event watch")
  (game.event/unregister-watch)
  (println "Shutdown agents")
  (shutdown-agents)
  ))
