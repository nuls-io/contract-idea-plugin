package io.nuls.contract.idea.plugin.toolwindow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import io.nuls.contract.idea.plugin.form.LoadContractPanel;
import io.nuls.contract.idea.plugin.logic.LogManager;
import io.nuls.contract.idea.plugin.model.NulsContract;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LoadContractDialog extends DialogWrapper {
    private final Project project;
    private final NulsToolWindowPanel parent;
    private final LogManager logManager;
    private LoadContractPanel loadContractPanel;
    private NulsContract contract;

    public LoadContractDialog(Project project, NulsToolWindowPanel parent, NulsContract contract) {
        super(parent, true);
        this.project = project;
        this.parent = parent;
        this.logManager = parent.getLogManager();
        this.contract = contract;
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        loadContractPanel = new LoadContractPanel(project, parent, logManager, contract);
        return loadContractPanel;
    }

    @Override
    protected Action[] createActions() {
        Action[] actions = new Action[0];
        return actions;
    }
}
