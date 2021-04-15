(ns hncljs.views
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [hncljs.handlers]
   [hncljs.subs]
   [clojure.string :as str]))


(def base-url "https://hacker-news.firebaseio.com/v0/")
(def topstories (str base-url "topstories.json"))

(defn query-item [i]
  (str base-url "item/" i ".json"))

(defn get-src [url]
  (nth (.split url "/") 2))

(defn each-comment [id]
  (let [data (rf/subscribe [:comments])
        loading? @(rf/subscribe [:comm-loading?])
        query (query-item id)
        key (keyword (str id))
        _ (when-not (key @data) (rf/dispatch [:get-comm-data query]))]
    (fn []
      [:div [:b (:by (key @data))]
       [:p {:dangerouslySetInnerHTML
            {:__html (:text (key @data))}}]])))

(defn post-page [id]
  (let [data @(rf/subscribe [:posts-data])
        key (keyword (str id))
        post (key data)]
    (fn []
      (if (nil? (keys (key data)))
        [:p "nothing to show . ."]
        [:div
         [:a {:href (:url post)} [:h3 (:title post)]]
         [:p (:by post)] [:hr ]
         (for [comm (:kids post)]
           [:div {:style {:margin "30px 0px"} :key comm}
            [each-comment comm]])]))))

(defn post-desc-view [data id]
  (if (nil? data) 
    [:p "..."]
    [:div.cards
     [:span {:style {:margin-right "25px"
                     :font-size "20px"}}
      (:score data)]
     [:div [:p [:b (:title data)]
            (when (:url data)
              [:a {:href (:url data)} " // " (get-src (:url data))])]
      [:p [:span (:by data)]
       [:button {:on-click (fn [] (rf/dispatch [:set-post-page id]))}
        (count (:kids data)) " comments"]]]]))

(defn each-post [id]
  (let [data (rf/subscribe [:posts-data])
        loading? @(rf/subscribe [:post-loading?])
        query (query-item id)
        key (keyword (str id))
        _ (when-not (key @data) (rf/dispatch [:get-post-data query]))] 
    (fn []
      [post-desc-view (key @data) id])))

#_(nil? @(rf/subscribe [:posts-data]))

(defn posts-list []
  [:div.container
   (let [page-n @(rf/subscribe [:page-n])
         items @(rf/subscribe [:items])
         _ (when-not items (rf/dispatch [:get-stories topstories]))]
     [:div
      [:button {:on-click (fn [] (rf/dispatch [:set-page-n (dec page-n)]))
                :disabled (= page-n 1)} "prev"]
      [:span "page " page-n]
      [:button {:on-click (fn [] (rf/dispatch [:set-page-n (inc page-n)]))} "next"]
        (for [i (drop (* (dec page-n) 10)
                      (take (* page-n 10) items))]
          [:div {:style {:margin "10px"} :key i} [each-post i]])])])

(defn app []
  [:div 
   [:h2.card-header.text-center "Hacker News PWA"]
   (let [post-id @(rf/subscribe [:show-page])]
     (if (nil? post-id)
       [posts-list]
       [:div
        [:button.back-button
         {:on-click (fn [] (rf/dispatch [:clear-post-page]))} "back"]
        [post-page post-id]]))])
