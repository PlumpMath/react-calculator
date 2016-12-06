(ns calculator.core
  (:require cljsjs.react
            cljsjs.react.dom))

(extend-type js/Symbol
  IPrintWithWriter
  (-pr-writer [obj writer _]
    (write-all writer (str "#object[" (.toString obj) "]"))))

(enable-console-print!)

(defonce app-state (atom {:display 0}))

(defn element [type props & children]
  "Create a Html element with associated attributes and children elements"
  (js/React.createElement type (clj->js props) children))

(defn component [name & {:keys [render]}]
  "Create a React component"
  (js/React.createClass
   #js {:displayName name
        :render (fn []
                  (this-as t
                    (render (js->clj (.-props t) :keywordize-keys true))))}))

(def History
  (component "History"
             :render (fn [props]
                       (element "div" {:className "history"} "5 + 20 +"))))

(def Display
  (component "Display"
             :render (fn [props]
                       (element "div" {:className "display"} (props :value)))))

(def Button
  (component "Button"
             :render (fn [props]
                       (element "button" {:className "button"} "-"))))

(def Keypad
  (component "Keypad"
             :render (fn [props]
                       (element "div" {}
                                (element "div" {}
                                         (element Button {})
                                         (element Button {})
                                         (element Button {})
                                         (element Button {}))
                                (element "div" {}
                                         (element Button {})
                                         (element Button {})
                                         (element Button {})
                                         (element Button {}))
                                (element "div" {}
                                         (element Button {})
                                         (element Button {})
                                         (element Button {})
                                         (element Button {}))
                                (element "div" {}
                                         (element Button {})
                                         (element Button {})
                                         (element Button {})
                                         (element Button {}))))))

(defn render [state]
  (js/ReactDOM.render (element Display {:value (:display state)})
                      (js/document.getElementById "app")))

(render @app-state)

(add-watch app-state :redraw (fn [_ _ _ state]
                                (render state)))
