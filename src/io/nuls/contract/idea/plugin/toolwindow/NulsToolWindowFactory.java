package io.nuls.contract.idea.plugin.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import io.nuls.contract.idea.plugin.logic.LogManager;
import io.nuls.contract.idea.plugin.logic.Notifier;
import io.nuls.contract.idea.plugin.toolwindow.ui.NulsToolWindowPanel;


public class NulsToolWindowFactory implements ToolWindowFactory {


    public NulsToolWindowFactory() {

    }

    // Create the tool window content.
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        NulsToolWindowPanel explorer = new NulsToolWindowPanel(project,
                LogManager.getInstance(project),
                Notifier.getInstance(project));
        final ContentManager contentManager = toolWindow.getContentManager();
        final Content content = contentManager.getFactory().createContent(explorer, null, false);
        contentManager.addContent(content);
        Disposer.register(project, explorer);
    }
}

