(ns rinzelight.effects.convolve
  "Produces an enhance convolve operator, having more operators "
  (:use [rinzelight.image
         :only [create-image]]
        [rinzelight.effects.convolve-op])
  (:import (java.awt Graphics2D))
  (:import (java.awt.image ConvolveOp
                           Kernel)))

;; In order to support more advanced edge ops, an intermediate
;; image must be used.

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
