(ns ^{:doc "Render the views for the jammer."}
  jammer.view
  (:use [domina :only (set-html! set-styles! styles by-id set-style!
                       by-class value set-value! set-text! nodes single-node)]
        [domina.xpath :only (xpath)]
        [one.browser.animation :only (play)])
  (:require-macros [jammer.snippets :as snippets])
  (:require [goog.events.KeyCodes :as key-codes]
            [goog.events.KeyHandler :as key-handler]
            [clojure.browser.event :as event]
            [one.dispatch :as dispatch]
            [jammer.animation :as fx]))

(def ^{:doc "A map of all the html templates"}
  snippets (snippets/snippets))

;; Form management, I can add in some validation like it's done in one later.

(defmulti render
  "Accepts a map that represents the current state of the application.
   Renders a view based on the value of the `:state` key."
  :state)


;; The inital page is the welcome page, it has a bunch of static intro
;; information and then a login form that takes a name and a room to
;; jam in.
;; We initialize the views, add event listeners to the login submit
;; so when they click it we fire a :jam event, meaning they want to jam.
(defmethod render :init [_]
  (fx/initialize-views (:greeting snippets) (:login snippets))
  (event/listen (by-id "jam-button")
                "click"
                #(dispatch/fire :jam
                                {:name (value (by-id "name-input"))
                                 :room (value (by-id "room-input"))})))

(defmethod render :loading-jamview [_]
  (fx/loading-screen (:loading snippets)))

(defmethod render :jamming [_]
  (fx/create-jamview (:piano snippets)))

(dispatch/react-to #{:state-change} (fn [_ m] (render m)))