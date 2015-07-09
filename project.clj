(defproject cljspace "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.6.0"]
    [ring "1.4.0-RC2"]
    [ring/ring-json "0.3.1"]
    [compojure "1.3.2"]]
  :plugins [[lein-ring "0.9.6"]]
  :main ^:skip-aot cljspace.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :ring { :handler cljspace.core/app
    :port 8080
    :open-browser? false
    :init cljspace.core/init
    :destroy cljspace.core/destroy })
