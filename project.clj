(defproject org.clojars.serabe/rinzelight "0.0.2"
  :description "Image proccessing for Clojure"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]]
  :dev-dependencies [[lein-clojars "0.6.0"]
                     [swank-clojure "1.2.1"]
                     [midje "0.6.1"]
                     [lein-run "1.0.0"]
	             [criterium "0.0.1-SNAPSHOT"]
		     [autodoc "0.7.1"]]
  :autodoc {
            :name "Rinzelight"
            :description "A beautiful image processing library for Clojure."
            :web-src-dir "http://github.com/Serabe/rinzelight/blob"
            :web-home "http://serabe.github.com/rinzelight"
            :trim-prefix "rinzelight"
            :page-title "Rinzelight image library"
            :copyright "Copyright 2010 Sergio Arbeo. All rights reserved."})
 
