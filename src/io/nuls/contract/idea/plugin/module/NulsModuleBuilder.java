package io.nuls.contract.idea.plugin.module;

import com.intellij.icons.AllIcons;
import com.intellij.ide.projectWizard.ProjectTypeStep;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;
import io.nuls.contract.idea.plugin.util.ModuleUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class NulsModuleBuilder extends JavaModuleBuilder {

    @Override
    public ModuleWizardStep modifyProjectTypeStep(@NotNull SettingsStep settingsStep) {
        ProjectTypeStep projectTypeStep = (ProjectTypeStep) settingsStep;
        projectTypeStep.dispose();
        return super.modifyProjectTypeStep(settingsStep);
    }

    @Nullable
    @Override
    public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep) {
        ModuleUtil.jdkVersion(null);
        return super.modifySettingsStep(settingsStep);
    }

    @Override
    public ModuleWizardStep modifyStep(SettingsStep settingsStep) {
        return super.modifyStep(settingsStep);
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
        ModuleUtil.artifact(rootModel.getModule());
        String sdk = ModuleUtil.addSdk(rootModel.getModule());
        addModuleLibrary(sdk, null);
        super.setupRootModel(rootModel);
    }

    @Override
    public ModuleType getModuleType() {
        return StdModuleTypes.JAVA;
    }

    @Override
    public Icon getNodeIcon() {
        return AllIcons.Nodes.Module;
    }

    @Override
    public String getDescription() {
        return "Nuls Contract";
    }

    @Override
    public String getPresentableName() {
        return "Nuls";
    }

    @Override
    public String getGroupName() {
        return "Nuls";
    }

    @Override
    public String getParentGroup() {
        return JavaModuleType.JAVA_GROUP;
    }

    @Override
    public boolean isTemplateBased() {
        return true;
    }


}
