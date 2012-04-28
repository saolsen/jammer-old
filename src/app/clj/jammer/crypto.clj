(ns jammer.crypto
  (:import
   (javax.crypto Mac)
   (javax.crypto.spec SecretKeySpec)
   (java.math BigInteger)))

;; Functions used for authenticating pusher clients.
;; The crypto stuff is ripped out of the middle of the real clojure
;; pusher library by bblimke
;; https://github.com/bblimke/clj-pusher
;; The bottom wraps it in a function that can be used to authenticate
;; a user.

(defn- ljust
  [xs x y]
  (apply str (concat (replicate (- x (.length #^String xs)) y) xs)))

(defn- byte-array-to-str [bytes]
  (let [big-integer (BigInteger. 1 bytes)
        hash (.toString big-integer 16)]
        (ljust hash 32 0)))

(defn hmac
  "Calculate HMAC signature for given data."
  [#^String key #^String data]
  (let [hmac-sha256 "HmacSHA256"
        signing-key (SecretKeySpec. (.getBytes key) hmac-sha256)
        mac (doto (Mac/getInstance hmac-sha256) (.init signing-key))]
    (byte-array-to-str (.doFinal mac (.getBytes data)))))

(defn get-signiture
  "Takes the post parameters and the secret key
   and returns the signiture"
  [channel socket_id key]
  (let [string (str socket_id ":" channel)]
    (hmac key string)))

;; Tests the hmac with the example that pusher provides in their documentation.
(defn testit
  []
  (let [expected
        "58df8b0c36d6982b82c3ecf6b4662e34fe8c25bba48f5369f135bf843651c3a4"
        start "1234.1234:private-foobar"
        private "7ad3773142a6692b25b8"
        result (hmac private start)
        withfn (get-signiture "private-foobar" "1234.1234" private)]
    (do
      (println expected)
      (println result)
      (println withfn)
      (= expected result withfn))))