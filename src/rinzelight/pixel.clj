(ns rinzelight.pixel)

(defstruct pixel :red :green :blue :alpha)

(def opaque-opacity 0)

(defn create-pixel
  "Creates a new pixel. If only one value is supplied, it uses it for red, green and blue. If opacity is not given, opaque-opacity is used." 
  ([r] (create-pixel r r r opaque-opacity))
  ([r g b] (create-pixel r g b opaque-opacity))
  ([r g b a] (struct pixel r g b a)))

(defn convert-to-pixel-seq
  "Converts an array of ints into a pixel seq."
  [pixels width height]
  (vec ))
