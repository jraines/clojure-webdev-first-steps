(ns user
  (:require [ragtime.jdbc :as jdbc]
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
