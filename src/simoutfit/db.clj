(ns simoutfit.db
  (:require [yesql.core :refer [defquery]]))

(def db-spec {:classname   "org.postgresql.DRIVER"
              :subprotocol "postgresql"
              :subname     "//localhost:5432/myapp"
              :user        "myappuser"
              :password    (System/getenv "SIMOUTFIT_DB_PWD")})

;; omit resources/ from path because it's on Lein classpath
(defquery last-message "queries/last_message.sql"
  {:connection db-spec})

