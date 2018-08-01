package org.river.nuls.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.river.nuls.model.NulsContract;
import org.river.nuls.model.NulsNode;
import org.river.nuls.toolwindow.ui.AddContractDialog;
import org.river.nuls.toolwindow.ui.AddNodeDialog;
import org.river.nuls.toolwindow.ui.NulsToolWindowPanel;
import org.river.nuls.util.NulsBundle;

public class AddContractAction extends AnAction implements DumbAware {
    private final NulsToolWindowPanel nulsToolWindowPanel;

    public AddContractAction(NulsToolWindowPanel nulsToolWindowPanel) {
        super(NulsBundle.message("toolwindow.action.add.contract"),
                NulsBundle.message("toolwindow.action.add.contract"), AllIcons.ToolbarDecorator.AddPackage);
        this.nulsToolWindowPanel = nulsToolWindowPanel;
    }
    @Override
    public void actionPerformed(AnActionEvent e) {
        NulsContract contract = NulsContract.byDefault();
        AddContractDialog addContractDialog = new AddContractDialog(e.getProject(), nulsToolWindowPanel, contract);
        addContractDialog.setTitle("Add a Nuls Contract");
        addContractDialog.show();
        if (!addContractDialog.isOK()) {
            return;
        }
    }
}
