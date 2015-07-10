(ns objects.cargo
  (:use [compojure.response :only [Renderable]])
  (:use [ring.util.response :only [response]])
  (:use [cljspace.util :only [rec-to-map rec-utils]])
  (:use [cheshire.generate :only [add-encoder encode-map]]))

(defprotocol cargo-utils
  (qty-used [this])
  (qty-free [this])
  (qty [this])
  (remove-qty [this elem])
  (add-qty [this elem])
  )

(defrecord cargo [max_qty]
  Renderable
    (render [this req] (response (rec-to-map this)))
  rec-utils
    (rec-name [this] "cargo")
  cargo-utils
    (qty [this] (or (:qty this) (list)))
    (qty-used [this] (reduce + 0 (map :qty (qty this))))
    (qty-free [this] (- (:max_qty this) (qty-used this)))
    (remove-qty [this elem]
      (assoc this :qty (remove (fn [e] (= e elem)) (qty this))))
    (add-qty [this elem] (assoc this :qty (concat (qty this) (list elem))))
  )

(add-encoder cargo (fn [f jg]
  (encode-map (rec-to-map f) jg)
  ))
