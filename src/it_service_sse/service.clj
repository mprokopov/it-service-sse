(ns it-service-sse.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.sse :as sse]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.log :as log]
            [clojure.core.async :as async]
            [taoensso.carmine :as car :refer [wcar]]
            [ring.util.response :as ring-resp]))


(def users (atom nil))

(def chan (async/chan 20))

(def chan-m (async/mult chan))

(defn inc-counter [uid]
  (log/counter :users -1)
  (swap! users update-in [:counters uid]
         (fn [item]
           (if item (inc item) 1))))

(defn dec-counter [uid]
  (swap! users update-in [:counters uid] dec))

(defn stream-ready [event-chan context]
  (let [new-chan (async/tap chan-m (async/chan))
        uid (get-in context [:request :path-params :uid])
        uid-pattern (re-pattern (str ".*:user-" uid))]
    (log/info :msg (str "connected user uid: " uid))
    (log/counter :users 1)
    (inc-counter uid)
    (async/go-loop []
      (let [msg (async/<! new-chan)
            [event channel pattern message] msg]
        (log/info :msg msg :uid uid :uid-pattern uid-pattern)
        (when (re-matches uid-pattern pattern)
          (async/>!! event-chan {:name pattern :data message}))
        (when (= "ticket:*:all" channel)
          (async/>!! event-chan {:name pattern :data message}))
        (when (= "*:agents:*" channel)
          (async/>!! event-chan {:name pattern :data message}))
        (recur)))))



(def users-page
  {:name :users-page
   :leave (fn [ctx]
            (println "users " @users)
            (assoc ctx :response (ring-resp/response @users)))})

(defn home-page
  [request]
  (ring-resp/response "Hello World!"))

(defn on-disconnect [ctx]
  (let [uid (get-in ctx [:request :path-params :uid])]
    (dec-counter uid)))

(def common-interceptors [(body-params/body-params) http/html-body])

(def routes #{["/" :get (conj common-interceptors `home-page)]
              ["/users/list" :get (conj common-interceptors users-page)]
              ["/channel/:uid" :get [(sse/start-event-stream stream-ready 10 10 {:on-client-disconnect on-disconnect})]
               :route-name :sse]})

;; Consumed by it-service-sse.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ::http/allowed-origins ["http://localhost:3000" "https://beta.it-premium.com.ua" "http://stage.it-premium.com.ua" "http://stage.it-premium.local"]

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ::http/secure-headers {:content-security-policy-settings {:object-src "none"}}
              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port 9292
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;:ssl-port 8443
                                        :ssl? false}})

