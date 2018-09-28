package io.nuls.contract.idea.plugin.logic;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

import java.util.LinkedList;
import java.util.List;

public class LogManager {

    private List<String> logs = new LinkedList<>();

    public static LogManager getInstance(Project project) {
        return ServiceManager.getService(project, LogManager.class);
    }

    public void cleanup() {
        logs.clear();
    }

    public void append(String strLog) {
        logs.add(strLog);
    }

    public List<String> getLogs() {
        return logs;
    }
}
