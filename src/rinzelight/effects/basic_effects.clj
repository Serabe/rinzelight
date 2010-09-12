(ns rinzelight.effects.basic-effects
  (:use [rinzelight.buffered-image :only [get-pixels-int-array
                                          set-pixels-int-array
                                          create-empty-canvas]]
        [rinzelight.pixel :only [create-pixel
                                 pixel-to-int-array
                                 pixel-round-to-quantum]])
  (:import (java.awt Color)
           (java.awt.image BufferedImage)))

(defn get-image-for-effect
  "Retrieves a new image like img but prepared for writing its pixels"
  [img]
  (assoc img :image (create-empty-canvas (:width img) (:height img))))

; TODO  finish
(defn map-image
  "Calls f for each pixel."
  [f img]
  (let [w   (:width img)
        h   (:height img)
        row-length (* w 4)
        ni (get-image-for-effect img)]
    (doseq [y (range h)]
      (let [row (get-pixels-int-array img 0 y w 1)
            nrw (int-array row-length)]
        (doseq [x (range w)]
          (let [ini (* x 4)
                pixel (create-pixel (aget row ini)
                                    (aget row (+ ini 1))
                                    (aget row (+ ini 2))
                                    (- 255 (aget row (+ ini 3))))
                npa (pixel-to-int-array (f pixel))]
            (doto nrw
              (aset ini (pixel-round-to-quantum (aget npa 0)))
              (aset (+ ini 1) (pixel-round-to-quantum (aget npa 1)))
              (aset (+ ini 2) (pixel-round-to-quantum (aget npa 2)))
              (aset (+ ini 3) (pixel-round-to-quantum (aget npa 3))))))
        (set-pixels-int-array ni 0 y w 1 nrw)))
    ni))
