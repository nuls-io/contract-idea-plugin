package io.nuls.contract.idea.plugin.toolwindow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import io.nuls.contract.idea.plugin.form.InvokePannel;
import io.nuls.contract.idea.plugin.form.LoadContractPanel;
import io.nuls.contract.idea.plugin.logic.LogManager;
import io.nuls.contract.idea.plugin.model.NulsAccount;
import io.nuls.contract.idea.plugin.model.NulsContract;
import io.nuls.contract.idea.plugin.model.NulsContractMethod;
import io.nuls.contract.idea.plugin.model.NulsNode;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class InvokeDialog extends DialogWrapper {
    private final Project project;
    private final NulsToolWindowPanel top;
    private final LoadContractPanel parent;
    private final LogManager logManager;
    private InvokePannel invokePannel;
    private final NulsNode node;
    private final NulsAccount account;
    private final NulsContract contract;
    private final NulsContractMethod method;

    public InvokeDialog(Project project, NulsToolWindowPanel top, LoadContractPanel parent,
                        NulsNode node, NulsAccount account, NulsContract contract, NulsContractMethod method) {
        super(parent, true);
        this.project = project;
        this.top = top;
        this.parent = parent;
        this.logManager = top.getLogManager();
        this.node = node;
        this.account = account;
        this.contract = contract;
        this.method = method;
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        invokePannel = new InvokePannel(project, parent, logManager, node, account, contract, method);
        return invokePannel;
    }

    @Override
    protected void doOKAction() {
        boolean validateResult = invokePannel.validateInputs();
        if (!validateResult) {
//            invokePannel.setErrorMessage(invokePannel.getErrorInfo());
            return;
        }
        super.doOKAction();
        invokePannel.doInvoke();
    }

}
