package io.nuls.contract.idea.plugin.toolwindow.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.GuiUtils;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import io.nuls.contract.idea.plugin.action.*;
import io.nuls.contract.idea.plugin.form.DeleteContractPanel;
import io.nuls.contract.idea.plugin.form.LoadContractPanel;
import io.nuls.contract.idea.plugin.logic.LogManager;
import io.nuls.contract.idea.plugin.logic.Notifier;
import io.nuls.contract.idea.plugin.model.*;
import io.nuls.contract.idea.plugin.toolwindow.viewmodel.NulsTreeBuilder;
import io.nuls.contract.idea.plugin.toolwindow.viewmodel.NulsTreeRemoveBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Set;

public class NulsToolWindowPanel extends SimpleToolWindowPanel implements Disposable {
    private static final URL pluginSettingsUrl = GuiUtils.class.getResource("/general/add.png");
    private Tree contentTree;
    private Tree delTree;
    private NulsTreeBuilder treeBuilder;
    private NulsTreeRemoveBuilder treeDelBuilder;
    private final Project project;
    private final LogManager logManager;
    private final Notifier notifier;
    private JPanel contentPanel;

    public LogManager getLogManager() {
        return logManager;
    }

    @Override
    public void dispose() {

    }

    public NulsToolWindowPanel(@NotNull Project project, LogManager logManager, Notifier notifier) {
        super(true, true);

        this.project = project;
        this.logManager = logManager;
        this.notifier = notifier;

        setToolbar(createToolbarPanel());
        contentTree = createTree();
        delTree = createDelTree();

        treeBuilder = new NulsTreeBuilder(contentTree);
        treeDelBuilder = new NulsTreeRemoveBuilder(delTree);
//        setContent(ScrollPaneFactory.createScrollPane(contentTree));
        layoutToolWindowPanel();
//        installPopupActionsMenu();
        loadAllTreeItems();
        addTreeSelectionListener();
    }

    private void layoutToolWindowPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        JPanel treePanel = new JPanel();
        treePanel.setLayout(new BorderLayout());
        treePanel.add(ScrollPaneFactory.createScrollPane(contentTree), BorderLayout.CENTER);
        treePanel.add(ScrollPaneFactory.createScrollPane(delTree), BorderLayout.EAST);
        container.add(ScrollPaneFactory.createScrollPane(treePanel), BorderLayout.NORTH);
        contentPanel = new JPanel();
        container.add(ScrollPaneFactory.createScrollPane(contentPanel), BorderLayout.CENTER);
        setContent(ScrollPaneFactory.createScrollPane(container));
    }

    public synchronized void replaceContentPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(ScrollPaneFactory.createScrollPane(panel));
        contentPanel.updateUI();
        contentPanel.repaint();
    }

    private JPanel createToolbarPanel() {
//        final NulsActionGroup nulsAddGroup = (NulsActionGroup)ActionManager.getInstance().getAction("Nuls.AddGroupPopup");
//        nulsAddGroup.removeAll();
        final NulsActionGroup nulsAddGroup = new NulsActionGroup();
        nulsAddGroup.add(new AddNodeAction(this));
        nulsAddGroup.add(new AddAccountAction(this));
        nulsAddGroup.add(new AddContractAction(this));
        final DefaultActionGroup group = new DefaultActionGroup();
        group.add(nulsAddGroup);
        group.add(new PackageAction(this));
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

    private Tree createDelTree() {
        Tree tree = new Tree();
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
        ConfigStorage storage = ConfigStorage.getInstance(project);
        setDefultInfo(storage);
        java.util.List<TreeItem> treeItems = storage.getTreeItems();
        return treeItems;
    }

    private void setDefultInfo(ConfigStorage storage) {
        if (storage.getJarFilePath() == null) {
            String path = project.getBasePath();
            String defultJarPath = "/out/artifacts/contract/contract.jar";
            setJarFilePath(path + defultJarPath);
        }

        if (storage.getNulsNodes().size() == 0) {
            NulsNode node = new NulsNode();
            node.setAgentAddress("127.0.0.1:8001");
            this.addTreeItem(node);
        }
    }

    private void loadAllTreeItems() {
        // 加载持久数据
        java.util.List<TreeItem> treeItems = loadTreeItemsfromCache();
        // 重绘树
        treeBuilder.removeAllTreeItems();
        treeDelBuilder.removeAllTreeItems();
        for (TreeItem treeItem : treeItems) {
            treeBuilder.addTreeItem(treeItem);
            treeDelBuilder.addTreeItem(treeItem);
        }
    }

    private void installPopupActionsMenu() {
        DefaultActionGroup actionPopupGroup = new DefaultActionGroup("NulsExplorerPopupGroup", true);
        if (ApplicationManager.getApplication() != null) {
            actionPopupGroup.add(new DeleteTreeItemAction(this));
        }
        PopupHandler.installPopupHandler(contentTree, actionPopupGroup, "POPUP", ActionManager.getInstance());
    }

    private void addTreeSelectionListener() {
        contentTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 如果在这棵树上点击了1次,即双击
                if (e.getSource() == contentTree && e.getClickCount() == 1) {
                    // 按照鼠标点击的坐标点获取路径
                    TreePath selPath = contentTree.getPathForLocation(e.getX(), e.getY());
                    // 谨防空指针异常!双击空白处是会这样
                    if (selPath != null) {
                        //选中删除树节点
                        selectDelTree();
                    }
                } else if (e.getSource() == contentTree && e.getClickCount() == 2) {
                    // 按照鼠标点击的坐标点获取路径
                    TreePath selPath = contentTree.getPathForLocation(e.getX(), e.getY());
                    // 谨防空指针异常!双击空白处是会这样
                    if (selPath != null) {
                        clickNode();
                    }
                }
            }
        });

        delTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 如果在这棵树上点击了1次,即双击
                if (e.getSource() == delTree && e.getClickCount() == 1) {
                    // 按照鼠标点击的坐标点获取路径
                    TreePath selPath = delTree.getPathForLocation(e.getX(), e.getY());
                    // 谨防空指针异常!双击空白处是会这样
                    if (selPath != null) {
                        //选中树节点
                        selectTree();
                        deleteNode();
                    }
                } /*else if (e.getSource() == delTree && e.getClickCount() == 2) {
                    // 按照鼠标点击的坐标点获取路径
                    TreePath selPath = delTree.getPathForLocation(e.getX(), e.getY());
                    // 谨防空指针异常!双击空白处是会这样
                    if (selPath != null) {
                        deleteNode();
                    }
                }*/
            }
        });
    }

    private void selectDelTree() {
        Object selectedItem = this.getSelectedItem();
        treeDelBuilder.select(selectedItem);
    }

    private void selectTree() {
        Object selectedItem = this.getDelSelectedItem();
        treeBuilder.select(selectedItem);
    }

    private void deleteNode() {
        Object selectedItem = this.getDelSelectedItem();

        if (selectedItem instanceof NulsNode) {
            NulsNode node = (NulsNode) selectedItem;
            deleteItem("node", node.toString(),
                    () -> this.removeTreeItem(node));
            return;
        }

        if (selectedItem instanceof NulsAccount) {
            NulsAccount account = (NulsAccount) selectedItem;
            deleteItem("account", account.toString(),
                    () -> this.removeTreeItem(account));
            return;
        }

        if (selectedItem instanceof NulsContract) {
            NulsContract contract = (NulsContract) selectedItem;
            this.removeTreeItem(contract);
            int result = JOptionPane.showConfirmDialog(null,
                    String.format("你是否想终止合约 '%s' 吗?", contract.toString()),
                    "WARNING",
                    JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                replaceContentPanel(new DeleteContractPanel(project, this, logManager, contract));
            }
        }
    }

    private void deleteItem(String itemTypeLabel, String itemLabel, Runnable deleteOperation) {
        int result = JOptionPane.showConfirmDialog(null,
                String.format("你是否想删除收藏夹中的 '%s' %s 吗?", itemLabel, itemTypeLabel),
                "WARNING",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            deleteOperation.run();
        }
    }

    private void clickNode() {
        Object selectedItem = this.getSelectedItem();
        if (selectedItem instanceof NulsNode) {
            NulsNode node = (NulsNode) selectedItem;
            updateItem(() -> {
                UpdateNodeDialog uploadNodeDialog = new UpdateNodeDialog(project, this, node, node);
                uploadNodeDialog.setTitle("update a Nuls Node");
                uploadNodeDialog.setModal(false);
                uploadNodeDialog.show();
                if (!uploadNodeDialog.isOK()) {
                    return;
                }

            });
            return;
        }
        if (selectedItem instanceof NulsAccount) {
            NulsAccount account = (NulsAccount) selectedItem;
            updateItem(() -> {
                UpdateAccountDialog addAccountDialog = new UpdateAccountDialog(project, this, account, account);
                addAccountDialog.setTitle("update a Nuls Account");
                addAccountDialog.setModal(false);
                addAccountDialog.show();
                if (!addAccountDialog.isOK()) {
                    return;
                }
            });
            return;
        }
        if (selectedItem instanceof NulsContract) {
            NulsContract contract = (NulsContract) selectedItem;
            if (null == contract.getStatus() || !contract.getStatus()) {
                JOptionPane.showMessageDialog(null, "The Contract (" + contract.getAddress() + ") is fail!, not load info ", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            updateItem(() -> {
               /* LoadContractDialog runDialog = new LoadContractDialog(project, this, contract);
                runDialog.setTitle("Load");
                runDialog.show();*/
                replaceContentPanel(new LoadContractPanel(project, this, getLogManager(), contract));
            });
        }
    }

    private void updateItem(Runnable updateOperation) {
        updateOperation.run();
    }

    public synchronized void setJarFilePath(String jarFilePath) {
        ConfigStorage.getInstance(project).setJarFilePath(jarFilePath);
    }

    public synchronized void addTreeItem(TreeItem treeItem) {
        treeBuilder.addTreeItem(treeItem);
        treeDelBuilder.addTreeItem(treeItem);
        ConfigStorage.getInstance(project).addTreeItem(treeItem);
    }

    public synchronized void removeTreeItem(TreeItem treeItem) {
        treeBuilder.removeTreeItem(treeItem);
        treeDelBuilder.removeTreeItem(treeItem);
        ConfigStorage.getInstance(project).removeTreeItem(treeItem);
    }

    public Object getSelectedItem() {
        Set<Object> selectedElements = treeBuilder.getSelectedElements();
        if (selectedElements.isEmpty()) {
            return null;
        }
        return selectedElements.iterator().next();
    }

    public Object getDelSelectedItem() {
        Set<Object> selectedElements = treeDelBuilder.getSelectedElements();
        if (selectedElements.isEmpty()) {
            return null;
        }
        return selectedElements.iterator().next();
    }
}
