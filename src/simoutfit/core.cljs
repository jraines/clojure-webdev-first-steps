(ns ^:figwheel-always simoutfit.core
  (:require [cognitect.transit :as t]
            [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom])
  (:import [goog.net XhrIo]))

(enable-console-print!)

(defui HelloWorld
  Object
  (render [this]
          (dom/div nil "Hello, world!")))

(def hello (om/factory HelloWorld))

(js/ReactDOM.render (hello) (gdom/getElement "app"))


(def r (t/reader :json))

(defn get-data [url cb]
  (XhrIo.send url
              (fn [e]
                (let [xhr (.-target e)]
                  (cb (.getResponseText xhr))))))

(get-data "/data"
          (fn [res]
            (println res)
            (println (t/read r res))))


(defn on-js-reload []
  (println "Reloaded!")
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

