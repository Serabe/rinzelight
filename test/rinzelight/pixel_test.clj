(ns rinzelight.pixel-test
  (:use midje.sweet)
  (:use clojure.test)
  (:use rinzelight.pixel))


(deftest create-pixel-facts
  (let [v 35
        p (create-pixel v)]
    (fact "When passed one argument, it should use it for red, green and blue."
          (:red   p) => v
          (:green p) => v
          (:blue  p) => v
          (:alpha p) => rl-opaque-opacity))

  (let [r 30
        g 60
        b 90
        p (create-pixel r g b)]
    (fact "When passed three arguments, they should be used for red, green and blue."
          (:red   p) => r
          (:green p) => g
          (:blue  p) => b
          (:alpha p) => rl-opaque-opacity))

  (let [r 30
        g 60
        b 90
        a 99
        p (create-pixel r g b a)]
    (fact "When passed four arguments, they should be used for red, green, blue and opacity."
          (:red   p) => r
          (:green p) => g
          (:blue  p) => b
          (:alpha p) => a)))

(deftest pixel-to-int-array-facts
  (let [r 30
        g 60
        b 90
        a 99
        actual-alpha (- 255 a)
        p (create-pixel r g b a)
        arr (pixel-to-int-array p)]
    (fact "First element should equal red."
          (aget arr 0) => r)
    (fact "Second element should equal green."
          (aget arr 1) => g)
    (fact "Third element should equal blue."
          (aget arr 2) => b)
    (fact "Four element should equal alpha."
          (aget arr 3) => actual-alpha)))

(deftest pixel-round-to-quantum-facts

  (fact "Less than zero, returns zero"
        (pixel-round-to-quantum -5) => 0)

  (doseq [v (range (inc rl-quantum-range))]
    (let [s (str "If " v " is passed, it returns " v ".")])
    (fact ;s ; But if s is used, somehow it fails...
     (pixel-round-to-quantum v) => v))
  
  (fact "Greater than rl-quantum-range, returns rl-quantum-range"
        (pixel-round-to-quantum (inc rl-quantum-range)) => rl-quantum-range
        (pixel-round-to-quantum (* 2 rl-quantum-range)) => rl-quantum-range))



(deftest invert-sample-value-facts
  (doseq [v (range (inc rl-quantum-range))]
    
    (let [n (- rl-quantum-range v)]
      (fact "When passed a value between 0 and rl-quantum-range, it returns rl-quantum-range - value" ; Variable string when 0.5 is out
            (invert-sample-value v) => n)))

  (fact "When passed a value below 0, rl-quantum-range is returned."
        (invert-sample-value -35) => rl-quantum-range)

  (fact "When passed a value above rl-quantum-range, 0 is returned"
        (invert-sample-value (inc rl-quantum-range)) => 0))

(deftest invert-pixel-facts
  (let [r 30
        g 60
        b 90
        a 99
        p (create-pixel r g b a)
        i (invert-pixel p)]
    (fact "It returns the inverted red sample"
          (:red   i) => (invert-sample-value r))
    (fact "It returns the inverted green sample"
          (:green i) => (invert-sample-value g))
    (fact "It returns the inverted blue sample"
          (:blue  i) => (invert-sample-value b))
    (fact "It returns the SAME alpha value"
          (:alpha i) => a)))
