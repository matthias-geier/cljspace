(ns objects.ftl
  (:use [compojure.response :only [Renderable]])
  (:use [ring.util.response :only [response]])
  (:use [cljspace.util :only [rec-to-map rec-utils]])
  (:use [cheshire.generate :only [add-encoder encode-map]]))

(defrecord ftl []
  Renderable
    (render [this req] (response (rec-to-map this)))
  rec-utils
    (rec-name [this] "ftl")
  )

(add-encoder ftl (fn [f jg]
  (encode-map (rec-to-map f) jg)
  ))
