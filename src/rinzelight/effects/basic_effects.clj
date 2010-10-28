(ns rinzelight.effects.basic-effects
  (:use [rinzelight.buffered-image
         :only [get-red-samples-int-array
                get-green-samples-int-array
                get-blue-samples-int-array
                get-alpha-samples-int-array
                set-red-samples-int-array
                set-green-samples-int-array
                set-blue-samples-int-array
                set-alpha-samples-int-array
                get-pixels-int-array
                set-pixels-int-array
                create-empty-canvas]]

        [rinzelight.constants
         :only [epsilon
                quantum-range
                sq2pi]]
        
        [rinzelight.effects.lookup-tables
         :only [apply-lookup-table
                create-lookup-table-from-pixel-function]]
        
        [rinzelight.pixel
         :only [create-pixel
                pixel-to-int-array
                round-to-quantum]])
  (:import (java.awt Color)
           (java.awt.image BufferedImage)))

(defn- afok
  "Auxiliar functon for optimal-kernel-width"
  [sigma]
  (let [s2 (* -2 sigma sigma)
        ssq (* (sq2pi) sigma)]
    (fn [u]
      (/ (Math/exp (/ (* u u)
                      s2))
         ssq))))

(defn optimal-kernel-width
  "Given the radius and sigma, returns an optimal kernel width"
  [radius, sigma]
  (int (cond
        (> radius 0.0) (+ 1 (* 2 (Math/ceil radius)))
        (<= (Math/abs sigma) (epsilon)) 1
        :else (let [f (afok sigma)]
                (loop [width 5]
                  (let [half (int (/ width 2))
                        r (range (* -1 half) (inc half))
                        normal (reduce + 0 (map f r))]
                    (if (<= (* (f half) (quantum-range)) 0)
                      (- width 2)
                      (recur (+ 2 width)))))))))

(defn get-image-for-effect
  "Retrieves a new image like img but prepared for writing its pixels"
  ([img]
     (assoc img :image (create-empty-canvas (:width img) (:height img))))
  ([img width height]
     (assoc img
       :image  (create-empty-canvas width height)
       :width  width
       :height height)))

(defn map-pixel-location
  "Calls f for each pixel location and returns another location.
f must acccept two parameters [x, y] and return another vector with the new coordinates.
new_size_f is a function that is applied to img to get the new size.
nw and nh are new width and new height respectively."
  ([f img] (map-pixel-location f img (:width img) (:height img)))
  ([f new_size_f img]
     (let [[nw nh] (new_size_f img)]
       (map-pixel-location f img nw nh)))
  ([f img nw nh]
     (let [w (:width img)
           h (:height img)
           row-length (* w 4)
           ni (get-image-for-effect img nw nh)]
       (doseq [y (range h)]
         (let [row (get-pixels-int-array img 0 y w 1)]
           (doseq [x (range w)]
             (let [ini (* x 4)
                   arr (int-array 4)
                   nl  (f [x y])]
               (do
                 (System/arraycopy row ini arr 0 4)
                 (set-pixels-int-array ni (nl 0) (nl 1) arr))))))
       ni)))

(defn map-image
  "Calls f for each pixel."
  [f img]
  (let [w (:width  img)
        h (:height img)
        ni (get-image-for-effect img)
        r (int-array w)
        g (int-array w)
        b (int-array w)
        a (int-array w)]
    (doseq [y (range h)]
      (let [r (get-red-samples-int-array   img 0 y w 1 r)
            g (get-green-samples-int-array img 0 y w 1 g)
            b (get-blue-samples-int-array  img 0 y w 1 b)
            a (get-alpha-samples-int-array img 0 y w 1 a)]

        (doseq [x (range w)]
          (let [np (f (create-pixel (aget r x)
                                    (aget g x)
                                    (aget b x)
                                    (aget a x)))]
            (aset r x (:red   np))
            (aset g x (:green np))
            (aset b x (:blue  np))
            (aset a x (:alpha np))))
        (set-red-samples-int-array   ni 0 y w 1 r)
        (set-green-samples-int-array ni 0 y w 1 g)
        (set-blue-samples-int-array  ni 0 y w 1 b)
        (set-alpha-samples-int-array ni 0 y w 1 a)))
    ni))
