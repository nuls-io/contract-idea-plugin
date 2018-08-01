package org.river.nuls.model;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;

import java.util.LinkedList;
import java.util.List;

@State(
        name = "NulsConfiguration",
        storages = {
                @Storage(file = "$PROJECT_FILE$"),
                @Storage(file = "$PROJECT_CONFIG_DIR$/nulsSettings.xml", scheme = StorageScheme.DIRECTORY_BASED)
        }
)
public class NulsConfiguration implements PersistentStateComponent<NulsConfiguration> {

    private List<TreeItem> treeItems = new LinkedList<>();

    public static NulsConfiguration getInstance(Project project) {
        return ServiceManager.getService(project, NulsConfiguration.class);
    }

    public NulsConfiguration getState() {
        return this;
    }

    public void loadState(NulsConfiguration nulsConfiguration) {
        XmlSerializerUtil.copyBean(nulsConfiguration, this);
    }

    public List<TreeItem> getTreeItems() {
        return treeItems;
    }
}
