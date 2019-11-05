(ns todo-list.core
  (:require [ring.adapter.jetty :as jetty]
            [clojure.walk :as walk]
            [clojure.java.jdbc :as j]
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

(def pg-db
  {:dbtype   "postgresql"
   :dbname   "users"
   :host     "localhost"
   :user     "test"
   :password "test"})

(def state-sql
  (j/create-table-ddl :state
                      [[:state_id :serial "PRIMARY KEY"]
                       [:state "VARCHAR(32)"]
                       [:abrv "VARCHAR(2)"]]))


(j/insert! pg-db :state {:state "Alabama" :abrv "AL"})

(j/query pg-db ["SELECT * FROM state WHERE state = ?" "Alabama"])

(j/insert-multi! pg-db :state [{:state "Alaska" :abrv "AK"}
                               {:state "Arizona" :abrv "AZ"}
                               {:state "Arkansas" :abrv "AR"}])

(j/db-do-prepared pg-db ["INSERT INTO state (state, abrv) VALUES (?, ?)"
                         ["California" "CA"]
                         ["Colorado" "CO"]
                         ["Connecticut" "XX"]] {:multi? true})

(j/update! pg-db :state {:abrv "CT"} ["abrv = ?" "XX"])

(j/query pg-db ["SELECT * FROM state"] {:result-set-fn count})

(j/query pg-db ["SELECT * FROM state"] {:row-fn :abrv})

(defn id-for-state [s]
  (if (= 2 (count s))
    (j/query pg-db ["SELECT state_id FROM state WHERE abrv = ?" s] {:row-fn :state_id :result-set-fn first})
    (j/query pg-db ["SELECT state_id FROM state WHERE state = ?" s] {:row-fn :state_id :result-set-fn first})))



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


(into {} '({:a 1} {:b 2} {:c 3}))