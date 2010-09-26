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
    [(l 1)
     (- (:width img) (l 0) 1)]))

(defn rotate-180
  "Returns a function for map-pixel-location to rotate 180º."
  [img]
  (fn [l]
    [(- (:width img) (l 0) 1)
     (- (:height img) (l 1) 1)]  ))

(defn rotate-180-new-size
  "Returns the new size of the image after map-pixel-location with rotate-180 is applied."
  [img]
  [(:width img) (:height img)])

(defn vertical-flip
  "Returns a function for map-pixel-location to flip vertically"
  [img]
  (fn [l]
    [(l 0) 
     (- (:height img) (l 1) 1)]))

(defn horizontal-flip
  "Returns a function for map-pixel-location to flip horizontally."
  [img]
  (fn [l]
    [(- (:width img) (l 0) 1)
     (l 1)]))

(defalias vertical-flip-new-size rotate-180-new-size
  "Returns the new size of the image after map-pixel-location with vertical-flip is applied.")

(defalias horizontal-flip-new-size rotate-180-new-size
  "Returns the new size of the image after map-pixel-location with horizontal-flip is applied.")
