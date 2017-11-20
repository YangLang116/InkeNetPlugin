package com.ingkee.plugin.ui;

import javax.swing.*;

public class MainDialog extends JFrame {
    private JPanel contentPane;
    private JTextField textField1;
    private JButton button1;
    private JButton button2;
    private JComboBox comboBox1;
    private JButton button3;
    private JButton button4;
    private JTextPane textPane2;
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
