package io.nuls.contract.idea.plugin.toolwindow.viewmodel;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import io.nuls.contract.idea.plugin.form.SettingsPanel;
import io.nuls.contract.idea.plugin.model.ConfigStorage;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class NulsConfigurable implements SearchableConfigurable {

    private final Project project;
    private SettingsPanel settingsPanel;

    public NulsConfigurable(Project project) {
        this.project = project;

    }

    public String getDisplayName() {
        return "Nuls";
    }

    public String getId() {
        return "nuls.settings";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        settingsPanel = new SettingsPanel(ConfigStorage.getInstance(project));
        return settingsPanel;
    }

    public synchronized boolean isModified() {
        return settingsPanel.isNulsSyntaxCheckSelected() != ConfigStorage.getInstance(project).isNulsSyntaxCheck();
    }

    public synchronized void apply() throws ConfigurationException {
        if (isModified()) {
            ConfigStorage.getInstance(project).setNulsSyntaxCheck(settingsPanel.isNulsSyntaxCheckSelected());
        }
    }
}