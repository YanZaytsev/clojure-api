(ns todo-list.core
  (:require [ring.adapter.jetty :as jetty]
            [clojure.walk :as walk]
            [clojure.java.jdbc :as j]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.handler.dump :refer [handle-dump]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [todo-list.handlers :as handlers]
            [clojure.string :as str]))

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


(j/execute! pg-db
            [(j/create-table-ddl :state_forest [[:state_id :int "REFERENCES state"]
                                                [:state_forest_id :serial "PRIMARY KEY"]
                                                [:state_forest "VARCHAR(256)"]
                                                [:acres :int]])])


(def al-state-forest [["Coccolocco" 4536]
                      ["Hauss" 319]
                      ["Geneva" 7120]
                      ["Little River" 2100]
                      ["Macun" 190]
                      ["Weogufka" 240]])

(defn load-state-forest! [sf-vec s]
  (let [state-id (id-for-state s)]
    (j/insert-multi! pg-db
                     :state_forest
                     (map #(hash-map :state_id state-id :state_forest (first %) :acres (second %)) sf-vec))))

(load-state-forest! al-state-forest "AL")


(j/delete! pg-db :state_forest ["state_forest = ?" "Macun"])

(j/query pg-db ["SELECT * from state_forest WHERE state_forest = ?" "Macun"])

(j/delete! pg-db :state ["abrv = ?" "AL"])

(j/execute! pg-db [(j/drop-table-ddl :state_forest)])

(j/execute! pg-db
            [(j/create-table-ddl :state_forest [[:state_id :int "REFERENCES state ON UPDATE CASCADE ON DELETE CASCADE"]
                                                [:state_forest_id :serial "PRIMARY KEY"]
                                                [:state_forest "VARCHAR(256)"]
                                                [:acres :int]])])

(j/insert! pg-db :state {:state "Alabama" :abrv "AL"})

(j/query pg-db
         ["SELECT constraint_name FROM information_schema.table_constraints WHERE table_name = ?" "state_forest"])

(j/execute! pg-db ["ALTER TABLE state_forest DROP CONSTRAINT state_forest_state_id_fkey"])

(j/execute! pg-db
            ["ALTER TABLE state_forest ADD CONSTRAINT state_forest_state_id_fkey FOREIGN KEY (state_id) REFERENCES state ON UPDATE CASCADE ON DELETE CASCADE"])


(j/execute! pg-db [(j/create-table-ddl :activity [[:activity_id :serial "PRIMARY KEY"]
                                                  [:activity "VARCHAR(64)"]])])

(j/insert-multi! pg-db :activity [{:activity "hunting"}
                                  {:activity "fishing"}
                                  {:activity "trail riding"}
                                  {:activity "hiking"}
                                  {:activity "primitive camping"}])

(j/execute! pg-db [(j/create-table-ddl :state_forest_activity [[:state_forest_id :int "REFERENCES state_forest ON UPDATE CASCADE ON DELETE CASCADE"]
                                                               [:activity_id :int "REFERENCES activity ON UPDATE CASCADE ON DELETE CASCADE"]
                                                               ["CONSTRAINT state_forest_activity_pkey PRIMARY KEY (state_forest_id, activity_id)"]])])


(defn id-for-state-forest [name]
  (j/query pg-db ["SELECT state_forest_id FROM state_foresr WHERE state_forest = ?" (str/capitalize name)])
  {:row-fn :state_forest_id :result-set-fn :first})




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