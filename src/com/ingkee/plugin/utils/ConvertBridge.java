package com.ingkee.plugin.utils;

import com.ingkee.plugin.config.ConfigCenter;
import com.ingkee.plugin.entitys.ParamEntity;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.http.util.TextUtils;

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
                PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(mFile.getProject());
                String resClassPath = createRespFile(elementFactory); //生成响应文件
                modifyReqFile(elementFactory, resClassPath); //修改请求文件
                openJaveEntityFile(resClassPath);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openJaveEntityFile(String resClassPath) {
        if (TextUtils.isEmpty(resClassPath)) {
            return;
        }
        String classFile = resClassPath.substring(resClassPath.lastIndexOf(".") + 1) + ".java";
        Project project = mFile.getProject();
        PsiDirectory entityDir = mFile.getParent().findSubdirectory("entity");
        if (entityDir == null) {
            return;
        }
        PsiFile javaBean = entityDir.findFile(classFile);
        if (javaBean == null) {
            return;
        }
        FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, javaBean.getVirtualFile()), true);
    }

    private void modifyReqFile(PsiElementFactory elementFactory, String rspfilePath) {
        addKeyFiledToClass(elementFactory);
        String className = addReqInnerToClass(elementFactory);
        addReqMethodToClass(elementFactory, className, rspfilePath);
        JavaCodeUtil.format(mFile, mClass);
    }

    private void addKeyFiledToClass(PsiElementFactory elementFactory) {
        StringBuffer keyFieldBuf = new StringBuffer();
        keyFieldBuf.append("private static final java.lang.String ")
                .append(ConfigCenter.KEY.toUpperCase())
                .append(" = ")
                .append("\"").append(ConfigCenter.KEY).append("\"")
                .append(";");
        PsiField keyField = elementFactory.createFieldFromText(keyFieldBuf.toString(), mClass);
        mClass.add(keyField);
    }

    private String addReqInnerToClass(PsiElementFactory elementFactory) {
        //创建innerclass
        String className = getClassNameByKey() + "Param";
        PsiClass reqClass = elementFactory.createClass(className);
        reqClass.getModifierList().getFirstChild().delete();
        reqClass.getModifierList().add(elementFactory.createKeyword("private"));
        reqClass.getModifierList().add(elementFactory.createKeyword("static"));
        reqClass.getExtendsList().add(getReferenceClass(elementFactory, "com.meelive.ingkee.network.http.param.ParamEntity"));
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
            innerClassFieldSb.append("public ").append(paramEntity.paramType).append(" ").append(paramEntity.paramName).append(" ; ");
            if (!TextUtils.isEmpty(paramEntity.paramDiscribe)) {
                innerClassFieldSb.append("//").append(paramEntity.paramDiscribe);
            }
            innerClass.add(elementFactory.createFieldFromText(innerClassFieldSb.toString(), innerClass));
        }
        return className;
    }

    private void addReqMethodToClass(PsiElementFactory elementFactory, String repClass, String rspClass) {
        String methodName = "req" + getClassNameByKey();
        String methodTemplate = getNetWorkMethodTemplate();

        StringBuffer methodBodyBuf = new StringBuffer();
        for (ParamEntity paramEntity : ConfigCenter.mParams) {
            methodBodyBuf.append("param.").append(paramEntity.paramName).append("=").append(paramEntity.paramName).append(";\n");
        }
        String method = methodTemplate.replaceAll("\\{rspEntity\\}", TextUtils.isEmpty(rspClass) ? "?" : rspClass)
                .replaceAll("\\{reqMethod\\}", methodName)
                .replaceAll("\\{reqEntity\\}", repClass)
                .replaceAll("\\{body\\}", methodBodyBuf.toString())
                .replaceAll("\\{NetMethod\\}", ConfigCenter.method);
        PsiMethod psiMethod = elementFactory.createMethodFromText(method, mClass);
        //添加参数赋值语句
        for (ParamEntity paramEntity : ConfigCenter.mParams) {
            psiMethod.getParameterList().add(elementFactory.createParameter(paramEntity.paramName, PsiUtil.getPsiTypeByString(elementFactory, paramEntity.paramType)));
        }
        mClass.add(psiMethod);
    }

    private String createRespFile(PsiElementFactory elementFactory) {
        String BaseModelStr = "com.meelive.ingkee.mechanism.http.BaseModel";
        if (!TextUtils.isEmpty(ConfigCenter.RspBody)) { //如果响应体不为空
            String className = getClassNameByKey() + "Entity";
            PsiDirectory entityDirectory = mFile.getParent().findSubdirectory("entity");
            if (entityDirectory == null) {
                entityDirectory = mFile.getParent().createSubdirectory("entity");
            }
            if (entityDirectory == null) {
                return null;
            }
            PsiClass respClass;
            PsiFile file = entityDirectory.findFile(className + ".java");
            if (file != null) {
                respClass = ((PsiJavaFile) file).getClasses()[0];
            } else {
                respClass = JavaDirectoryService.getInstance().createClass(entityDirectory, className);
                respClass.getExtendsList().add(getReferenceClass(elementFactory, BaseModelStr)); //确定继承关系
                respClass.getModifierList().add(elementFactory.createKeyword("public"));//修改权限
            }
            if (respClass == null) {
                return null;
            }
            // 根据输入的Json创建类成员
            JsonToFieldUtil.run(respClass, ConfigCenter.RspBody);
            return respClass.getQualifiedName().trim();
        } else {
            return BaseModelStr;
        }
    }

    private PsiJavaCodeReferenceElement getReferenceClass(PsiElementFactory elementFactory, String qualifiedName) {
        PsiClass extendsClass = JavaPsiFacade.getInstance(
                mFile.getProject()).findClass(qualifiedName, GlobalSearchScope.allScope(mFile.getProject()));
        return elementFactory.createClassReferenceElement(extendsClass);
    }

    //找了一下午的坑，方法内容前面不要带上 空格
    private String getNetWorkMethodTemplate() {
        StringBuffer methodSb = new StringBuffer();
        methodSb.append("public static rx.Observable<com.meelive.ingkee.network.http.responser.RspInkeDefault<{rspEntity}>> {reqMethod}() { \n")
                .append("{reqEntity} param = new {reqEntity}(); \n")
                .append("{body}")
                .append("com.meelive.ingkee.network.http.responser.RspInkeDefault<{rspEntity}> inkeDefault = new com.meelive.ingkee.network.http.responser.RspInkeDefault<>({rspEntity}.class); \n")
                .append("return com.meelive.ingkee.network.http.HttpWorker.getInstace(com.meelive.ingkee.base.utils.GlobalContext.getAppContext()).{NetMethod}(param, inkeDefault, null, com.meelive.ingkee.network.cache.CacheType.NO_CACHE); \n")
                .append("}");
        return methodSb.toString();
    }

    //获取首字母大写的串
    private String getClassNameByKey() {
        StringBuffer classNameBuf = new StringBuffer();
        String[] declareValues = ConfigCenter.KEY.toLowerCase().split("_");
        for (String declare : declareValues) {
            classNameBuf.append(declare.substring(0, 1).toUpperCase()).append(declare.substring(1));
        }
        return classNameBuf.toString();
    }
}
