(ns paraq.core
  (:require [clojure.core.async :as async]
            [clj-http.client :refer :all]))

(defn exec [urls chan]
  (with-async-connection-pool {:timeout 5 :threads 4 :insecure? true :default-per-route 10}
    (doseq [url urls]
      (get url {:async? true}
           (fn [response] (async/put! chan response))
           (fn [exeption] (async/put! chan exeption))))))
