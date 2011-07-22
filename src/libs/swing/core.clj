(in-ns 'libs.swing)

(defmacro invoke-later [& body]
  `(if (SwingUtilities/isEventDispatchThread)
     (do ~@body)
     (SwingUtilities/invokeLater
      (fn []
        ~@body))))

(defmacro invoke-and-wait [& body]
  `(if (SwingUtilities/isEventDispatchThread)
     (do ~@body)
     (SwingUtilities/invokeAndWait
      (fn []
        ~@body))))

(defn conf! [o & args]
  (invoke-later (config o args))
  o)

(defn as-dimension [size]
  (cond
   (number? size)             (Dimension. size size)
   (sequential? size)         (Dimension. (first size) (second size))
   (instance? Dimension size) size
   :else                      (fail "Can't treat" (class size) "as dimension")))

(def awt-colors
  [:white :light-gray :gray :dark-gray :black :red
   :pink :orange :yellow :green :magenta :cyan :blue])

(defmacro def-color-map []
  `(def color-map
     ~(into {}
            (for [col awt-colors]
              [col
               `(. Color ~(symbol (constant-name col)))]))))

(def-color-map)

(defn as-color [col]
  (let [double-as-float #(if (instance? Double %)
                           (float %)
                           %)]
    (cond (keyword? col)        (color-map col)
          (number? col)         (let [col (double-as-float col)]
                                  (Color. col col col))
          (sequential? col)     (let [[r g b alpha] (map double-as-float col)]
                                  (if alpha
                                    (Color. r g b alpha)
                                    (Color. r g b)))
          (instance? Color col) col
          :else                 (fail "Invalid color:" col))))

(defn close [o]
  (conf! o :close true))

(defn open [o]
  (conf! o :open true))

(defn value [o]
  (get o :value))

(defn set-value [o val]
  (conf! o :value val))

(defmethod set [Object :args]
  [o _ args]
  (doseq [arg args]
    (put o arg)))

(derive JMenu      ::menu)
(derive JPopupMenu ::menu)

(derive JList     ::list)
(derive JComboBox ::list)

;; Hack for not calling :action handlers
;; when changing items or value programmatically
(def disabled-combo-boxes (atom #{}))

(defn copy-to-clipboard [text]
  (.. (Toolkit/getDefaultToolkit)
      getSystemClipboard
      (setContents (StringSelection. text) nil)))

(defn get-ui-color [descr-string]
  (UIManager/getColor descr-string))

(defn set-native-look []
  (UIManager/setLookAndFeel
   (if (= "Linux" (System/getProperty "os.name"))
     "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"
     (UIManager/getSystemLookAndFeelClassName))))

(defn append-line [text-box text]
  (invoke-later (.append text-box (str text "\n"))))

