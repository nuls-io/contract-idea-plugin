package io.nuls.contract.idea.plugin.form;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import io.nuls.contract.idea.plugin.model.NulsContract;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

public class AddContractPanel extends JPanel {
    private JPanel rootPanel;
    private JTextField para01TextField;
    private JLabel feedbackLabel;
    private JLabel param01Label;
    private JTextField remarkTextField;
    private JLabel remarkLabel;

    private final Project project;

    private String errorInfo = "";

    public AddContractPanel(Project project) {
        this.project = project;
        setLayout(new BorderLayout());
        add(rootPanel);
    }

    public void loadNodeData(NulsContract contract) {
        if (contract == null) return;
        para01TextField.setText(contract.getAddress());
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
        if (StringUtils.isEmpty(agentAddress) || "the address".equals(agentAddress)) {
            this.errorInfo = "The address can not be null!";
            return false;
        }
        return true;
    }

    public NulsContract getEntitybyFields() {
        NulsContract contract = new NulsContract();
        contract.setAddress(para01TextField.getText());
        contract.setRemark(remarkTextField.getText());
        contract.setStatus(true);
        return contract;
    }
}
