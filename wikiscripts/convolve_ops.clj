(ns wikiscripts.convolve-ops
  (:use [rinzelight.effects.convolve.ops
         :only [zero-fill-op
                repeat-op
                thorus-op]]
        [rinzelight.effects.convolve.kernel
         :only [create-kernel]]
        [rinzelight.image
         :only [read-image
                write-image]]))

(def img1 (read-image "samples/clown.jpg"))
(def img2 (read-image "samples/northern-lights.jpg"))
(def kernel (create-kernel 60 60 (float-array (* 60 60))))


(defmacro op-example
  [name]
  (let [out-file-1 (str "out/op-"name "1.png")
        out-file-2 (str "out/op-"name "2.png")]
    `(do
       (write-image (~name ~'img1 ~'kernel)
                    ~out-file-1)
       (write-image (~name ~'img2 ~'kernel)
                 ~out-file-2))))

(op-example zero-fill-op)
(op-example repeat-op)
(op-example thorus-op)
