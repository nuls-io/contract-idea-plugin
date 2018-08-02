package org.river.nuls.toolwindow.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.*;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.river.nuls.action.*;
import org.river.nuls.logic.LogManager;
import org.river.nuls.logic.TreeItemManager;
import org.river.nuls.logic.Notifier;
import org.river.nuls.model.*;
import org.river.nuls.toolwindow.viewmodel.NulsTreeBuilder;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.net.URL;
import java.util.Set;

public class NulsToolWindowPanel extends SimpleToolWindowPanel implements Disposable {
    private static final URL pluginSettingsUrl = GuiUtils.class.getResource("/general/add.png");
    private Tree contentTree;
    private NulsTreeBuilder treeBuilder;
    private final Project project;
    private final TreeItemManager treeItemManager;
    private final LogManager logManager;
    private final Notifier notifier;


    public TreeItemManager getTreeItemManager() {
        return treeItemManager;
    }
    public LogManager getLogManager() {
        return logManager;
    }

    @Override
    public void dispose() {

    }

    public NulsToolWindowPanel(@NotNull Project project, TreeItemManager manager, LogManager logManager, Notifier notifier) {
        super(true, true);

        this.project = project;
        this.treeItemManager = manager;
        this.logManager = logManager;
        this.notifier = notifier;

        setToolbar(createToolbarPanel());
//        final DefaultTreeModel model = new DefaultTreeModel(new DefaultMutableTreeNode());
//        contentTree = new Tree(model);
//        contentTree.setRootVisible(false);
//        contentTree.setShowsRootHandles(true);
//        contentTree.setCellRenderer(new NodeRenderer());
        contentTree = createTree();
        treeBuilder = new NulsTreeBuilder(contentTree);
        setContent(ScrollPaneFactory.createScrollPane(contentTree));
        installPopupActionsMenu();
        loadAllTreeItems();
    }

    private JPanel createToolbarPanel() {
        final NulsActionGroup nulsAddGroup = (NulsActionGroup)ActionManager.getInstance().getAction("Nuls.AddGroupPopup");
        nulsAddGroup.removeAll();
        nulsAddGroup.add(new AddNodeAction(this));
        nulsAddGroup.add(new AddAccountAction(this));
        nulsAddGroup.add(new AddContractAction(this));
        final DefaultActionGroup group = new DefaultActionGroup();
        group.add(nulsAddGroup);
        group.add(new RunAction(this));
        group.add(new LogsAction(this));
        final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar("Nuls Toolbar", group, true);
        return JBUI.Panels.simplePanel(actionToolBar.getComponent());
    }

    private Tree createTree() {
        Tree tree = new Tree() {
            private final JLabel myLabel = new JLabel(String.format("<html><center>No configuration available<br><br>You may use <img src=\"%s\"> to add configuration</center></html>", pluginSettingsUrl));

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (!loadTreeItemsfromCache().isEmpty()) return;

                myLabel.setFont(getFont());
                myLabel.setBackground(getBackground());
                myLabel.setForeground(getForeground());
                Rectangle bounds = getBounds();
                Dimension size = myLabel.getPreferredSize();
                myLabel.setBounds(0, 0, size.width, size.height);
                int x = (bounds.width - size.width) / 2;
                Graphics g2 = g.create(bounds.x + x, bounds.y + 20, bounds.width, bounds.height);
                try {
                    myLabel.paint(g2);
                } finally {
                    g2.dispose();
                }
            }
        };
        tree.getEmptyText().clear();
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setName("NulsTree");
        tree.setRootVisible(false);
        new TreeSpeedSearch(tree, treePath -> {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
            final Object userObject = node.getUserObject();
            if (userObject instanceof NulsAccount) {
                return ((NulsAccount) userObject).getAddress();
            }
            if (userObject instanceof NulsNode) {
                return ((NulsNode) userObject).getAgentAddress();
            }
            return "<empty>";
        });
        return tree;
    }

    private java.util.List<TreeItem> loadTreeItemsfromCache() {
        java.util.List<TreeItem>  treeItems = NulsConfiguration.getInstance(project).getTreeItems();
        /*====================Test==============*/
//        NulsAccount account = new NulsAccount();
//        account.setAddress("Nse3fNhvidpAez94HDhX8x83mbYSMwTh");
//        account.setPassword("abcd1234");
//        account.setAlias("AnnaLee");
//        treeItems.add(account);
        NulsNode node = new NulsNode();
        node.setAgentAddress("127.0.0.1:8001");
        node.setRemark("Local");
        treeItems.add(node);
//        NulsContract contract = new NulsContract();
//        contract.setAddress("NseDWXTNeCr6bjVAfeWzMFCvDLksh4wn");
//        contract.setRemark("SimpleContract");
//        treeItems.add(contract);
        /*====================Test==============*/
        return treeItems;
    }

    private void loadAllTreeItems() {
        this.treeItemManager.cleanupTreeItems();
        java.util.List<TreeItem> treeItems = loadTreeItemsfromCache();
        for (TreeItem treeItem : treeItems) {
            addTreeItem(treeItem);
        }
    }

    private void installPopupActionsMenu(){
        DefaultActionGroup actionPopupGroup = new DefaultActionGroup("NulsExplorerPopupGroup", true);
        if (ApplicationManager.getApplication() != null) {
            actionPopupGroup.add(new DeleteTreeItemAction(this));
        }
        PopupHandler.installPopupHandler(contentTree, actionPopupGroup, "POPUP", ActionManager.getInstance());
    }

    public void addTreeItem(TreeItem treeItem){
        treeBuilder.addTreeItem(treeItem);
        this.treeItemManager.registerTreeItem(treeItem);
    }

    public void removeTreeItem(TreeItem treeItem){
        treeBuilder.removeTreeItem(treeItem);
        this.treeItemManager.removeTreeItem(treeItem);
    }

    public Object getSelectedItem() {
        Set<Object> selectedElements = treeBuilder.getSelectedElements();
        if (selectedElements.isEmpty()) {
            return null;
        }
        return selectedElements.iterator().next();
    }
}
