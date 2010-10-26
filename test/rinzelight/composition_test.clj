(ns rinzelight.gravity-test
  (:use midje.sweet)
  (:use clojure.test)
  (:use rinzelight.composition)
  (:import (javax.imageio ImageReader)))

(def rules '(clear
             dst
             dst-atop
             dst-in
             dst-out
             dst-over
             src
             src-atop
             src-in
             src-out
             src-over
             xor))

(def small  {:width 30 :height 30})
(def medium {:width 50 :height 50})
(def big    {:width 80 :height 80})

(def pictures '(small medium big))

(deftest porter-duff-rules-zero-arguments
  (doseq [rule rules]
    (fact "With zero arguments, it returns a composite."
          (rule) => composite?)

    (fact "With one argument, it returns a composite."
          (rule 0.5) => composite?)))
