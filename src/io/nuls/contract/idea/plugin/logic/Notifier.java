package io.nuls.contract.idea.plugin.logic;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

public class Notifier {

    private final NotificationGroup NULS_GROUP = NotificationGroup.logOnlyGroup("Nuls");
    private final Project project;

    public static Notifier getInstance(Project project) {
        return ServiceManager.getService(project, Notifier.class);
    }

    private Notifier(Project project) {
        this.project = project;
    }

    public void notifyInfo(String message) {
        notify(message, NotificationType.INFORMATION);
    }

    public void notifyError(String message) {
        notify(message, NotificationType.ERROR);
    }

    private void notify(String message, NotificationType notificationType) {
        NULS_GROUP.createNotification("[NulsPlugin] " + message, notificationType).notify(project);
    }
}
