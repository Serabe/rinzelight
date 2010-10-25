(ns wikiscripts.invert
  (:use rinzelight.image
        rinzelight.effects.basic-effects
        rinzelight.pixel))

(def img (read-image "samples/northern-lights.jpg"))

(write-image
 (map-image invert-pixel img)
 "out/mi-invert.png")
