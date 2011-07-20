(in-ns 'libs.swing)

(defn add-action-handler [o handler]
  (.addActionListener o
                      (reify ActionListener
                             (actionPerformed
                              [_ evt]
                              (handler evt)))))

(defn add-click-handler [o handler]
  (with-handlers [handler]
    (.addMouseListener
     o
     (proxy [MouseAdapter] []
       (mouseClicked [evt]
         ;; checking for one click only doesn't work
         (handler evt))))))

(defn add-double-click-handler [o handler]
  (with-handlers [handler]
    (.addMouseListener
     o
     (proxy [MouseAdapter] []
       (mouseClicked [evt]
         (when (= (.getClickCount evt)
                  2)
           (handler evt)))))))

(defn as-action [f]
  (proxy [AbstractAction] []
    (actionPerformed [e] (f e))))

(defn add-key-handler [win key handler]
  (with-handlers [handler]
    (let [key-id     (->> (name key)
                          str/upper-case
                          (str "VK_")
                          (static-field KeyEvent))
          key-stroke (KeyStroke/getKeyStroke key-id 0)
          root (.getRootPane win)]
      (.. root
          (getInputMap JComponent/WHEN_IN_FOCUSED_WINDOW)
          (put key-stroke (name key)))
      (.. root
          (getActionMap)
          (put (name key) (as-action handler))))))

(defn add-right-click-handler [o handler]
  (with-handlers [handler]
    (.addMouseListener
     o
     (proxy [MouseAdapter] []
       (mousePressed [evt]
         (when (.isPopupTrigger evt)
           (handler evt)))
       (mouseReleased [evt]
         (when (.isPopupTrigger evt)
           (handler evt)))))))

(defn table-cell-handler [table handler]
  (fn [evt]
    (when-let [cell (mouse-evt->row-col table evt)]
      (handler cell))))

(defmethod on [Component :action]
  [o _ handler]
  (add-action-handler o (as-handler handler)))

(defmethod on [Component :change]
  [o _ handler]
  (with-handlers [handler]
    (.addChangeListener o (reify ChangeListener
                            (stateChanged [_ evt] (handler evt))))))

(defmethod on [Component :click]
  [o _ handler]
  (add-click-handler o handler))

(defmethod on [Component :double-click]
  [o _ handler]
  (add-double-click-handler o handler))

(defmethod on [Component :key]
  [o _ handlers]
  (doseq [[key handler] handlers]
    (add-key-handler o key handler)))

(defmethod on [Component :popup]
  [o _  {:keys [hide show cancel]}]
  (with-handlers [hide show cancel]
    (.addPopupMenuListener
     o
     (reify PopupMenuListener
            (popupMenuCanceled            [_ evt] (when cancel (cancel evt)))
            (popupMenuWillBecomeVisible   [_ evt] (when show   (show   evt)))
            (popupMenuWillBecomeInvisible [_ evt] (when hide   (hide   evt)))))))


(defmethod on [Component :right-click]
  [o _ handler]
  (add-right-click-handler o handler))

(defmethod on [JComboBox :action]
  [o _ handler]
  (with-handlers [handler]
    (add-action-handler
     o
     #(when-not (@disabled-combo-boxes o)
        (handler %)))))

(defmethod on [JList :change]
  [o _ handler]
  (with-handlers [handler]
    (.addListSelectionListener
     o
     (reify ListSelectionListener
            (valueChanged [_ evt]
                          (handler evt))))))

(defmethod on [JTable :click]
  [o _ handler]
  (add-click-handler o (table-cell-handler o handler)))

(defmethod on [JTable :double-click]
  [o _ handler]
  (add-double-click-handler o (table-cell-handler o handler)))

(defmethod on [JTable :right-click]
  [o _ handler]
  (add-right-click-handler o (table-cell-handler o handler)))

(defmethod on [JTextComponent :change]
  [o _ handler]
  (with-handlers [handler]
    (.. o
        getDocument
        (addDocumentListener
         (reify DocumentListener
                (changedUpdate
                 [_ evt]
                 (handler evt))
                (insertUpdate
                 [_ evt]
                 (handler evt))
                (removeUpdate
                 [_ evt]
                 (handler evt)))))))

(defmethod on [Window :closing]
  [o _ handler]
  (with-handlers [handler]
    (.addWindowListener o (proxy [WindowAdapter] []
                            (windowClosing [evt] (handler evt))))))