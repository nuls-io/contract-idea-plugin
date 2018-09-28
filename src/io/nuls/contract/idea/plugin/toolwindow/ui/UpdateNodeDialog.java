package io.nuls.contract.idea.plugin.toolwindow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import io.nuls.contract.idea.plugin.form.AddNodePanel;
import io.nuls.contract.idea.plugin.model.NulsNode;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class UpdateNodeDialog extends DialogWrapper {
    private final Project project;
    private final NulsToolWindowPanel parent;
    private final NulsNode node;
    private final NulsNode oldNode;
    private AddNodePanel addNodePanel;

    public UpdateNodeDialog(Project project, NulsToolWindowPanel parent, NulsNode node, NulsNode oldNode) {
        super(parent, true);
        this.project = project;
        this.parent = parent;
        this.node = node;
        this.oldNode = oldNode;
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
        parent.removeTreeItem(oldNode);

        super.doOKAction();
    }
}
