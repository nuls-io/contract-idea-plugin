package io.nuls.contract.idea.plugin.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import io.nuls.contract.idea.plugin.form.RunPanel;
import io.nuls.contract.idea.plugin.toolwindow.ui.NulsToolWindowPanel;
import io.nuls.contract.idea.plugin.util.NulsBundle;

public class RunAction extends AnAction implements DumbAware {
    private final NulsToolWindowPanel nulsToolWindowPanel;

    public RunAction(NulsToolWindowPanel nulsToolWindowPanel) {
        super(NulsBundle.message("toolwindow.action.run"), NulsBundle.message("toolwindow.action.run"), AllIcons.Actions.Execute);
        this.nulsToolWindowPanel = nulsToolWindowPanel;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        nulsToolWindowPanel.replaceContentPanel(new RunPanel(e.getProject(), nulsToolWindowPanel, nulsToolWindowPanel.getLogManager()));
//        RunDialog runDialog = new RunDialog(e.getProject(), nulsToolWindowPanel);
//        runDialog.setTitle("Run");
//        runDialog.setModal(false);
//        runDialog.show();
    }
}
