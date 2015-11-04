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
      {:remote true})))


(defui HelloWorld
  static om/IQuery
  (query [this]
         '[:message :description :sender])
  Object
  (render [this]
          (let [props (om/props this)
                msg (:message props)
                desc (:description props)
                sender (if-not (= (:sender props) :not-found)
                         (:sender props)
                         "Unknown")]
            (dom/div nil (str msg ": " desc " -- " sender)))))


(defn transit-post [url]
  (fn [edn cb]
    (println edn)
    (.send XhrIo url
           (fn [e]
             (this-as this
                      (println (t/read (t/reader :json)
                                       (.getResponseText this)))
                      (cb (t/read (t/reader :json) (.getResponseText this)))))
           "POST" (t/write (t/writer :json) edn)
           #js {"Content-Type" "application/transit+json"})))



(def reconciler
  (om/reconciler
   {:state app-state
    :parser (om/parser {:read read})
    :send (transit-post "/api")}))

(om/add-root! reconciler
              HelloWorld (gdom/getElement "app"))



(defn on-js-reload []
  (println "Reloaded!")
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

