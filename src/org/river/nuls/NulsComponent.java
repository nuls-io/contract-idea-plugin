package org.river.nuls;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import org.river.nuls.toolwindow.NulsWindowManager;

public class NulsComponent extends AbstractProjectComponent {

    public NulsComponent(Project project) {
        super(project);
    }

    public String getComponentName(){
        return "Nuls";
    }

    public void projectOpened(){
        NulsWindowManager.getInstance(myProject);
    }

    public void projectClosed(){
        NulsWindowManager.getInstance(myProject).unregisterMyself();
    }
}
