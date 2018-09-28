package io.nuls.contract.idea.plugin.framework;

import com.intellij.framework.FrameworkTypeEx;
import com.intellij.framework.addSupport.FrameworkSupportInModuleConfigurable;
import com.intellij.framework.addSupport.FrameworkSupportInModuleProvider;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportModel;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.libraries.Library;
import io.nuls.contract.idea.plugin.module.NulsModuleBuilder;
import io.nuls.contract.idea.plugin.util.ModuleUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class NulsFramework extends FrameworkTypeEx {

    public static final String FRAMEWORK_ID = "Nuls_Framework";

    protected NulsFramework() {
        super(FRAMEWORK_ID);
    }

    @NotNull
    @Override
    public FrameworkSupportInModuleProvider createProvider() {
        return new FrameworkSupportInModuleProvider() {

            @NotNull
            @Override
            public FrameworkTypeEx getFrameworkType() {
                return NulsFramework.this;
            }

            @NotNull
            @Override
            public FrameworkSupportInModuleConfigurable createConfigurable(@NotNull FrameworkSupportModel model) {
                return new FrameworkSupportInModuleConfigurable() {

                    @Nullable
                    @Override
                    public JComponent createComponent() {
                        return null;
                    }

                    @Override
                    public void addSupport(@NotNull Module module,
                                           @NotNull ModifiableRootModel model,
                                           @NotNull ModifiableModelsProvider provider) {
                        ModuleUtil.jdkVersion(module);
                        ModuleUtil.artifact(module);
                        String sdk = ModuleUtil.addSdk(module);
                        Library library = ModuleUtil.addJar(module, sdk);
                        model.addLibraryEntry(library);
                    }

                };
            }

            @Override
            public boolean isEnabledForModuleType(@NotNull ModuleType type) {
                return true;
            }

            @Override
            public boolean isEnabledForModuleBuilder(@NotNull ModuleBuilder builder) {
                return builder instanceof NulsModuleBuilder;
            }

        };
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Nuls Contract";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return AllIcons.Nodes.Module;
    }

}
