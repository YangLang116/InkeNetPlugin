package com.ingkee.plugin.ui;

import com.ingkee.plugin.entitys.ParamEntity;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ParamEditDialog extends JDialog {

    private JPanel contentPane;
    private JLabel paramName;
    private JLabel paramType;
    private JLabel paramDiscribe;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField paramEdit;
    private JTextField discribeEdit;
    private JComboBox typeComboBox;

    private OnCompleteParam onCompleteParam;

    public void setOnCompleteParam(OnCompleteParam onCompleteParam) {
        this.onCompleteParam = onCompleteParam;
    }

    public ParamEditDialog() {
        setContentPane(contentPane);
        setModal(true);
        setAlwaysOnTop(true);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        ParamEntity paramEntity = new ParamEntity();
        paramEntity.paramName = paramEdit.getText();
        paramEntity.paramDiscribe = discribeEdit.getText();
        paramEntity.paramType = typeComboBox.getItemAt(typeComboBox.getSelectedIndex()).toString();
        if (onCompleteParam != null) {
            onCompleteParam.onCompleteParam(paramEntity);
        }
        dispose();
    }

    private void onCancel() {
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
