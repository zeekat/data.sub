(ns nl.zeekat.data.sub
  (:require [clojure.data :refer [diff]]))

(defn sub?
  "true if `sub` is either `nil`, equal to `x`, or a recursive
  subcollection of `x`.

  If sub is a sequential collection of size N the first N elements of
  `x` are tested. If sub is a map every value in sub is tested with
  the corresponding value in `x`. If `sub` is a set every *key* in sub
  should exist in `x`.

  **Examples:**

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
                 [:1 :2 :3]))
  "
  {:doc/format :markdown}
  [sub x]
  (cond (nil? sub)
        true
        (sequential? sub)
        (and (sequential? x)
             (<= (count sub)
                 (count x))
             (every? (fn [[i el]]
                       (sub? el (nth x i)))
                     (map-indexed vector sub)))
        (map? sub)
        (every? (fn [[k v]] (sub? v (get x k)))
                sub)
        (set? sub)
        (every? #(contains? x %)
                sub)
        :else
        (= sub x)))

(defn- droplast-while
  [pred x]
  (when-let [s (seq (reverse (drop-while pred (reverse x))))]
    (vec s)))


(defn- nil-col
  [c]
  (if (and (coll? c) (empty? c))
    nil
    c))

(declare prune-subdiff)

(defn- prune-subdiff*
  [x]
  (cond
    (sequential? x)
    (droplast-while nil? (map prune-subdiff x))
    (map? x)
    (into (empty x) (remove (fn [[k v]] (nil? v)) x))
    :else
    x))

(defn prune-subdiff
  [x]
  (-> x
      prune-subdiff*
      nil-col))

(defn subdiff-left
  [sub x]
  (cond (nil? sub)
        nil
        (= sub x)
        nil
        (and (sequential? sub) (sequential? x))
        (->> (map subdiff-left sub (concat x (repeat nil)))
             prune-subdiff)
        (and (map? sub) (map? x))
        (->> (into (empty sub)
                   (map (fn [[k v-sub]]
                          [k (subdiff-left v-sub (get x k))])
                        sub))
             prune-subdiff)
        (set? sub)
        (set (remove #(contains? x %) sub))
        :else
        sub))

(defn subdiff-right
  [sub x]
  (cond (nil? sub)
        nil
        (= sub x)
        nil
        (and (sequential? sub) (sequential? x))
        (->> (mapv subdiff-right sub x)
             (prune-subdiff))
        (and (map? sub) (map? x))
        (->> (into (empty sub)
                   (map (fn [[k v-sub]]
                          [k (subdiff-right v-sub (get x k))])
                        sub))
             (prune-subdiff))
        (set? sub)
        nil
        :else
        x))

(defn subdiff-both
  [sub x]
  (cond (nil? sub)
        sub
        (= sub x)
        sub
        (and (sequential? sub) (sequential? x))
        (->> (mapv subdiff-both sub x)
             (prune-subdiff))
        (and (map? sub) (map? x))
        (->> (into (empty sub)
                   (map (fn [[k v-sub]]
                          [k (subdiff-both v-sub (get x k))])
                        sub))
             (prune-subdiff))
        (set? sub)
        (set (filter #(contains? x %) sub))
        :else
        nil))

(defn subdiff
  [sub x]
  (if (sub? sub x)
    [nil nil sub]
    [(subdiff-left sub x) (subdiff-right sub x) (subdiff-both sub x)]))
