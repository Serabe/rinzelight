(ns rinzelight.display-image
  (:import (javax.swing JComponent JFrame WindowConstants)
           (java.awt BorderLayout Dimension)))

(defn display-fn
  "Just a function for displaying images"
  [img]
  (let [frame (JFrame. "Untitled Image")
        size (Dimension. (:width img) (:height img))]
    (do
      (doto frame
        (.setDefaultCloseOperation WindowConstants/DISPOSE_ON_CLOSE)
                                        ;(.setResizable false)
        (.setLayout (BorderLayout.))
        (.setSize size)
        (.pack))

      (doto (.getGraphics (.getRootPane frame))
        (.drawImage (:img img) 0 0 (:width img) (:height img) nil)
        (.dispose))

      (doto frame
        (.pack)
        (.show)))))
