(ns rinzelight.effects.convolve.kernel
  (:use [rinzelight.constants
         :only [epsilon ]]))

(defprotocol ConvolutionKernel
  "Encapsulates both rinzelight kernel struct and java.awt.image.Kernel"
  (to-java [this]
           "Given a kernel, returns a java.awt.image.Kernel")
  (valid? [this])
  
  (^floats  kernel [this]
            "Returns an array of floats representing the data")
  (width [this]
         "Returns the width of the kernel")
  (height [this]
          "Returns the height of the kernel")

  (x-orig [this]
          "Returns the X origin in the kernel")

  (y-orig [this]
          "Return the Y origin in the kernel")
  
  (change-kernel [this new-kern]
                 "Return a new kernel with new-kern data in. It doesn't check if it is valid or not."))

(extend-protocol ConvolutionKernel
  java.awt.image.Kernel
  (to-java [this]
           this)

  (valid? [this]
          (and (odd? (.getWidth this))
               (odd? (.getHeight this))))

  (kernel [this] (.getKernelData this nil))

  (width [this] (.getWidth this))

  (height [this] (.getHeight this))

  (x-orig [this] (.getXOrigin this))

  (y-orig [this] (.getYOrigin this))
  
  (change-kernel [this new-kern]
                 (java.awt.image.Kernel. (.getWidth this)
                          (.getHeight this)
                          new-kern)))


(defmacro origin-element-for-length
  "Returns the default origin given a length"
  [length]
  `(dec (if (odd? ~length)
          (/ (inc ~length) 2)
          (/ ~length 2))))

(extend-protocol ConvolutionKernel
  clojure.lang.IPersistentMap
  (to-java [this]
           (java.awt.image.Kernel. (:width this)
                                   (:height this)
                                   (:kernel this)))

  (valid? [this]
          (and (odd? (:width this))
               (odd? (:height this))
               (== (alength (:kernel this))
                   (* (:width this) (:height this)))))

  (kernel [this] (:kernel this))

  (width [this] (:width this))

  (height [this] (:height this))
  
  (x-orig [this] (origin-element-for-length (:width this)))

  (y-orig [this] (origin-element-for-length (:height this)))

  (change-kernel [this new-kern]
                 (assoc this :kernel new-kern)))

(defstruct kernel-struct :kernel :width :height)

(defn create-kernel
  "Creates a new kernel. Please, don't use direct maps."
  [width height coll]
  (struct kernel-struct (into-array Float/TYPE (map float coll))
          width height))

(defn- gamma
  [^floats arr]
  (let [pre-g (areduce arr idx res 0 (+ res (aget arr idx)))]
    (if (< pre-g (epsilon))
      (float 1.0)
      (float (/ 1.0 pre-g)))))

(defn- ^floats mult-by-gamma
  [^floats arr g]
  (amap arr idx ret
        (aset ret idx (* g (aget arr idx)))))

(defn normalize-kernel
  "Normalizes a kernel. Normalizing a kernel is to divide each component by the sum of all of them."
  [kern]
  (let [data (kernel kern)
        g (gamma data)]
    (if (== g 1.0)
      kern
      (change-kernel kern (mult-by-gamma data g)))))
