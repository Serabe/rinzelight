(ns rinzelight.histogram
  (:use [rinzelight.buffered-image
         :only [get-pixels-int-array]]
        [rinzelight.constants
         :only [quantum-range]]
        [rinzelight.pixel
         :only [intensity]]))

(defn histogram*
  "f work outs the index to increment. The maximum value is (quantum-range)"
  [img f]
  (let [hist (int-array (inc (quantum-range)))
        w (:width img)
        h (:height img)]
    (doseq [y (range (:height img))]
      (let [pxls (partition 4
                  (vec (get-pixels-int-array img
                                             0 y
                                             w 1)))]
        (doseq [[r g b a] pxls]
          (let [idx (f r g b a)]
            (aset hist idx (inc (aget hist idx)))))))
    (vec hist)))

(defn histogram
  "Calculates the histogram using the intensity."
  [img]
  (histogram* img (fn [r g b _] (intensity r g b))))

(defn red-histogram
  "Calculates the histogram of the red channel"
  [img]
  (histogram* img (fn [r _ _ _] r)))


(defn green-histogram
  "Calculates the histogram of the green channel"
  [img]
  (histogram* img (fn [_ g _ _] g)))


(defn blue-histogram
  "Calculates the histogram of the blue channel"
  [img]
  (histogram* img (fn [_ _ b _] b)))

(defn alpha-histogram
  "Calculates the histogram of the alpha channel"
  [img]
  (histogram* img (fn [_ _ _ a] a)))

(defn- min-value-non-zero
  "Returns the first idx whose value is not zero.
Don't use this with other data but histograms. I save checks."
  [coll]
  (loop [[x & xs] coll
         idx 0]
    (if (not (zero? x))
      idx
      (recur xs (inc idx)))))

(defn min-value
  "Returns the least value in intensity"
  [img]
  (min-value-non-zero (histogram img)))

(defn max-value
  "Returns the greates value in intensity"
  [img]
  (min-value-non-zero (reverse (histogram img))))


(defmacro values-for-channel
  [channel]
  (let [mn (symbol (str channel "-min-value"))
        mx (symbol (str channel "-max-value"))
        hist (symbol (str channel "-histogram"))
        doc-mn (str "Returns the least index whose value is non zero for channel " channel " in the histogram.")
        doc-mx (str "Returns the least index whose value is non zero for channel " channel " in the histogram.")]
    `(do
       (defn
         ~mn
         [~'img]
         (min-value-non-zero (~hist ~'img)))

       (alter-meta! #'~mn #(assoc % :doc ~doc-mn))
       
       (defn ;~doc-mx
         ~mx
         [~'img]
         (min-value-non-zero (reverse ~hist ~'img)))

       (alter-meta! #'~mx #(assoc % :doc ~doc-mx)))))

(values-for-channel red)
(values-for-channel green)
(values-for-channel blue)
(values-for-channel alpha)
