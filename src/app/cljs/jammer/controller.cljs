(ns ^{:doc "Respond to user actions by updating local and remote
  application state."}
  jammer.controller
  (:use [one.browser.remote :only (request)]
        [jammer.model :only (state)])
  (:require [cljs.reader :as reader]
            [clojure.browser.event :as event]
            [one.dispatch :as dispatch]
            [goog.uri.utils :as uri]))


(defmulti action :type)

(defmethod action :init [_]
  (reset! state {:state :init}))

;; So what is needed to set up the jamming, first we need to send a
;; message telling pusher to connect, then once pusher is connected
;; they will send us a message telling us it's time to jam, when that
;; happens we will need to tell the audio system to load everything,
;; once it's ready to go it'll tell us and we'll set the state of the
;; program to jamming with will send a change-state method which will
;; render the view. Should probably also have a state called loading
;; so that the user sees something and doesn't just think the button
;; is broken.
(defmethod action :jam [{:keys [name room]}]
  (dispatch/fire :pusher-login {:name name :room (str "private-" room)})
  (reset! state {:state :loading-jamview}))

;; When one of these dispatches are fired we add the type to the data
;; map and call action on it.
(dispatch/react-to #{:init :jam}
                   (fn [t d] (action (assoc d :type t))))