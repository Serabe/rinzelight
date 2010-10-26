(ns rinzelight.crop
  (:use [rinzelight.image
         :only [create-image]]
        [rinzelight.composition
         :only [src-over
                compose]])
  (:import (java.awt AlphaComposite)))

(defn crop
  "Crops an image to a width of w, height of h, starting on pixel x,y.
If a gravity is passed, it is used to calculate the x and y."
  ([img w h]
     (crop img w h 0 0))
  ([img w h grav]
     (let [[x y] (grav (:width img) (:height img)
                       w h)]
       (crop img w h x y)))
  ([img w h x y]
     (let [ni (create-image w h) ; just in case the crop exceed the
                                 ; image boundaries
           si (create-image (.getSubimage (:image img) x y
                                          (min w (- (:width  img) x))
                                          (min h (- (:height img) y))))]
       (compose ni si (src-over)))))
