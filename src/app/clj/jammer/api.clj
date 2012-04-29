(ns jammer.api
  "The server side of the sample application. Provides a simple API for
  updating an in-memory database."
  (:require [jammer.crypto :as crypto]
            [clj-json.core :as json])
  (:use [compojure.core :only (defroutes POST)]))

(defonce ^:private next-id (atom 0))

(defonce ^:dynamic *database* (atom #{}))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defmulti remote
  "Multimethod to handle incoming API calls. Implementations are
  selected based on the :fn key in the data sent by the client.
  Implementation are called with whatever data struture the client
  sends (which will already have been read into a Clojure value) and
  can return any Clojure value. The value the implementation returns
  will be serialized to a string before being sent back to the client."
  :fn)

(defmethod remote :default [data]
  {:status :error :message "Unknown endpoint."})

(defmethod remote :add-name [data]
  (let [n (-> data :args :name)
        response {:exists (contains? @*database* n)}]
    (swap! *database* conj n)
    response))

(defroutes remote-routes
  (POST "/remote" {{data "data"} :params}
        (pr-str
         (remote
          (binding [*read-eval* false]
            (read-string data))))))

;;This data should also be more hidden in env vars or something.
(def app_key "c31ce5204231d5cdd28d")
(def secret_key "f824caa7dea2ec85e92a")

(defroutes pusher-ajax-auth
  (POST "/pusher/auth" {params :params}
        (let [channel_name (get params "channel_name")
              socket_id (get params "socket_id")]
          (println channel_name)
          (println socket_id)
          (json-response {:auth (str
                                 app_key
                                 ":"
                                 (crypto/get-signiture
                                  channel_name socket_id secret_key))}))))
