package io.nuls.contract.idea.plugin.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import io.nuls.contract.idea.plugin.model.NulsContract;
import io.nuls.contract.idea.plugin.toolwindow.ui.AddContractDialog;
import io.nuls.contract.idea.plugin.toolwindow.ui.NulsToolWindowPanel;
import io.nuls.contract.idea.plugin.util.NulsBundle;

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
        addContractDialog.setModal(false);
        addContractDialog.show();
        if (!addContractDialog.isOK()) {
            return;
        }
    }
}
