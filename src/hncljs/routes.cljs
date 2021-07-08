(ns hncljs.routes
  (:require
   [hncljs.post :as post]
   [hncljs.list :as list]
   [reitit.frontend :as rtf]
   [reitit.frontend.controllers :as rtfc]
   [reitit.frontend.easy :as rtfe]
   [reagent.core :as r]))

(defonce match (r/atom nil))

(def routes
  [["/" {:name :home
         :view list/view}]
   ["/post/:id" {:name :post/id
                 :view post/view}]])

(defn route-handler [new-match]
  (swap! match
         (fn [old-match]
           (when new-match
             (assoc new-match
                    :controllers (rtfc/apply-controllers (:controllers old-match) new-match))))))

(defn init! []
  (rtfe/start!
   (rtf/router routes)
   route-handler
   {:use-fragment true}))
