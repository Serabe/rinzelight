(ns rinzelight.pixel)

(defstruct pixel-packet :red :green :blue :alpha)

; TODO: Define this as macros.
(def rl-opaque-opacity 0)
(def rl-quantum-range 255)
(def rl-quantum-scale (/ 1.0 255))
(def rl-transparent-opacity rl-quantum-range)

(defn create-pixel
  "Creates a new pixel. If only one value is supplied, it uses it for red, green and blue. If opacity is not given, opaque-opacity is used." 
  ([r] (create-pixel r r r rl-opaque-opacity))
  ([r g b] (create-pixel r g b rl-opaque-opacity))
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

(defn pixel-round-to-quantum
  "Assures that a value is between 0 and rl-quantum-value"
  [value]
  (max 0 (min rl-quantum-range value)))

(defn invert-sample-value
  [val]
  (- rl-quantum-range (pixel-round-to-quantum val)))

(defn invert-pixel
  "Returns an inverted pixel."
  [p]
  (create-pixel (invert-sample-value (:red   p))
                (invert-sample-value (:green p))
                (invert-sample-value (:blue  p))
                (:alpha p)))
