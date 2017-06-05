(ns paraq.core
  (:require [clj-http.client :refer :all]))

(defn exec [urls]
  (with-async-connection-pool {:timeout 5 :threads 4 :insecure? true :default-per-route 10}
    (doseq [url urls]
      (get url {:async? true}
           (fn [response] (println response))
           (fn [exeption] (println exeption))))))
