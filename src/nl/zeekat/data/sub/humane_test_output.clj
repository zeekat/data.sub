(ns nl.zeekat.data.sub.humane-test-output
  (:require [pjstadig.humane-test-output]
            [nl.zeekat.data.sub :refer [sub? subdiff]]
            [clojure.test :refer [assert-expr do-report]]))

(defn sub?-body
  [msg [pred sub x :as form]]
  `(let [sub# ~sub
         x#   ~x]
     (let [result# (~pred sub# x#)]
       (if result#
         (do-report {:type     :pass, :message ~msg,
                     :expected sub#,  :actual  [x#]})
         (do-report {:type     :fail, :message ~msg,
                     :expected sub#,  :actual  [x#]
                     :diffs    [[x# (subdiff sub# x#)]]}))
       result#)))

(defonce activation-body
  (delay
   (when (not (System/getenv "INHUMANE_TEST_OUTPUT"))
     (defmethod assert-expr 'sub?
       [msg form]
       (sub?-body msg form))
     (defmethod assert-expr 'nl.zeekat.data.sub/sub?
       [msg form]
       (sub?-body msg form)))))

(defn activate! []
  @activation-body)
