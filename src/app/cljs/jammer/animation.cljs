(ns ^{:doc "Defines animations which are used in the sample
  application."}
  jammer.animation
  (:use [one.core :only (start)]
        [one.browser.animation :only (bind parallel serial play play-animation)]
        [domina :only (by-id set-html! set-styles! destroy-children!
                             append! single-node)]
        [domina.xpath :only (xpath)])
  (:require [goog.dom.forms :as gforms]
            [goog.style :as style]))

(defn initialize-views
  "This accepts the welcome and login snippets and then animationly adds
   them to the page however I would like."
  [welcome-html login-form-html]
  ;; This is the content div of the application html page.
  (let [content (xpath "//div[@id='content']")]
    (destroy-children! content)
    (set-html! content welcome-html)
    (append! content login-form-html)))

(defn loading-screen
  "Does some kind of cool loading animation (someday)"
  [loading-page]
  (let [content (xpath "//div[@id='content']")]
    (destroy-children! content)
    (set-html! content loading-page)))
