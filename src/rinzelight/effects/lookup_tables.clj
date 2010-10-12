(ns rinzelight.effects.lookup-tables
  (:use rinzelight.constants
        [rinzelight.pixel :only [create-pixel]]
        [rinzelight.rendering-hints :only [create-rendering-hint]])
  (:import (java.awt.image LookupOp ShortLookupTable)))

(defn lookup-table-from
  "Creates a new lookuptable given a function."
  [f]
  (let [a (short-array (inc (quantum-range)))]
    (doseq [x (range (inc (quantum-range)))]
      (aset a x (short (f x))))
    a))

(def zero
     (lookup-table-from (fn [x] 0)))

(def straight
     (lookup-table-from (fn [x] x)))

(def invert
     (lookup-table-from (fn [x] (- (quantum-range) x))))

(def brighten
     (lookup-table-from
      (fn [x] (/ (+ (quantum-range) x) 2))))

(def better-brighten
     (lookup-table-from
      (fn [x] (* (quantum-range)
                (Math/sqrt (* (quantum-scale) x))))))

(def posterize
     (lookup-table-from
      (fn [x] (- x (mod x 32)))))

(defn multisample-lookup-table
  "Creates a multidimensional lookup-table given 1, 3 or 4 lookup tables."
  ([x] (multisample-lookup-table x x x straight))
  ([r g b] (multisample-lookup-table r g b straight))
  ([r g b a] (into-array [r g b a])))

(defn create-lookup-table-from-pixel-function
  "Given a function passed as parameter to map-image, returns a lookup table."
  [f]
  (let [r   (short-array (inc (quantum-range)))
        g   (short-array (inc (quantum-range)))
        b   (short-array (inc (quantum-range)))
        a   (short-array (inc (quantum-range)))]
    (doseq [x (range (inc (quantum-range)))]
      (let [p (f (create-pixel x x x x))]
        (aset r x (short (:red   p)))
        (aset g x (short (:green p)))
        (aset b x (short (:blue  p)))
        (aset a x (short (:alpha p)))))
    (into-array [r g b a])))

(defn apply-lookup-table
  "Applies lookup table t to img."
  [img t & rendering-hints]
  (assoc img :image
         (.filter (LookupOp. (ShortLookupTable. 0 t)
                             (apply create-rendering-hint rendering-hints))
                  (:image img)
                  nil)))
