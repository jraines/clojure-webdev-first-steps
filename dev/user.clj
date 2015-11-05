(ns user
  (:require [figwheel-sidecar.repl-api :refer [cljs-repl]]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :as rtr]))

(defn load-config []
  {:datastore  (jdbc/sql-database (str "jdbc:postgresql://localhost:5432/myapp?password="
                                       (System/getenv "SIMOUTFIT_DB_PWD")))
   :migrations (jdbc/load-resources "migrations")})

(defn migrate []
  (println "migrating")
  (rtr/migrate (load-config)))

(defn rollback []
  (println "rolling back last migration")
  (rtr/rollback (load-config)))

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
