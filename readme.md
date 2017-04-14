# data.sub - describe expected data

## humane test output

`data.sub` can integrate with `pjstadig/humane-test-output` to report
subdiffs on failing (sub?) tests.

To enable this, setup your leiningen profiles.clj or project.clj like:

       {:dependencies [[pjstadig/humane-test-output "0.8.1"]
                       [nl.zeekat/data.sub "0.1.2"]]
        :injections   [(require 'pjstadig.humane-test-output)
                       (pjstadig.humane-test-output/activate!)
                       (require 'nl.zeekat.data.sub.humane-test-output)
                       (nl.zeekat.data.sub.humane-test-output/activate!)]}
