(ns request.sanity
  (:use ring.util.response)
  (:use [cljspace.util :only [rec-to-map cap-by-type]])
  (:use [objects.mineable])
  (:import [objects.mineable mineable])
  (:use [game.world :only [from by-id] :rename { from from-world }]))

(def id-missing? (fn [id]
  (if-not (contains? (from-world (fn [w] (:objects w))) id)
    (response { :status false :error (str "id: " id " not found") }))
  ))

(def has-cargo? (fn [obj]
  (println "has cargo" obj)
  ))

(def has-mine-resource? (fn [obj resource]
  (let [cap (cap-by-type obj mineable)]
    (if (or (nil? cap)
      (not (has-resource? cap resource))
      (<= (resource-qty cap resource) 0))

      (response { :status false
        :error (str "target id: " (:id obj) " lacks the resource " resource) }))
    )
  ))

(def split-fns (fn [args]
  (split-with (comp not list?) args)
  ))

(def bind-error-chain-or-next (fn [nfn & args]
  (let [[n_args fns] (split-fns args)]
    (if-let [err (some eval fns)]
      err
      (apply nfn n_args))
    )
  ))

(def when-id-exists? (fn [fnc id]
  (if (contains? (from-world (fn [w] (:objects w))) id)
    (fnc id)
    (response { :status false :error (str "id: " id " not found") }))
  ))
