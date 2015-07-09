(ns objects.object
  (:use [compojure.response :only [Renderable]])
  (:use [ring.util.response :only [response]])
  (:use [game.world :only [add] :rename { add world-add }])
  (:use [cljspace.util :only [rec-to-map rec-utils]])
  (:use [objects.ftl])
  (:use [objects.mineable])
  (:use [cheshire.generate :only [add-encoder encode-map]]))

(defrecord obj [id name size wgt mass qty caps]
  Renderable
    (render [this req] (response (rec-to-map this)))
  rec-utils
    (rec-name [this] "obj")
    (cap-by-type [this cap] (first (filter (fn [c] (instance? cap c)) caps)))
  )

(add-encoder obj (fn [o jg]
  (encode-map (rec-to-map o) jg)
  ))

(def ship (fn [args]
  (let [ship_obj (map->obj (assoc args :caps [(->ftl)]))]
    (world-add ship_obj))
  ))

(def asteroid (fn [args]
  (let [ast_obj (map->obj (assoc args :caps [(map->mineable args)]))]
    (world-add ast_obj))
  ))
