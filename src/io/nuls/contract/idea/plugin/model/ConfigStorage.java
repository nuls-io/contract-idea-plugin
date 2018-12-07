package io.nuls.contract.idea.plugin.model;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;

import java.util.LinkedList;
import java.util.List;

@State(
        name = "NulsSettings",
        storages = {
                @Storage(file = "$PROJECT_FILE$"),
                @Storage(file = "$PROJECT_CONFIG_DIR$/nulsSettings.xml", scheme = StorageScheme.DIRECTORY_BASED)
        }
)

public class ConfigStorage implements PersistentStateComponent<ConfigStorage> {

    private List<NulsNode> nulsNodes = new LinkedList<>();
    private List<NulsAccount> nulsAccounts = new LinkedList<>();
    private List<NulsContract> nulsContracts = new LinkedList<>();

    private boolean nulsSyntaxCheck = false;

    private String jarFilePath;

    public static ConfigStorage getInstance(Project project) {
        return ServiceManager.getService(project, ConfigStorage.class);
    }

    public ConfigStorage getState() {
        return this;
    }

    public void loadState(ConfigStorage configStorage) {
        XmlSerializerUtil.copyBean(configStorage, this);
    }

    public boolean isNulsSyntaxCheck() {
        return nulsSyntaxCheck;
    }

    public void setNulsSyntaxCheck(boolean nulsSyntaxCheck) {
        this.nulsSyntaxCheck = nulsSyntaxCheck;
    }

    public void setJarFilePath(String jarFilePath) {
        this.jarFilePath = jarFilePath;
    }

    public String getJarFilePath() {
        return jarFilePath;
    }

    public List<NulsNode> getNulsNodes() {
        return nulsNodes;
    }

    public void setNulsNodes(List<NulsNode> nulsNodes) {
        this.nulsNodes = nulsNodes;
    }

    public List<NulsAccount> getNulsAccounts() {
        return nulsAccounts;
    }

    public void setNulsAccounts(List<NulsAccount> nulsAccounts) {
        this.nulsAccounts = nulsAccounts;
    }

    public List<NulsContract> getNulsContracts() {
        return nulsContracts;
    }

    public void setNulsContracts(List<NulsContract> nulsContracts) {
        this.nulsContracts = nulsContracts;
    }

    public void addTreeItem(TreeItem treeItem) {
        if (treeItem instanceof NulsNode) {
            nulsNodes.add((NulsNode) treeItem);
        }
        if (treeItem instanceof NulsAccount) {
            nulsAccounts.add((NulsAccount) treeItem);
        }
        if (treeItem instanceof NulsContract) {
            nulsContracts.add((NulsContract) treeItem);
        }
    }

    public void removeTreeItem(TreeItem treeItem) {
        if (treeItem instanceof NulsNode) {
            nulsNodes.remove(treeItem);
        }
        if (treeItem instanceof NulsAccount) {
            nulsAccounts.remove(treeItem);
        }
        if (treeItem instanceof NulsContract) {
            nulsContracts.remove(treeItem);
        }
    }

    public List<TreeItem> getTreeItems() {
        List<TreeItem> treeItems = new LinkedList<>();
        for (NulsNode item : nulsNodes) {
            treeItems.add(item);
        }
        for (NulsAccount item : nulsAccounts) {
            treeItems.add(item);
        }
        for (NulsContract item : nulsContracts) {
            treeItems.add(item);
        }
        return treeItems;
    }
}
