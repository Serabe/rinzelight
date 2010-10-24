(ns rinzelight.effects.rescale
  (:use [rinzelight.effects.lookup-tables
         :only [lookup-table-from
                multisample-lookup-table
                apply-lookup-table]]
        [rinzelight.pixel
         :only [pixel-round-to-quantum]]))

(defn complete-multisample
  [factor default]
  (if (number? factor)
    [factor factor factor default]
    (condp = (count factor)
        1 (conj (vec factor) default default default)
        2 (conj (vec factor) default default)
        3 (conj (vec factor) default)
        4 (vec factor)
        :else (take 4 (vec factor)))))

(defn create-rescale-lookup-table
  "Creates a multisample lookup table for rescaling an image."
  [factor offset]
  (let [fs (complete-multisample factor 1.0)
        os (complete-multisample offset 0)]
    (apply multisample-lookup-table
           (map (fn [f o]
                  (lookup-table-from (fn [x] (pixel-round-to-quantum (+ o (* f x))))))
                fs
                os))))

; Yes, I do not use RescaleOp.
(defmacro rescale
  "Rescales an image by multiplying each pixel value by factor and adding an offset.
Both factor and offset can be vectors. They'll be completed upto 4 numbers (or only the first four will be used)."
  [img factor offset & rhs]
  `(apply-lookup-table
    ~img
    (create-rescale-lookup-table ~factor ~offset)
    ~@rhs))
