(ns wikiscripts.rotate-left
  (:use rinzelight.image
        rinzelight.effects.basic-effects
        rinzelight.effects.helper-functions))

(def img (read-image "samples/northern-lights.jpg"))

(write-image (map-pixel-location (rotate-left img)
                                 rotate-left-new-size
                                 img)
             "out/mpl-rotate-left.png")

