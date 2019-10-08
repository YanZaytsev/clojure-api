(ns todo-list.core
  (:require [ring.adapter.jetty :as jetty]
            [clojure.walk :as walk]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]))

(defn welcome
  [request]
  {:status 200
   :body "<h1>Well hello, old man</h1><p>What the hell are you doing</p>"
   :headers {}})

(defroutes app
  (GET "/" [] welcome)
  (not-found "<h1>This is not the page you're looking for"))

(defn -main
  "A simple web server using Ring & Jetty."
  [port-number]
  (jetty/run-jetty
   app
   {:port (Integer. port-number)}))

(defn -dev-main
  "A very simple web server using Ring & Jetty that reloads code changes via the development profile of Leiningen"
  [port-number]
  (jetty/run-jetty
   (wrap-reload #'app)
   {:port (Integer. port-number)}))


