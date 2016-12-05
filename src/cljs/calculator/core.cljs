(ns calculator.core
  (:require cljsjs.react
            cljsjs.react.dom))

(extend-type js/Symbol
  IPrintWithWriter
  (-pr-writer [obj writer _]
    (write-all writer (str "#object[" (.toString obj) "]"))))

(enable-console-print!)

(defn element [type props & children]
  (js/React.createElement type (clj->js props) children))

(def Display
  (js/React.createClass
   #js {:displayName "Display"
        :render (fn []
                  (element "div" {:style {:border "1px solid black"
                                          :fontFamily "Monospace"
                                          :fontSize "2.5rem"
                                          :lineHeight "3rem"
                                          :textAlign "right"
                                          :width "20rem"
                                          :height "3rem"}}
                           "0"))}))

(js/ReactDOM.render (element Display {})
                    (js/document.getElementById "app"))
