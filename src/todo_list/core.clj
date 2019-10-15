(ns todo-list.core
  (:require [ring.adapter.jetty :as jetty]
            [clojure.walk :as walk]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.handler.dump :refer [handle-dump]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]))

(def operands {"+" + "/" / "*" * "-" -})

(defn calculator
  [request]
  (let [a (Integer. (get-in request [:route-params :a]))
        b (Integer. (get-in request [:route-params :b]))
        op-str (get-in request [:route-params :op])
        f (get operands op-str)]
    (if f
      {:status 200
       :body (str (f a b))
       :headers {}}
      {:status 404
       :body "Sorry, unknown operator"
       :headers {}})))

(defn welcome
  [request]
  {:status 200
   :body "<h1>Well hello, old man</h1><p>What the hell are you doing</p>"
   :headers {}})

(defn goodbye
  [request]
  {:status 200
   :body "<p>See you soon!</p>"
   :headers {}})

(defn about
  "Information about the website developer"
  [request]
  {:status 200
   :body "<h3>Some information about me</h3>"
   :headers {}})

(defn request-info
  "View the information about the request"
  [request]
  {:status 200
   :body (pr-str request)
   :headers {}})

(defn hello
  [request]
  (let [name (get-in request [:route-params :name])]
    {:status 200
     :body (str "Hello " name ".  I got your name from the web URL")
     :headers {}}))


(defroutes app
  (GET "/" [] welcome)
  (GET "/goodbye" [] goodbye)
  (GET "/about" [] about)
  (GET "/request-info" [] handle-dump)
  (GET "/hello/:name" [] hello)
  (GET "/calc/:op/:a/:b" [] calculator)
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


