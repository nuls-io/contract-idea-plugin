package io.nuls.contract.idea.plugin.template;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class TemplateManager {

    public static final List<String> TEMPLATE_NAMES = Arrays.asList(
            "Contract"
    );

    public static FileTemplate addTemplate(@NotNull final Project project, String templateName) {
        final FileTemplateManager fileTemplateManager = FileTemplateManager.getInstance(project);
        FileTemplate fileTemplate = fileTemplateManager.addTemplate(templateName, "java");
        InputStream inputStream = TemplateManager.class.getResourceAsStream("/templates/" + templateName + ".java.ft");
        try {
            String text = IOUtils.toString(inputStream, "utf8");
            fileTemplate.setText(text);
            return fileTemplate;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
