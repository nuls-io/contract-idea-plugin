package io.nuls.contract.idea.plugin.toolwindow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import io.nuls.contract.idea.plugin.form.AddAccountPanel;
import io.nuls.contract.idea.plugin.model.NulsAccount;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class UpdateAccountDialog extends DialogWrapper {
    private final Project project;
    private final NulsToolWindowPanel parent;
    private final NulsAccount account;
    private final NulsAccount oldAccount;
    private AddAccountPanel addAccountPanel;

    public UpdateAccountDialog(Project project, NulsToolWindowPanel parent, NulsAccount account, NulsAccount oldAccount) {
        super(parent, true);
        this.project = project;
        this.parent = parent;
        this.account = account;
        this.oldAccount = oldAccount;
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        addAccountPanel = new AddAccountPanel(project);
        addAccountPanel.loadAccountData(account);
        return addAccountPanel;
    }

    @Override
    protected void doOKAction() {
        boolean validateResult = addAccountPanel.validateInputs();
        if (!validateResult) {
            addAccountPanel.setErrorMessage(addAccountPanel.getErrorInfo());
            return;
        }
        NulsAccount nulsAccount = addAccountPanel.getEntitybyFields();
        this.parent.addTreeItem(nulsAccount);
        this.parent.removeTreeItem(oldAccount);

        super.doOKAction();
    }

}
