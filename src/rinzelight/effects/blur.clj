(ns rinzelight.effects.blur
  (:use [rinzelight.constants
         :only [kernel-rank rinze-sigma sq2pi]]
        [rinzelight.effects.basic-effects
         :only [optimal-kernel-width]]
        [rinzelight.effects.convolve
         :only [convolve]]
        [rinzelight.effects.convolve.kernel
         :only [create-kernel kernel normalize-kernel width]]
        [rinzelight.effects.convolve.ops
         :only [repeat-op]]))

(defn get-kernel
  [radius sigma]
  (let [w (optimal-kernel-width radius sigma)
        size w
        data (float-array size 0.0)
        ms (rinze-sigma sigma)
        div (* 2 (kernel-rank) (kernel-rank) ms ms)
        div2 (* ms (sq2pi))
        bias (* (kernel-rank) (/ (dec w) 2))]
    (loop [idx (- 0 bias)]
      (if (> idx bias)
        (normalize-kernel (create-kernel w 1 data))
        (let [ni (int (/ (int (+ idx bias)) (kernel-rank)))
              alpha (Math/exp (/ (double (* -1 idx idx)) div))]
          (aset data ni (float (+ (aget data ni) (/ alpha div2))))
          (recur (unchecked-inc idx)))))))

(defn blur
  "Blur effect for img with radius and sigma"
  ([img] (blur img 0.0 1.0))
  ([img radius] (blur img radius 1.0))
  ([img radius  sigma & opts]
    (let [vkernel (get-kernel radius sigma)
          hkernel (create-kernel 1 (width vkernel) (kernel vkernel))]
      (apply convolve img hkernel opts))))
