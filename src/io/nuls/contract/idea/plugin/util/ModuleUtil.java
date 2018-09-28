package io.nuls.contract.idea.plugin.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModulePointer;
import com.intellij.openapi.module.ModulePointerManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.LanguageLevelProjectExtension;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.impl.libraries.ProjectLibraryTable;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packaging.artifacts.ArtifactManager;
import com.intellij.packaging.artifacts.ArtifactType;
import com.intellij.packaging.artifacts.ModifiableArtifact;
import com.intellij.packaging.artifacts.ModifiableArtifactModel;
import com.intellij.packaging.elements.ArtifactRootElement;
import com.intellij.packaging.impl.artifacts.JarArtifactType;
import com.intellij.packaging.impl.elements.ArchivePackagingElement;
import com.intellij.packaging.impl.elements.ArtifactRootElementImpl;
import com.intellij.packaging.impl.elements.ProductionModuleOutputPackagingElement;
import com.intellij.pom.java.LanguageLevel;
import io.nuls.contract.idea.plugin.NulsConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ModuleUtil {

    private static final String LIB_NAME = "lib";

    public static Library addJar(final Module module, final String jarPath) {
        final Project project = module.getProject();
        final LibraryTable libraryTable = ProjectLibraryTable.getInstance(project);

        Library library = libraryTable.getLibraryByName(LIB_NAME);
        if (library == null) {
            library = libraryTable.createLibrary(LIB_NAME);
        }

        final Library.ModifiableModel libraryModifiableModel = library.getModifiableModel();
        if (StringUtils.isNotEmpty(jarPath)) {
            final VirtualFile file = JarFileSystem.getInstance().findLocalVirtualFileByPath(jarPath);
            if (file != null) {
                libraryModifiableModel.addRoot(file, OrderRootType.CLASSES);
            } else {
                Messages.showInfoMessage("don't add lib: " + jarPath, "Error Message");
            }
        }
        libraryModifiableModel.commit();

        return library;
    }

    public static void jdkVersion(final Module module) {
        Project project = null;
        if (module == null) {
            project = ProjectManager.getInstance().getDefaultProject();
        } else {
            project = module.getProject();
        }
        final LanguageLevelProjectExtension languageLevelProjectExtension = LanguageLevelProjectExtension.getInstance(project);
        languageLevelProjectExtension.setLanguageLevel(LanguageLevel.JDK_1_6);
        languageLevelProjectExtension.setDefault(false);
    }

    public static String addSdk(final Module module) {
        InputStream inputStream = ModuleUtil.class.getResourceAsStream("/sdk/" + NulsConstants.SDK);
        String basePath = module.getProject().getBasePath();
        String path = basePath + "/lib/" + NulsConstants.SDK;
        try {
            FileUtils.copyInputStreamToFile(inputStream, new File(path));
            return path;
        } catch (IOException e) {
            Messages.showInfoMessage(e.getMessage(), "Error Message");
        }
        return null;
    }

    public static void artifact(final Module module) {
        final Project project = module.getProject();

        final ModulePointer modulePointer = ModulePointerManager.getInstance(project).create(module);

        final ProductionModuleOutputPackagingElement moduleOutputPackagingElement = new ProductionModuleOutputPackagingElement(project, modulePointer);

        final ArchivePackagingElement archivePackagingElement = new ArchivePackagingElement();
        archivePackagingElement.setArchiveFileName("contract.jar");
        archivePackagingElement.addFirstChild(moduleOutputPackagingElement);

        final ArtifactRootElement artifactRootElement = new ArtifactRootElementImpl();
        artifactRootElement.addFirstChild(archivePackagingElement);

        final ModifiableArtifactModel modifiableArtifactModel = ArtifactManager.getInstance(project).createModifiableModel();
        final ArtifactType artifactType = JarArtifactType.getInstance();
        final ModifiableArtifact modifiableArtifact = modifiableArtifactModel.addArtifact("contract", artifactType);
        modifiableArtifact.setRootElement(artifactRootElement);
        modifiableArtifact.setBuildOnMake(true);
        modifiableArtifactModel.commit();
    }

}
