(ns examples.only-green-lookup-tables
  (:use criterium.core
        rinzelight.image
        rinzelight.effects.lookup-tables
        rinzelight.rendering-hints))

(def only-green
     (multisample-lookup-table zero straight zero))

(def img (read-image "samples/northern-lights.jpg"))

(with-progress-reporting
  (bench
   (def ni (apply-lookup-table img only-green color-rendering-quality antialiasing-on rendering-quality))
   :verbose))

(display-image ni)
