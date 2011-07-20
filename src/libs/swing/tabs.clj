(in-ns 'libs.swing)

(defn tab-index [tabs key]
  (let [title (translate key)
        res   (find-by #(= title (.getTitleAt tabs %))
                       (range (.getTabCount tabs)))]
    (if res
      res
      (fail "tab doesn't exist:" key))))

(defn add-tab [o & tab-descr]
  (let-args [[icon tooltip args & more] tab-descr]
    (let [[key panel] (if (empty? args) more args)]
      (invoke-and-wait
       (.addTab o (translate key) icon panel tooltip)
       (when-let [render-tab (:tab-renderer (m/meta o))]
         (.setTabComponentAt o (tab-index o key) (render-tab o key)))))))

(defn get-current-tab [tabs]
  (let [index (.getSelectedIndex tabs)]
    (when-not (= -1 index)
      (.getTabComponentAt tabs index))))

(defn get-tab [tabs key]
  (.getTabComponentAt tabs (tab-index tabs key)))

(defn remove-tab [tabs key]
  (invoke-and-wait
   (.removeTabAt tabs (tab-index tabs key))))

(defn select-tab [tabs key]
  (invoke-later
   (.setSelectedIndex tabs (tab-index tabs key))))

