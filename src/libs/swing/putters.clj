(in-ns 'libs.swing)

(defmethod put [Component Object]
  [o val]
  (set o :value val))

(defmethod put [Container Component]
  [o inner]
  (.add o inner))

(defmethod put [ButtonGroup Component]
  [o inner]
  (.add o inner))

(defmethod put [Component String]
  [o text]
  (set o :text text))

(defmethod put [Component Keyword]
  [o text]
  (set o :text text))

(defmethod put [Component Fn]
  [o f]
  (on o :action f))

(defmethod put [JTabbedPane IPersistentVector]
  [o tab-descr]
  (apply add-tab o tab-descr))

(defmethod get [::list :items]
  [o _]
  (let [model (.getModel o)]
    (doall (for [i (range (.getSize model))]
             (.getElementAt model i)))))

(defmethod put [JComboBox Object]
  [o val]
  (swap! disabled-combo-boxes conj o)
  (.addItem o val)
  (swap! disabled-combo-boxes disj o))

(prefer-method put
               [JComboBox Object]
               [Component String])

(prefer-method put
               [JComboBox Object]
               [Component Keyword])

(prefer-method put
               [Component Fn]
               [JComboBox Object])

