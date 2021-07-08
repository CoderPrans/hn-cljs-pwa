(ns hncljs.db)

(def initial-db
  {;; server state
   :items nil
   :posts-data nil
   :comments nil
   ;; local state
   :page-n 1
   ;; loading state
   :stories-loading? false
   ;; error state
   :error nil})

;; posts-data: {:id1 {:url url :title title :by by :kids []}
;;              :id2 {:url url :title title :by by :kids []}}

