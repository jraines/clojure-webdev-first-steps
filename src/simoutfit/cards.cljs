(ns simoutfit.cards
  (:require
   [goog.dom :as gdom]
   [om.next :as om :refer-macros [defui]]
   [simoutfit.core :as so :refer [hello]]
   [om.dom :as dom])
  (:require-macros
   [devcards.core :refer [defcard]]))

(defcard simple-component
  "Test"
  (hello {:message "Hello" :description "This is a basic component" :sender "Jeremy"}))
