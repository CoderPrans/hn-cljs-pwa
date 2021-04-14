(ns hncljs.core
    (:require
      [reagent.core :as r]
      [reagent.dom :as d]
      [kitchen-async.promise :as p]
      [re-frame.core :as rf]
      [hncljs.handlers]
      [hncljs.subs]
      [clojure.string :as str]))

;; -------------------------
;; Views

(def items (r/atom nil))
(def show-page (r/atom nil))
(def page-n (r/atom 1))

;; app-state [{:id 245189
;;             :title "Post Title"} {}] 

(def base-url "https://hacker-news.firebaseio.com/v0/")
(def topstories (str base-url "topstories.json"))

(defn query-item [i]
  (str base-url "item/" i ".json"))

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
           [:span {:style
                   {:margin-right "25px"
                    :font-size "20px"}} (.-score @post-data)]
           [:div [:p [:b (.-title @post-data)]
                  " // "
                  (when (contains?
                         (set (keys (js->clj @post-data))) "url")
                    [:a {:href (.-url @post-data)}
                     (get-src (.-url @post-data))])]
            [:p [:span (.-by @post-data)]
             [:button {:on-click #(reset! show-page id)}
              (.-descendants @post-data) " comments"]]]])))))

(defn get-src [url]
  (nth (.split url "/") 2))

;; ((set (keys {:a "apple" :b "ball" :c "cherry"})) :c)

;; (get-src "https://www.google.com/now/present/always")

(drop 10 (take 20 (range 500)))

; posts list component
(defn posts-list []
  [:div
   [:div
    [:button {:on-click #(swap! page-n dec) :disabled (= @page-n 1)} "prev"]
    [:span "page " @page-n]
    [:button {:on-click #(swap! page-n inc)} "next"]]
   (do
     (when-not @items (fetch-stuff topstories items))
     (for [i (drop (* (dec @page-n) 10)
                   (take (* @page-n 10) @items))]
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
          [:div [:b (.-by @comm-data)]
           [:p {:dangerouslySetInnerHTML
                {:__html (.-text @comm-data)}}]])))))

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
           [:a {:href (.-url @post-data)}
            [:h3 (.-title @post-data)]]
           [:p (.-by @post-data)]
           [:hr ]
           (for [comm (.-kids @post-data)]
             [:div {:style {:margin "30px 0px"} :key comm}
              [each-comment comm]])])))))

;;; Re-frame

;; -- Domino 1 - Event Dispatch

(defn dispatch-timer-event
  []
  (let [now (js/Date.)]
    (rf/dispatch [:timer now])))

(defonce do-timer (js/setInterval dispatch-timer-event 1000))

;; -- Domino 2 - Event Handlers
;;; from hncljs.handlers

;; -- Domino 4 - Query
;;; from hnljs.subs

;; -- Domino 5 - View Function

(defn clock []
  [:div.example-clock
   {:style {:color @(rf/subscribe [:time-color])}}
   (first (str/split
           (.toTimeString
            @(rf/subscribe [:time])) " "))])

(defn color-input []
  [:div.color-input
   [:input {:type "text"
            :style {:width "40px" :margin-left "10px"}
            :value @(rf/subscribe [:time-color])
            :on-change #(rf/dispatch
                         [:time-color-change
                          (.-value (.-target %))])}]])

(defn ui []
  [:div {:style {:display "flex"}}
   [clock]
   [color-input]])

;; -------------------------
;; Initialize app

; app component
(defn app []
  [:div 
   [:h2.card-header.text-center "Hacker News PWA"]
   [ui]
   (if (nil? @show-page)
     [posts-list]
     [:div
      [:button.back-button
       {:on-click #(reset! show-page nil)} "back"]
      [post-page @show-page]])])

(defn mount-root []
  (d/render [app] (.getElementById js/document "app")))

(defn ^:dev/after-load clear-cache-and-render!
  []
  ;; The `:dev/after-load` metadata causes this function to be called
  ;; after shadow-cljs hot-reloads code. We force a UI update by clearing
  ;; the Reframe subscription cache.
  (rf/clear-subscription-cache!)
  (mount-root))

(defn ^:export init! []
  (rf/dispatch-sync [:initialize])
  (mount-root))


;; (def test-db (r/atom {:data {}}))

;; ((fn [id] (swap! test-db assoc-in [:data (keyword (str id))] 23456)) 23)

;; (assoc test-db :items [2324 2365 2334])

;; (:23 (:data @test-db))


