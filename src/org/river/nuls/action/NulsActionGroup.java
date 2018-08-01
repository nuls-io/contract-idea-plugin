package org.river.nuls.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.actionSystem.ShortcutSet;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class NulsActionGroup extends DefaultActionGroup implements DumbAware {

    public NulsActionGroup(){
        super("Nuls Add", true);
        super.setDefaultIcon(true);
//        super.setShortcutSet(new ShortcutSet() {
//            @NotNull
//            @Override
//            public Shortcut[] getShortcuts() {
//                return new Shortcut[0];
//            }
//        });
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        e.getPresentation().setEnabled(true);
    }

}
