package io.nuls.contract.idea.plugin.toolwindow;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import io.nuls.contract.idea.plugin.logic.LogManager;
import io.nuls.contract.idea.plugin.logic.Notifier;
import io.nuls.contract.idea.plugin.toolwindow.ui.NulsToolWindowPanel;

public class NulsWindowManager {
    private static final String NULS_EXPLORER = "Nuls";

    private final Project project;
    private final NulsToolWindowPanel nulsToolWindowPanel;

    public static NulsWindowManager getInstance(Project project) {
        return ServiceManager.getService(project, NulsWindowManager.class);
    }

    private NulsWindowManager(Project project) {
        this.project = project;

        nulsToolWindowPanel = new NulsToolWindowPanel(project, LogManager.getInstance(project), Notifier.getInstance(project));
        Content content = ContentFactory.SERVICE.getInstance().createContent(nulsToolWindowPanel, null, false);
        ToolWindow nulsToolWindow = ToolWindowManager.getInstance(project).registerToolWindow(
                "Nuls", false, ToolWindowAnchor.RIGHT, project, true);
        nulsToolWindow.getContentManager().addContent(content);
        nulsToolWindow.setIcon(IconLoader.findIcon("/icons/nuls_tb.png"));
    }

    public void unregisterMyself() {
        nulsToolWindowPanel.dispose();
        ToolWindowManager.getInstance(project).unregisterToolWindow("Nuls");
    }
}
