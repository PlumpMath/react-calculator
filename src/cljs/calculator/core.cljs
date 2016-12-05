(ns calculator.core
  (:require cljsjs.react
            cljsjs.react.dom))

(extend-type js/Symbol
  IPrintWithWriter
  (-pr-writer [obj writer _]
    (write-all writer (str "#object[" (.toString obj) "]"))))

(enable-console-print!)

(set! (.-innerHTML (js/document.getElementById "app"))
      "<h1>Hello React! What a great way to develop :)</h1>")

(js/React.createElement "div" #js {}
                        (js/React.createElement "p" #js {} "Hello from React")
                        (js/React.createElement "img" #js {:src "/parrot.gif"}))
