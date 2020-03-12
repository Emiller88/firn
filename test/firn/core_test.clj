(ns firn.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [firn.core :as sut]
            [firn.config :as config]
            [me.raynes.fs :as fs]
            [clojure.string :as str]))

(def test-dir      "./test/firn/demo_org/")
(def f-1           (io/file (str test-dir "file1.org")))
(def f-2           (io/file (str test-dir "file2.org")))
(def config-sample (config/default test-dir))

(deftest _setup
  (let [out-dir       (config-sample :out-dir)
        out-media-dir (config-sample :out-media-dir)
        setup-res     (sut/setup config-sample)]

    (testing "It creates _site output dir."
      (is (.isDirectory (io/file out-dir))))

    (testing "It creates a _site/media output dir."
      (is (.isDirectory (io/file out-media-dir))))

    (testing "Config returns unchanged"
      (is (= config-sample setup-res)))

    ;; cleanup
    (fs/delete-dir out-dir)))

(deftest _read-file
  (testing "correct outputs"
    (let [stub     (-> (sut/setup config-sample)
                       (config/set-curr-file f-1))
          res      (sut/read-file stub)
          res-json (-> res :curr-file :as-json)]
      (testing "Returns valid, updated sub map (:curr-file)"
        (is (not= nil (-> res :curr-file :name)))
        (is (not= nil res-json))
        (is (> (count res-json) 0))))))



(defn main-runner
  []
  (fs/delete-dir (config-sample :out-dir)) ;; clear it out!
  (sut/-main test-dir)) ;; delete folder if it exists

(main-runner)
