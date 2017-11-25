package com.ingkee.plugin.utils;

import com.ingkee.plugin.config.ConfigCenter;
import com.ingkee.plugin.entitys.ParamEntity;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.http.util.TextUtils;
import sun.security.krb5.Config;

import java.io.File;

/**
 * Created by YangLang on 2017/11/25.
 */
public class ConvertBridge {

    private final PsiFile mFile;
    private final PsiClass mClass;

    public ConvertBridge(PsiFile mFile, PsiClass mClass) {
        this.mFile = mFile;
        this.mClass = mClass;
    }

    public void run() {
        try {
            WriteCommandAction.runWriteCommandAction(mFile.getProject(), () -> {
                String filePath = createRespFile(); //生成响应文件
                modifyReqFile(filePath); //修改请求文件
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void modifyReqFile(String rspfilePath) {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(mFile.getProject());
        addKeyFiledToClass(elementFactory);
        String className = addReqInnerToClass(elementFactory);
        addReqMethodToClass(elementFactory, className, rspfilePath);
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mFile.getProject());
        styleManager.optimizeImports(mFile);
        styleManager.shortenClassReferences(mClass);
    }

    private void addKeyFiledToClass(PsiElementFactory elementFactory) {
        PsiType psiFieldType = elementFactory.createType(JavaPsiFacade.getInstance(mFile.getProject())
                .findClass("java.lang.String", GlobalSearchScope.allScope(mFile.getProject())));
        PsiField keyField = elementFactory.createField(ConfigCenter.KEY.toUpperCase(), psiFieldType);
        keyField.getModifierList().add(elementFactory.createKeyword("static"));
        keyField.getModifierList().add(elementFactory.createKeyword("final"));
        keyField.setInitializer(elementFactory.createExpressionFromText("\"" + ConfigCenter.KEY + "\"", keyField));
        mClass.add(keyField);
    }

    private String addReqInnerToClass(PsiElementFactory elementFactory) {
        //创建innerclass
        String[] declareValues = ConfigCenter.KEY.toLowerCase().split("_");
        StringBuffer classNameBuf = new StringBuffer();
        for (String declare : declareValues) {
            classNameBuf.append(declare.substring(0, 1).toUpperCase()).append(declare.substring(1));
        }
        classNameBuf.append("Param");
        PsiClass reqClass = elementFactory.createClass(classNameBuf.toString());
        reqClass.getModifierList().getFirstChild().delete();
        reqClass.getModifierList().add(elementFactory.createKeyword("private"));
        reqClass.getModifierList().add(elementFactory.createKeyword("static"));
        reqClass.getExtendsList().add(getDefaultNetWorkReqParam(elementFactory));
        PsiElement innerClass = mClass.add(reqClass);
        //添加注解
        PsiAnnotation annotation = elementFactory.createAnnotationFromText(String.format("@com.meelive.ingkee.network.builder.URLBuilder.Path(urlKey = %s, builder = com.meelive.ingkee.mechanism.http.build.InkeDefaultURLBuilder.class)", ConfigCenter.KEY.toUpperCase()), mClass);
        mClass.addBefore(annotation, innerClass);
        //添加内部参数
        for (ParamEntity paramEntity : ConfigCenter.mParams) {
            if (paramEntity == null) {
                continue;
            }
            StringBuffer innerClassFieldSb = new StringBuffer();
            innerClassFieldSb.append("public ").append(paramEntity.paramType).append(" ").append(paramEntity.paramName).append(" ; //").append(paramEntity.paramDiscribe);
            innerClass.add(elementFactory.createFieldFromText(innerClassFieldSb.toString(), innerClass));
        }
        return classNameBuf.toString();
    }

    private void addReqMethodToClass(PsiElementFactory elementFactory, String repClass, String rspClass) {
        StringBuffer methodBuf = new StringBuffer("req");
        for (String declare : ConfigCenter.KEY.toLowerCase().split("_")) {
            methodBuf.append(declare.substring(0, 1).toUpperCase()).append(declare.substring(1));
        }
        String methodTemplate = getNetWorkMethodTemplate();
        String method = methodTemplate.replaceAll("\\{rspEntity\\}", rspClass)
                .replaceAll("\\{reqMethod\\}", methodBuf.toString())
                .replaceAll("\\{reqEntity\\}", repClass)
                .replaceAll("\\{NetMethod\\}", ConfigCenter.method);
        PsiMethod psiMethod = elementFactory.createMethodFromText(method, mClass);
        mClass.add(psiMethod);
    }

    private String createRespFile() {
        if (!TextUtils.isEmpty(ConfigCenter.RspBody.trim())) {
            String[] declareValues = ConfigCenter.KEY.toLowerCase().split("_");
            StringBuffer classNameBuf = new StringBuffer();
            for (String declare : declareValues) {
                classNameBuf.append(declare.substring(0, 1).toUpperCase()).append(declare.substring(1));
            }
            classNameBuf.append("Entity");

            String fileDirectoryPath = mFile.getVirtualFile().getParent().getPath();
            String EntityDirectotyPath = fileDirectoryPath + "/Entity";
            File EntiryDirectory = new File(EntityDirectotyPath);
            if (!(EntiryDirectory.exists() && EntiryDirectory.isDirectory())) { //创建目录
                EntiryDirectory.mkdir();
            }

            PsiClass respClass = null;
            PsiDirectory entityPsiDirectory = null;
            for (PsiDirectory f : mFile.getParent().getSubdirectories()) {
                if (f.getName().equals("Entity")) {
                    entityPsiDirectory = f;
                    PsiFile file = f.findFile(classNameBuf.toString());
                    if (file != null) {
                        respClass = ((PsiJavaFile) file).getClasses()[0];
                    } else {
                        respClass = JavaDirectoryService.getInstance().createClass(entityPsiDirectory, classNameBuf.toString());
                    }
                    break;
                }
            }
            if (respClass == null) {
                return "";
            }
            // 根据输入的Json创建类成员
            JsonToFieldUtil.run(respClass, ConfigCenter.RspBody);
            return respClass.getQualifiedName();
        } else {
            return "com.meelive.ingkee.common.plugin.model.BaseModel";
        }
    }

    private PsiJavaCodeReferenceElement getDefaultNetWorkReqParam(PsiElementFactory elementFactory) {
        PsiClass extendsClass = JavaPsiFacade.getInstance(
                mFile.getProject()).findClass("com.meelive.ingkee.network.http.param.ParamEntity",
                GlobalSearchScope.allScope(mFile.getProject()));
        return elementFactory.createClassReferenceElement(extendsClass);
    }

    //找了一下午的坑，方法内容前面不要带上 空格
    private String getNetWorkMethodTemplate() {
        StringBuffer methodSb = new StringBuffer();
        methodSb.append("public static rx.Observable<com.meelive.ingkee.network.http.responser.RspInkeDefault<{rspEntity}>> {reqMethod}() {")
                .append("{reqEntity} param = new {reqEntity}; ")
                .append("com.meelive.ingkee.network.http.responser.RspInkeDefault<{rspEntity}> inkeDefault = new com.meelive.ingkee.network.http.responser.RspInkeDefault<>({rspEntity}.class); ")
                .append("return com.meelive.ingkee.network.http.HttpWorker.getInstace(com.meelive.ingkee.base.utils.GlobalContext.getAppContext()).{NetMethod}(param, inkeDefault, null, com.meelive.ingkee.network.cache.CacheType.NO_CACHE);")
                .append(" }");
        return methodSb.toString();
    }
}
