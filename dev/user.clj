(ns user
  (:require [figwheel-sidecar.repl-api :refer [cljs-repl]]))
;; (ns user
;;   (:require [com.stuartsierra.component :as component]
;;             [figwheel-sidecar.system :as sys]))

;; (def system
;;   (component/system-map
;;    :figwheel-system (sys/figwheel-system (sys/fetch-config))))

;; (defn fig []
;;   (alter-var-root #'system component/start)
;;   (sys/build-switching-cljs-repl (:figwheel-system system)))



;; (println "use (fig) to start figwheel repl")
