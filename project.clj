; Copyright (C) 2011 by kristian.foster@gmail.com
; 
; Permission is hereby granted, free of charge, to any person obtaining a copy
; of this software and associated documentation files (the "Software"), to deal
; in the Software without restriction, including without limitation the rights
; to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
; copies of the Software, and to permit persons to whom the Software is
; furnished to do so, subject to the following conditions:
; 
; The above copyright notice and this permission notice shall be included in
; all copies or substantial portions of the Software.
; 
; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
; IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
; FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
; AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
; LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
; OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
; THE SOFTWARE.

(defproject clj-ucm "1.0.0-SNAPSHOT"
  :description "Simple library wrapping around the Oracle RIDC client library fo UCM.
                This has been developed and tested against UCM 10Gr3 - may work with the new version but haven't tried it yet.
                The one Oracle library is not available through clojars - or jarvana
                so it needs to be added to you local maven repository as below:

                  $ mvn install:install-file -Dfile=./ridc/oracle-ridc-client-10g.jar -DgroupId=oracle -DartifactId=ridc -Dversion=10.0 -Dpackaging=jar -DgeneratePom=true

               The above bit of script requires that the jar is downloaded and placed in the named directories. They 
               can be downloaded from - accept the the license agreement and click on the link to download Content Integration Suite:

               * http://www.oracle.com/technetwork/middleware/content-management/downloads/index-ucm10g-082682.html

               "
  :dependencies [[org.clojure/clojure "1.2.0-beta1"]
                 [org.clojure/clojure-contrib "1.2.0-beta1"]
                 [clj-time "0.1.0-RC1"]
                 [oracle/ridc "10.0"]
                 [clojure-csv "1.2.4"]
                 ]
  :dev-dependencies [
                ; Needed for vim integration
                [vimclojure/server "2.3.0-SNAPSHOT"]
                [org.clojars.autre/lein-vimclojure "1.0.0"]
                [org.clojars.technomancy/clj-stacktrace "0.2.1-SNAPSHOT"]]
                ;
  ; This loads the user specific conf - in this case some
  ; utility functions and the connection details to my UCM instance
  :repl-init-script "src/conf.clj"
  )

