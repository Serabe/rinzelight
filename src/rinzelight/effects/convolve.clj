(ns rinzelight.effects.convolve
  "Produces an enhance convolve operator, having more operators "
  (:use [rinzelight.image
         :only [create-image]])
  (:import (java.awt Graphics2D))
  (:import (java.awt.image BufferedImage
                           ConvolveOp
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

(defn helper-image-size
  "Retrieves the size of the auxiliar image."
  [img kern]
  [(+ (:width img) (dec (:width kern)))
   (+ (:height img) (dec (:height kern)))])

(defn orig-img-starting-pixel
  [kern]
  [(/ (inc (:width  kern)) 2)
   (/ (inc (:height kern)) 2)])

(defmacro set-to-image
  [img]
  `(fn [data# x# y#]
     (.drawImage ~img data#
                 nil x# y#)))

(defmacro get-from-image
  [img]
  `(fn [x# y# w# h#]
     (.getSubimage ~img
                   x# y#
                   w# h#)))

(defmacro convolve-op
  [name [img kern] & body]
  (let [set-to-ni (symbol (str "set-to-new-image"))
        get-from-img (symbol (str "get-from-" img))]
    `(defn ~name
       [~img ~kern]
       (let [[nw# nh#] (helper-image-size ~img ~kern)
             [ix# iy#] (orig-img-starting-pixel ~kern)
             ni# (create-image nw# nh#)
             ibi# ^BufferedImage (:image ~img)
             nbi# ^BufferedImage (:image ni#)
             ng#  ^Graphics2D (.getGraphics nbi#)
             ~set-to-ni (set-to-image ng#)
             ~get-from-img (get-from-image ibi#)]
         (~set-to-ni (:image ~img) ix# iy#)
         ~@body
         ni#))))

(convolve-op edge-no-op
             [img kern]
             (let [[w h]     (helper-image-size img kern)
                   [ix iy]   (orig-img-starting-pixel kern)
                   clr java.awt.Color/BLACK
                   hor-black (:image (create-image w iy clr))
                   ver-black (:image (create-image ix (:height img) clr))]
               (set-to-new-image hor-black 0 0)
               (set-to-new-image hor-black 0 (inc (- h iy)))
               (set-to-new-image ver-black 0 iy)
               (set-to-new-image ver-black (inc (- w ix)) iy)))

