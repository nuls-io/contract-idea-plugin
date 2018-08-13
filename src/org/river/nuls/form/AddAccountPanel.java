package org.river.nuls.form;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;
import org.river.nuls.model.NulsAccount;

import javax.swing.*;
import java.awt.*;

public class AddAccountPanel extends JPanel {
    private JPanel rootPanel;
    private JTextField para01TextField;
    private JLabel feedbackLabel;
    private JLabel param01Label;
    private JLabel passLabel;
    private JPasswordField passwordField;
    private JTextField aliasTextField;
    private JLabel aliasLabel;

    private final Project project;

    private String errorInfo = "";

    public AddAccountPanel(Project project){
        this.project = project;
        setLayout(new BorderLayout());
        add(rootPanel);
    }

    public void loadAccountData(NulsAccount account){
        if (account == null) return;
        para01TextField.setText(account.getAddress());
    }

    public void setErrorMessage(String message) {
        feedbackLabel.setIcon(AllIcons.Ide.Error);
        feedbackLabel.setText(message);
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public boolean validateInputs(){
        String accountAddress = para01TextField.getText();
        char[] password = passwordField.getPassword();
        if (StringUtils.isEmpty(accountAddress) || "the address".equals(accountAddress)){
            this.errorInfo = "The account address can not be null!";
            return false;
        }
        //if (StringUtils.isEmpty(String.valueOf(password))){
        //    this.errorInfo = "The password can not be null!";
        //    return false;
        //}
        return true;
    }

    public NulsAccount getEntitybyFields(){
        NulsAccount account = new NulsAccount();
        account.setAddress(para01TextField.getText());
        account.setPassword(String.valueOf(passwordField.getPassword()));
        if (StringUtils.isNotEmpty(aliasTextField.getText())){
            account.setAlias(aliasTextField.getText());
        }
        return account;
    }
}
