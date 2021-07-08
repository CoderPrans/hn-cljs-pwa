(ns hncljs.core
    (:require
      [reagent.dom :as d]
      [re-frame.core :as rf]
      [hncljs.routes :as routes]))

(defn app []
  [:div
   [:h2.card-header.text-center "Hacker News PWA"]
   (if-let [match @routes/match]
     (let [view (:view (:data match))]
       [view match]))])

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


