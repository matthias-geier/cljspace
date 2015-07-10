(ns objects.object
  (:use [compojure.response :only [Renderable]])
  (:use [ring.util.response :only [response]])
  (:use [game.world :only [add] :rename { add world-add }])
  (:use [cljspace.util :only [rec-to-map map-into-rec rec-utils]])
  (:use [objects.ftl])
  (:use [objects.mineable])
  (:use [objects.cargo])
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
  (let [cargo_obj (map-into-rec (map->cargo {}) args),
    args (assoc args :caps [(->ftl) cargo_obj]),
    ship_obj (map-into-rec (map->obj {}) args)]

    (world-add ship_obj))
  ))

(def asteroid (fn [args]
  (let [mine_obj (map-into-rec (map->mineable {}) args),
    ast_obj (map-into-rec (map->obj {}) (assoc args :caps [mine_obj]))]

    (world-add ast_obj))
  ))
