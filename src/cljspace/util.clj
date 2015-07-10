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
  (map keyword (. (. (type rec) getMethod "getBasis" nil) invoke nil nil))
  ))

(def rec-to-map (fn [rec]
  (reduce
    (fn [acc field] (assoc acc field (field rec)))
    { :rec-type (rec-name rec) }
    (reverse (rec-fields rec)))
  ))

(def map-into-rec (fn [rec args]
  (reduce (fn [acc field] (assoc acc field (field args))) rec (rec-fields rec))
  ))
