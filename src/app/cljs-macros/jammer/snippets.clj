;; Again, this definately just sounds like another library thing.

(ns jammer.snippets
  "Macros for including HTML snippets in the ClojureScript application
  at compile time."
  (:use [one.templates :only (render)])
  (:require [net.cgrand.enlive-html :as html]))

(defn- snippet [file id]
  (render (html/select (html/html-resource file) id)))


(defmacro snippets
  "Expands to a map of HTML snippets which are extracted from the
  design templates."
  []
  {:greeting (snippet "greeting.html" [:div#greeting])
   :login (snippet "loginform.html" [:div#form])
   :loading (snippet "loadingscreen.html" [:div#loading])
   :piano (snippet "piano.html" [:div#piano])})
