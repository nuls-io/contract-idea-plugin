package io.nuls.contract.idea.plugin.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAware;

public class NulsActionGroup extends DefaultActionGroup implements DumbAware {

    public NulsActionGroup() {
        super("Nuls Add", true);
        super.setDefaultIcon(true);

        getTemplatePresentation().setIcon(AllIcons.General.Add);
        getTemplatePresentation().setText("Add");
        getTemplatePresentation().setDescription("Add Nuls Elements");
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        e.getPresentation().setEnabled(true);
    }

}
