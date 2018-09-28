package io.nuls.contract.idea.plugin.toolwindow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import io.nuls.contract.idea.plugin.form.AddNodePanel;
import io.nuls.contract.idea.plugin.model.NulsNode;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AddNodeDialog extends DialogWrapper {
    private final Project project;
    private final NulsToolWindowPanel parent;
    private final NulsNode node;
    private AddNodePanel addNodePanel;

    public AddNodeDialog(Project project, NulsToolWindowPanel parent, NulsNode node) {
        super(parent, true);
        this.project = project;
        this.parent = parent;
        this.node = node;
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        addNodePanel = new AddNodePanel(project);
        addNodePanel.loadNodeData(node);
        return addNodePanel;
    }

    @Override
    protected void doOKAction() {
        boolean validateResult = addNodePanel.validateInputs();
        if (!validateResult) {
            addNodePanel.setErrorMessage(addNodePanel.getErrorInfo());
            return;
        }
        NulsNode nulsNode = addNodePanel.getEntitybyFields();
        parent.addTreeItem(nulsNode);

        super.doOKAction();
    }
}
