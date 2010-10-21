(ns rinzelight.geometry-test
  (:use midje.sweet)
  (:use clojure.test)
  (:use rinzelight.geometry)
  (:import (javax.imageio ImageReader)))

(def geometries '(north
                  north-east
                  east
                  south-east
                  south
                  south-west
                  west
                  north-west))

(def small  {:width 30 :height 30})
(def medium {:width 50 :height 50})
(def big    {:width 80 :height 80})

(def pictures '(small medium big))

(deftest geometries-zero-limit-test
  (for [geo geometries pic1 pictures pic2 pictures]
    (let [[x y] (geo pic1 pic2)
          [x2 y2] (geo (:width pic1)
                       (:height pic1)
                       (:width pic2)
                       (:height pic2))]
      (fact "Coordinate should be greater than zero"
            x => (partial < 0)
            y => (partial < 0))
      (fact "Coordinate should be smaller than size of dst"
            x => (partial > (:width pic1))
            y => (partial > (:height pic1)))
      (fact "x coordinate must be the same either way it is calculated"
            x => x2)
      (fact "y coordinate must be the same either way it is calculated"
            y => y2))))
