(ns rinzelight.effects.basic-effects
  (:use [rinzelight.buffered-image :only [get-pixel
                                          get-pixels-int-array
                                          set-pixel
                                          set-pixels-int-array
                                          create-empty-canvas]]
        [rinzelight.effects.lookup-tables :only
         [apply-lookup-table
          create-lookup-table-from-pixel-function]]
        [rinzelight.pixel :only [create-pixel
                                 pixel-to-int-array
                                 pixel-round-to-quantum]])
  (:import (java.awt Color)
           (java.awt.image BufferedImage)))

(defn get-image-for-effect
  "Retrieves a new image like img but prepared for writing its pixels"
  ([img]
     (assoc img :image (create-empty-canvas (:width img) (:height img))))
  ([img width height]
     (assoc img
       :image  (create-empty-canvas width height)
       :width  width
       :height height)))

; TODO add new size parameter
(defn map-pixel-location
  "Calls f for each pixel location and returns another location.
   f must acccept two parameters [x, y] and return another vector with the new coordinates."
  [f img]
  (let [w (:width img)
        h (:height img)
        row-length (* w 4)
        ni (get-image-for-effect img)]
    (doseq [y (range h)]
      (let [row (get-pixels-int-array img 0 y w 1)]
        (doseq [x (range w)]
          (let [ini (* x 4)
                arr (int-array 4)
                nl  (f [x y])]
            (do
              (System/arraycopy row ini arr 0 4)
              (set-pixels-int-array ni (nl 0) (nl 1) arr))))))))

; TODO lookup tables are not valid for this method.
(defn map-image
  "Calls f for each pixel."
  [f img]
  (apply-lookup-table img
                      (create-lookup-table-from-pixel-function f)))
