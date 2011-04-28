(defproject clj-ucm "1.0.0-SNAPSHOT"            :description "Simple library wrapping around the Oracle RIDC client library fo UCM.                         This has been developed and tested against UCM 10Gr3 - may work with                          the new version but haven't tried it yet. The one Oracle library is not                          available through clojars - or jarvana so it needs to be added to you local                          maven repository as below:\n                         \n                         $ mvn install:install-file -Dfile=./ridc/oracle-ridc-client-10g.jar -DgroupId=oracle -DartifactId=ridc -Dversion=10.0 -Dpackaging=jar -DgeneratePom=true\n                        \n                         The above bit of script requires that the jar is downloaded and placed in the named directories. They                         can be downloaded from - accept the the license agreement and click on the link to download Content Integration Suite:                        \n                         * http://www.oracle.com/technetwork/middleware/content-management/downloads/index-ucm10g-082682.html"            :dependencies [                            [org.clojure/clojure "1.2.0-beta1"]                            [org.clojure/clojure-contrib "1.2.0-beta1"]                            [clj-time "0.1.0-RC1"]                            [oracle/ridc "10.0"]                            [clojure-csv "1.2.4"]                            [commons-httpclient/commons-httpclient "3.0.1"]]            :dev-dependencies [                                [vimclojure/server "2.3.0-SNAPSHOT"]                                [org.clojars.autre/lein-vimclojure "1.0.0"]                                [org.clojars.technomancy/clj-stacktrace "0.2.1-SNAPSHOT"]                                [lein-search "0.3.4"]                                [lein-notes  "0.0.1"]                                [lein-clojars "0.6.0"]]            :repl-init-script "src/conf.clj")