(ns nl.zeekat.data.sub-test
  (:require [nl.zeekat.data.sub :refer [sub? subdiff]]
            [clojure.test :refer [deftest are testing]]))

(deftest test-sub?
  (testing "matching types"
    (are [x] x
      (sub? nil
            {:anything :at-all})
      (sub? [:a :b]
            [:a :b :c])
      (not (sub? [:a :b :c]
                 [:a :b]))
      (sub? {:a [1 2 3]}
            {:a [1 2 3 4] :b 2})
      (sub? {:a [1 nil 3]}
            {:a [1 2 3 4] :b 2})
      (not (sub? {:a [1 2 3 4]}
                 {:a [1 2 3] :b 2}))
      (sub? #{:a}
            {:a 1 :b 2})
      (sub? #{:a}
            #{:a :b})
      (not (sub? #{:a :c}
                 #{:a :b}))
      (sub? :something
            :something)
      (sub? [:1 :2 :3]
            (list :1 :2 :3))
      (sub? [:1 :2]
            (list :1 :2 :3))
      (sub? (list :1 :2 :3)
            [:1 :2 :3])
      (not (sub? (list nil 2)
                 [:1 :2 :3]))))
  (testing "incompatible types"
    (are [x y] (not (sub? x y))
      [:a :b]  {:a :b}
      {:a :b}  [:a :b]
      '(:a :b) {:a :b})))

(deftest test-subdiff
  (testing "sequential items"
    (are [x] x
      (= [nil nil nil] (subdiff nil [:a :b :c]))
      (= [[nil :b] nil [:a]] (subdiff [:a :b] [:a]))
      (= [[[nil :b] :c] [[nil 1] 2] [[:a]]]  (subdiff [[:a :b] :c] [[:a 1] 2]))
      (= [{:a [nil :c]} {:a [nil 2]} {:a [:b]}] (subdiff {:a [:b :c]} {:a [:b 2]}))
      (= [[nil {:a 1}] [nil {:a 2}] [0 {:b 3}]] (subdiff [0 {:a 1 :b 3}] [0 {:a 2 :b 3}])))))

