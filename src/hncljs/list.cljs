(ns hncljs.list
  (:require
   [re-frame.core :as rf]
   [hncljs.handlers]
   [hncljs.subs]
   [clojure.string :as str]
   [hncljs.state :as state]))


(defn post-desc-view [data id]
  (if (or (nil? data) (= data "fetching")) 
    [:p "..."]
    [:div.cards
     [:span {:style {:margin-right "25px"
                     :font-size "20px"}}
      (:score data)]
     [:div [:p [:b (:title data)]
            (when (:url data)
              [:a {:href (:url data)}
               " // " (state/get-src (:url data))])]
      [:p [:span (:by data)]
       [:a {:href (state/href :post/id {:id id})}
        [:button (:descendants data) " comments"]]]]]))


(defn each-post [id]
  (let [data (rf/subscribe [:posts-data])
        query (state/query-item id)
        key (keyword (str id))
        _ (when-not (key @data) (rf/dispatch [:get-post-data query id]))] 
    (fn []
      [post-desc-view (key @data) id])))


(defn view []
  [:div.container
   (let [page-n @(rf/subscribe [:page-n])
         items @(rf/subscribe [:items])
         _ (when-not items (rf/dispatch [:get-stories state/topstories]))]
     [:div
      [:button {:on-click (fn [] (rf/dispatch [:set-page-n (dec page-n)]))
                :disabled (= page-n 1)} "prev"]
      [:span "page " page-n]
      [:button {:on-click
                (fn [] (rf/dispatch [:set-page-n (inc page-n)]))} "next"]
        (for [i (drop (* (dec page-n) 10)
                      (take (* page-n 10) items))]
          [:div {:style {:margin "10px"} :key i} [each-post i]])])])
