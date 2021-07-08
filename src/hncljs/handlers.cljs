(ns hncljs.handlers
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]
            [clojure.string :as str]
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

;; set page-n

(rf/reg-event-db
 :set-page-n
 (fn [db [_ new-page]]
   (assoc db :page-n new-page)))

;; get topstories

(rf/reg-event-fx
 :get-stories
 (fn [{:keys [db]} [_ url]]
   {:http-xhrio {:method            :get
                 :uri               url
                 :format            (ajax/json-request-format)
                 :response-format   (ajax/json-response-format {:keywords? true})
                 :on-success        [:process-response :story]
                 :on-failure        [:bad-response :story]}
    :db (assoc db :stories-loading? true)}))

;; get post data

(rf/reg-event-fx
 :get-post-data
 (fn [{:keys [db]} [_ url item]]
   {:http-xhrio {:method            :get
                 :uri               url
                 :format            (ajax/json-request-format)
                 :response-format   (ajax/json-response-format {:keywords? true})
                 :on-success        [:process-response :post]
                 :on-failure        [:bad-response :post]}
    :db (assoc-in db [:posts-data (keyword (str item))] "fetching")}))

;; get comments data

(rf/reg-event-fx
 :get-comm-data
 (fn [{:keys [db]} [_ url item]]
   {:http-xhrio {:method            :get
                 :uri               url
                 :format            (ajax/json-request-format)
                 :response-format   (ajax/json-response-format {:keywords? true})
                 :on-success        [:process-response :comm]
                 :on-failure        [:bad-response :comm]}
    :db (assoc-in db [:comments (keyword (str item))] "fetching")}))

;; handle sucess

(rf/reg-event-db
 :process-response
 (fn [db [_ req-key response]]
   (case req-key
     :story (-> db
                (assoc :story-loading? false)
                (assoc :items response))
     :post (-> db
               (assoc-in
                [:posts-data (keyword (str (:id response)))]
                response))
     :comm (-> db
               (assoc-in
                [:comments (keyword (str (:id response)))]
                response)))))

;; handle failure

(rf/reg-event-db
 :bad-response
 (fn [db [_ req-key response]]
   (case req-key
     :story (-> db
                (assoc :story-loading? false)
                (assoc :error response))
     :post (-> db
               (assoc :error response))
     :comm (-> db
               (assoc :error response)))))

