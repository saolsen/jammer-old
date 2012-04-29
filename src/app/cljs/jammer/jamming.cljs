(ns ^{:doc "Manages the jamming session"}
  jammer.jamming
  (:require [jammer.pusher :as pusher]
            [jammer.sound :as sound]
            [jammer.model :as model]))


;; Used for calculatng offsets and managing latencies.
(def offset 0)
(def latencies {})
(def real-latency 0)

;; Functions used externally.
(defn get-time []
  (+ (.getTime (new js/Date)) offset))

(defn playat-time
  "Calculates the offset you should send to sound
   for playing a note that came from a specific user"
  [user-name]
  (- real-latency ((keyword user-name) latencies)))

(defn playat-me
  []
  real-latency)

(defn calc-real-latency []
  (set! real-latency (reduce #(max %1 %2) 0 (vals latencies))))


;; Couple functions that are used to calculate these latencies.
(defn send-ping [user-name]
  (pusher/trigger "client-ping" (lib/clj->js
                                 {:username user-name
                                  :time (get-time)})))
(defn on-ping [data]
  (let [user (.-username data)
        time (.-time data)]
    (do
      (set! latencies (assoc latencies (keyword user) (- (get-time) time)))
      (calc-real-latency))))

;; Don't call init until after the channel has been joined
(defn init-timer [user-name]
  (do
    (pusher/bind "realtime"
                 (fn [data]
                   (let [servertime (.-time data)]
                     (set! offset (- (.getTime (new js/Date)) servertime)))))
    (pusher/bind "calc-latencies"
                 (fn [data]
                   (send-ping user-name)))
    (pusher/bind "client-ping" on-ping)))