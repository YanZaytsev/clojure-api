(ns todo-list.handlers
  (:require [hiccup.core :refer :all]
            [hiccup.page :refer :all]))

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
  (html [:h1 "Hello, Clojure world"]
        [:p "Welcome to your first clojure app!"]))

(defn trying-hiccup
  [request]
  (html5 {:lang "en"}
         [:head (include-js "myscript.js") (include-css "mystyles.css")]
         [:body
          [:div [:h1 {:class "info"} "This is Hiccup"]]
          [:div [:p "Take a look at the TML generated in this page"]]
          [:div [:p "Style-wise there is no difference between the pages as we havent added anything in the stylesheet, however the hiccup page generates a more complete page in terms of HTML"]]]))

(defn goodbye
  [request]
  (html5 {:lang "en"}
         [:head (include-js "myscript.js") (include-css "mystyle.css")]
         [:body
          [:div
           [:h1.info "Walking back to happiness"]]
          [:div [:p "Walking back to happiness with you"]]
          [:div [:p "Laid aside foolish pride"]]
          [:div [:p "Learnt the truth from tears I cried"]]]))

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