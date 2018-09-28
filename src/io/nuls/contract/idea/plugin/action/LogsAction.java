package io.nuls.contract.idea.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.IconLoader;
import io.nuls.contract.idea.plugin.toolwindow.ui.LogsDialog;
import io.nuls.contract.idea.plugin.toolwindow.ui.NulsToolWindowPanel;
import io.nuls.contract.idea.plugin.util.NulsBundle;

public class LogsAction extends AnAction implements DumbAware {
    private final NulsToolWindowPanel nulsToolWindowPanel;

    public LogsAction(NulsToolWindowPanel nulsToolWindowPanel) {
        super(NulsBundle.message("toolwindow.action.log"), NulsBundle.message("toolwindow.action.log"), IconLoader.findIcon("/icons/balloon_green_16.png"));
        this.nulsToolWindowPanel = nulsToolWindowPanel;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        LogsDialog logsDialog = new LogsDialog(e.getProject(), nulsToolWindowPanel);
        logsDialog.setTitle("Logs");
        logsDialog.setModal(false);
        logsDialog.show();
//        logsDialog.setSize(1000, 900);    // 无效
        if (!logsDialog.isOK()) {
            return;
        }
    }
}
