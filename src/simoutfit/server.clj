(ns simoutfit.server
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [cognitect.transit :as t]
            [org.httpkit.server :refer [run-server]])
  (:import [java.io ByteArrayOutputStream]))

(defn write [x]
  (let [baos (ByteArrayOutputStream.)
        w    (t/writer baos :json)
        _    (t/write w x)
        ret  (.toString baos)]
    (.reset baos)
    ret))

(defn transit-response [x]
  {:headers {"Content-Type" "application/transit+json"}
   :body (write x)}
  )

(defroutes app
  (route/resources "/")
  (GET "/data" [] (transit-response {:message "Hello from server"})))

(defn -main []
  (run-server app {:port 5000}))

