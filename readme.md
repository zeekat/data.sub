# data.sub - test values against subcollections

## Abstract

`(data.sub/sub? sub x)` will return true if the _expected_ `sub`
subtree matches the _actual_ `x` value.

    (sub? {:a [1 nil [3]]}
          {:a [1 2   [3 4] :continued] :b "something"})
    => true

The `sub` description is designed to look similar to the actual `x`
value, and matches recursively, where "uninteresting" parts can be
left off or specified as `nil` - which will match anything.

## Humane test output

`data.sub` can integrate with `pjstadig/humane-test-output` to report
subdiffs on failing (sub?) tests.

To enable this, setup your leiningen profiles.clj or project.clj like:

       {:dependencies [[pjstadig/humane-test-output "0.8.1"]
                       [nl.zeekat/data.sub "0.1.2"]]
        :injections   [(require 'pjstadig.humane-test-output)
                       (pjstadig.humane-test-output/activate!)
                       (require 'nl.zeekat.data.sub.humane-test-output)
                       (nl.zeekat.data.sub.humane-test-output/activate!)]}
## Alternatives

`clojure.spec.alpha`

`clojure.core.match`

## Changes

 - 0.1.4

        Fixes for the humane-test-output diffs, added a few tests.

