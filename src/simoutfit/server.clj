(ns simoutfit.server
  (:gen-class)
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.transit :refer [wrap-transit-params]]
            [cognitect.transit :as t]
            [environ.core :refer [env]]
            [om.next.server :as om]
            [org.httpkit.server :refer [run-server]])
  (:import [java.io ByteArrayOutputStream]))

(defn env-setup []
  (if (= (env :environment) "dev")
    (require '[ring.middleware.reload :refer [wrap-reload]])
    (def wrap-reload nil)))

(env-setup)

(defn write [x]
  (let [baos (ByteArrayOutputStream.)
        w    (t/writer baos :json)
        _    (t/write w x)
        ret  (.toString baos)]
    (.reset baos)
    ret))

(defn transit-response [x]
  {:headers {"Content-Type" "application/transit+json"}
   :body (write x)})

(def state (atom {:message "Hello from server"
                  :description "I am from the server!"} ))

(defmulti readfn (fn [env key params] key))

(defmethod readfn :default
  [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:value :not-found})))

(def parser
  (om/parser {:read readfn}))

(defn api [req]
  (transit-response
   (parser
    {:state state}
    (-> req :params :remote))))


(defroutes app
  (route/resources "/")
  (POST "/api" params api))

(defn start-dev-server []
  (run-server (-> app
                  wrap-transit-params
                  wrap-reload)
              {:port 5000}))

(defn start-prod-server []
  (run-server (-> app
                  wrap-transit-params)
              {:port 5000}))

(defn -main []
  (if (= (env :environment) "dev")
    (start-dev-server)
    (start-prod-server)))

