(in-ns 'libs.swing)

(defmethod set [Component :border]
  [o _ borders]
  (let [[top left bottom right] (if (number? borders)
                                  (repeat borders)
                                  (map borders [:top :left :right :bottom]))]
    (.setBorder o
                (BorderFactory/createEmptyBorder top left bottom right))))

(defmethod set [Component :line-border]
  [o _ color]
  (.setBorder o (BorderFactory/createLineBorder (as-color color))))

(defmethod set [Component :text]
  [o _ text]
  (.setText o (translate text))
  (when (translatable? (descriptive text))
    (set o :tooltip (descriptive text))))

(defmethod set [Component :title]
  [o _ text]
  (.setTitle o (translate text)))

(defmethod set [Component :titled-border]
  [o _ text]
  (.setBorder o (BorderFactory/createTitledBorder (translate text))))

(defmethod set [Component :tooltip]
  [o _ tooltip]
  (.setToolTipText o (translate tooltip)))

(defmethod set [JCheckBox :value]
  [o _ val]
  (.setSelected o val))

(defmethod set [JSplitPane :bottom]
  [o _ elt]
  (.setBottomComponent o elt))

(defmethod set [JSplitPane :left]
  [o _ elt]
  (.setLeftComponent o elt))

(defmethod set [JSplitPane :orientation]
  [o _ orientation]
  (.setOrientation o (case orientation
                           :vertical   JSplitPane/VERTICAL_SPLIT
                           :horizontal JSplitPane/HORIZONTAL_SPLIT)))

(defmethod set [JSplitPane :right]
  [o _ elt]
  (.setRightComponent o elt))

(defmethod set [JSplitPane :top]
  [o _ elt]
  (.setTopComponent o elt))

(defmethod set [JTabbedPane :tab-placement]
  [o _ pos]
  (.setTabPlacement o (case pos
                            :top    SwingConstants/TOP
                            :left   SwingConstants/LEFT
                            :right  SwingConstants/RIGHT
                            :bottom SwingConstants/BOTTOM)))

(defmethod set [JTextComponent :value]
  [o _ val]
  (.setText o (str val)))

(defmethod set [Window :text]
  [o _ text]
  (.setTitle o (translate text)))


(defmethod set [::menu :args]
  [o _ entries]
  (doseq [e (->> entries
                 (partition 2)
                 (map #(apply menu-item %)))]
    (.add o e)))

(defmethod set [Component :popup-menu]
  [o _ menu]
  (on o :right-click (fn [e] (.show menu
                                   (.getComponent e)
                                   (.getX e)
                                   (.getY e)))))

(defmethod set [Component :background]
  [o _ color]
  (when color
    (.setBackground o (as-color color))))

(defmethod set [Component :foreground]
  [o _ color]
  (when color
    (.setForeground o (as-color color))))


(defmethod set [Component :enabled?]
  [o _ enabled?]
  (.setEnabled o (boolean enabled?)))

(defmethod set [Component :line-wrap]
  [o _ wrap]
  (.setLineWrap o (boolean wrap))
  (.setWrapStyleWord o (= :words wrap)))

(defmethod set [Component :selected?]
  [o _ selected?]
  (.setSelected o (boolean selected?)))

(defmethod set [JComboBox :value]
  [o _ value]
  (swap! disabled-combo-boxes conj o)
  (.setSelectedItem o value)
  (swap! disabled-combo-boxes disj o))

(defmethod set [JList :value]
  [o _ value]
  (.setSelectedValue o value true))

(defmethod set [JComboBox :items]
  [o _ items]
  (swap! disabled-combo-boxes conj o)
  (.removeAllItems o)
  (doseq [i items]
    (.addItem o i))
  (.setSelectedIndex o 0)
  (swap! disabled-combo-boxes disj o))

(defmethod set [JList :items]
  [o _ items]
  (.setListData o (to-array items)))

(defmethod set [Window :open]
  [o _ open?]
  (when open?
    (.pack o)
    (.setVisible o true)))

(defmethod set [Window :close]
  [o _ close?]
  (when close?
    (.setVisible o false)
    (.dispose o)))

(defmethod set [Component :min-size]
  [o _ sz]
  (.setMinimumSize o (as-dimension sz)))

(defmethod set [Component :pref-size]
  [o _ sz]
  (.setPreferredSize o (as-dimension sz)))

(defmethod set [Component :max-size]
  [o _ sz]
  (.setMaximumSize o (as-dimension sz)))

(defmethod set [Component :size]
  [o _ sz]
  (set-all o {:min-size  sz
              :pref-size sz
              :max-size  sz}))

(defmethod set [JScrollPane :horizontal]
  [o _ policy]
  (let [pol (case policy
                  :always    ScrollPaneConstants/HORIZONTAL_SCROLLBAR_ALWAYS
                  :never     ScrollPaneConstants/HORIZONTAL_SCROLLBAR_NEVER
                  :as-needed ScrollPaneConstants/HORIZONTAL_SCROLLBAR_AS_NEEDED)]
    (.setHorizontalScrollBarPolicy o pol)))

(defmethod set [JScrollPane :vertical]
  [o _ policy]
  (let [pol (case policy
                  :always    ScrollPaneConstants/VERTICAL_SCROLLBAR_ALWAYS
                  :never     ScrollPaneConstants/VERTICAL_SCROLLBAR_NEVER
                  :as-needed ScrollPaneConstants/VERTICAL_SCROLLBAR_AS_NEEDED)]
    (.setVerticalScrollBarPolicy o pol)))

(defmethod set [JTable :size]
  [o _ sz]
  (.setPreferredScrollableViewportSize o (as-dimension sz)))

