(ns rinzelight.pixel
  (:use rinzelight.constants))

(defstruct pixel-packet :red :green :blue :alpha)

(defn create-pixel
  "Creates a new pixel. If only one value is supplied, it uses it for red, green and blue. If opacity is not given, opaque-opacity is used." 
  ([r] (create-pixel r r r (opaque-opacity)))
  ([r g b] (create-pixel r g b (opaque-opacity)))
  ([r g b a] (struct pixel-packet r g b a)))

(comment 
  (defn convert-to-pixel-seq
    "Converts an array of ints into a pixel seq."
    [pixels width height]
    (vec )))

(defn pixel-to-int-array
  "Converts a pixel to a int-array"
  [p]
  (let [a (int-array 4)]
    (doto a
      (aset 0 (:red   p))
      (aset 1 (:green p))
      (aset 2 (:blue  p))
      (aset 3 (- 255 (:alpha p))))))

(defn round-to-quantum
  "Assures that a value is between 0 and rl-quantum-value"
  [value]
  (int (Math/round (double (max 0 (min (quantum-range) value))))))

(defn invert-sample-value
  [val]
  (- (quantum-range) (round-to-quantum val)))

(defmacro intensity
  "Returns the intensity of the pixel."
  ([pxl] `(intensity (:red   ~pxl)
                     (:green ~pxl)
                     (:blue  ~pxl)
                     (:alpha ~pxl)))
  ([r g b] `(intensity ~r ~g ~b 0))
  ([r g b a]
     `(round-to-quantum (+ (* ~r 0.299)
                           (* ~g 0.587)
                           (* ~b 0.114)))))

(defn invert-pixel
  "Returns an inverted pixel."
  [p]
  (create-pixel (invert-sample-value (:red   p))
                (invert-sample-value (:green p))
                (invert-sample-value (:blue  p))
                (:alpha p)))
