package com.ingkee.plugin.ui;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ParamEditDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField paramEdit;
    private JSpinner typeSpinner;
    private JLabel paramName;
    private JLabel paramType;
    private JLabel paramDiscribe;
    private JTextField discribeEdit;

    private List<String> dataTypes =new ArrayList<>();

    public ParamEditDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        dataTypes.add("String");
        dataTypes.add("int");
        dataTypes.add("boolean");
        dataTypes.add("long");
        dataTypes.add("object");

        SpinnerListModel spinnerListModel = new SpinnerListModel(dataTypes);
        typeSpinner.setModel(spinnerListModel);
        JSpinner.ListEditor dataTypeEdit = new JSpinner.ListEditor(typeSpinner);
        typeSpinner.setEditor(dataTypeEdit);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        ParamEditDialog dialog = new ParamEditDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
