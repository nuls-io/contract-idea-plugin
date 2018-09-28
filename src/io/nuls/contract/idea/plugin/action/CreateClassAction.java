package io.nuls.contract.idea.plugin.action;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.actions.JavaCreateTemplateInPackageAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.PlatformIcons;
import io.nuls.contract.idea.plugin.template.TemplateManager;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CreateClassAction extends JavaCreateTemplateInPackageAction<PsiClass> implements DumbAware {

    private String templateName;

    public CreateClassAction(String templateName) {
        super(templateName, "Create new " + templateName, PlatformIcons.CLASS_ICON, true);
        this.templateName = templateName;
    }

    @Override
    protected void buildDialog(final Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
        builder
                .setTitle("Create New " + templateName)
                .addKind(templateName, PlatformIcons.CLASS_ICON, templateName);

        LanguageLevel level = PsiUtil.getLanguageLevel(directory);

        builder.setValidator(new InputValidatorEx() {
            @Override
            public String getErrorText(String inputString) {
                if (inputString.length() > 0 && !PsiNameHelper.getInstance(project).isQualifiedName(inputString)) {
                    return "This is not a valid Java qualified name";
                }
                if (level.isAtLeast(LanguageLevel.JDK_10) && PsiKeyword.VAR.equals(StringUtil.getShortName(inputString))) {
                    return "var cannot be used for type declarations";
                }
                return null;
            }

            @Override
            public boolean checkInput(String inputString) {
                return true;
            }

            @Override
            public boolean canClose(String inputString) {
                return !StringUtil.isEmptyOrSpaces(inputString) && getErrorText(inputString) == null;
            }
        });
    }

    @Override
    protected String removeExtension(String templateName, String className) {
        return StringUtil.trimEnd(className, ".java");
    }

    @Override
    protected String getErrorTitle() {
        return "Cannot Create Class";
    }


    @Override
    protected String getActionName(PsiDirectory directory, String newName, String templateName) {
        return IdeBundle.message("progress.creating.class", StringUtil.getQualifiedName(JavaDirectoryService.getInstance().getPackage(directory).getQualifiedName(), newName));
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    protected final PsiClass doCreate(PsiDirectory dir, String className, String templateName) throws IncorrectOperationException {
        TemplateManager.addTemplate(dir.getProject(), templateName);
        return JavaDirectoryService.getInstance().createClass(dir, className, templateName, true);
    }

    @Override
    protected PsiElement getNavigationElement(@NotNull PsiClass createdElement) {
        return createdElement.getLBrace();
    }

    @Override
    protected void postProcess(PsiClass createdElement, String templateName, Map<String, String> customProperties) {
        super.postProcess(createdElement, templateName, customProperties);

        moveCaretAfterNameIdentifier(createdElement);
    }

}
