(in-ns 'libs.swing)

(defn make-enable-renderer
  "A list cell renderer that greys out items not enabled.
   :separator is also accepted instead of an item."
  []
  (let [lbl (label :text ""
                   :opaque true
                   :border 1)]
    (reify ListCellRenderer
           (getListCellRendererComponent
            [_ list value index selected? focus?]
            (if (= value :separator)
              (JSeparator. JSeparator/HORIZONTAL)
              (let [[bg fg] (if-not (get value :enabled?)
                              [(.getBackground list)
                               (get-ui-color "Label.disabledForeground")]
                              (if selected?
                                [(.getSelectionBackground list)
                                 (.getSelectionForeground list)]
                                [(.getBackground list)
                                 (.getForeground list)]))]
                (conf lbl
                      :background bg
                      :foreground fg
                      :font (get list :font)
                      :text value)))))))

(defn make-icon-renderer
  "A list cell renderer that also shows icons"
  [value->icon]
  (let [lbl (label ""
                   :opaque true
                   :border 1)]
    (reify ListCellRenderer
      (getListCellRendererComponent
        [_ list val index selected? focus?]
        ;; don't use conf which does it in another thread and too late
        (set-all lbl
                 [:background (if selected?
                                (.getSelectionBackground list)
                                (.getBackground list))
                  :foreground (if selected?
                                (.getSelectionForeground list)
                                (.getForeground list))
                  ;; :font (.getFont list)
                  :icon (value->icon val)
                  :text val])))))

