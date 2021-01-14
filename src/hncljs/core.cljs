(ns hncljs.core
    (:require
      [reagent.core :as r]
      [reagent.dom :as d]
      [kitchen-async.promise :as p]))

;; -------------------------
;; Views

(def items (r/atom nil))
(def show-page (r/atom nil))

;; app-state [{:id 245189
;;             :title "Post Title"} {}] 

(def base-url "https://hacker-news.firebaseio.com/v0/")
(def topstories (str base-url "topstories.json"))
(defn query-item [i]
  (str base-url "item/" i ".json"))

(comment
  (.then (fetch-link! (str base-url "item/25710055.json"))
         #(js/console.log (.-title %)))
  )

; fetch stuff form url and reset state
(defn fetch-stuff [url state]
  (p/let [res (-> (js/fetch url)
                  (p/then #(.json %))
                  (p/then (fn [data] data)))]
    (reset! state res)))

; post item component
(defn each-post [id]
  (let [post-data (r/atom "")
        query (query-item id)] 
    (fn []
      (do
        (when (= @post-data "") (fetch-stuff query post-data))
        (if (nil? (keys (js->clj @post-data)))
          [:p "..."]
          [:div.cards
           [:span {:style {:margin-right "25px"
                           :font-size "20px"}}
            (.-score @post-data)]
           [:div 
            [:p [:b (.-title @post-data)]
             " // " [:a {:href (.-url @post-data)}
                     (get-src (.-url @post-data))]]
            [:p [:span (.-by @post-data)]
             
             [:button {:on-click #(reset! show-page id)}
              (.-descendants @post-data) " comments"]
             ]]])))))

(defn get-src [url]
  (nth (.split url "/") 2))

(get-src "https://www.google.com/now/present/always")

; posts list component
(defn posts-list []
  [:div
   #_[:button {:on-click #(fetch-stuff topstories data)} "fetch-hn_data"]
   (do
     (when-not @items (fetch-stuff topstories items))
     (for [i (take 10 @items)]
       [:div {:style {:margin "10px"} :key i} [each-post i]]))])

; each comment component
(defn each-comment [id]
  (let [comm-data (r/atom "")
        query (query-item id)]
    (fn []
      (do
        (when (= @comm-data "") (fetch-stuff query comm-data))
        (if (nil? (keys (js->clj @comm-data)))
          [:p "..."]
          [:div
           [:b (.-by @comm-data)]
           [:p {:dangerouslySetInnerHTML {:__html (.-text @comm-data)}}]
           ])))))

; post page component
(defn post-page [id]
  (let [post-data (r/atom "")
        query (query-item id)]
    (fn []
      (do
        (when (= @post-data "") (fetch-stuff query post-data))
        (if (nil? (keys (js->clj @post-data)))
          [:p "..."]
          [:div
           [:h4 (.-title @post-data)]
           [:p (.-by @post-data)]
           [:hr ]
           (for [comm (.-kids @post-data)]
             [:div {:style {:margin "30px 0px"} :key comm}
              [each-comment comm]])])))))

; app component
(defn app []
  [:div 
   [:h2.card-header.text-center "Hacker News PWA"]
   (if (nil? @show-page)
     [posts-list]
     [:div
      [:button {:on-click #(reset! show-page nil)
                :class "back-button"} "back"]
      [post-page @show-page]])])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [app] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
