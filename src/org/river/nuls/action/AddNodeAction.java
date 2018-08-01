package org.river.nuls.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.river.nuls.model.NulsAccount;
import org.river.nuls.model.NulsNode;
import org.river.nuls.toolwindow.ui.AddAccountDialog;
import org.river.nuls.toolwindow.ui.AddNodeDialog;
import org.river.nuls.toolwindow.ui.NulsToolWindowPanel;
import org.river.nuls.util.NulsBundle;

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
        addNodeDialog.show();
        if (!addNodeDialog.isOK()) {
            return;
        }
    }
}
