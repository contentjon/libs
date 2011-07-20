(in-ns 'libs.swing)

(defn window [& args]
  (config (JFrame.) args))

(deff dialog [parent & other-args]
  (let [o (if parent
            (JDialog. parent)
            (JDialog.))]
    (config o other-args)))

(defn button [& args]
  (config (JButton.) args))

(deff hover-button [hover-icon & args]
  (doto (JButton.)
    (.setUI (BasicButtonUI.))
    (.setContentAreaFilled false)
    (.setFocusable false)
    (.setBorder (BorderFactory/createEtchedBorder))
    (.setBorderPainted false)
    ; TODO: use hover-icon
    (config args)))

(defn check-box [& args]
  (config (JCheckBox.) args))

(defn combo-box [& args]
  (config (JComboBox.) args))

(defn label [& args]
  (config (JLabel.) args))

(defn list-box [& args]
  (config (JList.) args))

(defn menu [& args]
  (config (JMenu.) args))

(defn panel [& args]
  (config (JPanel.) args))

(defn popup-menu [& args]
  (config (JPopupMenu.) args))

(defn progress-bar [& args]
  (config (JProgressBar.) args))

(defn splitter [& args]
  (config (JSplitPane.) args))

(deff tabs [tab-renderer & args]
  (let [tabbed-pane (JTabbedPane.)]
    (when tab-renderer
      (m/assoc-meta! tabbed-pane :tab-renderer tab-renderer))
    (config tabbed-pane args)))

(defn text-area [& args]
  (config (JTextArea. 3 15) args))

(defn text-field [& args]
  (config (JTextField. 15) args))

(defn password-field [& args]
  (config (JPasswordField. 15) args))

(defn layout [elements]
  (apply miglayout (JPanel.) elements))

(defn horizontal [& args]
  (layout (unpack-args args)))

(defn vertical [& args]
  (layout (list* :layout
                 :flowy
                 (unpack-args args))))


(deff rigid-area [args]
  (let [[width height] args]
    (Box/createRigidArea (Dimension. width height))))

(deff grid [columns args]
  (layout (list* :layout [:wrap columns] args)))

(deff form [args]
  (let [form (->> args
                  (partition 2)
                  (mapcat (fn [[k v]]
                            [(label k)
                             v]))
                  (list* :column "[]20[]")
                  (grid :columns 2))]
    (m/assoc-meta! form
                   :type         ::form
                   :form-mapping (apply hash-map args))
    form))

(deff options [format-fn init layout args]
  (let [format-fn     (or format-fn translate)
        layout        (or layout :horizontal)
        current-value (atom init)
        buttons       (for [o args]
                        (conf (JRadioButton.)
                                :text (format-fn o)
                                :selected (= o init)
                                :on {:click #(reset! current-value o)}))
        group         (make ButtonGroup buttons)
        inner-panel   (case layout
                            :horizontal (horizontal (list* :layout "ins 0"
                                                           buttons))
                            :vertical   (vertical buttons))
        outer-panel   (panel inner-panel)]
    (m/assoc-meta! outer-panel
                   :type ::options
                   :value-atom current-value)
    outer-panel))

(deff scrollable [arg & other-args]
  (config (JScrollPane. arg)
          other-args))

(defn menu-item [text handler]
  (make JMenuItem
        :text text
        (as-handler handler)))

(defn icon [path]
  (if-let [res (io/resource path)]
    (ImageIcon. res)
    (warn "can't find icon:" path)))

(deff table [arg
             tool-tip-generator
             & more-args]
  (let [model arg
        table-model (make-table-model model)
        table (if tool-tip-generator
                (proxy [JTable] [table-model]
                  (getToolTipText [evt]
                    (let [[row col] (mouse-evt->row-col this evt)]
                      (when (and row col)
                        (tool-tip-generator table-model row col)))))
                (JTable. table-model))
        {columns     :columns
         sort-keys   :sort
         primary-key :primary} model
        column-keys (map :key columns)
        key->index (into {}
                         (map-indexed (fn [id col]
                                        [(:key col) id])
                                      columns))
        get-row-id  (fn [row-number]
                      (if (sequential? primary-key)
                        (map #(.getValueAt table-model
                                           row-number
                                           (key->index %))
                             primary-key)
                        (.getValueAt table-model
                                     row-number
                                     (key->index primary-key))))]
    (m/assoc-meta! table
                   :keys       column-keys
                   :get-row-id get-row-id)
    (doseq [{:keys [key width renderer caption]} columns]
      (let [col (.getColumn table (str key))]
        (.setHeaderValue col (translate (or caption key)))
        (when renderer
          (.setCellRenderer col renderer))
        (when width
          (.setPreferredWidth col width))))
    (doto table
      (.setShowVerticalLines false)
      (.setShowHorizontalLines false)
      (.setAutoCreateRowSorter true)
      (.setFillsViewportHeight true)
      (.setAutoResizeMode JTable/AUTO_RESIZE_OFF)
      (.setSelectionMode ListSelectionModel/SINGLE_SELECTION))
    (when sort-keys (.. table
                        getRowSorter
                        (setSortKeys (for [[key order] sort-keys]
                                       (RowSorter$SortKey. (key->index key)
                                                           (sort-order order))))))
    (config table more-args)
    table))