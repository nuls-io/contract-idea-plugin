package org.river.nuls.toolwindow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;
import org.river.nuls.form.RunPanel;
import org.river.nuls.logic.LogManager;
import org.river.nuls.logic.TreeItemManager;

import javax.swing.*;

public class RunDialog extends DialogWrapper {
    private final Project project;
    private final NulsToolWindowPanel parent;
    private final LogManager logManager;
    private RunPanel runPanel;

    public RunDialog(Project project, NulsToolWindowPanel parent){
        super(parent, true);
        this.project = project;
        this.parent = parent;
        this.logManager = parent.getLogManager();
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel(){
        runPanel = new RunPanel(project, parent, logManager);
        return runPanel;
    }

    @Override
    protected Action[] createActions(){
        Action[] actions = new Action[0];
        return actions;
    }
}
