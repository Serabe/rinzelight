(ns rinzelight.effects.convolve.kernel
  (:import (java.awt.image Kernel)))


(defstruct kernel :kernel :width :height)

(defn to-java-kernel
  "Retrieves the java representation of the kernel."
  [kern]
  (Kernel. (:width kern)
           (:height kern)
           (into-array Float/TYPE (:kernel kern))))

(defmacro check-kernel
  "Checks that both width and height are odd numbers."
  [kern]
  (let [checker (fn [w h arr-s]
                  (and (odd? w) ; width must be odd
                      (odd? h) ; height must be odd
                       (= (* w h)
                          (alength arr-s))))] ; array size must be equal to w*h
    `(if (isa? (class ~kern) java.awt.image.Kernel)
       (~checker (.getWidth  ~kern)
                 (.getHeight ~kern)
                 (.getKernelData ~kern nil))
       (~checker (:width  ~kern)
                 (:height ~kern)
                 (:kernel ~kern)))))

(defn normalize-kernel
  "Normalizes a kernel. Normalizing a kernel is to divide each component by the sum of all of them."
  [kern]
  (let [data  (:kernel kern)
        gamma (/ 1.0 (areduce data idx res 0 (+ res (aget data idx))))]
    (if (== gamma 1.0)
      kern
      (assoc kern :kernel
             (amap data idx ret
                   (aset ret idx (* gamma (aget data idx))))))))
