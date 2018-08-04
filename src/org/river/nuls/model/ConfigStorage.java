package org.river.nuls.model;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;

@State(
        name = "NulsSettings",
        storages = {
                @Storage(file = "$PROJECT_FILE$"),
                @Storage(file = "$PROJECT_CONFIG_DIR$/nulsSettings.xml", scheme = StorageScheme.DIRECTORY_BASED)
        }
)

public class ConfigStorage implements PersistentStateComponent<ConfigStorage> {

    private boolean nulsSyntaxCheck = true;

    public static ConfigStorage getInstance(Project project){
        return ServiceManager.getService(project, ConfigStorage.class);
    }

    public ConfigStorage getState(){
        return this;
    }

    public void loadState(ConfigStorage configStorage){
        XmlSerializerUtil.copyBean(configStorage, this);
    }

    public boolean isNulsSyntaxCheck() {
        return nulsSyntaxCheck;
    }

    public void setNulsSyntaxCheck(boolean nulsSyntaxCheck) {
        this.nulsSyntaxCheck = nulsSyntaxCheck;
    }
}
