(ns hncljs.core
    (:require
      [reagent.dom :as d]
      [re-frame.core :as rf]
      [hncljs.views :as views]))

(defn mount-root []
  (d/render [views/app] (.getElementById js/document "app")))

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


