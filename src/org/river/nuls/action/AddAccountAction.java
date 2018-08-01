package org.river.nuls.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.river.nuls.model.NulsAccount;
import org.river.nuls.toolwindow.ui.AddAccountDialog;
import org.river.nuls.toolwindow.ui.NulsToolWindowPanel;
import org.river.nuls.util.NulsBundle;

public class AddAccountAction extends AnAction implements DumbAware {
    private final NulsToolWindowPanel nulsToolWindowPanel;

    public AddAccountAction(NulsToolWindowPanel nulsToolWindowPanel) {
        super(NulsBundle.message("toolwindow.action.add.account"),
                NulsBundle.message("toolwindow.action.add.account"), AllIcons.ToolbarDecorator.AddFolder);
        this.nulsToolWindowPanel = nulsToolWindowPanel;
    }
    @Override
    public void actionPerformed(AnActionEvent e) {
        NulsAccount account = NulsAccount.byDefault();
        AddAccountDialog addAccountDialog = new AddAccountDialog(e.getProject(), nulsToolWindowPanel, account);
        addAccountDialog.setTitle("Add a Nuls Account");
        addAccountDialog.show();
        if (!addAccountDialog.isOK()) {
            return;
        }
    }
}
