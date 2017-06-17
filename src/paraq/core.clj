(ns paraq.core
  (:require [clojure.core.async :as async]
            [clj-http.client :as client]))

(defn fmt [url response]
  (let [status (:status response)
        time (:request-time response)]
    (format "%d,%d,%s" status time url)))

(defn exec [urls chan]
  (client/with-async-connection-pool {:timeout 5 :threads 4 :insecure? true :default-per-route 10}
    (doseq [url urls]
      (client/get url {:async? true}
           (fn [response] (async/put! chan (fmt url response)))
           (fn [exeption] (async/put! chan (fmt url (.getData exeption))))))))

(defn exec-with-logging [urls]
  (let [chan (async/chan)]
    (do
      (async/thread
        (while true
          (println (async/<!! chan))))
      (exec urls chan))))
