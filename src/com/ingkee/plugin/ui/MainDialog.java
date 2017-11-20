package com.ingkee.plugin.ui;

import javax.swing.*;

public class MainDialog extends JFrame {
    private JPanel contentPane;
    private JTextField edittext_key;
    private JButton btn_cancel;
    private JButton btn_ok;
    private JComboBox spinner_reqmethod;
    private JButton btn_add;
    private JButton btn_reduce;
    private JTextPane edittext_rsp;
    private JPanel listview_req;
    private JButton formatButton;
    private JButton buttonOK;

    public MainDialog() {
        initFrameAttr();
    }

    private void initFrameAttr() {
        setContentPane(contentPane);
        setTitle("ParamMaker");
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        MainDialog dialog = new MainDialog();
        dialog.pack();
        dialog.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
