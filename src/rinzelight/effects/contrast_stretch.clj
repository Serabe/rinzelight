(ns rinzelight.effects.contrast-stretch
  (:use [rinzelight.constants
         :only [quantum-range]]
        [rinzelight.effects.lookup-tables
         :only [apply-lookup-table
                lookup-table-from
                multisample-lookup-table]]
        [rinzelight.histogram]
        [rinzelight.pixel
         :only [round-to-quantum]]))

(defn- create-stretch-fn
  [min max]
  (if (== min max)
    (fn [i] (if (>= i max) (quantum-range) 0))
    (let [dif (- max min)]
      (fn [i]
        (cond
         (<= i min) 0
         (>= i max) (quantum-range)
         :else (round-to-quantum
                (* (quantum-range)
                   (/ (- i min)
                      dif))))))))

(defn- max-limit
  [hist white-point]
  (loop [[x & xs] (reverse hist)
         intensity 0
         idx (quantum-range)]
    (if (empty? xs)
      0
      (let [new-int (+ intensity x)]
        (if (> new-int white-point)
          idx
          (recur xs new-int (dec idx)))))))

(defn- min-limit
  [hist black-point]
  (loop [[x & xs] hist
         intensity 0
         idx 0]
    (if (empty? xs)
      (quantum-range)
      (let [new-int (+ intensity x)]
        (if (> new-int black-point)
          idx
          (recur xs new-int (inc idx)))))))

(defn valid-string?
  [s]
  (and (string? s)
       (not (empty? (re-seq #"^\d{1,2}(\.\d{1,2})?%$" s)))))

(defmacro apply-to-size
  "Given the size of an image and a valid string for either a black or a white point, calculates the number of pixels."
  [size s]
  `(int (* ~size 
           (Double/parseDouble (.substring ~s 0
                                           (dec (.length ~s)))))))

(defn- compute-one-point
  [size point]
  (cond
   (number? point) point
   (valid-string? point) (apply-to-size size point)
   :else (throw (IllegalArgumentException. "points must be an integer or a string of the form 'N.N%'"))))

(defn- compute-black-and-white-points
  [img black white]
  (let [size (* (:width img) (:height img))]
    [(compute-one-point size black)
     (- size (compute-one-point size white))]))

(defn contrast-stretch
  "Computes a contrast stretch with whitepoint wp and black point bp."
  [img black-point white-point & rhs]
  (let [[bp wp] (compute-black-and-white-points
                 img black-point white-point)
        hist (histogram img)
        f (create-stretch-fn (min-limit hist bp)
                             (max-limit hist wp))
        t (multisample-lookup-table
           (lookup-table-from f))]
    (apply apply-lookup-table img t rhs)))
