(in-ns 'libs.swing)

(def sort-order
  {:asc  SortOrder/ASCENDING
   :desc SortOrder/DESCENDING})

(defn add-table-row [table record]
  (invoke-later
   (let [keys (:keys (m/meta table))
         ;; records don't implement IFn, so no (map record keys)
         row   (to-array (map #(% record) keys))]
     (.. table getModel (addRow row)))))

(defn add-table-rows [table records]
  (invoke-later
   (let [keys  (:keys (m/meta table))
         model (.getModel table)]
     (doseq [record records]
       ;; records don't implement IFn, so no (map record keys)
       (.addRow model (to-array (map #(% record) keys)))))))

(defn clear-table [table]
  (invoke-later
   (let [model (.getModel table)]
     (while (> (.getRowCount model) 0)
       (.removeRow model 0)))))

(defn make-table-cell-renderer [render]
  (proxy [DefaultTableCellRenderer] []
    (setValue [val]
      (render this val))))

(defn make-table-model [{:keys [columns]}]
  (let [classes     (vec (map :class columns))
        table-model (proxy [DefaultTableModel] []
                      (isCellEditable [row col]
                        false)
                      (getColumnClass [col]
                        (classes col)))]
    (doseq [col columns]
      (->> col
           :key
           str
           (.addColumn table-model)))
    table-model))

(defn mouse-evt->row-col [table evt]
  (let [pt (.getPoint evt)
        row (.rowAtPoint table pt)
        col (.columnAtPoint table pt)]
    (when-not (or (= -1 row)
                  (= -1 col))
      [(.convertRowIndexToModel    table row)
       (.convertColumnIndexToModel table col)])))

(defn table-row-id [table row]
  ((-> table m/meta :get-row-id)
   row))

(defn remove-table-row [table id]
  (invoke-later
   (let [model      (.getModel table)
         row-number (some #(when (= (table-row-id table %)
                                    id)
                             %)
                          (range (.getRowCount model)))]
     (.removeRow model row-number))))

(defn set-regex-filter [table regex]
  (invoke-later
   (.. table
       getRowSorter
       (setRowFilter (RowFilter/regexFilter regex 0)))))

