package org.river.nuls.toolwindow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;
import org.river.nuls.form.AddNodePanel;
import org.river.nuls.logic.TreeItemManager;
import org.river.nuls.model.NulsNode;

import javax.swing.*;

public class AddNodeDialog extends DialogWrapper {
    private final Project project;
    private final NulsToolWindowPanel parent;
    private final TreeItemManager treeItemManager;
    private final NulsNode node;
    private AddNodePanel addNodePanel;

    public AddNodeDialog(Project project, NulsToolWindowPanel parent, NulsNode node){
        super(parent, true);
        this.project = project;
        this.parent = parent;
        this.treeItemManager = parent.getTreeItemManager();
        this.node = node;
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel(){
        addNodePanel = new AddNodePanel(project, treeItemManager);
        addNodePanel.loadNodeData(node);
        return addNodePanel;
    }

    @Override
    protected void doOKAction(){
        boolean validateResult = addNodePanel.validateInputs();
        if (!validateResult){
            addNodePanel.setErrorMessage(addNodePanel.getErrorInfo());
            return;
        }
        NulsNode nulsNode = addNodePanel.getEntitybyFields();
        parent.addTreeItem(nulsNode);

        super.doOKAction();
    }
}
