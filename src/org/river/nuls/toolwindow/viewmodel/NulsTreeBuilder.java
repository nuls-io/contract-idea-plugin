package org.river.nuls.toolwindow.viewmodel;

import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeBuilder;
import com.intellij.ide.util.treeView.AbstractTreeStructure;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.ide.util.treeView.PresentableNodeDescriptor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ArrayUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.river.nuls.model.NulsAccount;
import org.river.nuls.model.NulsContract;
import org.river.nuls.model.NulsNode;
import org.river.nuls.model.TreeItem;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.LinkedList;
import java.util.List;

public class NulsTreeBuilder extends AbstractTreeBuilder {

    private final List<TreeItem> treeItems = new LinkedList<>();
    private static final RootDescriptor ROOT_DESCRIPTOR = new RootDescriptor();

    public NulsTreeBuilder(@NotNull Tree tree) {
        init(tree,
                new DefaultTreeModel(new DefaultMutableTreeNode()),
                new NulsTreeStructure(),
                (descriptorLeft, descriptorRight) -> {
                    if (descriptorLeft instanceof NulsNodeDescriptor && descriptorRight instanceof NulsNodeDescriptor) {
                        NulsNode left = (NulsNode) descriptorLeft.getElement();
                        NulsNode right = (NulsNode) descriptorRight.getElement();
                        return left.getAgentAddress().compareTo(right.getAgentAddress());
                    } else if (descriptorLeft instanceof NulsAccountDescriptor && descriptorRight instanceof NulsAccountDescriptor) {
                        String left = ((NulsAccountDescriptor) descriptorLeft).getElement().getAddress();
                        String right = ((NulsAccountDescriptor) descriptorRight).getElement().getAddress();
                        return left.compareTo(right);
                    }
                    return 0;
                 },
                true);
        initRootNode();
    }

    private class NulsTreeStructure extends AbstractTreeStructure {
        @Override
        public Object getRootElement() {
            return RootDescriptor.ROOT;
        }

        @Override
        public Object[] getChildElements(Object element) {
            if (element == RootDescriptor.ROOT) {
                return ArrayUtil.toObjectArray(treeItems);
            }
            return ArrayUtil.EMPTY_OBJECT_ARRAY;
        }

        @Nullable
        @Override
        public Object getParentElement(Object element) {
            return null;
        }

        @NotNull
        @Override
        public NodeDescriptor createDescriptor(Object element, NodeDescriptor parentDescriptor) {
            if (element == RootDescriptor.ROOT) {
                return ROOT_DESCRIPTOR;
            } else if (element instanceof NulsAccount) {
                return new NulsAccountDescriptor(parentDescriptor, (NulsAccount) element);
            } else if (element instanceof NulsNode) {
                return new NulsNodeDescriptor(parentDescriptor, (NulsNode) element);
            } else  if (element instanceof NulsContract) {
                return new NulsContractDescriptor(parentDescriptor, (NulsContract) element);
            }
            throw new IllegalStateException("Element not supported : " + element.getClass().getName());
        }

        @Override
        public void commit() {
            // do nothing
        }

        @Override
        public boolean hasSomethingToCommit() {
            return false;
        }
    }

    private static abstract class NulsTreeNodeDescriptor<T> extends PresentableNodeDescriptor<T> {
        private final T nodeObject;

        NulsTreeNodeDescriptor(@Nullable NodeDescriptor parentDescriptor, @NotNull T object) {
            super(null, parentDescriptor);
            nodeObject = object;
        }

        @Override
        public T getElement() {
            return nodeObject;
        }
    }

    private static class RootDescriptor extends NulsTreeNodeDescriptor<Object> {
        static final Object ROOT = new Object();

        private RootDescriptor() {
            super(null, ROOT);
        }

        @Override
        protected void update(PresentationData presentation) {
            presentation.addText("<root>", SimpleTextAttributes.REGULAR_ATTRIBUTES);
        }
    }

    static class NulsAccountDescriptor extends NulsTreeNodeDescriptor<NulsAccount> {
        NulsAccountDescriptor(NodeDescriptor parentDescriptor, NulsAccount account) {
            super(parentDescriptor, account);
        }

        @Override
        protected void update(PresentationData presentation) {
            NulsAccount account = getElement();
            presentation.setIcon(AllIcons.Actions.RealIntentionBulb);
            String label = account.getAddress() + " - (Alias:" + (StringUtils.isEmpty(account.getAlias()) ? "-" : account.getAlias()) + ") - Account";
            presentation.addText(label, SimpleTextAttributes.REGULAR_ATTRIBUTES);
//            presentation.setTooltip(account.getConfiguration().getUrlsInSingleString());
        }
    }

    static class NulsNodeDescriptor extends NulsTreeNodeDescriptor<NulsNode> {
        NulsNodeDescriptor(NodeDescriptor parentDescriptor, NulsNode node) {
            super(parentDescriptor, node);
        }

        @Override
        protected void update(PresentationData presentation) {
            NulsNode node = getElement();
            presentation.setIcon(AllIcons.Nodes.Deploy);
            String label = node.getAgentAddress() + " - (Remark:" + (StringUtils.isEmpty(node.getRemark()) ? "" : node.getRemark())  + ") - Node";
            presentation.addText(label, SimpleTextAttributes.REGULAR_ATTRIBUTES);
        }
    }

    static class NulsContractDescriptor extends NulsTreeNodeDescriptor<NulsContract> {
        NulsContractDescriptor(NodeDescriptor parentDescriptor, NulsContract contract) {
            super(parentDescriptor, contract);
        }

        @Override
        protected void update(PresentationData presentation) {
            NulsContract contract = getElement();
            presentation.setIcon(AllIcons.Actions.RealIntentionBulb);
            String label = contract.getAddress() + " - (Remark:" + (StringUtils.isEmpty(contract.getRemark()) ? "" : contract.getRemark()) + ") - Contract" ;
            presentation.addText(label, SimpleTextAttributes.REGULAR_ATTRIBUTES);
        }
    }

    public TreeItem addTreeItem(@NotNull TreeItem treeItem) {
        treeItems.add(treeItem);
        queueUpdateFrom(RootDescriptor.ROOT, true);
        return treeItem;
    }

    public void removeTreeItem( TreeItem treeItem) {
        treeItems.remove(treeItem);
        queueUpdateFrom(RootDescriptor.ROOT, true);
    }
}
