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
