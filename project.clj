(defproject todo-list "0.1.0-SNAPSHOT"
  :description "A Todo List server-side webapp using Ring & Compojure"
  :url "https://github.com/practicalli/clojure-todo-list-example"
  :license {:name "Creative Commons Attribution Share-Alike 4.0 International"
            :url  "https://creativecommons.org"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring "1.7.1"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]]
  :min-lein-version "2.0.0"
  :uberjar-name "todo-list.jar"
  :main todo-list.core
  :repl-options {:init-ns todo-list.core}
  :profiles {:dev {:main todo-list.core/-dev-main}})
