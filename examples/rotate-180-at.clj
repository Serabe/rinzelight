(ns examples.only-green
  (:use criterium.core
        rinzelight.image
        rinzelight.effects.affine-transforms))

(def img (read-image "samples/northern-lights.jpg"))
(def mx (/ (:width img) 2))
(def my (/ (:height img) 2))

(with-progress-reporting
  (bench
   (def ni (rotate img (Math/PI) mx my))
   :verbose))

(display-image ni)
