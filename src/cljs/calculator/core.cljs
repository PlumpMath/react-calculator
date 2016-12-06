(ns calculator.core
  (:require cljsjs.react
            cljsjs.react.dom
            [clojure.string :as str]))

(extend-type js/Symbol
  IPrintWithWriter
  (-pr-writer [obj writer _]
    (write-all writer (str "#object[" (.toString obj) "]"))))

(enable-console-print!)

(def app-state (atom {:display 0 :history []}))

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
                       (element "div" {:className "history"} (str/join " " (props :history))))))

(def Display
  (component "Display"
             :render (fn [props]
                       (element "div" {:className "display"} (props :value)))))


(defn digit-pressed [digit]
  (swap! app-state update :display #(long (str % digit))))

(def Button
  (component "Button"
             :render (fn [props]
                       (let [label (props :label)
                             handler (props :onPress)]
                         (element "button" {:className "button"
                                            :onClick #(handler label)} label)))))

(defn operator-pressed [op]
  (swap! app-state (fn [state]
                     (-> state
                         (update :history #(conj % (:display state) op))
                         (assoc :display 0)))))

(defn compute [result & [op num & xs]]
  (case op
    "+" (recur (+ result num) xs)
    "-" (recur (- result num) xs)
    "/" (recur (/ result num) xs)
    "*" (recur (* result num) xs)
    result))

(defn equals-pressed [_]
  (swap! app-state (fn [state]
                     (let [history (conj (:history state) (:display state))
                           result (apply compute history)]
                       (-> state
                           (assoc :display result)
                           (assoc :history []))))))

(def Keypad
  (component "Keypad"
             :render (fn [props]
                       (element "div" {}
                                (element "div" {}
                                         (element Button {:label "7" :onPress digit-pressed})
                                         (element Button {:label "8" :onPress digit-pressed})
                                         (element Button {:label "9" :onPress digit-pressed})
                                         (element Button {:label "/" :onPress operator-pressed}))
                                (element "div" {}
                                         (element Button {:label "4" :onPress digit-pressed})
                                         (element Button {:label "5" :onPress digit-pressed})
                                         (element Button {:label "6" :onPress digit-pressed})
                                         (element Button {:label "*" :onPress operator-pressed}))
                                (element "div" {}
                                         (element Button {:label "1" :onPress digit-pressed})
                                         (element Button {:label "2" :onPress digit-pressed})
                                         (element Button {:label "3" :onPress digit-pressed})
                                         (element Button {:label "-":onPress operator-pressed}))
                                (element "div" {}
                                         (element Button {:label "0" :onPress digit-pressed})
                                         (element Button {:label "."})
                                         (element Button {:label "="})
                                         (element Button {:label "+" :onPress operator-pressed}))))))

(def Calculator
  (component "Calculator"
             :render (fn [props]
                       (element "div" {}
                                (element History {:history (props :history)})
                                (element Display {:value (props :display)})
                                (element Keypad {})))))

(defn render [state]
  (js/ReactDOM.render (element Calculator state)
                      (js/document.getElementById "app")))

(render @app-state)

(add-watch app-state :redraw (fn [_ _ _ state]
                                (render state)))
