package io.nuls.contract.idea.plugin.form;

import com.intellij.openapi.project.Project;
import feign.Feign;
import feign.gson.GsonEncoder;
import feign.jaxrs.JAXRSContract;
import io.nuls.contract.idea.plugin.logic.LogManager;
import io.nuls.contract.idea.plugin.model.*;
import io.nuls.contract.idea.plugin.toolwindow.ui.InvokeDialog;
import io.nuls.contract.idea.plugin.toolwindow.ui.NulsToolWindowPanel;
import io.nuls.contract.idea.plugin.util.JsonFormater;
import io.nuls.contract.rpc.form.ContractCall;
import io.nuls.contract.rpc.model.ContractTransactionDto;
import io.nuls.contract.rpc.resource.ContractResource;
import io.nuls.contract.rpc.resource.ResultfGsonDecoder;
import io.nuls.contract.rpc.result.ContractInfoResult;
import io.nuls.contract.vm.program.ProgramMethod;
import io.nuls.contract.vm.program.ProgramMethodArg;
import io.nuls.kernel.model.RpcClientResult;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class LoadContractPanel extends JPanel {
    private JPanel contentPane;
    private JLabel contractLabel;
    private JComboBox contractComboBox;
    private JList methodList;
    private JLabel nodeLabel;
    private JComboBox nodeComboBox;
    private JComboBox accountComboBox;
    private JLabel accountLabel;
    private JLabel hashLabel;
    //    private JLabel txnHashLabel;
    private JLabel nodeError;
    private JLabel accountError;
    private JLabel contractError;
    private JLabel message;
    private JScrollPane methodsScrollPane;
    private JTextArea resultMessage;
    private JTextField txnHashLabel;
//    private JLabel resultMessage;

    private final Project project;
    private final NulsToolWindowPanel nulsToolWindowPanel;
    private final LogManager logManager;
    private NulsContract contract;

    private String latestUsedNode = "";
    private String latestLoadedContractNode = "";
    private String latestLoadedContractAddress = "";

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

    public LoadContractPanel(Project project, NulsToolWindowPanel nulsToolWindowPanel, LogManager logManager, NulsContract contract) {
        this.project = project;
        this.nulsToolWindowPanel = nulsToolWindowPanel;
        this.logManager = logManager;
        this.contract = contract;
        refreshComboBoxes();
        setLayout(new BorderLayout());
        add(contentPane);
        initListener();
        resultMessage.setEditable(false);
        txnHashLabel.setEditable(false);
    }

    private void refreshComboBoxes() {
        TreeItem nodeSelected = (TreeItem) nodeComboBox.getSelectedItem();
        TreeItem accountSelected = (TreeItem) accountComboBox.getSelectedItem();
        accountComboBox.removeAllItems();
        nodeComboBox.removeAllItems();
        contractComboBox.removeAllItems();
        ConfigStorage storage = ConfigStorage.getInstance(project);
        for (TreeItem item : storage.getNulsNodes()) {
            nodeComboBox.addItem(item);
        }
        for (TreeItem item : storage.getNulsAccounts()) {
            accountComboBox.addItem(item);
        }
        for (TreeItem item : storage.getNulsContracts()) {
            contractComboBox.addItem(item);
        }
        if (nodeSelected != null) {
            nodeComboBox.setSelectedItem(nodeSelected);
        }
        if (accountSelected != null) {
            accountComboBox.setSelectedItem(accountSelected);
        }
        if (contract != null) {
            contractComboBox.setSelectedItem(contract);
        }

        changeContract();
    }

    private void initListener() {
        contractComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                //如果选中了一个
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    changeContract();
                }
            }
        });

        methodList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JList jList = (JList) e.getSource();
                    int index = jList.getSelectedIndex();
                    NulsContractMethod method = (NulsContractMethod) jList.getModel().getElementAt(index);

                    boolean showInvokeDialog = false;
                    if (method.isView()) {
                        List<ProgramMethodArg> argsList = method.getArgs();
                        if (Objects.nonNull(argsList) && argsList.size() > 0) {
                            showInvokeDialog = true;
                        }
                    } else {
                        showInvokeDialog = true;
                    }

                    if (!showInvokeDialog) {
                        String accountAddress = accountComboBox.getSelectedItem() == null ? "" : accountComboBox.getSelectedItem().toString();
                        String password = ((NulsAccount) accountComboBox.getSelectedItem()).getPassword();
                        ContractCall call = new ContractCall();
                        call.setSender(accountAddress);
                        call.setPassword(password);
                        call.setGasLimit(0l);
                        call.setPrice(0l);
                        call.setValue(0l);
                        call.setRemark(null);
                        call.setContractAddress(contract.getAddress());
                        call.setMethodName(method.getName());
                        call.setMethodDesc(method.getDesc());
                        call.setArgs(null);
                        doInvoke(call, method.isView());
                    } else {
                        InvokeDialog invokeDialog = new InvokeDialog(project, nulsToolWindowPanel, getRunpanel(), (NulsNode) nodeComboBox.getSelectedItem(),
                                (NulsAccount) accountComboBox.getSelectedItem(), (NulsContract) contractComboBox.getSelectedItem(), method);
                        invokeDialog.setTitle("Invoke");
                        invokeDialog.setModal(false);
                        invokeDialog.show();
                    }
                }
            }
        });

        txnHashLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //预防重复发起事件
                if (!"Confirming......".equals(message.getText()) && !"Success!".equals(message.getText()) && txnHashLabel.getText().length() > 0) {
                    obtainResult();
                }
            }
        });
    }

    private void changeContract() {
        String contractAddress = contractComboBox.getSelectedItem() == null ? "" : contractComboBox.getSelectedItem().toString();
        String nodeAddress = nodeComboBox.getSelectedItem() == null ? "" : nodeComboBox.getSelectedItem().toString();

        if (StringUtils.isEmpty(nodeAddress)) {
            nodeError.setText("No Node Selected!");
            return;
        } else {
            nodeError.setText("");
        }
        NulsContract contract = (NulsContract) contractComboBox.getSelectedItem();
        if (contract == null || StringUtils.isEmpty(contract.getAddress())) {
            contractError.setText("No Contract Selected! ");
            return;
        } else {
            contractError.setText("");
        }
        long currentMili = System.currentTimeMillis();
        logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " LOA-REQ " + contractAddress);
        RpcClientResult<ContractInfoResult> result = generateContractResource(nodeAddress).getContractInfo(contractAddress, null);
        if (result.isSuccess()) {
            latestUsedNode = nodeAddress;
            latestLoadedContractNode = nodeAddress;
            latestLoadedContractAddress = contractAddress;
            ContractInfoResult resultObject = result.getData();
            logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " LOA-RES " + resultObject.toString());
            java.util.List<ProgramMethod> methodList = resultObject.getMethod();
            String methodNames = "";
            for (ProgramMethod method : methodList) {
                methodNames += method.getName() + ",";
            }
            message.setText("<html>Got Contract:" + resultObject.getAddress() + "<br/>Method Count:" + methodList.size() +
                    ", <br/>Methond Names:" + (StringUtils.isNotEmpty(methodNames) ? "-" : methodNames) + "</html>");
            message.setForeground(Color.BLACK);
            updateMethodList(methodList);
        } else {
            logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " LOA-RES " + result.getErrorData());
            message.setText("Query Failed! " + result.getErrorData().getMsg());
            message.setForeground(Color.RED);
            return;
        }
    }

    private LoadContractPanel getRunpanel() {
        return this;
    }

    private ContractResource generateContractResource(String nodeAddress) {
        return Feign.builder()
                .encoder(new GsonEncoder())
                .decoder(new ResultfGsonDecoder())
                .contract(new JAXRSContract())
                .target(ContractResource.class, "http://" + nodeAddress + "/api");
    }

    private void updateMethodList(java.util.List<ProgramMethod> methodList) {
        if (methodList == null || methodList.size() <= 0) {
            this.methodList.setModel(new DefaultComboBoxModel());
            return;
        }
        java.util.List<NulsContractMethod> nulsContractMethodList = new ArrayList<>();
        for (ProgramMethod method : methodList) {
            if (!method.isEvent()) {
                nulsContractMethodList.add(NulsContractMethod.fromApiResult(method));
            }
        }
        NulsContractMethod[] arrMethod = new NulsContractMethod[nulsContractMethodList.size()];
        for (NulsContractMethod method : nulsContractMethodList) {
            arrMethod[nulsContractMethodList.indexOf(method)] = method;
        }
        ListModel<NulsContractMethod> listModel = new DefaultComboBoxModel<NulsContractMethod>(arrMethod);
        this.methodList.setModel(listModel);
    }


    public void doInvoke(ContractCall contractCall, boolean isView) {
        resultMessage.setText("");
        txnHashLabel.setText("");
        long currentMili = System.currentTimeMillis();
        logManager.append(sdf.format(new Date()) + " " + currentMili + " " + latestLoadedContractNode + " INV-REQ " + contractCall.toString());
        RpcClientResult result = generateContractResource(latestLoadedContractNode).callContract(contractCall);
        if (result.isSuccess()) {
            latestUsedNode = latestLoadedContractNode;
            if (isView) {
                String returnValue = result.getData() == null ? "[NULL]" : result.getData().toString();
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + latestLoadedContractNode + " INV-RES " + returnValue);
                /*JOptionPane.showMessageDialog(null,
                        "Remote method invoked successfully! Getter method, return value:\n" + returnValue,
                        "INFO", JOptionPane.INFORMATION_MESSAGE);*/
                resultMessage.setText("Remote method invoked successfully! \n Getter method, return value:\n" + returnValue);
                message.setText("Success!");
                message.setForeground(Color.BLACK);
            } else {
                String txnHash = result.getData() == null ? "-" : result.getData().toString();
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + latestLoadedContractNode + " INV-RES " + txnHash);
                txnHashLabel.setText("-".equals(txnHash) ? "" : txnHash);
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + latestLoadedContractNode + " [HASH]  " + txnHash);

                message.setText("Confirming......");
                message.setForeground(Color.BLACK);

                new Thread() {
                    public void run() {
                        obtainResult();
                    }
                }.start();
            }
        } else {
            logManager.append(sdf.format(new Date()) + " " + currentMili + " " + latestLoadedContractNode + " INV-RES " + result.getErrorData());
            /*JOptionPane.showMessageDialog(null, "Invoke Failed! " + result.getErrorData().getMsg(), "ERROR", JOptionPane.ERROR_MESSAGE);*/
            message.setText("Invoke Failed! " + result.getErrorData().getMsg());
            message.setForeground(Color.RED);
        }
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
                    message.setText(errorMessage);
                    message.setForeground(Color.RED);
                    return true;
                }
            } else {
                // 合约发布成功,返回结果值
//                resultMessage.setText("<html>" + JsonFormater.format(resultInfo).replace("\n", "<br/>").replace(" ", "&nbsp;") + "</html>");
                resultMessage.setText(JsonFormater.format(resultInfo));
                message.setText("Success!");
                message.setForeground(Color.BLACK);
                return true;
            }
        } catch (Exception e) {
            message.setText("The update result failed. Please click txnHash again.");
            message.setForeground(Color.RED);
            return true;
        }
    }
}
