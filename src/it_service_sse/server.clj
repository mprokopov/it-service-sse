(ns it-service-sse.server
  (:gen-class) ; for -main method in uberjar
  (:require [io.pedestal.http :as server]
            [io.pedestal.http.route :as route]
            [clojure.core.async :as async]
            [taoensso.carmine :as car :refer [wcar]]
            [environ.core :refer [env]]
            [io.pedestal.log :as log]
            [it-service-sse.service :as service]))

;; This is an adapted service map, that can be started and stopped
                                        ;close; From the REPL you can call server/start and server/stoo on this service
(defonce runnable-service (server/create-server service/service))

(defn run-dev
  "The entry-point for 'lein run-dev'"
  [& args]
  (println "\nCreating your [DEV] server...")
  (-> service/service ;; start with production configuration
      (merge {:env :dev
              ;; do not block thread that starts web server
              ::server/join? false
              ;; Routes can be a function that resolve routes,
              ;;  we can use this to set the routes to be reloadable
              ::server/routes #(route/expand-routes (deref #'service/routes))
              ;; all origins are allowed in dev mode
              ::server/allowed-origins {:creds true :allowed-origins (constantly true)}})
      ;; Wire up interceptor chains
      server/default-interceptors
      server/dev-interceptors
      server/create-server
      server/start))

(defn redis-uri []
  (if-let [uri (:redis-url env)]
    {:uri uri} { :host "localhost"}))

(def server1-conn {:pool {} :spec (redis-uri)})

(defn start-redis
  "returns redis instance"
  []
  (car/with-new-pubsub-listener (:spec server1-conn)
    {"*:agents:*"
     (fn [msg]
       (do
         (log/info :msg msg)
         (async/>!! service/chan msg)))
     "ticket:*:all"
     (fn [msg]
       (do
         (log/info :msg msg)
         (async/>!! service/chan msg)))
     "*:user-*"
     (fn [msg]
       (do
         (log/info :msg msg)
         (async/>!! service/chan msg)))}
    (car/psubscribe "ticket:*:all")
    (car/psubscribe "*:agents:*")
    (car/psubscribe "*:user-*")))

(defn stop-redis [redis]
  (car/close-listener redis))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (let [redis (start-redis)]
    (server/start runnable-service)
    (stop-redis redis)))

;; If you package the service up as a WAR,
;; some form of the following function sections is required (for io.pedestal.servlet.ClojureVarServlet).

;;(defonce servlet  (atom nil))
;;
;;(defn servlet-init
;;  [_ config]
;;  ;; Initialize your app here.
;;  (reset! servlet  (server/servlet-init service/service nil)))
;;
;;(defn servlet-service
;;  [_ request response]
;;  (server/servlet-service @servlet request response))
;;
;;(defn servlet-destroy
;;  [_]
;;  (server/servlet-destroy @servlet)
;;  (reset! servlet nil))

