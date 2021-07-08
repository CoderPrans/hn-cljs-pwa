(ns hncljs.post
  (:require
   [re-frame.core :as rf]
   [hncljs.handlers]
   [hncljs.subs]
   [clojure.string :as str]
   [hncljs.state :as state]))


(defn comm-view [data]
  [:div
   [:b (:by data)]
   [:p {:dangerouslySetInnerHTML
        {:__html (:text data)}}]])


(defn each-comment [id]
  (let [data (rf/subscribe [:comments])
        query (state/query-item id)
        key (keyword (str id))
        _ (when-not (key @data)
            (rf/dispatch [:get-comm-data query id]))]
    [:div
     [comm-view (key @data)]
     (when-let [kids (:kids (key @data))] 
       (for [cid kids]
         [:div.replies {:key cid} [each-comment cid]]))]))


(defn view [{:keys [path-params] :as props}]
  (let [id (js/parseInt (:id path-params))
        query (state/query-item id)
        data @(rf/subscribe [:posts-data])
        _ (when-not data
            (rf/dispatch [:get-post-data query id]))
        key (keyword (str id))
        post (key data)]
    [:div
     [:a {:href (state/href :home)}
      [:button.back-button
       "back"]]
     (js/console.log data)
     (if (nil? (keys (key data)))
       [:p "nothing to show . ."]
       [:div
        [:a {:href (:url post)} [:h3 (:title post)]]
        [:p (:by post)] [:hr ]
        (for [comm (:kids post)]
          [:div {:style {:margin "30px 0px"} :key comm}
           [each-comment comm]])])]))

