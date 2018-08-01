package org.river.nuls.logic;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.river.nuls.model.TreeItem;

import java.util.LinkedList;
import java.util.List;

public class TreeItemManager {

    private List<TreeItem> treeItems = new LinkedList<>();
    
    public static TreeItemManager getInstance(Project project) {
        return ServiceManager.getService(project, TreeItemManager.class);
    }

    public void cleanupTreeItems() {
        treeItems.clear();
    }

    public void registerTreeItem(TreeItem treeItem) {
        treeItems.add(treeItem);
    }

    public void removeTreeItem(TreeItem treeItem) {
        treeItems.remove(treeItem);
    }

    public List<TreeItem> getTreeItems() {
        return treeItems;
    }
}
