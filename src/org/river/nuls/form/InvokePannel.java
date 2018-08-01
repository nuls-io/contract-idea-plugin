package org.river.nuls.form;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import feign.Feign;
import feign.gson.GsonEncoder;
import feign.jaxrs.JAXRSContract;
import io.nuls.contract.rpc.form.ContractCall;
import io.nuls.contract.rpc.resource.ContractResource;
import io.nuls.contract.rpc.resource.ResultfGsonDecoder;
import io.nuls.kernel.model.RpcClientResult;
import org.apache.commons.lang3.StringUtils;
import org.river.nuls.logic.LogManager;
import org.river.nuls.model.NulsAccount;
import org.river.nuls.model.NulsContract;
import org.river.nuls.model.NulsContractMethod;
import org.river.nuls.model.NulsNode;
import org.river.nuls.toolwindow.ui.NulsToolWindowPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class InvokePannel extends JPanel {
    private JPanel rootPanel;
    private JTextField argsTextField;
    private JTextField gasTextField;
    private JTextField priceTextField;
    private JTextField valueTextField;
    private JTextField remarkTextField;
    private JLabel argsLabel;
    private JLabel gasLabel;
    private JLabel priceLabel;
    private JLabel valueLabel;
    private JLabel remarkLabel;
    private JLabel feedbackLabel;

    private final Project project;
    private final RunPanel parent;
    private final LogManager logManager;
    private final NulsNode node;
    private final NulsAccount account;
    private final NulsContract contract;
    private final NulsContractMethod method;

    private String errorInfo = "";

    public InvokePannel(Project project, RunPanel parent, LogManager logManager,
                        NulsNode node, NulsAccount account, NulsContract contract, NulsContractMethod method){
        this.project = project;
        this.parent = parent;
        this.logManager = logManager;
        this.node = node;
        this.account = account;
        this.contract = contract;
        this.method = method;

        setLayout(new BorderLayout());
        add(rootPanel);
    }

    public void setErrorMessage(String message) {
        feedbackLabel.setIcon(AllIcons.Ide.Error);
        feedbackLabel.setText(message);
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public boolean validateInputs(){
        Long gas = 0L;
        Long price = 0L;
        Long value = 0L;
        String strGas = gasTextField.getText();
        String strPrice = priceTextField.getText();
        String strValue = valueTextField.getToolTipText();
        if (StringUtils.isNotEmpty(strGas)){
            try{
                gas = Long.parseLong(strGas);
            }catch (Exception e){
                this.errorInfo = "The gas must be a long value!";
                return false;
            }
        }
        if (StringUtils.isNotEmpty(strPrice)){
            try{
                price = Long.parseLong(strPrice);
            }catch (Exception e){
                this.errorInfo = "The price must be a long value!";
                return false;
            }
        }
        if (StringUtils.isNotEmpty(strValue)){
            try{
                value = Long.parseLong(strValue);
            }catch (Exception e){
                this.errorInfo = "The 'value' must be a long value!";
                return false;
            }
        }
        return true;
    }

    public void doInvoke(){
        Long gas = 0L;
        Long price = 0L;
        Long value = 0L;
        String strGas = gasTextField.getText();
        String strPrice = priceTextField.getText();
        String strValue = valueTextField.getToolTipText();
        if (StringUtils.isNotEmpty(strGas)){
            gas = Long.parseLong(strGas);
        }
        if (StringUtils.isNotEmpty(strPrice)){
            price = Long.parseLong(strPrice);
        }
        if (StringUtils.isNotEmpty(strValue)){
            value = Long.parseLong(strValue);
        }
        ContractCall call = new ContractCall();
        call.setSender(account.getAddress());
        call.setPassword(account.getPassword());
        call.setGasLimit(gas);
        call.setPrice(price);
        call.setValue(value);
        call.setRemark(remarkTextField.getText());
        call.setContractAddress(contract.getAddress());
        call.setMethodName(method.getName());
        call.setMethodDesc(method.getDesc());
        String[] args = StringUtils.isEmpty(argsTextField.getText()) ? null : argsTextField.getText().split(",");
        call.setArgs(args);
        parent.doInvoke(call);
    }
}
