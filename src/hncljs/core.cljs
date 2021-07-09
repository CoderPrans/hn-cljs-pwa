(ns hncljs.core
    (:require
      [reagent.dom :as d]
      [reagent.core :as r]
      [re-frame.core :as rf]
      [hncljs.routes :as routes]))

(def dark? (r/atom true))

(defn theme-button []
  [:button.theme
   {:on-click
    #(swap! dark? (fn [] (if @dark? false true)))}
   (if @dark? "dark" "light")])

(defn get-width []
  (let [width (.-innerWidth js/window)]
    (if (> width 850) 850 width)))

(def dark-mode
  {:background-color "rgb(20, 32, 37)"
   :color "#fafafa"})

(defn app []
  [:div.wrapper
   {:style (if @dark? dark-mode {})}
   [theme-button]
   [:div.container {:style {:width (get-width)}}
    [:h2.card-header.text-center "Hacker News PWA"]
    (if-let [match @routes/match]
      (let [view (:view (:data match))]
        [view match]))]])

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
  (routes/init!)
  (mount-root))


