package io.nuls.contract.idea.plugin.form;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import io.nuls.contract.idea.plugin.model.NulsNode;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

public class AddNodePanel extends JPanel {
    private JPanel rootPanel;
    private JTextField para01TextField;
    private JLabel feedbackLabel;
    private JLabel param01Label;
    private JTextField remarkTextField;
    private JLabel remarkLabel;

    private final Project project;

    private String errorInfo = "";

    public AddNodePanel(Project project) {
        this.project = project;
        setLayout(new BorderLayout());
        add(rootPanel);
    }

    public void loadNodeData(NulsNode node) {
        if (node == null) return;
        para01TextField.setText(node.getAgentAddress());
    }

    public void setErrorMessage(String message) {
        feedbackLabel.setIcon(AllIcons.Ide.Error);
        feedbackLabel.setText(message);
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public boolean validateInputs() {
        String agentAddress = para01TextField.getText();
        if (StringUtils.isEmpty(agentAddress)) {
            this.errorInfo = "The agent address can not be null!";
            return false;
        }
        return true;
    }

    public NulsNode getEntitybyFields() {
        NulsNode node = new NulsNode();
        node.setAgentAddress(para01TextField.getText());
        node.setRemark(remarkTextField.getText());
        return node;
    }

}
