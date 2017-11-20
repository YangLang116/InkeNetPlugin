package com.ingkee.plugin.actions;

import com.ingkee.plugin.ui.MainDialog;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;

/**
 * Created by YangLang on 2017/11/20.
 * 用于收集环境信息数据
 */
public class MainAction extends BaseGenerateAction {

    public MainAction() {
        super(null);
    }

    public MainAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        Project project = e.getData(PlatformDataKeys.PROJECT);
        if (editor == null || project == null) {
            return;
        }
        // 获取当前编辑器中的文件
        PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        PsiClass targetClass = getTargetClass(editor, psiFile); //当前的文件类
        JavaPsiFacade.getElementFactory(project);
        MainDialog mainDialog = new MainDialog();
        mainDialog.setVisible(true);

    }
}
