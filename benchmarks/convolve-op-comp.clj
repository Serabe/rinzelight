(ns examples.only-green
  (:use criterium.core
        rinzelight.image
        rinzelight.effects.convolve
        rinzelight.effects.convolve-op))

(def img (read-image "samples/northern-lights.jpg"))
(def kern {:kernel (float-array (* 19 19))
           :width 19
           :height 19})

(println "*** repeat-op ***")
(with-progress-reporting
  (bench
   (def ni (repeat-op img kern))
   :verbose))
(display-image ni)

(defn repeat-fn [img kern]
  (let [[w h]   (helper-image-size img kern)
        [ix iy] (orig-img-starting-pixel kern)
        iw      (:width img)
        max-w   (dec iw)
        ih      (:height img)
        max-h   (dec ih)
        right-x (+ ix iw)
        bot-y   (+ iy ih)
        f-x     (fn [x] (cond
                        (< x ix) 0
                        (>= x right-x) max-w
                        :else (- x ix)))
        f-y     (fn [y] (cond
                        (< y iy) 0
                        (>= y bot-y) max-h
                        :else (- y iy)))]
    (fn [x y]
      [(f-x x) (f-y y)])))

(convolve-op-by-location custom-op repeat-fn) ()

(println "*** custom-op ***")
(with-progress-reporting
  (bench
   (def ni (custom-op img kern))))

(display-image ni)
