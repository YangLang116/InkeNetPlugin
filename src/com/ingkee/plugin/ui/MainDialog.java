package com.ingkee.plugin.ui;

import com.ingkee.plugin.config.ConfigCenter;
import com.ingkee.plugin.entitys.ParamEntity;
import com.ingkee.plugin.utils.ConvertBridge;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.apache.http.util.TextUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.util.ArrayList;

public class MainDialog extends JFrame implements ParamEditDialog.OnCompleteParam {
    private JPanel contentPane;
    private JTextField edittext_key;
    private JButton btn_cancel;
    private JButton btn_ok;
    private JComboBox spinner_reqmethod;
    private JButton btn_add;
    private JButton btn_reduce;
    private JTextPane edittext_rsp;
    private JList listview_req;
    private JButton formatButton;
    private JButton buttonOK;

    private ArrayList<ParamEntity> mDatas;
    private DefaultListModel<ParamEntity> mModels;
    private PsiClass mClass;
    private PsiFile mFile;

    public MainDialog() {
        initViews();
        initListener();
    }

    private void initViews() {
        setContentPane(contentPane);
        setTitle("ParamMaker");
        setAlwaysOnTop(true);
        setSize(500, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        mDatas = new ArrayList<>();
        mModels = new DefaultListModel<>();
        listview_req.setModel(mModels);
        listview_req.setCellRenderer(new ParamListCellRender());
    }

    private void initListener() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        btn_add.addActionListener(e -> ParamEditDialog.showParamEdit(MainDialog.this::onCompleteParam));
        btn_reduce.addActionListener(e -> reduceParam(listview_req.getSelectedIndex()));
        formatButton.addActionListener(e -> formatRsp(edittext_rsp, edittext_rsp.getText()));
        btn_ok.addActionListener(e -> dealFile());
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void dealFile() {
        if (TextUtils.isEmpty(edittext_key.getText())) {
            return;
        }
        ConfigCenter.KEY = edittext_key.getText();
        ConfigCenter.mParams = mDatas;
        ConfigCenter.RspBody = edittext_rsp.getText();
        ConfigCenter.method = spinner_reqmethod.getSelectedIndex() == 0 ? "get" : "post";
        //生成文件
        new ConvertBridge(mFile, mClass).run();
    }

    @Override
    public void onCompleteParam(ParamEntity paramEntity) {
        if (paramEntity != null && mModels != null) {
            mDatas.add(paramEntity);
            mModels.addElement(paramEntity);
        }
    }

    private void reduceParam(int index) {
        mModels.remove(index);
    }

    private void formatRsp(JTextPane editText, String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        String json = content.trim();
        if (json.startsWith("{")) {
            JSONObject jsonObject = new JSONObject(json);
            String formatJson = jsonObject.toString(4);
            editText.setText(formatJson);
        } else if (json.startsWith("[")) {
            JSONArray jsonArray = new JSONArray(json);
            String formatJson = jsonArray.toString(4);
            editText.setText(formatJson);
        }
    }


    public void onCancel() {
        dispose();
    }

    public void setPsiClass(PsiClass mClass) {
        this.mClass = mClass;
    }

    public void setPsiFile(PsiFile mFile) {
        this.mFile = mFile;
    }

    public static void main(String[] args) {
        MainDialog dialog = new MainDialog();
        dialog.pack();
        dialog.setVisible(true);
    }


    //参数列表JList的适配器对象
    private static class ParamListCellRender implements ListCellRenderer<ParamEntity>, Serializable {

        private static Color M_WHITE = new Color(238, 238, 238);
        private static Color M_GRAP = new Color(60, 63, 65);

        @Override
        public Component getListCellRendererComponent(JList<? extends ParamEntity> list, ParamEntity value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel container = new JPanel();
            container.setBackground(M_GRAP);
            container.setLayout(new GridLayoutManager(1, 3));
            container.setMaximumSize(new Dimension(500, 20));
            if (value == null) {
                return container;
            }
            JLabel nameLabel = new JLabel(value.paramName);
            nameLabel.setFont(list.getFont());
            JLabel typeLabel = new JLabel(value.paramType);
            typeLabel.setFont(list.getFont());
            JLabel desLabel = new JLabel(value.paramDiscribe);
            desLabel.setFont(list.getFont());
            container.add(nameLabel, new GridConstraints(0, 0, 1, 1, 0, 1, 1, 1, null, null, null));
            container.add(typeLabel, new GridConstraints(0, 1, 1, 1, 0, 1, 1, 1, null, null, null));
            container.add(desLabel, new GridConstraints(0, 2, 1, 1, 0, 1, 1, 1, null, null, null));
            if (cellHasFocus) {
                container.setBackground(M_WHITE);
            } else {
                container.setBackground(M_GRAP);
            }
            return container;
        }
    }
}
