(ns paraq.core-test
  (:require [clojure.test :refer :all]
            [ring.adapter.jetty :as server]
            [paraq.core :refer :all]))

(defonce server (atom nil))
(def port 3000)

(defn handler [req]
  (condp = [(:request-method req) (:uri req)]
    [:get "/get"]
    {:status 200 :body "get"}
    [:get "/not_found"]
    {:status 404 :body "not found"}
    [:get "/server_error"]
    {:status 500 :body "server error"}))

(defn start-server []
  (when-not @server
    (reset! server (server/run-jetty handler {:port port :join? false}))))

(defn stop-server []
  (when @server
    (.stop @server)
    (reset! server nil)))

(defn restart-server []
  (when @server
    (stop-server)
    (start-server)))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
