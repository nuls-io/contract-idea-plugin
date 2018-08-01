package org.river.nuls.toolwindow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;
import org.river.nuls.form.AddAccountPanel;
import org.river.nuls.form.LogsPanel;
import org.river.nuls.logic.LogManager;
import org.river.nuls.logic.TreeItemManager;

import javax.swing.*;
import java.util.List;

public class LogsDialog extends DialogWrapper {
    private final Project project;
    private final TreeItemManager treeItemManager;
    private final LogManager logManager;
    private LogsPanel logsPanel;

    public LogsDialog(Project project, NulsToolWindowPanel parent){
        super(parent, true);
        this.project = project;
        this.treeItemManager = parent.getTreeItemManager();
        this.logManager = parent.getLogManager();
        init();
//        setSize(1000, 900);   // 无效
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel(){
        logsPanel = new LogsPanel(project, logManager);
        return logsPanel;
    }

    @Override
    protected Action[] createActions(){
        Action[] actions = new Action[0];
        return actions;
    }
}
