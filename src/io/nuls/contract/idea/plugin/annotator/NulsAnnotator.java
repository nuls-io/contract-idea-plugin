package io.nuls.contract.idea.plugin.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.*;
import io.nuls.contract.idea.plugin.model.ConfigStorage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NulsAnnotator implements Annotator {

    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // 文件类型过滤（只检查JAVA文件）
        if (!element.getContainingFile().getVirtualFile().getName().toLowerCase().endsWith(".java")) {
            return;
        }
        // 设定过滤（项目设定需要检查才做检查）
        if (!ConfigStorage.getInstance(element.getProject()).isNulsSyntaxCheck()) {
            return;
        }

        // 关键字校验
        if (element instanceof PsiKeyword) {
            List<String> unsupportedKeywords = Arrays.asList("native", "strictfp", "synchronized", "transient", "volatile",
                    /*"try", "catch", "finally", "throw", "throws",*/
                    "enum", "assert");
            String elementText = ((PsiKeyword) element).getText();
            if (unsupportedKeywords.contains(elementText)) {
                holder.createErrorAnnotation(element, "Unsupported keyword \"" + elementText + "\"!");
            }
            return;
        }

        if (element instanceof PsiImportStatement || element instanceof PsiDeclarationStatement) {
            // 可使用的类型
            List<String> supportedTypes = Arrays.asList(
                    "io.nuls.contract.sdk.Address",
                    "io.nuls.contract.sdk.Block",
                    "io.nuls.contract.sdk.BlockHeader",
                    "io.nuls.contract.sdk.Contract",
                    "io.nuls.contract.sdk.Event",
                    "io.nuls.contract.sdk.Msg",
                    "io.nuls.contract.sdk.Utils",
                    "io.nuls.contract.sdk.annotation.View",
                    "io.nuls.contract.sdk.annotation.Required",
                    "io.nuls.contract.sdk.annotation.Payable",
                    "java.lang.Boolean",
                    "java.lang.Byte",
                    "java.lang.Short",
                    "java.lang.Character",
                    "java.lang.Integer",
                    "java.lang.Long",
                    "java.lang.Float",
                    "java.lang.Double",
                    "java.lang.String",
                    "java.lang.StringBuilder",
                    "java.lang.Exception",
                    "java.lang.RuntimeException",
                    "java.math.BigInteger",
                    "java.util.List",
                    "java.util.ArrayList",
                    "java.util.Map",
                    "java.util.HashMap",
                    "java.util.Set",
                    "java.util.HashSet");
            // 自定义类型
            List<String> supportClzs = new ArrayList<>();
            List<String> internalClzs = null;
            if (internalClzs == null) {
                String thisFileFullName = element.getContainingFile().getVirtualFile().getPath();
                int pos;
                List<File> files = new ArrayList<>();
                if (thisFileFullName.indexOf("src/test/java") > 0) {
                    pos = thisFileFullName.indexOf("src/test/java");
                    String testJavaBaseUrl = thisFileFullName.substring(0, pos + 13);
                    files.addAll(getJavaFiles(testJavaBaseUrl));
                    String mainJavaBaseUrl = thisFileFullName.substring(0, pos) + "src/main/java";
                    files.addAll(getJavaFiles(mainJavaBaseUrl));
                } else if (thisFileFullName.indexOf("src/main/java") > 0) {
                    pos = thisFileFullName.indexOf("src/main/java");
                    String baseUrl = thisFileFullName.substring(0, pos + 13);
                    files.addAll(getJavaFiles(baseUrl));
                } else if (thisFileFullName.indexOf("src") > 0) {
                    pos = thisFileFullName.indexOf("src");
                    String baseDir = thisFileFullName.substring(0, pos + 3);
                    files.addAll(getJavaFiles(baseDir));
                }
                internalClzs = listClzFullName(files);
            }
            supportClzs.addAll(internalClzs);       // 自定义的类型
            supportClzs.addAll(supportedTypes);     // 支持的类型

            // import校验
            if (element instanceof PsiImportStatement) {
                String importText = ((PsiImportStatement) element).getText();
                importText = importText.replace("import", "").replace(";", "");
                importText = importText.trim();
                if (!supportClzs.contains(importText)) {
                    holder.createErrorAnnotation(element, "Unsupported import \"" + importText + "\"!");
                }
                return;
            }

            // 声明类型校验
            if (element instanceof PsiDeclarationStatement) {
                PsiReference psiReference = ((PsiDeclarationStatement) element).findReferenceAt(0);
                if (psiReference != null) {
                    String typeText = psiReference.getCanonicalText();
                    if (typeText.contains("<")) {
                        typeText = typeText.substring(0, typeText.indexOf("<"));
                    }
                    if (typeText.contains("[")) {
                        typeText = typeText.substring(0, typeText.indexOf("["));
                    }
                    if (!supportClzs.contains(typeText)) {
                        holder.createErrorAnnotation(element, "Unsupported type \"" + typeText + "\"!");
                    }
                }
                return;
            }
        }
    }

    /**
     * 递归获取目录下所有JAVA文件
     *
     * @param path
     * @return
     */
    private List<File> getJavaFiles(String path) {
        List<File> fileList = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            return fileList;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    fileList.addAll(getJavaFiles(files[i].getPath()));
                } else if (files[i].isFile()) {
                    if (files[i].getName().toLowerCase().endsWith(".java")) {
                        fileList.add(files[i]);
                    }
                }

            }
        } else {
            if (file.getName().toLowerCase().endsWith(".java")) {
                fileList.add(file);
            }
        }
        return fileList;
    }

    /**
     * 将文件列表转换为全类名列表
     *
     * @param files
     * @return
     */
    private List<String> listClzFullName(List<File> files) {
        List<String> list = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                String fullName = file.getAbsolutePath().replaceAll("\\\\", "/");
                if (fullName.contains("src/main/java")) {
                    String relativePath = fullName.substring(fullName.indexOf("src/main/java") + 14, fullName.length());
                    list.add(relativePath.replaceAll(".java", "").replaceAll("/", "."));
                } else if (fullName.contains("src/test/java")) {
                    String relativePath = fullName.substring(fullName.indexOf("src/test/java") + 14, fullName.length());
                    list.add(relativePath.replaceAll(".java", "").replaceAll("/", "."));
                } else if (fullName.contains("src")) {
                    String relativePath = fullName.substring(fullName.indexOf("src") + 4, fullName.length());
                    list.add(relativePath.replaceAll(".java", "").replaceAll("/", "."));
                }
            }
        }
        return list;
    }
}
