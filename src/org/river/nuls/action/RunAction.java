package org.river.nuls.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.river.nuls.toolwindow.ui.NulsToolWindowPanel;
import org.river.nuls.toolwindow.ui.RunDialog;
import org.river.nuls.util.NulsBundle;

import javax.swing.*;

public class RunAction extends AnAction implements DumbAware {
    private final NulsToolWindowPanel nulsToolWindowPanel;

    public RunAction(NulsToolWindowPanel nulsToolWindowPanel) {
        super(NulsBundle.message("toolwindow.action.run"), NulsBundle.message("toolwindow.action.run"), AllIcons.Actions.Execute);
        this.nulsToolWindowPanel = nulsToolWindowPanel;
    }
    @Override
    public void actionPerformed(AnActionEvent e) {
        RunDialog runDialog = new RunDialog(e.getProject(), nulsToolWindowPanel);
        runDialog.setTitle("Run");
        runDialog.show();
    }
}
