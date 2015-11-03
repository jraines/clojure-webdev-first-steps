(ns ^:figwheel-always simoutfit.core
  (:require [cognitect.transit :as t])
  (:import [goog.net XhrIo]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

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



(defonce app-state (atom {:text "Hello world!!!"}))


(defn on-js-reload []
  (println "Reloaded!")
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

