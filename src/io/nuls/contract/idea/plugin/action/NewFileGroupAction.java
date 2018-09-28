package io.nuls.contract.idea.plugin.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import io.nuls.contract.idea.plugin.template.TemplateManager;

public class NewFileGroupAction extends DefaultActionGroup implements DumbAware {

    public NewFileGroupAction() {
        setPopup(true);
        Presentation presentation = getTemplatePresentation();
        presentation.setText("Nuls");
        presentation.setIcon(AllIcons.Nodes.Module);

        for (String templateName : TemplateManager.TEMPLATE_NAMES) {
            add(new CreateClassAction(templateName));
        }
    }

}
