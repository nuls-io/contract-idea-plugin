package io.nuls.contract.idea.plugin.form;

import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import feign.Feign;
import feign.gson.GsonEncoder;
import feign.jaxrs.JAXRSContract;
import io.nuls.contract.idea.plugin.logic.LogManager;
import io.nuls.contract.idea.plugin.model.NulsAccount;
import io.nuls.contract.idea.plugin.model.NulsContract;
import io.nuls.contract.idea.plugin.model.NulsContractMethod;
import io.nuls.contract.idea.plugin.model.NulsNode;
import io.nuls.contract.rpc.form.ContractCall;
import io.nuls.contract.rpc.form.ImputedGasContractCall;
import io.nuls.contract.rpc.form.ImputedPrice;
import io.nuls.contract.rpc.resource.ContractResource;
import io.nuls.contract.rpc.resource.ResultfGsonDecoder;
import io.nuls.contract.vm.program.ProgramMethodArg;
import io.nuls.kernel.model.RpcClientResult;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class InvokePannel extends JPanel {
    private JPanel rootPanel;
    private JLabel gasLabel;
    private JTextField gasTextField;
    private JTextField priceTextField;
    private JTextField valueTextField;
    private JTextField remarkTextField;
    private JLabel priceLabel;
    private JLabel valueLabel;
    private JLabel remarkLabel;
    private JLabel feedbackLabel;
    private JLabel gasError;
    private JLabel priceError;
    private JLabel valueError;

    private final Project project;
    private final LoadContractPanel parent;
    private final LogManager logManager;
    private final NulsNode node;
    private final NulsAccount account;
    private final NulsContract contract;
    private final NulsContractMethod method;

    private Long defaultGas;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
    private Map<Integer, JTextField> argsTextFields;
    private Map<Integer, JLabel> errorLabels;

    public InvokePannel(Project project, LoadContractPanel parent, LogManager logManager,
                        NulsNode node, NulsAccount account, NulsContract contract, NulsContractMethod method) {
        this.project = project;
        this.parent = parent;
        this.logManager = logManager;
        this.node = node;
        this.account = account;
        this.contract = contract;
        this.method = method;

        setLayout(new BorderLayout());
        add(rootPanel);

        initDefultInfo();
    }

    private void initDefultInfo() {
        priceTextField.setText(getDefaultPrice() + "");
        defaultGas = getDefaultGas();
        gasTextField.setText(defaultGas + "");
        newLoadArgs();
    }

    private void newLoadArgs() {
        List<ProgramMethodArg> argsList = method.getArgs();
        if (Objects.isNull(argsList) || argsList.size() == 0) {
            return;
        }

        argsTextFields = new HashMap<Integer, JTextField>();
        errorLabels = new HashMap<Integer, JLabel>();
        for (int i = 0; i < argsList.size(); i++) {
            String name = argsList.get(i).getName() + ":";
            JLabel label = new JLabel();
            label.setText(name);
            JTextField textField = new JTextField();
            argsTextFields.put(i, textField);
            JLabel errorLabel = new JLabel();
            errorLabel.setForeground(Color.RED);
            errorLabels.put(i, errorLabel);
            rootPanel.add(label, new GridConstraints(i * 2, 0, 1, 1, 8, 0, 6, 0, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1)));
            rootPanel.add(textField, new GridConstraints(i * 2, 1, 1, 1, 8, 0, 6, 0, new Dimension(300, -1), new Dimension(300, -1), new Dimension(300, -1)));
            rootPanel.add(errorLabel, new GridConstraints((i * 2 + 1), 1, 1, 1, 8, 0, 6, 0, new Dimension(300, -1), new Dimension(300, -1), new Dimension(300, -1)));
        }

    }


    private Long getDefaultPrice() {
        ImputedPrice imputedPrice = new ImputedPrice();
        imputedPrice.setSender(account.getAddress());
        String nodeAddress = node.getAgentAddress();
        long currentMili = System.currentTimeMillis();

        if (StringUtils.isEmpty(imputedPrice.getSender()) || StringUtils.isEmpty(nodeAddress)) {
            return 1L;
        }

        logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEP-REQ " + imputedPrice.toString());
        RpcClientResult result = generateContractResource(nodeAddress).imputedPrice(imputedPrice);
        if (result.isSuccess()) {
            logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEP-RES " + result.getData().toString());
            Double price = (Double) result.getData();
            return price.longValue();
        } else {
            return 1L;
        }
    }

    private Long getDefaultGas() {
        //获取Gax默认值
        ImputedGasContractCall gasContractCreate = new ImputedGasContractCall();
        gasContractCreate.setSender(account.getAddress());
        gasContractCreate.setContractAddress(contract.getAddress());
        gasContractCreate.setPrice(Long.parseLong(priceTextField.getText()));
        gasContractCreate.setMethodName(method.getName());

        String nodeAddress = node.getAgentAddress();
        if (StringUtils.isEmpty(gasContractCreate.getSender()) || StringUtils.isEmpty(nodeAddress)) {
            return 10000l;
        }

        long currentMili = System.currentTimeMillis();
        logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEP-REQ " + gasContractCreate.toString());

        RpcClientResult result = generateContractResource(nodeAddress).imputedGasCallContract(gasContractCreate);
        if (result.isSuccess()) {
            logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEP-RES " + result.getData().toString());
            Map<String, Double> params = (Map<String, Double>) result.getData();
            return params.get("gasLimit").longValue();
        } else {
            return 10000l;
        }
    }

    private ContractResource generateContractResource(String nodeAddress) {
        return Feign.builder()
                .encoder(new GsonEncoder())
                .decoder(new ResultfGsonDecoder())
                .contract(new JAXRSContract())
                .target(ContractResource.class, "http://" + nodeAddress + "/api");
    }

   /* public void setErrorMessage(String message) {
        feedbackLabel.setIcon(AllIcons.Ide.Error);
        feedbackLabel.setText(message);
    }*/

    public boolean validateInputs() {
        Long gas = 0L;
        Long price = 0L;
        Long value = 0L;
        String strGas = gasTextField.getText();
        String strPrice = priceTextField.getText();
        String strValue = valueTextField.getText();

        List<ProgramMethodArg> argsList = method.getArgs();
        if (Objects.nonNull(argsList) && argsList.size() > 0) {
            for (int i = 0; i < argsList.size(); i++) {
                ProgramMethodArg arg = argsList.get(i);
                JTextField textField = argsTextFields.get(i);
                String text = textField.getText();
                if (arg.isRequired() && StringUtils.isEmpty(text)) {
                    errorLabels.get(i).setText(arg.getName() + " Can't be empty");
                    return false;
                } else {
                    errorLabels.get(i).setText("");
                }

                if ("initialAmount".equals(arg.getName())) {
                    BigInteger max = new BigInteger("2").pow(256).subtract(BigInteger.ONE);
                    try {
                        if (BigInteger.ONE.compareTo(new BigInteger(text)) == 1 || max.compareTo(new BigInteger(text)) == -1) {
                            errorLabels.get(i).setText(arg.getName() + " invalid");
                            return false;
                        }
                    } catch (Exception e) {
                        errorLabels.get(i).setText(arg.getName() + " invalid");
                        return false;
                    }

                } else if ("decimals".equals(arg.getName())) {
                    try {
                        if (Integer.parseInt(text) <= 0 || Integer.parseInt(text) > 16) {
                            errorLabels.get(i).setText(arg.getName() + " invalid");
                            return false;
                        }
                    } catch (Exception e) {
                        errorLabels.get(i).setText(arg.getName() + " invalid");
                        return false;
                    }
                }
            }
        }

        if (StringUtils.isNotEmpty(strGas)) {
            try {
                gas = Long.parseLong(strGas);
                if (gas < defaultGas) {
                    gasError.setText("Gas value is small, which may cause the call contract failure.");
                    return false;
                }
            } catch (Exception e) {
                gasError.setText("The gas must be a long value!");
                return false;
            }
        }
        gasError.setText("");

        if (StringUtils.isNotEmpty(strPrice)) {
            try {
                price = Long.parseLong(strPrice);
            } catch (Exception e) {
                priceError.setText("The price must be a long value!");
                return false;
            }
        }

        priceError.setText("");

        if (StringUtils.isNotEmpty(strValue)) {
            try {
                value = Long.parseLong(strValue);
            } catch (Exception e) {
                priceError.setText("The value must be a long value!");
                return false;
            }
        }

        priceError.setText("");
        return true;
    }

    public void doInvoke() {
        Long gas = 0L;
        Long price = 0L;
        Long value = 0L;
        String strGas = gasTextField.getText();
        String strPrice = priceTextField.getText();
        String strValue = valueTextField.getText();
        if (StringUtils.isNotEmpty(strGas)) {
            gas = Long.parseLong(strGas);
        }
        if (StringUtils.isNotEmpty(strPrice)) {
            price = Long.parseLong(strPrice);
        }
        if (StringUtils.isNotEmpty(strValue)) {
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

        String[] args = getArgsInfo();

        call.setArgs(args);
        parent.doInvoke(call, method.isView());
    }

    private String[] getArgsInfo() {
        List<ProgramMethodArg> argsList = method.getArgs();
        if (Objects.isNull(argsList) || argsList.size() == 0) {
            return null;
        }

        String[] args = new String[method.getArgs().size()];
        for (int i = 0; i < argsList.size(); i++) {
            JTextField textField = argsTextFields.get(i);
            String text = textField.getText();
            args[i] = text;
        }

        return args;
    }
}
