package org.river.nuls.toolwindow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;
import org.river.nuls.form.InvokePannel;
import org.river.nuls.form.RunPanel;
import org.river.nuls.logic.LogManager;
import org.river.nuls.model.NulsAccount;
import org.river.nuls.model.NulsContract;
import org.river.nuls.model.NulsContractMethod;
import org.river.nuls.model.NulsNode;

import javax.swing.*;

public class InvokeDialog extends DialogWrapper {
    private final Project project;
    private final NulsToolWindowPanel top;
    private final RunPanel parent;
    private final LogManager logManager;
    private InvokePannel invokePannel;
    private final NulsNode node;
    private final NulsAccount account;
    private final NulsContract contract;
    private final NulsContractMethod method;

    public InvokeDialog(Project project, NulsToolWindowPanel top, RunPanel parent,
                        NulsNode node, NulsAccount account, NulsContract contract, NulsContractMethod method){
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
    protected JComponent createCenterPanel(){
        invokePannel = new InvokePannel(project, parent, logManager, node, account, contract, method);
        return invokePannel;
    }

    @Override
    protected void doOKAction(){
        boolean validateResult = invokePannel.validateInputs();
        if (!validateResult){
            invokePannel.setErrorMessage(invokePannel.getErrorInfo());
            return;
        }
        super.doOKAction();
        invokePannel.doInvoke();
    }

}
