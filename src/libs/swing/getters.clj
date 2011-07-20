(in-ns 'libs.swing)

(defmethod get [JTextComponent :value]
  [o _]
  (.getText o))

(defmethod get [JCheckBox :value]
  [o _]
  (.isSelected o val))

(defmethod get [Object :enabled?]
  [_ _]
  false)

(defmethod get [nil :enabled?]
  [_ _]
  false)

(defmethod get [JComboBox :value]
  [o _]
  (.getSelectedItem o))

(defmethod get [JList :value]
  [o _]
  (.getSelectedValue o))

(defmethod get [JList :values]
  [o _]
  (.getSelectedValues o))

(defmethod get [::options :value]
  [o _]
  @(:value-atom (m/meta o)))

(defmethod get1 ::form
  [o key]
  (-> o
      m/meta
      :form-mapping
      key
      (get :value)))

(defmethod get [::form :value]
  [o _]
  (let [m (:form-mapping (m/meta o))]
    (zipmap (keys m)
            (map #(get % :value)
                 (vals m)))))


