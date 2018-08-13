package org.river.nuls.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.IconLoader;
import org.river.nuls.form.LogsPanel;
import org.river.nuls.toolwindow.ui.LogsDialog;
import org.river.nuls.toolwindow.ui.NulsToolWindowPanel;
import org.river.nuls.util.NulsBundle;

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
        logsDialog.show();
//        logsDialog.setSize(1000, 900);    // 无效
        if (!logsDialog.isOK()) {
            return;
        }
    }
}
