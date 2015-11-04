(ns simoutfit.db
  (:require [yesql.core :refer [defquery]]))

(def db-spec {:classname   "org.sqlite.JDBC"
              :subprotocol "sqlite"
              :subname     "db/demo.sqlite"})

;; omit resources/ from path because it's on Lein classpath
(defquery last-message "queries/last_message.sql"
  {:connection db-spec})

