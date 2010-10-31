(ns rinzelight.effects.blur
  (:use [rinzelight.effects.basic-effects
         :only [optimal-kernel-width]]
        [rinzelight.constants
         :only [epsilon
                kernel-rank
                sq2pi]]
        [rinzelight.effects.convolve
         :only [convolve]]
        [rinzelight.effects.convolve.kernel
         :only [create-kernel
                width
                kernel
                normalize-kernel]]))

(defmacro alpha
  [i div]
  `(Math/exp (* -1.0 (/ (* ~i ~i) ~div))))

(defn blur-kernel
  "Given a radius and a sigma, returns a kernel for blurring."
  [width sigma]
  (let [data (float-array width (float 0.0))
        s    (if (< sigma (epsilon))
               1.0
               sigma)
        div  (* 2.0 (kernel-rank) (kernel-rank) s s)
        div2 (* (sq2pi) s)
        bias (* (kernel-rank) (int (/ width 2)))]
    (doseq [i (range (* -1.0 bias) (inc bias))]
      (let [idx (int (/ (+ i bias) (kernel-rank)))
            pv  (aget data idx)
            nv  (float (+ pv (/ (alpha i div) div2)))]
        (aset data idx nv)))
    (normalize-kernel (create-kernel width 1 data))))

(defn- transpose-horizontal-kernel
  [kern]
  (create-kernel 1 (width kern) (kernel kern)))

(defn blur
  "Blurs an image. rhs will be passed to convolve, so feel free to add a convolve op if you want."
  [img radius sigma & rhs]
  (let [width (optimal-kernel-width radius sigma)
        kern  (blur-kernel width sigma)
        nobc  (apply convolve img kern rhs)]
    (apply convolve img (transpose-horizontal-kernel kern) rhs)))
