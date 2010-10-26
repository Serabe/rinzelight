(ns rinzelight.gravity-test
  (:use midje.sweet)
  (:use clojure.test)
  (:use rinzelight.gravity)
  (:import (javax.imageio ImageReader)))

(def gravities '(north
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

(deftest gravities-limit-test
  (for [grav gravities pic1 pictures pic2 pictures]
    (let [[x y] (grav pic1 pic2)
          [x2 y2] (grav (:width pic1)
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


(deftest compose-gravities-facts
  (for [g1 gravities g2 gravities
        pic1 pictures pic2 pictures]
    (let [[x _] (g1 pic1 pic2)
          [_ y] (g2 pic1 pic2)
          _ (compose-gravities grav g1 g2)]
      
      (fact "Composed gravity first coordinate must be the first coordinate of the first composing gravities"
            ((grav pic1 pic2) 0) => x)
      
      (fact "Composed gravity second coordinate must be the second coordinate of the second composing gravities"
            ((grav pic1 pic2) 1) => y))))
