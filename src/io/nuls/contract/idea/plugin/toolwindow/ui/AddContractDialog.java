package io.nuls.contract.idea.plugin.toolwindow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import io.nuls.contract.idea.plugin.form.AddContractPanel;
import io.nuls.contract.idea.plugin.model.NulsContract;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AddContractDialog extends DialogWrapper {
    private final Project project;
    private final NulsToolWindowPanel parent;
    private final NulsContract contract;
    private AddContractPanel addContractPanel;

    public AddContractDialog(Project project, NulsToolWindowPanel parent, NulsContract contract) {
        super(parent, true);
        this.project = project;
        this.parent = parent;
        this.contract = contract;
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        addContractPanel = new AddContractPanel(project);
        addContractPanel.loadNodeData(contract);
        return addContractPanel;
    }

    @Override
    protected void doOKAction() {
        boolean validateResult = addContractPanel.validateInputs();
        if (!validateResult) {
            addContractPanel.setErrorMessage(addContractPanel.getErrorInfo());
            return;
        }
        NulsContract contract = addContractPanel.getEntitybyFields();
        parent.addTreeItem(contract);

        super.doOKAction();
    }
}
