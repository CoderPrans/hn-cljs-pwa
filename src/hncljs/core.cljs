(ns hncljs.core
    (:require
      [reagent.core :as r]
      [reagent.dom :as d]
      [kitchen-async.promise :as p]))

;; -------------------------
;; Views

(def items (r/atom nil))

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
        [:div.cards
         [:span {:style {:flex 1}} (.-score @post-data)]
         [:div {:style {:flex 10}} [:b (.-title @post-data)]
          [:p [:span (.-by @post-data)]
           [:a {:href (.-url @post-data)
                :style {:margin-left "20px"}} "visit"]]]
         ]))))

; posts list component
(defn posts-list []
  [:div
   [:h2.card-header.text-center "Hacker News Data"]
   #_[:button {:on-click #(fetch-stuff topstories data)} "fetch-hn_data"]
   (do
     (when-not @items (fetch-stuff topstories items))
     (for [i (take 10 @items)]
       [:div {:style {:margin "10px"} :key i} [each-post i]]))])

; app component
(defn app []
  [:div
   [posts-list]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [app] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
