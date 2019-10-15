(ns todo-list.core
  (:require [ring.adapter.jetty :as jetty]
            [clojure.walk :as walk]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.handler.dump :refer [handle-dump]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [todo-list.handlers :as handlers]))

(defroutes app
  (GET "/" [] handlers/welcome)
  (GET "/goodbye" [] handlers/goodbye)
  (GET "/about" [] handlers/about)
  (GET "/request-info" [] handle-dump)
  (GET "/hello/:name" [] handlers/hello)
  (GET "/calc/:op/:a/:b" [] handlers/calculator)
  (GET "/hiccup" [] handlers/trying-hiccup)
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


