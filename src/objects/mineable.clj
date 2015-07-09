(ns objects.mineable
  (:use [compojure.response :only [Renderable]])
  (:use [ring.util.response :only [response]])
  (:use [cljspace.util :only [rec-to-map rec-utils]])
  (:use [cheshire.generate :only [add-encoder encode-map]]))

(defprotocol mine-utils
  (has-resource? [this res])
  (resource-qty [this res])
  )

(defrecord mineable [resource_qty]
  Renderable
    (render [this req] (response (rec-to-map this)))
  rec-utils
    (rec-name [this] "mineable")
  mine-utils
    (has-resource? [this res] (contains? (:resource_qty this) res))
    (resource-qty [this res]
      (if (has-resource? this res)
        (get (:resource_qty this) res)
        nil))
  )

(add-encoder mineable (fn [f jg]
  (encode-map (rec-to-map f) jg)
  ))
