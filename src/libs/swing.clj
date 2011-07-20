(ns libs.swing
  (:refer-clojure :exclude [get set])
  (:use (clojure.contrib miglayout)
        (libs args
              debug
              fn
              generic
              log
              predicates
              translate)
        (libs.java gen reflect))
  (:require [clojure.java.io :as io]
            [clojure.string  :as str]
            [libs.java.meta  :as m])
  (:import (clojure.lang Keyword
                         Fn
                         IFn
                         IPersistentMap
                         IPersistentVector)
           (java.awt BorderLayout
                     Color
                     Component
                     Container
                     Dimension
                     Event
                     Toolkit
                     Window)
           (java.awt.datatransfer StringSelection)
           (java.awt.event ActionListener
                           KeyAdapter
                           KeyEvent
                           MouseAdapter
                           WindowAdapter)
           (javax.swing AbstractAction
                        AbstractButton
                        BorderFactory
                        Box
                        BoxLayout
                        ButtonGroup
                        DefaultListModel
                        ImageIcon
                        JButton
                        JCheckBox
                        JComboBox
                        JComponent
                        JDialog
                        JFrame
                        JPanel
                        JLabel
                        JList
                        JMenu
                        JMenuItem
                        JOptionPane
                        JPasswordField
                        JPopupMenu
                        JProgressBar
                        JRadioButton
                        JSeparator
                        JScrollPane
                        JSplitPane
                        JTabbedPane
                        JTable
                        JTextArea
                        JTextField
                        KeyStroke
                        ListCellRenderer
                        ListSelectionModel
                        RowSorter$SortKey
                        RowFilter
                        ScrollPaneConstants
                        SortOrder
                        SwingConstants
                        SwingUtilities
                        ToolTipManager
                        UIManager)
           (javax.swing.event ChangeListener
                              DocumentListener
                              ListSelectionListener
                              PopupMenuListener)
           (javax.swing.plaf.basic BasicButtonUI)
           (javax.swing.table AbstractTableModel
                              DefaultTableModel
                              DefaultTableCellRenderer)
           (javax.swing.text JTextComponent)))

(->> '[core
       tables
       tabs
       widgets
       getters
       setters
       putters
       handlers
       dialogs]
     (map #(str "swing/" %))
     (apply load))