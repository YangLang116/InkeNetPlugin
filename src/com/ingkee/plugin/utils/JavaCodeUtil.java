package com.ingkee.plugin.utils;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

/**
 * Created by YangLang on 2017/11/26.
 */
public class JavaCodeUtil {

    public static void format(PsiFile psiFile, PsiClass psiClass){
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(psiFile.getProject());
        styleManager.optimizeImports(psiFile);
        styleManager.shortenClassReferences(psiClass);
    }
}
