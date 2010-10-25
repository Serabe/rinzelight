(ns wikiscripts.composition
  (:use rinzelight.image
        rinzelight.composition
        rinzelight.geometry
        rinzelight.rendering-hints))

(def nl (read-image "samples/northern-lights.jpg"))
(def src-img (read-image "samples/clojure.png"))

(def hx (/ (:width src-img) 2))
(def hy (/ (:height src-img) 2))

(def cx (- (:width nl) hx))
(def cy (- (:height nl) hy))

(def dst-img (compose (create-image (+ (:width nl)
                                       (/ (:width src-img) 2))
                                    (+ (:height nl)
                                       (/ (:height src-img) 2))
                                    java.awt.Color/GRAY)
                      nl))


(defmacro porter-duff-example
  [rule]
  (let [out-file (str "out/pd-" rule ".png")]
    `(write-image (compose ~'dst-img
                           ~'src-img
                           (~rule)
                           ~'cx ~'cy)
                  ~out-file)))

(defmacro geometry-example
  [geom]
  (let [out-file (str "out/geom-" geom ".png")]
    `(write-image (compose ~'nl
                           ~'src-img
                           (src-over)
                           ~geom)
                  ~out-file)))

(porter-duff-example clear)
(porter-duff-example dst)
(porter-duff-example dst-atop)
(porter-duff-example dst-in)
(porter-duff-example dst-out)
(porter-duff-example dst-over)
(porter-duff-example src)
(porter-duff-example src-atop)
(porter-duff-example src-in)
(porter-duff-example src-out)
(porter-duff-example src-over)
(porter-duff-example xor)

(geometry-example north)
(geometry-example north-east)
(geometry-example east)
(geometry-example south-east)
(geometry-example south)
(geometry-example south-west)
(geometry-example west)
(geometry-example north-west)
