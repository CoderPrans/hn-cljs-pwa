(ns hncljs.state
  (:require
   [reitit.frontend :as rtf]
   [reitit.frontend.controllers :as rtfc]
   [reitit.frontend.easy :as rtfe]))

(def href rtfe/href)

(def base-url "https://hacker-news.firebaseio.com/v0/")

(def topstories (str base-url "topstories.json"))

(defn query-item [i]
  (str base-url "item/" i ".json"))

(defn get-src [url]
  (nth (.split url "/") 2))


