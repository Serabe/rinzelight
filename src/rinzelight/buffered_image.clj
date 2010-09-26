(ns rinzelight.buffered-image
  (:use [clojure.contrib.str-utils2 :only [split]]
        [rinzelight.format :only [get-normalized-format]]
        [rinzelight.pixel  :only [create-pixel
                                  pixel-to-int-array]])  
  (:import (java.awt Color)
          (java.awt.image BufferedImage)
          (java.io FileOutputStream)
          (javax.imageio ImageIO)))

(defmacro create-sample-get
  [sample-name sample-band]
  (let [fn-name (symbol (str "get-" sample-name "-sample-int"))
        doc-str (str "Returns " sample-name " sample from pixel x,y")]
    `(do
       (defn
         ~fn-name
         [img# x# y#]
         (.. ^BufferedImage (:image img#) getRaster (getSample x# y# ~sample-band)))
       (alter-meta! #'~fn-name
                    #(assoc %
                       :doc ~doc-str
                       :arglists '([img x y]))))))

(defmacro create-samples-get
  [sample-name sample-band]
  (let [fn-name (symbol (str "get-" sample-name "-samples-int-array"))
        doc-str (str "Returns " sample-name " samples from pixel x,y to pixel x+width,y+height")]
    `(do
       (defn
         ~fn-name
         ([img# x# y# w# h#] (~fn-name img# x# y# w# h# (int-array (* w# h#))))
         ([img# x# y# w# h# ^ints arr#]
             (.. ^BufferedImage (:image img#) getRaster (getSamples x# y# w# h# ~sample-band arr#))))
       (alter-meta! #'~fn-name
                    #(assoc %
                       :doc ~doc-str
                       :arglists '([img x y width height] [img x y width height array]))))))

(defmacro create-sample-set
  [sample-name sample-band]
  (let [fn-name (symbol (str "set-" sample-name "-sample-int"))
        doc-str (str "Sets " sample-name " sample to pixel x,y")]
    `(do
       (defn
         ~fn-name
         [img# x# y# v#]
         (.. ^BufferedImage (:image img#) getRaster (setSample x# y# ~sample-band v#)))
       (alter-meta! #'~fn-name
                    #(assoc %
                       :doc ~doc-str
                       :arglists '([img x y v]))))))

(defmacro create-samples-set
  [sample-name sample-band]
  (let [fn-name (symbol (str "set-" sample-name "-samples-int-array"))
        doc-str (str "Sets " sample-name " samples from pixel x,y to pixel x+width,y+height")]
    `(do
       (defn
         ~fn-name
         [img# x# y# w# h# ^ints v#]
         (.. ^BufferedImage (:image img#) getRaster (setSamples x# y# w# h# ~sample-band v#)))
       (alter-meta! #'~fn-name
                    #(assoc %
                       :doc ~doc-str
                       :arglists '([img x y w h v]))))))

(defmacro create-sample-accessors
  [sample-name sample-band]
  `(do
     (create-sample-get  ~sample-name ~sample-band)
     (create-samples-get ~sample-name ~sample-band)
     (create-sample-set  ~sample-name ~sample-band)
     (create-samples-set ~sample-name ~sample-band)))

(create-sample-accessors red   0)
(create-sample-accessors green 1)
(create-sample-accessors blue  2)
(create-sample-accessors alpha 3)

(defn write-buffered-image
  [buf-img uri]
  "Writes a BufferedImage to a File whose path is uri"
  (with-open [file (FileOutputStream. uri)]
    (ImageIO/write buf-img (get-normalized-format uri) file)))

(defn create-empty-canvas
  "Creates an empty BufferedImage with the predefined color."
  ([width height]
     (create-empty-canvas width height Color/WHITE))

  ([width height color]
     (let [buf-img (BufferedImage. width height BufferedImage/TYPE_INT_ARGB)]
       (doto (.getGraphics buf-img)
         (.setBackground color)
         (.clearRect 0 0 width height)
         (.dispose))
       buf-img)))

(defn create-new-canvas-for-image
  "Creates a new canvas an copies the image to it"
  [img]
  (let [buf-img (create-empty-canvas (:width img) (:height img))]
    (doto (.getGraphics buf-img)
      (.drawImage (:image img) 0 0 nil))
    buf-img))

(defn get-pixels-int-array
  "Returns pixels from pixel x,y to x+width,y+height"
  ([img x y w h]
     (let [size (* w h 4)
           arr (int-array size)]
       (.. ^BufferedImage (:image img) getRaster (getPixels x y w h arr))
       arr))
  ([img x y]
     (.. ^BufferedImage (:image img) getRaster (getPixel x y (int-array 4)))))

(defn get-pixel
  "Returns the pixel (x,y) in img as a pixel struct."
  [img x y]
  (let [a ^ints (get-pixels-int-array img x y)]
    (create-pixel (aget a 0)
                  (aget a 1)
                  (aget a 2)
                  (- 255 (aget a 3)))))

(defn set-pixels-int-array
  "Set pixels from pixel x,y to x+width,y+height."
  ([img x y w h pixels]
     (.. ^BufferedImage (:image img) getRaster (setPixels x y w h pixels)))
  ([img x y pixel]
     (.. ^BufferedImage (:image img) getRaster (setPixel x y pixel))))

(defn set-pixel
  "Given a pixel (in a pixel struct), writes it in (x,y) in img."
  [img x y p]
  (set-pixels-int-array img x y (pixel-to-int-array p)))
