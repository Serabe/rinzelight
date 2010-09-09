(ns rinzelight.effects.basic-effects
  (:use [rinzelight.buffered-image :only [get-pixels
                                          create-empty-canvas]])
  (:import (java.awt Color)
           (java.awt.image BufferedImage)))

; TODO  finish
(defn map-image
  "Calls f for each pixel."
  [f img]
  (let [w (:width img)
        h (:height img)
        nbi ] (create-empty-canvas w h))
  (doseq [y (range h)]
    (let [row (get-pixels img 0 y (:width img) 1)])))
