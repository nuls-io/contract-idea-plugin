package io.nuls.contract.idea.plugin.toolwindow.ui;

import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.CellAppearanceEx;
import com.intellij.ui.HtmlListCellRenderer;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;


abstract class NulsNodeDescriptor extends NodeDescriptor implements CellAppearanceEx {
    public NulsNodeDescriptor(Project project, NodeDescriptor parentDescriptor) {
        super(project, parentDescriptor);
    }

    public abstract boolean isAutoExpand();

    public void customize(@NotNull SimpleColoredComponent component) {
        component.append(toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
    }

    @Override
    public void customize(@NotNull final HtmlListCellRenderer renderer) {
        renderer.append(toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
    }

    @NotNull
    public String getText() {
        return toString();
    }
}