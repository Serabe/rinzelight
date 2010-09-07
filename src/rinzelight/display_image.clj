(ns rinzelight.display-image
  (:import (javax.swing JComponent JFrame WindowConstants)
           (java.awt BorderLayout Dimension)))

(defn display-fn
  "Just a function for displaying images"
  [img]
  (let [frame (JFrame. "Untitled Image")
        picture (proxy [JComponent] []
                  (paintComponent [g]
                                  (.drawImage g (:image img) 0 0 nil)))
        size (Dimension. (:width img) (:height img))]
    (do
      (doto picture
        (.setPreferredSize size))
      
      (doto frame
        (.setDefaultCloseOperation WindowConstants/DISPOSE_ON_CLOSE)
                                        ;(.setResizable false)
        (.setLayout (BorderLayout.))
        (.add picture BorderLayout/CENTER)
        (.pack)
        (.show)))))
