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

(ns clj-ucm.core
  (:import (oracle.stellent.ridc.*)))

  ;; Service definitions - strings used by UCM

  (def search-svr           "GET_SEARCH_RESULTS")
  (def ping-svr             "PING_SERVER")
  (def checkin-svr          "CHECKIN_UNIVERSAL")
  (def doc-info-by-name-svr "DOC_INFO_BY_NAME")
  (def doc-update-svr       "UPDATE_DOCINFO")

  ;; Connection code

  (def *idc-env* nil)

  (defn client [conn-url]
    (-> (new oracle.stellent.ridc.IdcClientManager)
      (.createClient conn-url)))

  (defn basic-creds [user pass]
    (new oracle.stellent.ridc.auth.impl.BasicCredentials user pass))

  (defn uctx [user pass]
    (new oracle.stellent.ridc.IdcContext (basic-creds user pass)))

  (defn get-env
    "Sets up the objects needed to connect to a UCM instance - 
    a client connection and a user context."
    [conn-url user password]
    {:client (client conn-url)
    :uctx   (uctx user password) })

  (defn with-connection*
    ""
    [conn-spec func]
    (binding [*idc-env* (get-env (:conn-url conn-spec) (:user conn-spec) (:pass conn-spec))]
      (func)))

  (defmacro with-connection
    "Evaluates the body within the connection params specified:
    conn-url:
    user:
    pass:
    "
    [conn-spec & body]
    `(with-connection* ~conn-spec (fn [] ~@body)))

  ;; Code for handling calls to RIDC and bridging the gap with Clojure

  (defn get-fields [result-set]
    (apply merge (for [f (.getFields result-set)]
                      (hash-map (.getName f)
                        {:type         (.getType f)
                         :max-len      (.getMaxLen f)
                         :is-fixed-len (.isFixedLen f)}))))

  (defn conv-rs* [result-set]
    (let [obj-rows (.getRows result-set)
          num-rows (count obj-rows)
          fields   (get-fields result-set)
          rows     (for [r obj-rows]
                     (apply merge (for [k (.keySet r)]
                       (let [ky     (str k)
                             v      (.get r k)
                             norm-v (if (nil? v)
                                     " "
                                     (str v))]
                         (hash-map ky norm-v)))))]
      {:fields   fields
       :num-rows num-rows
       :rows     rows}))

  (defn conv-rs [result-sets str-name]
    (let [rs (get (:result-sets result-sets) str-name)]
      (merge
        {:name str-name}
        (conv-rs* rs))))

  (defn conv-all-rs [result-sets]
    (apply merge (for [[k v] (.entrySet result-sets)]
                   (hash-map k (conv-rs* v)))))

  (defn call-service-with-file* [env service-name props file]
    (let [client (:client env)
          uctx   (:uctx   env)
          bndr   (.createBinder client)]
      (.putLocal bndr "IdcService" service-name)
      (doall (for [k (keys props)] (.putLocal bndr (str k) (get props k))))
      (-> (.sendRequest client uctx bndr) (.getResponseAsBinder))
      ))

  (defn call-service* [env service-name props]
    (let [client (:client env)
          uctx   (:uctx   env)
          bndr   (.createBinder client)]
      (.putLocal bndr "IdcService" service-name)
      (doall (for [k (keys props)] (.putLocal bndr (str k) (get props k))))
      (-> (.sendRequest client uctx bndr) (.getResponseAsBinder))
      ))

  (defn call-service [env service-name props]
    (let [result       (call-service* env service-name props)
          local-data   (into {} (.getLocalData result))
          result-sets  (.getResultSets result)
          option-lists (.getOptionLists result)]
                      {:local-data  local-data
                       :result-sets (conv-all-rs result-sets)}))

  ;; Funcs to call particular services

  (defn ping []
      (call-service *idc-env* ping-svr nil))

  (defn search [search-str props]
      (let [all-props (merge {"QueryText" search-str} props)]
        (call-service *idc-env* search-svr all-props)))

  (defn quick-search [search-str]
    (let [props {"SortField"         "dInDate"
                 "SortOrder"         "Desc"
                 "ResultCount"       "20"
                 "QueryText"         search-str
                 "SearchQueryFormat" "Universal"}]
      (call-service *idc-env* search-svr props)))

  (defn doc-info
    ([doc-name]
      (let [props {"dDocName" doc-name}]
        (call-service *idc-env* doc-info-by-name-svr props)))
    ([*idc-env* doc-name did]
      nil)
    )

  (def doc-info-rs       "DOC_INFO")
  (def rev-history-rs    "REVISION_HISTORY")
  (def search-results-rs "SearchResults")

  (defn get-rs
    "Extracts a named result ste from the data structure returned by a service. This returns a function that takes the
     data structure and extracts the named result set"
    [rs-name]
    (fn [col]
      (:rows (get (:result-sets col) rs-name))))

  (def get-doc-info
    (comp first (get-rs doc-info-rs)))

  (def get-search-results
    (get-rs search-results-rs))

  (def get-rev-history
    (get-rs rev-history-rs))

  (defn update [doc-name did props]
    (let [props {"dDocName" doc-name}
          props {"dID"      did}]
      (call-service *idc-env* doc-update-svr props)))



