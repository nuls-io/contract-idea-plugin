package io.nuls.contract.idea.plugin.form;

import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import feign.Feign;
import feign.gson.GsonEncoder;
import feign.jaxrs.JAXRSContract;
import io.nuls.contract.entity.ContractInfoDto;
import io.nuls.contract.idea.plugin.logic.LogManager;
import io.nuls.contract.idea.plugin.model.ConfigStorage;
import io.nuls.contract.idea.plugin.model.NulsAccount;
import io.nuls.contract.idea.plugin.model.NulsContract;
import io.nuls.contract.idea.plugin.model.TreeItem;
import io.nuls.contract.idea.plugin.toolwindow.ui.NulsToolWindowPanel;
import io.nuls.contract.idea.plugin.util.JsonFormater;
import io.nuls.contract.rpc.form.*;
import io.nuls.contract.rpc.model.ContractTransactionDto;
import io.nuls.contract.rpc.resource.ContractResource;
import io.nuls.contract.rpc.resource.ResultfGsonDecoder;
import io.nuls.contract.rpc.result.ContractCreateResult;
import io.nuls.contract.vm.program.ProgramMethod;
import io.nuls.contract.vm.program.ProgramMethodArg;
import io.nuls.kernel.model.RpcClientResult;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class RunPanel extends JPanel {
    private JPanel rootPanel;
    private JLabel nodeLabel;
    private JComboBox nodeComboBox;
    private JLabel accountLabel;
    private JComboBox accountComboBox;
    private JLabel gasLabel;
    private JTextField gasTextField;
    private JButton deployButton;
    private JList methodList;
    private JLabel priceLabel;
    private JLabel remarkLabel;
    private JTextField priceTextField;
    private JTextField remarkTextField;
    private JScrollPane methodsScrollPane;
    private JLabel hashLabel;
    //    private JLabel txnHashLabel;
    private JTextField jarFileTextField;
    private JLabel jarFileLabel;
    //    private JLabel message;
    private JLabel errorTips;
    private JLabel priceError;
    private JLabel gasError;
    private JLabel nodeError;
    private JLabel accountError;
    private JLabel jarFilePathError;
    private JLabel addtionError;
    private JPanel paramsPanel;
    private JCheckBox seniorCheckBox;
    private JButton testDeployButton;
    private JLabel testTips;
    private JTextArea message;
    private JTextField txnHashLabel;
    private JLabel nbspLabel;

    private final Project project;
    private final NulsToolWindowPanel nulsToolWindowPanel;
    private final LogManager logManager;

    private String latestUsedNode = "";
    private String latestLoadedContractNode = "";
    private String latestLoadedContractAddress = "";
    private Long defaultGas;
    private NulsContract nulsContract;
    private Long gas;
    private Long price;

    private Map<Integer, JTextField> argsTextFields;
    private Map<Integer, JLabel> errorLabels;
    private List<ProgramMethodArg> argsList;
    private boolean isNrc20;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

    public RunPanel(Project project, NulsToolWindowPanel nulsToolWindowPanel, LogManager logManager) {
        this.project = project;
        this.nulsToolWindowPanel = nulsToolWindowPanel;
        this.logManager = logManager;
//        setSize(new Dimension(800, 600)); // 无效
        refreshComboBoxes();
        setLayout(new BorderLayout());
        add(rootPanel);

        initListeners();
        paramsPanel.setVisible(false);
//        seniorCheckBox.setVisible(false);
        message.setEditable(false);
        txnHashLabel.setEditable(false);
    }

    private void refreshComboBoxes() {
        TreeItem nodeSelected = (TreeItem) nodeComboBox.getSelectedItem();
        TreeItem accountSelected = (TreeItem) accountComboBox.getSelectedItem();
        accountComboBox.removeAllItems();
        nodeComboBox.removeAllItems();
        ConfigStorage storage = ConfigStorage.getInstance(project);
        for (TreeItem item : storage.getNulsNodes()) {
            nodeComboBox.addItem(item);
        }
        for (TreeItem item : storage.getNulsAccounts()) {
            accountComboBox.addItem(item);
        }

        if (nodeSelected != null) {
            nodeComboBox.setSelectedItem(nodeSelected);
        }
        if (accountSelected != null) {
            accountComboBox.setSelectedItem(accountSelected);
        }
        if (storage.getJarFilePath() != null) {
            jarFileTextField.setText(storage.getJarFilePath());
        }

        getDefaultArgs();
        initInfo();

    }

    private void initInfo() {
        if (null == price) {
            price = getDefaultPrice();
            if (null != price && StringUtils.isEmpty(priceTextField.getText())) {
                priceTextField.setText(price + "");
            }
        }
        if (null != price && null == defaultGas) {
            defaultGas = getDefaultGas();
        }
        if (null != defaultGas && StringUtils.isEmpty(gasTextField.getText())) {
            gas = defaultGas;
            gasTextField.setText(gas + "");
        }
    }

    private void getDefaultArgs() {
        String nodeAddress = nodeComboBox.getSelectedItem() == null ? "" : nodeComboBox.getSelectedItem().toString();
        ContractCode contractCode = new ContractCode();
        String code = "";
        try {
            code = Hex.encodeHexString(FileUtils.readFileToByteArray(FileUtils.getFile(jarFileTextField.getText())));
        } catch (Exception e) {
        }
        contractCode.setContractCode(code);
        RpcClientResult<ContractInfoDto> result = generateContractResource(nodeAddress).contractConstructor(contractCode);
        if (result.isSuccess()) {
            ProgramMethod method = result.getData().getConstructor();
            isNrc20 = result.getData().isNrc20();
            argsList = method.getArgs();
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
                rootPanel.add(label, new GridConstraints((i + 6) * 2, 0, 1, 1, 8, 0, 6, 0, null, null, null));
                rootPanel.add(textField, new GridConstraints((i + 6) * 2, 2, 1, 1, 8, 0, 6, 0, new Dimension(-1, -1), new Dimension(270, -1), new Dimension(-1, -1)));
                rootPanel.add(errorLabel, new GridConstraints(((i + 6) * 2 + 1), 2, 1, 1, 8, 0, 6, 0, new Dimension(-1, -1), new Dimension(270, -1), new Dimension(-1, -1)));
            }
        }

    }

    private boolean checkGasInfo() {
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

                if (isNrc20) {
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
        }
        return true;
    }

    private String[] getArgsInfo() {
        if (Objects.isNull(argsList) || argsList.size() == 0) {
            return null;
        }

        String[] args = new String[argsList.size()];
        for (int i = 0; i < argsList.size(); i++) {
            JTextField textField = argsTextFields.get(i);
            String text = textField.getText();
            args[i] = text;
        }

        return args;
    }

    private Long getDefaultPrice() {
        ImputedPrice imputedPrice = new ImputedPrice();
        String accountAddress = accountComboBox.getSelectedItem() == null ? "" : accountComboBox.getSelectedItem().toString();
        imputedPrice.setSender(accountAddress);

        String nodeAddress = nodeComboBox.getSelectedItem() == null ? "" : nodeComboBox.getSelectedItem().toString();

        if (StringUtils.isEmpty(accountAddress) || StringUtils.isEmpty(nodeAddress)) {
            return null;
        }

        long currentMili = System.currentTimeMillis();
        logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEP-REQ " + imputedPrice.toString());
        RpcClientResult result = generateContractResource(nodeAddress).imputedPrice(imputedPrice);
        if (result.isSuccess()) {
            logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEP-RES " + result.getData().toString());
            Double price = (Double) result.getData();
            return price.longValue();
        } else {
            return null;
        }
    }

    private Long getDefaultGas() {
        //获取Gax默认值
        ImputedGasContractCreate gasContractCreate = new ImputedGasContractCreate();
        String accountAddress = accountComboBox.getSelectedItem() == null ? "" : accountComboBox.getSelectedItem().toString();
        gasContractCreate.setSender(accountAddress);
        gasContractCreate.setPrice(price);
        gasContractCreate.setArgs(getArgsInfo());
        String code = "";
        try {
            code = Hex.encodeHexString(FileUtils.readFileToByteArray(FileUtils.getFile(jarFileTextField.getText())));
        } catch (Exception e) {
            return null;
        }
        gasContractCreate.setContractCode(code);
        String nodeAddress = nodeComboBox.getSelectedItem() == null ? "" : nodeComboBox.getSelectedItem().toString();
        if (StringUtils.isEmpty(accountAddress) || StringUtils.isEmpty(nodeAddress) || !checkGasInfo()) {
            return null;
        }

        long currentMili = System.currentTimeMillis();
        logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEP-REQ " + gasContractCreate.toString());
        RpcClientResult result = generateContractResource(nodeAddress).imputedGasCreateContract(gasContractCreate);
        if (result.isSuccess()) {
            logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEP-RES " + result.getData().toString());
            Map<String, Double> params = (Map<String, Double>) result.getData();
            return params.get("gasLimit").longValue();
        } else {
            return null;
        }
    }

    private void initListeners() {
        seniorCheckBox.addActionListener(actionEvent -> {
            if (seniorCheckBox.isSelected()) {
                paramsPanel.setVisible(true);
            } else {
                paramsPanel.setVisible(false);
            }
        });

        testDeployButton.addActionListener(actionEvent -> {
            testTips.setText("");
            errorTips.setText("");
            message.setText("");
            String nodeAddress = nodeComboBox.getSelectedItem() == null ? "" : nodeComboBox.getSelectedItem().toString();
            String accountAddress = accountComboBox.getSelectedItem() == null ? "" : accountComboBox.getSelectedItem().toString();
            String jarFilePath = jarFileTextField.getText();
            String remark = remarkTextField.getText();

            if (StringUtils.isEmpty(nodeAddress)) {
//                JOptionPane.showMessageDialog(null, "No Node Selected!", "ERROR", JOptionPane.ERROR_MESSAGE);
                nodeError.setText("No Node Selected!");
                return;
            } else {
                nodeError.setText("");
            }
            if (StringUtils.isEmpty(accountAddress)) {
                accountError.setText("No Account Selected!");
                return;
            } else {
                accountError.setText("");
            }
            if (StringUtils.isEmpty(jarFilePath)) {
                jarFilePathError.setText("File path should be a jar file full path!");
                return;
            } else {
                jarFilePathError.setText("");
            }

            if (!checkGasInfo()) {
                return;
            }
            initInfo();

            if (StringUtils.isNotEmpty(gasTextField.getText())) {
                try {
                    gas = Long.parseLong(gasTextField.getText());
                    if (defaultGas != null && gas < defaultGas) {
                        gasError.setText("Gas value is small, which may cause the call contract failure.");
                        return;
                    } else {
                        gasError.setText("");
                    }
                } catch (Exception e) {
                    gasError.setText("Invalid param gas!");
                    return;
                }
            } else {
                gasError.setText("Invalid param gas!");
                return;
            }

            if (StringUtils.isNotEmpty(priceTextField.getText())) {
                try {
                    price = Long.parseLong(priceTextField.getText());
                    priceError.setText("");
                } catch (Exception e) {
                    priceError.setText("Invalid param price!");
                    return;
                }
            } else {
                priceError.setText("Invalid param price!");
                return;
            }

            if (StringUtils.isNotEmpty(remark) && remark.length() > 60) {
                addtionError.setText("Additional information can not exceed 60 characters.");
                return;
            } else {
                addtionError.setText("");
            }

            String code = "";
            try {
                code = Hex.encodeHexString(FileUtils.readFileToByteArray(FileUtils.getFile(jarFilePath)));
            } catch (Exception e) {
                errorTips.setText("Bad Contract File!");
                return;
            }
            nulsToolWindowPanel.setJarFilePath(jarFilePath);

            PreContractCreate create = new PreContractCreate();
            create.setSender(accountAddress);
            create.setGasLimit(gas);
            create.setPrice(price);
            create.setRemark(remark);
            create.setArgs(getArgsInfo());
            create.setContractCode(code);

            long currentMili = System.currentTimeMillis();
            logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEP-REQ " + create.toString());
            RpcClientResult<ContractCreateResult> result = generateContractResource(nodeAddress).preCreateContract(create);
            if (result.isSuccess()) {
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEP-RES " + " success!");
                testTips.setText("Success!");
                testTips.setForeground(Color.BLACK);
            } else {
//                JOptionPane.showMessageDialog(null, "Creation Failed! " + result.getErrorData().getMsg(), "ERROR", JOptionPane.ERROR_MESSAGE);
                testTips.setText("Creation Failed! " + result.getErrorData().getMsg());
                testTips.setForeground(Color.RED);
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEP-RES " + result.getErrorData());
                return;
            }

        });
        deployButton.addActionListener(actionEvent -> {
            testTips.setText("");
            errorTips.setText("");
            message.setText("");
            String nodeAddress = nodeComboBox.getSelectedItem() == null ? "" : nodeComboBox.getSelectedItem().toString();
            String accountAddress = accountComboBox.getSelectedItem() == null ? "" : accountComboBox.getSelectedItem().toString();
            String jarFilePath = jarFileTextField.getText();
            String remark = remarkTextField.getText();
            if (StringUtils.isEmpty(nodeAddress)) {
//                JOptionPane.showMessageDialog(null, "No Node Selected!", "ERROR", JOptionPane.ERROR_MESSAGE);
                nodeError.setText("No Node Selected!");
                return;
            } else {
                nodeError.setText("");
            }
            if (StringUtils.isEmpty(accountAddress)) {
                accountError.setText("No Account Selected!");
                return;
            } else {
                accountError.setText("");
            }
            if (StringUtils.isEmpty(jarFilePath)) {
                jarFilePathError.setText("File path should be a jar file full path!");
                return;
            } else {
                jarFilePathError.setText("");
            }

            if (!checkGasInfo()) {
                return;
            }
            initInfo();

            if (StringUtils.isNotEmpty(gasTextField.getText())) {
                try {
                    gas = Long.parseLong(gasTextField.getText());
                    if (defaultGas != null && gas < defaultGas) {
                        gasError.setText("Gas value is small, which may cause the call contract failure.");
                        return;
                    } else {
                        gasError.setText("");
                    }
                } catch (Exception e) {
                    gasError.setText("Invalid param gas!");
                    return;
                }
            } else {
                gasError.setText("Invalid param gas!");
                return;
            }

            if (StringUtils.isNotEmpty(priceTextField.getText())) {
                try {
                    price = Long.parseLong(priceTextField.getText());
                    priceError.setText("");
                } catch (Exception e) {
                    priceError.setText("Invalid param price!");
                    return;
                }
            } else {
                priceError.setText("Invalid param price!");
                return;
            }

            if (StringUtils.isNotEmpty(remark) && remark.length() > 60) {
                addtionError.setText("Additional information can not exceed 60 characters.");
                return;
            } else {
                addtionError.setText("");
            }

            String code = "";
            try {
                code = Hex.encodeHexString(FileUtils.readFileToByteArray(FileUtils.getFile(jarFilePath)));
            } catch (Exception e) {
                errorTips.setText("Bad Contract File!");
                return;
            }
            nulsToolWindowPanel.setJarFilePath(jarFilePath);

            String password = ((NulsAccount) accountComboBox.getSelectedItem()).getPassword();
            if (StringUtils.isEmpty(password)) {
                int result = JOptionPane.showConfirmDialog(null,
                        "Is there no password in the account to confirm the transaction?",
                        "WARNING",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.NO_OPTION) {
                    JPasswordField pwd = new JPasswordField();
                    Object[] message = {"Please input a password:", pwd};
                    int resultPwd = JOptionPane.showConfirmDialog(null, message, "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (resultPwd == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                    password = pwd.getText();
                }
            }

            // 创建合约参数对象
            ContractCreate create = new ContractCreate();
            create.setSender(accountAddress);
            create.setPassword(password);
            create.setGasLimit(gas);
            create.setPrice(price);
            create.setRemark(remark);
            create.setArgs(getArgsInfo());
            create.setContractCode(code);
            long currentMili = System.currentTimeMillis();
            logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEP-REQ " + create.toString());
            RpcClientResult<ContractCreateResult> result = generateContractResource(nodeAddress).createContract(create);
            if (result.isSuccess()) {
                errorTips.setText("");
                latestUsedNode = nodeAddress;
               /* JOptionPane.showMessageDialog(null,
                        "Creation invoked successfully, Please do the further check by clicking the transaction hash.",
                        "INFO", JOptionPane.INFORMATION_MESSAGE);*/
                // 结果处理
                ContractCreateResult resultObject = result.getData();
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEP-RES " + resultObject.toString());
                // 记录交易Hash
                String txnHash = resultObject.getTxHash();
                txnHashLabel.setText(txnHash);
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " [HASH]  " + txnHash);
                // 通过结果对象封装合约
                nulsContract = new NulsContract();
                nulsContract.setAddress(resultObject.getContractAddress());
                nulsContract.setRemark(remark);
                nulsContract.setStatus(true);
                // 更新ToolWindow
                nulsToolWindowPanel.addTreeItem(nulsContract);
                // 更新运行对话框下拉列表
                refreshComboBoxes();

                errorTips.setText("Confirming......");
                errorTips.setForeground(Color.BLACK);

                new Thread() {
                    public void run() {
                        obtainResult();
                    }
                }.start();
            } else {
//                JOptionPane.showMessageDialog(null, "Creation Failed! " + result.getErrorData().getMsg(), "ERROR", JOptionPane.ERROR_MESSAGE);
                errorTips.setText("Creation Failed! " + result.getErrorData().getMsg());
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEP-RES " + result.getErrorData());
                return;
            }
        });

        txnHashLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //预防重复发起事件
                if (!"Confirming......".equals(errorTips.getText()) && !"Success!".equals(errorTips.getText()) && txnHashLabel.getText().length() > 0) {
                    System.out.println("111...............");
                    obtainResult();
                }
            }
        });
    }

    private void obtainResult() {
        boolean status = checkResult();
        while (!status) {
            status = checkResult();
        }
    }

    private Boolean checkResult() {
        String txnHash = txnHashLabel.getText();
        if (StringUtils.isEmpty(txnHash)) return true;
        try {
            RpcClientResult<ContractTransactionDto> result = generateContractResource(latestUsedNode).getContractTx(txnHash);
            String realStatus = "F";
            String errorMessage = "-";
            String resultInfo = "";
            if (result.isSuccess()) {
                ContractTransactionDto dto = result.getData();
                if (dto == null) {
                    errorMessage = "No Response.";
                } else {
                    if (dto.getStatus() == 1) {
                        if (dto.getContractResult() == null) {
                            errorMessage = "No Contract Result.";
                        } else {
                            if (!dto.getContractResult().isSuccess()) {
                                errorMessage = dto.getContractResult().getErrorMessage();
                            } else {
                                realStatus = dto.getContractResult().getResult();
                                realStatus = StringUtils.isEmpty(realStatus) ? "-" : realStatus;
                                resultInfo = result.getJson();
                            }
                        }
                    }
                }
            } else {
                if (result.getErrorData() == null) {
//                errorMessage = "Unknown.";
                } else {
                    errorMessage = result.getErrorData().getMsg() + "(" + result.getErrorData().getCode() + ")";
                }
            }
            if ("F".equals(realStatus)) {
                if ("-".equals(errorMessage)) {
                    return false;
                } else {
                    errorTips.setText(errorMessage);
                    errorTips.setForeground(Color.RED);

                    // 更新ToolWindow
                    NulsContract oldContract = nulsContract;
                    nulsToolWindowPanel.removeTreeItem(oldContract);
                    nulsContract.setStatus(false);
                    nulsToolWindowPanel.addTreeItem(nulsContract);

                    return true;
                }
            } else {
                // 合约发布成功,返回结果值
//                message.setText("<html>" + JsonFormater.format(resultInfo).replace("\n", "<br/>").replace(" ", "&nbsp;") + "</html>");

                message.setText(JsonFormater.format(resultInfo));
                errorTips.setText("Success!");
                errorTips.setForeground(Color.BLACK);

                return true;
            }
        } catch (Exception e) {
            errorTips.setText("The update result failed. Please click txnHash again.");
            errorTips.setForeground(Color.RED);
            return true;
        }
    }

    private ContractResource generateContractResource(String nodeAddress) {
        return Feign.builder()
                .encoder(new GsonEncoder())
                .decoder(new ResultfGsonDecoder())
                .contract(new JAXRSContract())
                .target(ContractResource.class, "http://" + nodeAddress + "/api");
    }
}
