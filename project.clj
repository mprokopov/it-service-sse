(defproject it-service-sse "1.3.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [io.pedestal/pedestal.service "0.5.2"]

                 ;; Remove this line and uncomment one of the next lines to
                 ;; use Immutant or Tomcat instead of Jetty:
                 [io.pedestal/pedestal.jetty "0.5.2"]
                 ;; [io.pedestal/pedestal.immutant "0.5.2"]
                 ;; [io.pedestal/pedestal.tomcat "0.5.2"]

                 [ch.qos.logback/logback-classic "1.1.8" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.22"]
                 [org.slf4j/jcl-over-slf4j "1.7.22"]
                 [org.slf4j/log4j-over-slf4j "1.7.22"]
                 [org.clojure/core.async "0.4.490"]
                 [com.taoensso/carmine "2.16.0"]
                 [environ "1.1.0"]]
                 ;; [celtuce "0.1.1-SNAPSHOT"]]
  :min-lein-version "2.0.0"
  ;; :resource-paths ["config", "resources"]
  ;; If you use HTTP/2 or ALPN, use the java-agent to pull in the correct alpn-boot dependency
  ;:java-agents [[org.mortbay.jetty.alpn/jetty-alpn-agent "2.0.5"]]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "it-service-sse.server/run-dev"]}
                   :resource-paths ["config", "resources"]
                   :env {:redis-host "localhost"}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.2"]]}
             :uberjar {:aot [it-service-sse.server]}}
  :main ^{:skip-aot true} it-service-sse.server)

