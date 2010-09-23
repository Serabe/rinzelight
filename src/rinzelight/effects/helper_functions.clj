(ns rinzelight.effects.helper-functions
  (:use clojure.contrib.def))

(defn rotate-right
  "Returns a function for map-pixel-location to rotate 90º to the right."
  [img]
  (fn [l]
    [(- (:height img) (l 1) 1)
     (l 0)]))

(defn rotate-right-new-size
  "Returns the new size of the image after map-pixel-location with rotate-right is applied."
  [img]
  [(:height img) (:width img)])

(defalias rotate-left-new-size rotate-right-new-size
  "Returns the new size of the image after map-pixel-location with rotate-left is applied.")

(defn rotate-left
  "Returns a function for map-pixel-location to rotate 90º to the left"
  [img]
  (fn [l]
    [(l 1) (- (:width img) (l 0) 1)]))
