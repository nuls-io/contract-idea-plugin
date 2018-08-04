package org.river.nuls.toolwindow.viewmodel;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import org.river.nuls.form.SettingsPanel;
import org.river.nuls.model.ConfigStorage;

import javax.swing.*;

public class NulsConfigurable implements SearchableConfigurable {

    private final Project project;
    private SettingsPanel settingsPanel;

    public NulsConfigurable(Project project){
        this.project = project;

    }

    public String getDisplayName(){
        return "Nuls";
    }

    public String getId(){
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