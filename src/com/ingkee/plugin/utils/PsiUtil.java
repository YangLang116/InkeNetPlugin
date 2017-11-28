package com.ingkee.plugin.utils;

import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiType;

/**
 * Created by YangLang on 2017/11/25.
 */
public class PsiUtil {

    /**
     * Created by YangLang on 2017/11/25.
     * @param elementFactory
     * @param type
     * @return
     */
    public static PsiType getPsiTypeByString(PsiElementFactory elementFactory, String type) {
        PsiType psiType;
        switch (type) {
            case "String":
                psiType = elementFactory.createTypeFromText("java.lang.String", null);
                break;
            case "Object":
                psiType = elementFactory.createTypeFromText("java.lang.Object", null);
                break;
            case "int":
                psiType = elementFactory.createPrimitiveTypeFromText("int");
                break;
            case "long":
                psiType = elementFactory.createPrimitiveTypeFromText("long");
                break;
            case "double":
                psiType = elementFactory.createPrimitiveTypeFromText("double");
                break;
            case "float":
                psiType = elementFactory.createPrimitiveTypeFromText("float");
                break;
            default:
                psiType = elementFactory.createTypeFromText("java.lang.String", null);
                break;
        }
        return psiType;
    }
}
