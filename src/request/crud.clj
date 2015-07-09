(ns request.crud
  (:use [game.world :only [from by-id] :rename { from from-world }])
  (:use [request.sanity])
  (:require [objects.object :as object :only [ship asteroid]])
  (:use [cljspace.util :only [sanitize-args]]))

(def ship (fn [request]
  (if (= (:request-method request) :put)
    (object/ship (sanitize-args (:body request))))
  ))

(def asteroid (fn [request]
  (if (= (:request-method request) :put)
    (object/asteroid (sanitize-args (:body request))))
  ))

(def object (fn [request]
  (if (= (:request-method request) :get)
    (let [id (Integer/parseInt (:id (:route-params request)))]
      (when-id-exists? (partial from-world by-id) id))
    )
  ))

(def mine-event (fn [mobj tobj res]
  (println "mine event" mobj tobj res)
  ))

(def convert-mine-ids (fn [mid rid res]
  (let [mobj (from-world by-id mid), robj (from-world by-id rid)]
    (bind-error-chain-or-next mine-event mobj robj res
      (list has-cargo? mobj) (list has-mine-resource? robj res))
    )
  ))

(def mine (fn [request]
  (if (= (:request-method request) :post)
    (let [mid (Integer/parseInt (:id (:route-params request))),
      rid (get (:body request) "target_id"),
      res (get (:body request) "resource")]

      (bind-error-chain-or-next convert-mine-ids mid rid res
        (list id-missing? mid) (list id-missing? rid))
      )
    )
  ))
