package com.ingkee.plugin.ui;

import com.ingkee.plugin.entitys.ParamEntity;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainDialog extends JFrame implements ParamEditDialog.OnCompleteParam {
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
        setSize(500, 500);
        setLocationRelativeTo(null);

        btn_add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ParamEditDialog.showParamEdit(MainDialog.this::onCompleteParam);
            }
        });
    }

    public static void main(String[] args) {
        MainDialog dialog = new MainDialog();
        dialog.pack();
        dialog.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    /**
     * 获取到了param
     *
     * @param paramEntity
     */
    @Override
    public void onCompleteParam(ParamEntity paramEntity) {
        System.out.println(paramEntity.toString());
    }
}
