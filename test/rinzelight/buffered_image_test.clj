(ns rinzelight.buffered-image-test
  (:use midje.sweet)
  (:use clojure.test)
  (:use rinzelight.buffered-image)
  (:use rinzelight.image)
  (:import (java.awt Color)
           (java.awt.image BufferedImage)))

(defn get-color-from-empty-image
  "Returns the color from an empty image."
  [bi]
  (let [a (int-array 4)
        a (get-pixels-int-array {:image bi} 1 1)]
    (Color. (aget a 0)
            (aget a 1)
            (aget a 2))))

(deftest create-empty-canvas-facts
  (let [w 100
        h 150
        color Color/BLUE
        c1 (create-empty-canvas w h)
        c2 (create-empty-canvas w h color)]
    (fact "Returns a BufferedImage."
          (class c1) => BufferedImage
          (class c2) => BufferedImage)
    (fact "Uses the proper width."
          (.getWidth c1) => w
          (.getWidth c2) => w)
    (fact "Uses the proper height."
          (.getHeight c1) => h
          (.getHeight c2) => h)
    (fact "Default background color is white."
          (get-color-from-empty-image c1) => Color/WHITE)
    (fact "Uses the proper color if provided."
          (get-color-from-empty-image c2) => color)))

(deftest create-new-canvas-for-image-facts
  (let [img (read-image "samples/clojure.png")
        ncv (create-new-canvas-for-image img)]
    (fact "Both images have the same width"
          (.getWidth ncv) => (:width img))
    (fact "Both images have the same height"
          (.getHeight ncv) => (:height img))))
