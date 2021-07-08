(ns hncljs.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :time
 (fn [db _]
   (:time db)))

(rf/reg-sub
 :time-color
 (fn [db _]
   (:time-color db)))

(rf/reg-sub
 :items
 (fn [db _]
   (:items db)))

(rf/reg-sub
 :posts-data
 (fn [db _]
   (:posts-data db)))

(rf/reg-sub
 :comments
 (fn [db _]
   (:comments db)))

(rf/reg-sub
 :page-n
 (fn [db _]
   (:page-n db)))

(rf/reg-sub
 :stories-loading?
 (fn [db _]
   (:stories-loading? db)))

(rf/reg-sub
 :error
 (fn [db _]
   (:error db)))
