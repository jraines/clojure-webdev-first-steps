(ns ^:figwheel-always simoutfit.core
  (:require [cognitect.transit :as t]
            [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom])
  (:import [goog.net XhrIo]))

(enable-console-print!)

(defonce app-state (atom {:message "Hello Om"}))

(defmulti read (fn [env key params] key))

(defmethod read :default
  [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:value :not-found})))


(defui HelloWorld
  static om/IQuery
  (query [this]
         '[:message])
  Object
  (render [this]
          (dom/div nil (get (om/props this) :message))))


(def reconciler
  (om/reconciler
   {:state app-state
    :parser (om/parser {:read read})}))

(om/add-root! reconciler
              HelloWorld (gdom/getElement "app"))




(def r (t/reader :json))

(defn get-data [url cb]
  (XhrIo.send url
              (fn [e]
                (let [xhr (.-target e)]
                  (cb (.getResponseText xhr))))))

(get-data "/data"
          (fn [res]
            (let [resp (t/read r res)
                  _ (println resp)
                  msg (:message resp)]
              (swap! app-state assoc :message msg))))


(defn on-js-reload []
  (println "Reloaded!")
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

