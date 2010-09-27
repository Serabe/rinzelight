(ns examples.only-green
  (:use criterium.core
        rinzelight.image
        rinzelight.effects.basic-effects
        rinzelight.effects.helper-functions))

(def img (read-image "samples/northern-lights.jpg"))

(with-progress-reporting
  (bench
   (def ni (map-pixel-location (rotate-right img)
                               img
                               ((rotate-right-new-size img) 0)
                               ((rotate-right-new-size img) 1)))
   :verbose))

(display-image ni)
