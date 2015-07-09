(ns cljspace.util)

(defprotocol rec-utils
  (rec-name [this])
  (cap-by-type [this cap])
  )

(def sanitize-args (fn [args]
  (if (map? args)
    (reduce (fn [acc [k v]] (assoc acc (keyword k) v)) {} args)
    args)
  ))

(def rec-fields (fn [rec]
  (. (. (type rec) getMethod "getBasis" nil) invoke nil nil)
  ))

(def rec-to-map (fn [rec]
  (let [fields (map keyword (rec-fields rec))]
    (reduce
      (fn [acc field] (assoc acc field (field rec)))
      { :rec-type (rec-name rec) }
      (reverse fields)))
  ))
