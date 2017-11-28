package com.ingkee.plugin.utils;

import com.intellij.openapi.ui.MessageType;
import com.intellij.psi.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by YangLang on 2017/11/25.
 */
public class JsonToFieldUtil {
    public static void run(PsiClass psiClass, String jsonString) {
        try {
            if (jsonString.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonString);
                addArrayField(psiClass, jsonArray);
            } else {
                JSONObject jsonObject = new JSONObject(jsonString);
                addObjectField(psiClass, jsonObject);
            }
        } catch (JSONException e) {
            // 忽略
        }
    }

    private static void addObjectField(PsiClass psiClass, JSONObject jsonObject) {
        PsiElementFactory elementFactory = PsiElementFactory.SERVICE.getInstance(psiClass.getProject());
        for (Object key : jsonObject.keySet()) {
            if (key.equals("dm_error") || key.equals("error_msg")) {
                continue;
            }
            Object value = jsonObject.get((String) key);
            PsiType psiType = null;
            PsiField psiField = null;
            if (value instanceof Integer) {
                psiType = elementFactory.createPrimitiveTypeFromText("int");
            } else if (value instanceof Boolean) {
                psiType = elementFactory.createPrimitiveTypeFromText("boolean");
            } else if (value instanceof Float) {
                psiType = elementFactory.createPrimitiveTypeFromText("float");
            } else if (value instanceof Double) {
                psiType = elementFactory.createPrimitiveTypeFromText("double");
            } else if (value instanceof Long) {
                psiType = elementFactory.createPrimitiveTypeFromText("long");
            } else if (value instanceof String) {
                psiType = elementFactory.createTypeFromText("java.lang.String", null);
            } else if (value instanceof JSONArray) {
                ToastUtil.make(psiClass.getProject(), MessageType.INFO, "数组类型暂不支持");
            } else if (value instanceof JSONObject) {
                String className = ((String) key).substring(0, 1).toUpperCase() + ((String) key).substring(1) + "Item";
                PsiClass itemClass = elementFactory.createClass(className);
                itemClass.getModifierList().getFirstChild().delete();
                itemClass.getModifierList().add(elementFactory.createKeyword("public"));
                itemClass.getModifierList().add(elementFactory.createKeyword("static"));
                addObjectField(itemClass, (JSONObject) value);
                psiClass.addBefore(itemClass, psiClass.getRBrace());
                psiField = elementFactory.createField((String) key, elementFactory.createType(itemClass));
            }
            if (psiType != null) {
                psiField = elementFactory.createField((String) key, psiType);
            }
            if (psiField != null) {
                psiField.getModifierList().getFirstChild().delete();
                psiField.getModifierList().add(elementFactory.createKeyword("public"));
                psiClass.addAfter(psiField, psiClass.getFields().length == 0 ? null : psiClass.getFields()[psiClass.getFields().length - 1]);
            }
        }
    }

    private static void addArrayField(PsiClass psiClass, JSONArray jsonArray) {
        PsiElementFactory elementFactory = PsiElementFactory.SERVICE.getInstance(psiClass.getProject());
        ToastUtil.make(psiClass.getProject(), MessageType.INFO, "暂时不支持[{}...]形式！");
    }
}
