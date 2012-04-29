(ns ^{:doc "Functions for playing sounds with html5 and for loading
           the sound files."}
  jammer.sound)

(def audio-files {})
(def context (new js/webkitAudioContext))

(defn add-file
  "Gets a sound file from the server, adds it to audio-files "
  [name url]
  (let [request (new js/XMLHttpRequest)
        success (fn [buffer] (set!
                              audio-files
                              (assoc audio-files (keyword name) buffer)))
        error (fn [] (js/alert "Error, file does not exist."))
        on-load (fn []
                  (.decodeAudioData context
                                    (.-response request)
                                    success
                                    error))]
    (do
      (.open request "GET" url true)
      (set! (.-responseType request) "arraybuffer")
      (set! (.-onload request) on-load)
      (.send request))))

;; Play an audio file now!
(defn play-at
  "Plays the audio file in audio-files with that name"
  [name offset]
  (let [source (.createBufferSource context)
        file ((keyword name) audio-files)]
    (if (nil? file)
      (js/alert (str "File '" name "' does not exist"))
      (do
        (set! checkit file)
        (set! (.-buffer source) file)
        (.connect source (.-destination context))
        (.noteOn source offset)))))

(defn play-now
  "Plays the audio file in audio-files with that name now"
  [name]
  (play-at name 0))

(defn load-files []
  (add-file "kick" "/boom.wav"))