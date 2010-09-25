(ns rinzelight.buffered-image
  (:use [clojure.contrib.str-utils2 :only [split]]
        [rinzelight.format :only [get-normalized-format]]
        [rinzelight.pixel  :only [create-pixel
                                  pixel-to-int-array]])  
  (:import (java.awt Color)
          (java.awt.image BufferedImage)
          (java.io FileOutputStream)
          (javax.imageio ImageIO)))

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
