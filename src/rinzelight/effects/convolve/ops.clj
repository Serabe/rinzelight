(ns rinzelight.effects.convolve.ops
  (:use [rinzelight.image]
        [rinzelight.effects.convolve.kernel])
  (:import (java.awt Graphics2D)
           (java.awt.image BufferedImage)))


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
  "Creates a new convolve-op. A convolve-op is just a function that prepares the image to be convolve by the kernel. For that, it creates an expanded image.
name is the name of the convolve-op.
img is the symbol for the image received by the op.
kern is the symbol for the kernel received by the op.
Inside body, two functions are available:
* set-to-new-image receives an image and draws it in the new image at position x,y.
* get-from-image get portions of image from the original one."
  [name [img kern] & body]
  (let [set-to-ni (symbol (str "set-to-new-image"))
        get-from-img (symbol (str "get-from-image"))]
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

(convolve-op
 zero-fill-op
 [img kern]
 (let [[w h]     (helper-image-size img kern)
       [ix iy]   (orig-img-starting-pixel kern)
       clr java.awt.Color/BLACK
       hor-black (:image (create-image w iy clr))
       ver-black (:image (create-image ix (:height img) clr))]
   (get-from-image 0 0 (:width img) 1)
   (set-to-new-image hor-black 0 0)
   (set-to-new-image hor-black 0 (inc (- h iy)))
   (set-to-new-image ver-black 0 iy)
   (set-to-new-image ver-black (inc (- w ix)) iy)))

(convolve-op
 repeat-op
 [img kern]
 (let [[w h]     (helper-image-size img kern)
       [ix iy]   (orig-img-starting-pixel kern)
       iw        (:width  img)
       ih        (:height img)
       up        (get-from-image 0 0 iw 1)
       bot       (get-from-image 0 (dec ih) iw 1)
       left      (get-from-image 0 0 1 ih)
       right     (get-from-image (dec iw) 0 1 ih)
       up-left   (get-from-image 0 0 1 1)
       up-right  (get-from-image (dec iw) 0 1 1)
       bot-left  (get-from-image 0 (dec ih) 1 1)
       bot-right (get-from-image (dec iw) (dec ih)
                                 1 1)
       right-x  (+ ix iw)
       bot-y    (+ iy ih)]
    ; Create upper border.
   (doseq [y (range iy)]
     (doseq [x (range ix)]
       (set-to-new-image up-left x y)
       (set-to-new-image up-right (+ right-x x) y))
     (set-to-new-image up ix y))

    ; Create lateral borders.
   (doseq [x (range ix)]
     (set-to-new-image left x iy)
     (set-to-new-image right (+ right-x x) iy))

    ; Create bottom border.
   (doseq [y (range bot-y w)]
     (doseq [x (range ix)]
       (set-to-new-image bot-left x y)
       (set-to-new-image bot-right (+ right-x x) y))
     (set-to-new-image bot ix y))))

(convolve-op
 thorus-op
 [img kern]
 (let [[w h]     (helper-image-size img kern)
       [ix iy]   (orig-img-starting-pixel kern)
       iw        (:width  img)
       ih        (:height img)
       up        (get-from-image 0 0 iw iy)
       bot       (get-from-image 0 (- ih iy) iw iy)
       left      (get-from-image 0 0 ix ih)
       right     (get-from-image (- iw ix) 0 ix ih)
       up-left   (get-from-image 0 0 ix iy)
       up-right  (get-from-image (- iw ix) 0 ix iy)
       bot-left  (get-from-image 0 (- ih iy) ix iy)
       bot-right (get-from-image (- iw ix) (- ih iy)
                                 ix iy)
       right-x  (+ ix iw)
       bot-y    (+ iy ih)]

   (set-to-new-image bot-right 0            0)
   (set-to-new-image bot       ix           0)
   (set-to-new-image bot-left  right-x      0)
   (set-to-new-image right     0           iy)
   (set-to-new-image left      right-x     iy)
   (set-to-new-image up-right  0        bot-y)
   (set-to-new-image up        ix       bot-y)
   (set-to-new-image up-left   right-x  bot-y)))

(defmacro convolve-op-by-location
  "Using this macro is circa five times slower than one written with convolve-op.

f-creator must be a function that, given the image and the kernel, returns a function that maps pixels from the extended image to the original image."
  [name f-creator]
  `(convolve-op
    ~name
    [~'img ~'kern]
    (let [f#          (~f-creator ~'img ~'kern)
          [w# h#]     (helper-image-size ~'img ~'kern)
          [ix# iy#]   (orig-img-starting-pixel ~'kern)
          iw#         (:width  ~'img)
          ih#         (:height ~'img)]

                                        ; Create upper border.
      (doseq [y# (range iy#)]
        (doseq [x# (range w#)]
          (let [[nx# ny#] (f# x# y#)]
            (~'set-to-new-image (~'get-from-image nx# ny# 1 1) x# y#))))

                                        ; Create lateral borders.
      (doseq [y# (range iy# (+ iw# iy#))]
                                        ; Create left border.
        (doseq [x# (range ix#)]
          (let [[nx# ny#] (f# x# y#)]
            (~'set-to-new-image (~'get-from-image nx# ny# 1 1) x# y#)))

                                        ;Create right border.
        (doseq [x# (range (+ ix# iw#) w#)]
          (let [[nx# ny#] (f# x# y#)]
            (~'set-to-new-image (~'get-from-image nx# ny# 1 1) x# y#))))

                                        ; Create bottom border.
      (doseq [y# (range (+ iy# ih#) h#)]
        (doseq [x# (range w#)]
          (let [[nx# ny#] (f# x# y#)]
            (~'set-to-new-image (~'get-from-image nx# ny# 1 1) x# y#)))))))
