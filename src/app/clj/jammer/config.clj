(ns jammer.config
  "Contains configuration jammer"
  (:require [net.cgrand.enlive-html :as html]))

(defn- production-transform [h]
  (html/transform h
                  [:ul#navigation]
                  (html/substitute (html/html-snippet ""))))

(def ^{:doc "Configuration for Jammer"}
  config {:src-root "src"
          :app-root "src/app/cljs"
          :top-level-package "jammer"
          :js "public/javascripts"
          :dev-js-file-name "main.js"
          :prod-js-file-name "mainp.js"
          ;; These are going to be my cljs namespaces, not the sample apps.
          :dev-js ["goog.require('jammer.core');"
                   "goog.require('jammer.model');"
                   "goog.require('jammer.controller');"
                   "goog.require('jammer.history');"
                   "goog.require('jammer.logging');"
                   "goog.require('jammer.pusher');"
                   "goog.require('jammer.jamming');"
                   "goog.require('jammer.sound');"
                   "jammer.core.start();jammer.core.repl();"]
          ;; This needs to be the entrypoint of my app, not the sample.
          :prod-js ["jammer.core.start();"]
          ;; These will be the files that my server has not the sample app.
          :reload-clj ["/one/host_page"
                       "/one/reload"
                       "/one/templates"
                       "/jammer/api"
                       "/jammer/config"
                       "/jammer/dev_server"]
          :prod-transform production-transform})
