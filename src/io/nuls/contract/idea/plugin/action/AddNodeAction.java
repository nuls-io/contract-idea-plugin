package io.nuls.contract.idea.plugin.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import io.nuls.contract.idea.plugin.model.NulsNode;
import io.nuls.contract.idea.plugin.toolwindow.ui.AddNodeDialog;
import io.nuls.contract.idea.plugin.toolwindow.ui.NulsToolWindowPanel;
import io.nuls.contract.idea.plugin.util.NulsBundle;

public class AddNodeAction extends AnAction implements DumbAware {
    private final NulsToolWindowPanel nulsToolWindowPanel;

    public AddNodeAction(NulsToolWindowPanel nulsToolWindowPanel) {
        super(NulsBundle.message("toolwindow.action.add.node"),
                NulsBundle.message("toolwindow.action.add.node"), AllIcons.ToolbarDecorator.AddLink);
        this.nulsToolWindowPanel = nulsToolWindowPanel;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        NulsNode node = NulsNode.byDefault();
        AddNodeDialog addNodeDialog = new AddNodeDialog(e.getProject(), nulsToolWindowPanel, node);
        addNodeDialog.setTitle("Add a Nuls Node");
        addNodeDialog.setModal(false);
        addNodeDialog.show();
        if (!addNodeDialog.isOK()) {
            return;
        }
    }
}
