(ns rinzelight.constants)

(defmacro rinze-constant
  [name value]
  `(defmacro ~name [] ~value))

(defmacro rinze-int-constant
  [name value]
  `(defmacro ~name [] (list int ~value)))

(defmacro rinze-double-constant
  [name value]
  `(defmacro ~name [] (list double ~value)))

(rinze-int-constant quantum-range 255)
(rinze-int-constant opaque-opacity 255)
(rinze-int-constant transparent-opacity 0)
(rinze-double-constant quantum-scale (/ 1.0 (quantum-range)))
(rinze-double-constant epsilon 1.0e-10)
(rinze-double-constant sq2pi 2.50662827463100024161235523934010416269302368164062)
(rinze-int-constant kernel-rank 3)
