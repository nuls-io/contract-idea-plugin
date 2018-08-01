package org.river.nuls.form;

import com.intellij.openapi.project.Project;
import org.river.nuls.logic.LogManager;

import javax.swing.*;
import java.awt.*;

public class LogsPanel extends JPanel {

    private JPanel rootPanel;
    private JTextArea logTextArea;
    private JScrollPane logSCrollPane;

    private final Project project;
    private final LogManager logManager;

    public LogsPanel(Project project, LogManager logManager){
        this.project = project;
        this.logManager = logManager;
//        setSize(new Dimension(800, 600)); // 无效
        setLayout(new BorderLayout());
        add(rootPanel);
        // 加载日志
        for(String log : logManager.getLogs()){
            logTextArea.append(log + "\n");
        }
    }
}
