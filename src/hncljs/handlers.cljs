(ns hncljs.handlers
  (:require [re-frame.core :as rf]
            [hncljs.db :as db]))

(rf/reg-event-db
 :initialize
 (fn [_ _]
   db/initial-db))

(rf/reg-event-db
 :time-color-change
 (fn [db [_ new-color-value]]
   (assoc db :time-color new-color-value)))

(rf/reg-event-db
 :timer
 (fn [db [_ new-time]]
   (assoc db :time new-time)))
