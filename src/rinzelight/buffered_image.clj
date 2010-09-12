(ns rinzelight.buffered-image
  (:use [clojure.contrib.str-utils2 :only [split]]
        [rinzelight.format :only [get-normalized-format]])  
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
       (doto (.createGraphics buf-img)
         (.setBackground color)
         (.clearRect 0 0 width height)
         (.dispose))
       buf-img)))

(defn create-new-canvas-for-image
  "Creates a new canvas an copies the image to it"
  [img]
  (let [buf-img (create-empty-canvas (:width img) (:height img))]
    (doto (.createGraphics buf-img)
      (.drawImage (:image img) 0 0 nil))
    buf-img))

(defn get-pixels-int-array
  "Returns pixels from pixel x,y to x+width,y+height"
  ([img x y w h]
     (let [size (* w h 4)
           arr (int-array size)]
       (.. (:image img) getRaster (getPixels x y w h arr))
       arr))
  ([img x y]
     (.. (:image img) getRaster (getPixel x y (int-array 4)))))

(defn set-pixels-int-array
  "Set pixels from pixel x,y to x+width,y+height."
  ([img x y w h pixels]
     (.. (:image img) getRaster (setPixels x y w h pixels)))
  ([img x y pixel]
     (.. (:image img) getRaster (setPixel x y pixel))))
