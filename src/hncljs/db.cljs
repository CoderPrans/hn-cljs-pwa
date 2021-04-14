(ns hncljs.db)

(def initial-db
  {:time (js/Date.)
   :time-color "#f88"
 ;; server state
   :items nil
   :posts-data nil
   :comments nil
 ;; local state
   :show-page nil
   :page-n 1
 ;; loading state
   :stories-loading? false
   :post-loading? false
   :comm-loading? false
 ;; error state
   :error nil})

;; posts-data: {:id1 {:url url :title title :by by :kids []}
;;              :id2 {:url url :title title :by by :kids []}}

