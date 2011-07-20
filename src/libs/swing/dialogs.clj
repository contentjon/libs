(in-ns 'libs.swing)

(deff ask-user [title arg]
  (let [title  (or title :question)]
    (= JOptionPane/YES_OPTION
        (JOptionPane/showConfirmDialog
         nil
         (translate arg)
         (translate title)
         JOptionPane/YES_NO_OPTION
         JOptionPane/QUESTION_MESSAGE))))

(deff message [icon title type arg]
  (let [type     (or type :plain)
        msg-type (case type
                       :error    JOptionPane/ERROR_MESSAGE
                       :info     JOptionPane/INFORMATION_MESSAGE
                       :warning  JOptionPane/WARNING_MESSAGE
                       :question JOptionPane/QUESTION_MESSAGE
                       :plain    JOptionPane/PLAIN_MESSAGE)]
    (JOptionPane/showMessageDialog
     nil
     (translate arg)
     (translate title)
     msg-type
     icon)))

(deff ok-cancel-dialog [args on-ok open validator & other-args]
  (let [d             (apply dialog other-args)
        validator     (or validator (fn []))
        on-ok         (or on-ok (fn []))
        ok-button     (button #(if-let [error (validator)]
                                 (message :type :error
                                          error)
                                 (do (close d)
                                     (on-ok)))
                              :ok)
        cancel-button (button #(close d)
                              :cancel)]
    (invoke-later
     (.. d getRootPane (setDefaultButton ok-button))
     (conf d
           :on {:key {:escape #(doto cancel-button
                                 .requestFocusInWindow
                                 (.doClick 100))}}
           (vertical (concat args
                             [(horizontal [ok-button [:tag :ok]
                                           cancel-button [:tag :cancel]])
                              :align :center]))
           :open open))
    d))

