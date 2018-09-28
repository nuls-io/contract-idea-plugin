package io.nuls.contract.idea.plugin.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.task.ProjectTaskManager;
import io.nuls.contract.idea.plugin.form.PackagePanel;
import io.nuls.contract.idea.plugin.toolwindow.ui.NulsToolWindowPanel;
import io.nuls.contract.idea.plugin.util.NulsBundle;

public class PackageAction extends AnAction implements DumbAware {
    private final NulsToolWindowPanel nulsToolWindowPanel;

    public PackageAction(NulsToolWindowPanel nulsToolWindowPanel) {
        super(NulsBundle.message("toolwindow.action.package"), NulsBundle.message("toolwindow.action.package"), AllIcons.Toolwindows.ToolWindowBuild);
        this.nulsToolWindowPanel = nulsToolWindowPanel;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final ProjectTaskManager projectTaskManager = ProjectTaskManager.getInstance(e.getProject());
        projectTaskManager.rebuildAllModules();

        String path = e.getProject().getBasePath();
        String defultJarPath = "/out/artifacts/contract/contract.jar";
        nulsToolWindowPanel.setJarFilePath(path + defultJarPath);
        nulsToolWindowPanel.replaceContentPanel(new PackagePanel(e.getProject(), nulsToolWindowPanel, nulsToolWindowPanel.getLogManager()));
    }

}
