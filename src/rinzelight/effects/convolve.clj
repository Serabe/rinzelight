(ns rinzelight.effects.convolve
  "Produces an enhance convolve operator, having more operators "
  (:use [rinzelight.image
         :only [create-image]]
        [rinzelight.rendering-hints
         :only [create-rendering-hint]]
        [rinzelight.effects.convolve.ops]
        [rinzelight.effects.convolve.kernel])
  (:import (java.awt Graphics2D))
  (:import (java.awt.image ConvolveOp)))

(defn convolve
  "Convolves img using the given kernel and the convolve-op. If there is no convolve-op, zero-fill-op is used.
If kernel is not valid (see check-kernel), nil is returned."
  [img kern & opt]
  (if (valid? kern)
    (let [conv-op (if (map? (first opt))
                    zero-fill-op
                    (first opt))
          rh      (apply create-rendering-hint
                         (if (map? (first opt))
                           opt
                           (rest opt)))
          [ix iy] (orig-img-starting-pixel kern)
          ni      (conv-op img kern)
          res-bi  (.filter (ConvolveOp. (to-java kern)
                                        ConvolveOp/EDGE_NO_OP
                                        rh)
                           (:image ni) nil)]
      (create-image (.getSubimage res-bi ix iy
                                  (:width img) (:height img))))
    nil))
