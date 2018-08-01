package org.river.nuls.toolwindow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;
import org.river.nuls.form.AddContractPanel;
import org.river.nuls.form.AddNodePanel;
import org.river.nuls.logic.TreeItemManager;
import org.river.nuls.model.NulsContract;
import org.river.nuls.model.NulsNode;

import javax.swing.*;

public class AddContractDialog extends DialogWrapper {
    private final Project project;
    private final NulsToolWindowPanel parent;
    private final TreeItemManager treeItemManager;
    private final NulsContract contract;
    private AddContractPanel addContractPanel;

    public AddContractDialog(Project project, NulsToolWindowPanel parent, NulsContract contract){
        super(parent, true);
        this.project = project;
        this.parent = parent;
        this.treeItemManager = parent.getTreeItemManager();
        this.contract = contract;
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel(){
        addContractPanel = new AddContractPanel(project, treeItemManager);
        addContractPanel.loadNodeData(contract);
        return addContractPanel;
    }

    @Override
    protected void doOKAction(){
        boolean validateResult = addContractPanel.validateInputs();
        if (!validateResult){
            addContractPanel.setErrorMessage(addContractPanel.getErrorInfo());
            return;
        }
        NulsContract contract = addContractPanel.getEntitybyFields();
        parent.addTreeItem(contract);

        super.doOKAction();
    }
}
