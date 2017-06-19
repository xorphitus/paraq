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
                  (fn [response] (async/put! chan [url response]))
                  (fn [exeption] (async/put! chan [url (.getData exeption)]))))))

(defmacro gen-logger [formatter chan]
  `(async/thread
     (while true
       (let [data# (async/<!! ~chan)
             url# (first data#)
             response# (last data#)]
         (println (~formatter url# response#))))))

(defn exec-with-logging [urls]
  (let [chan (async/chan)]
    (do
      (gen-logger fmt chan)
      (exec urls chan))))
