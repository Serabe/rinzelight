(ns rinzelight.effects.edge
  (:use [rinzelight.effects.basic-effects
         :only [optimal-kernel-width]]
        [rinzelight.effects.convolve
         :only [convolve]]
        [rinzelight.effects.convolve.kernel
         :only [create-kernel]]
        [rinzelight.effects.convolve.ops
         :only [repeat-op]]))

(defn- get-kernel
  [radius]
  (let [w (optimal-kernel-width radius 0.5)
        size (* w w)
        data (float-array size -1.0)]
    (aset data (/ (inc size) 2) (float size))
    (create-kernel w w data)))

(defn edge
  "Edge effect for img with radius r"
  [img radius & opts]
  (apply convolve img (get-kernel radius) opts))
