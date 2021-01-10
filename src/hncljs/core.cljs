(ns hncljs.core
    (:require
      [reagent.core :as r]
      [reagent.dom :as d]
      [kitchen-async.promise :as p]))

;; -------------------------
;; Views

(def data (r/atom nil))

;; app-state [{:id 245189
;;             :title "Post Title"} {}] 

(def base-url "https://hacker-news.firebaseio.com/v0/")
(def topstories (str base-url "topstories.json"))

(defn fetch-link! [url]
  (-> (js/fetch url)
      (p/then #(.json %))
      (p/then (fn [data] data))))

(comment
  (.then (fetch-link! (str base-url "item/25710055.json"))
         #(js/console.log (.-title %)))
  )

(defn fetch-stuff [url state]
  (p/let [res (fetch-link! url)]
    (reset! state res)))

;(nil? nil)

(defn each-post [id]
  (let [post-data (r/atom "data")
        query (str base-url "item/" id ".json")] 
    (fn []
      (do
        (when (= @post-data "data") (fetch-stuff query post-data))
        [:p (.-title @post-data)]))))

(defn posts-list []
  [:div.cards
   [:h2.card-header.text-center "Hacker News Data"]
   #_[:button {:on-click #(fetch-stuff topstories data)} "fetch-hn_data"]
   (do
     (when-not @data (fetch-stuff topstories data))
     (for [i (take 10 @data)]
       [:div {:style {:margin "10px"} :key i} [each-post i]]))])

(defn home-page []
  [:div
   [posts-list]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
