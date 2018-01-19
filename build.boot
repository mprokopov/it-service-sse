(set-env!
 :source-paths #{"src"}
 :resource-paths #{"config"}
 :dependencies '[[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.3.443"]
                 [io.pedestal/pedestal.service "0.5.2"]
                 [io.pedestal/pedestal.jetty "0.5.2"]
                 [ch.qos.logback/logback-classic "1.1.8" :exclusions [org.slf4j/slf4j-api]]
                 ;[org.slf4j/jul-to-slf4j "1.7.22"]
                 ;[org.slf4j/jcl-over-slf4j "1.7.22"]
                 ;[org.slf4j/log4j-over-slf4j "1.7.22"]
                 [com.taoensso/carmine "2.16.0"]
                 [environ "1.1.0"]])

(task-options!
 pom {:project 'it-service-sse
      :version "1.2.0"
      :description "IT Service server sent events Pedestal based"}
 aot {:namespace '#{it-service-sse.server}}
 jar {:main 'it-service-sse.server}
 sift {:include #{#"\.jar$"}})

(deftask build []
  (comp (pom) (aot) (uber) (jar) (sift) (target)))
