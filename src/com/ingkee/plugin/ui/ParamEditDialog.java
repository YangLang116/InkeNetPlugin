package com.ingkee.plugin.ui;

import com.ingkee.plugin.entitys.ParamEntity;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ParamEditDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField paramEdit;
    private JLabel paramName;
    private JLabel paramType;
    private JLabel paramDiscribe;
    private JTextField discribeEdit;
    private JComboBox typeComboBox;

    private ParamEntity paramEntity = new ParamEntity();

    private OnCompleteParam onCompleteParam;

    public void setOnCompleteParam(OnCompleteParam onCompleteParam) {
        this.onCompleteParam = onCompleteParam;
    }

    public ParamEditDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        typeComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Object item = e.getItem();
                paramEntity.paramType = item.toString();
            }
        });

        buttonOK.addActionListener(e -> onOK());

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

        paramEntity.paramName = paramEdit.getText();
        paramEntity.paramDiscribe = paramDiscribe.getText();
        if (onCompleteParam != null) {
            onCompleteParam.onCompleteParam(paramEntity);
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void showParamEdit(OnCompleteParam onCompleteParam) {
        ParamEditDialog dialog = new ParamEditDialog();
        dialog.setOnCompleteParam(onCompleteParam);
        dialog.pack();
        dialog.setVisible(true);
    }

    public interface OnCompleteParam {
        void onCompleteParam(ParamEntity paramEntity);
    }
}
