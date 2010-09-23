(ns examples.only-green
  (:use criterium.core
        rinzelight.image
        rinzelight.effects.basic-effects))

(defn only-green
  [p]
  (assoc p
         :red  0
         :blue 0))

(def img (read-image "samples/clojure.png"))

(with-progress-reporting
  (bench
   (map-image only-green img)
   :verbose))
