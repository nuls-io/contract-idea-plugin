package org.river.nuls.toolwindow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;
import org.river.nuls.form.LogsPanel;
import org.river.nuls.logic.LogManager;

import javax.swing.*;

public class LogsDialog extends DialogWrapper {
    private final Project project;
    private final LogManager logManager;
    private LogsPanel logsPanel;

    public LogsDialog(Project project, NulsToolWindowPanel parent){
        super(parent, true);
        this.project = project;
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
