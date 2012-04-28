(ns ^{:doc "Functions and State for managing pusher connections."}
  jammer.pusher)

;; Internal State of Pusher
(def pusher (js-obj))
(def channel (js-obj))

(defn initialize-pusher
  "logs in to pusher"
  [key]
  (set! pusher (new js/Pusher key)))

(defn subscribe
  "Subscribes to a channel"
  [ch]
  (set! channel (.subscribe pusher ch)))

(defn bind
  "Binds a function to an event."
  [event fctn]
  (.bind channel event fctn))

(defn trigger
  "triggers an event"
  [event data]
  (.trigger channel event data))