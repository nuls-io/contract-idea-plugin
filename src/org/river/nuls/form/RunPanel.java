package org.river.nuls.form;

import com.intellij.openapi.project.Project;
import feign.Feign;
import feign.gson.GsonEncoder;
import feign.jaxrs.JAXRSContract;
import io.nuls.contract.rpc.form.ContractCall;
import io.nuls.contract.rpc.form.ContractCreate;
import io.nuls.contract.rpc.form.ContractDelete;
import io.nuls.contract.rpc.model.ContractTransactionDto;
import io.nuls.contract.rpc.resource.ContractResource;
import io.nuls.contract.rpc.resource.ResultfGsonDecoder;
import io.nuls.contract.rpc.result.ContractCreateResult;
import io.nuls.contract.rpc.result.ContractInfoResult;
import io.nuls.contract.rpc.result.ContractMethod;
import io.nuls.kernel.model.RpcClientResult;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.river.nuls.logic.LogManager;
import org.river.nuls.logic.TreeItemManager;
import org.river.nuls.model.*;
import org.river.nuls.toolwindow.ui.InvokeDialog;
import org.river.nuls.toolwindow.ui.NulsToolWindowPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RunPanel extends JPanel{
    private JPanel rootPanel;
    private JLabel nodeLabel;
    private JComboBox nodeComboBox;
    private JLabel accountLabel;
    private JComboBox accountComboBox;
    private JLabel gasLabel;
    private JTextField gasTextField;
    private JLabel contractLabel;
    private JComboBox contractComboBox;
    private JButton deployButton;
    private JList methodList;
    private JButton deleteButton;
    private JButton loadButton;
    private JLabel priceLabel;
    private JLabel remarkLabel;
    private JTextField priceTextField;
    private JTextField remarkTextField;
    private JScrollPane methodsScrollPane;
    private JLabel hashLabel;
    private JLabel txnHashLabel;
    private JTextField jarFileTextField;
    private JLabel jarFileLabel;
    private JTextField argsTextField;
    private JLabel argsLabel;

    private final Project project;
    private final NulsToolWindowPanel nulsToolWindowPanel;
    private final TreeItemManager treeItemManager;
    private final LogManager logManager;

    private String latestUsedNode = "";
    private String latestLoadedContractNode = "";
    private String latestLoadedContractAddress = "";

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

    public RunPanel(Project project, NulsToolWindowPanel nulsToolWindowPanel, TreeItemManager treeItemManager, LogManager logManager){
        this.project = project;
        this.nulsToolWindowPanel = nulsToolWindowPanel;
        this.treeItemManager = treeItemManager;
        this.logManager = logManager;
//        setSize(new Dimension(800, 600)); // 无效
        refreshComboBoxes();
        setLayout(new BorderLayout());
        add(rootPanel);

        initListeners();
    }

    private void refreshComboBoxes(){
        TreeItem nodeSelected = (TreeItem)nodeComboBox.getSelectedItem();
        TreeItem accountSelected = (TreeItem)accountComboBox.getSelectedItem();
        TreeItem contractSelected = (TreeItem)contractComboBox.getSelectedItem();
        accountComboBox.removeAllItems();
        nodeComboBox.removeAllItems();
        contractComboBox.removeAllItems();
        for(TreeItem treeItem : this.treeItemManager.getTreeItems()){
            if (treeItem instanceof NulsAccount){
                accountComboBox.addItem(treeItem);
            }else if(treeItem instanceof NulsNode) {
                nodeComboBox.addItem(treeItem);
            }else if (treeItem instanceof NulsContract){
                contractComboBox.addItem(treeItem);
            }
        }
        if (nodeSelected != null ){
            nodeComboBox.setSelectedItem(nodeSelected);
        }
        if (accountSelected != null ){
            accountComboBox.setSelectedItem(accountSelected);
        }
        if (contractSelected != null ){
           contractComboBox.setSelectedItem(contractSelected);
        }
    }

    private void  initListeners(){
        deployButton.addActionListener(actionEvent ->{
            String nodeAddress = nodeComboBox.getSelectedItem() == null ? "" : nodeComboBox.getSelectedItem().toString();
            String accountAddress = accountComboBox.getSelectedItem() == null ? "" : accountComboBox.getSelectedItem().toString();
            String jarFilePath = jarFileTextField.getText();
            String strGas = gasTextField.getText();
            String strPrice = priceTextField.getText();
            Long gas = 0L;
            long price = 0L;
            String remark = remarkTextField.getText();
            if (StringUtils.isEmpty(nodeAddress)){
                JOptionPane.showMessageDialog(null, "No Node Selected!", "ERROR",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (StringUtils.isEmpty(accountAddress)){
                JOptionPane.showMessageDialog(null, "No Account Selected!", "ERROR",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (StringUtils.isEmpty(jarFilePath)){
                JOptionPane.showMessageDialog(null, "File path should be a jar file full path!", "ERROR",JOptionPane.ERROR_MESSAGE);
                return;
            }
            try{
                gas = Long.parseLong(strGas);
            }catch (Exception e){
                JOptionPane.showMessageDialog(null, "Invalid param gas!", "ERROR",JOptionPane.ERROR_MESSAGE);
                return;
            }
            try{
                price = Long.parseLong(strPrice);
            }catch (Exception e){
                JOptionPane.showMessageDialog(null, "Invalid param price!", "ERROR",JOptionPane.ERROR_MESSAGE);
                return;
            }
//            String path = ContractResourceTest.class.getResource("contract.txt").getFile();
            String code = "";
            try{
                code = Hex.encodeHexString(FileUtils.readFileToByteArray(FileUtils.getFile(jarFilePath)));
            }catch (Exception e){
                JOptionPane.showMessageDialog(null, "Bad Contract File!", "ERROR",JOptionPane.ERROR_MESSAGE);
                return;
            }
            // 创建合约参数对象
            ContractCreate create = new ContractCreate();
            create.setSender(accountAddress);
            create.setPassword(((NulsAccount)accountComboBox.getSelectedItem()).getPassword());
            create.setGasLimit(gas);
            create.setPrice(price);
            create.setRemark(remark);
            String[] args = StringUtils.isEmpty(argsTextField.getText()) ? null : argsTextField.getText().split(",");
            create.setArgs(args);
            create.setContractCode(code);
            long currentMili = System.currentTimeMillis();
            logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEP-REQ " + create.toString());
            RpcClientResult<ContractCreateResult> result = generateContractResource(nodeAddress).createContract(create);
            if (result.isSuccess()){
                latestUsedNode = nodeAddress;
                JOptionPane.showMessageDialog(null,
                        "Creation invoked successfully, Please do the further check by clicking the transaction hash.",
                        "INFO",JOptionPane.INFORMATION_MESSAGE);
                // 结果处理
                ContractCreateResult resultObject = result.getData();
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEP-RES " + resultObject.toString());
                // 记录交易Hash
                String txnHash = resultObject.getTxHash();
                txnHashLabel.setText(txnHash);
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " [HASH]  " + txnHash);
                // 通过结果对象封装合约
                NulsContract contract = new NulsContract();
                contract.setAddress(resultObject.getContractAddress());
                contract.setRemark(remark);
                // 更新ToolWindow
                nulsToolWindowPanel.addTreeItem(contract);
                // 更新运行对话框下拉列表
                 refreshComboBoxes();
            }else {
                JOptionPane.showMessageDialog(null, "Creation Failed! " + result.getErrorData().getMsg(), "ERROR",JOptionPane.ERROR_MESSAGE);
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEP-RES " + result.getErrorData());
                return;
            }
        });

        loadButton.addActionListener(actionEvent ->{
            String nodeAddress = nodeComboBox.getSelectedItem() == null ? "" : nodeComboBox.getSelectedItem().toString();
            String contractAddress = contractComboBox.getSelectedItem() == null ? "" : contractComboBox.getSelectedItem().toString();
            if (StringUtils.isEmpty(nodeAddress)){
                JOptionPane.showMessageDialog(null, "No Node Selected!", "ERROR",JOptionPane.ERROR_MESSAGE);
                return;
            }
            NulsContract contract = (NulsContract)contractComboBox.getSelectedItem();
            if (contract == null || StringUtils.isEmpty(contract.getAddress())){
                JOptionPane.showMessageDialog(null, "No Contract Selected! ", "ERROR",JOptionPane.ERROR_MESSAGE);
                return;
            }
            long currentMili = System.currentTimeMillis();
            logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " LOA-REQ " + contractAddress);
            RpcClientResult<ContractInfoResult> result = generateContractResource(nodeAddress).getContractInfo(contractAddress);
            if (result.isSuccess()){
                latestUsedNode = nodeAddress;
                this.latestLoadedContractNode = nodeAddress;
                this.latestLoadedContractAddress = contractAddress;
                ContractInfoResult resultObject = result.getData();
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " LOA-RES " + resultObject.toString());
                java.util.List<ContractMethod> methodList = resultObject.getMethod();
                String methodNames = "";
                for (ContractMethod method : methodList) {
                    methodNames += method.getName() + ",";
                }
                JOptionPane.showMessageDialog(null,
                        "Got Contract:" + resultObject.getAddress() + "\nMethod Count:" + methodList.size() +
                                ", Methond Names:" + (StringUtils.isNotEmpty(methodNames) ? "-" : methodNames),
                        "INFO",JOptionPane.INFORMATION_MESSAGE);
                updateMethodList(methodList);
            }else {
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " LOA-RES " + result.getErrorData());
                JOptionPane.showMessageDialog(null, "Query Failed! " + result.getErrorData().getMsg(), "ERROR",JOptionPane.ERROR_MESSAGE);
                return;
            }
        });

        deleteButton.addActionListener(actionEvent ->{
            String nodeAddress = nodeComboBox.getSelectedItem() == null ? "" : nodeComboBox.getSelectedItem().toString();
            String accountAddress = accountComboBox.getSelectedItem() == null ? "" : accountComboBox.getSelectedItem().toString();
            String contractAddress = contractComboBox.getSelectedItem() == null ? "" : contractComboBox.getSelectedItem().toString();
            if (StringUtils.isEmpty(nodeAddress)){
                JOptionPane.showMessageDialog(null, "No Node Selected!", "ERROR",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (StringUtils.isEmpty(accountAddress)){
                JOptionPane.showMessageDialog(null, "No Account Selected!", "ERROR",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (StringUtils.isEmpty(contractAddress)){
                JOptionPane.showMessageDialog(null, "No Contract Selected!", "ERROR",JOptionPane.ERROR_MESSAGE);
                return;
            }
            String remark = JOptionPane.showInputDialog(null,
                    "Are you sure? This action will permanently delete this contract. \nRemark:\n",
                    "CONFIRM",
                    JOptionPane.PLAIN_MESSAGE);
            if (remark == null){
                return;
            }
            ContractDelete delete = new ContractDelete();
            delete.setContractAddress(contractAddress);
            delete.setSender(accountAddress);
            delete.setPassword(((NulsAccount)accountComboBox.getSelectedItem()).getPassword());
            delete.setRemark(remark);
            long currentMili = System.currentTimeMillis();
            logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEL-REQ " + delete.toString());
            RpcClientResult result = generateContractResource(nodeAddress).deleteContract(delete);
            if (result.isSuccess()){
                latestUsedNode = nodeAddress;
                String txnHash = result.getData().toString();
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEL-RES " + txnHash);
                txnHashLabel.setText(txnHash);
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " [HASH]  " + txnHash);
                JOptionPane.showMessageDialog(null,
                        "Deletion invoked successfully, please do the further check by clicking the transaction hash." , "INFO",JOptionPane.INFORMATION_MESSAGE);
                methodList.removeAll();
                // 更新ToolWindow
                nulsToolWindowPanel.removeTreeItem(((NulsContract)contractComboBox.getSelectedItem()));
                // 更新运行对话框下拉列表
                refreshComboBoxes();
                // 清空方法列表
                methodList.setModel(new DefaultComboBoxModel());
            }else {
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEL-RES " + result.getErrorData());
                JOptionPane.showMessageDialog(null, "Delete Failed! " + result.getErrorData().getMsg(), "ERROR",JOptionPane.ERROR_MESSAGE);
                return;
            }
        });

        methodList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    JList jList = (JList) e.getSource();
                    int index = jList.getSelectedIndex();
                    NulsContractMethod method = (NulsContractMethod) jList.getModel().getElementAt(index);

                    InvokeDialog invokeDialog = new InvokeDialog(project, nulsToolWindowPanel, getRunpanel(), (NulsNode)nodeComboBox.getSelectedItem(),
                            (NulsAccount)accountComboBox.getSelectedItem() , (NulsContract)contractComboBox.getSelectedItem(), method);
                    invokeDialog.setTitle("Invoke");
                    invokeDialog.show();
                }
            }
        });

        txnHashLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String txnHash = txnHashLabel.getText();
                if (StringUtils.isEmpty(txnHash)) return;
                RpcClientResult<ContractTransactionDto> result = generateContractResource(latestUsedNode).getContractTx(txnHash);
                String realStatus = "F";
                String errorMessage = "-";
                if (result.isSuccess()) {
                    ContractTransactionDto dto = result.getData();
                    if (dto == null){
                        errorMessage = "No Response.";
                    }else{
                        if (dto.getStatus() == 1) {
                            if (dto.getContractResult() == null){
                                errorMessage = "No Contract Result.";
                            }else{
                                if (dto.getContractResult().isError()){
                                    errorMessage = dto.getContractResult().getErrorMessage();
                                }else {
                                    realStatus = dto.getContractResult().getResult();
                                    realStatus = StringUtils.isEmpty(realStatus) ? "-" : realStatus;
                                }
                            }
                        } else {
                            errorMessage = "Transaction Unchecked.";
                        }
                    }
                } else {
                    if (result.getErrorData() == null){
                        errorMessage = "Unknown.";
                    }else {
                        errorMessage = result.getErrorData().getMsg() + "(" + result.getErrorData().getCode() + ")";
                    }
                }
                if("F".equals(realStatus)){
                    JOptionPane.showMessageDialog(null,
                            "Transaction is not confirmed, Info:\n" + errorMessage,
                            "INFO",JOptionPane.INFORMATION_MESSAGE);
                }else {
                    JOptionPane.showMessageDialog(null,
                            "Transaction Confirmed:" + realStatus,
                            "INFO",JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    private ContractResource generateContractResource(String nodeAddress){
        return Feign.builder()
                .encoder(new GsonEncoder())
                .decoder(new ResultfGsonDecoder())
                .contract(new JAXRSContract())
                .target(ContractResource.class, "http://"+ nodeAddress +"/api");
    }

    private void  updateMethodList(java.util.List<ContractMethod> methodList ){
        if (methodList == null || methodList.size() <= 0){
            this.methodList.setModel(new DefaultComboBoxModel());
            return;
        }
        java.util.List<NulsContractMethod> nulsContractMethodList = new ArrayList<>();
        for (ContractMethod method : methodList){
            nulsContractMethodList.add(NulsContractMethod.fromApiResult(method));
        }
        NulsContractMethod[] arrMethod = new NulsContractMethod[nulsContractMethodList.size()];
        for (NulsContractMethod method : nulsContractMethodList){
            arrMethod[nulsContractMethodList.indexOf(method)] = method;
        }
        ListModel<NulsContractMethod> listModel = new DefaultComboBoxModel<NulsContractMethod>(arrMethod);
        this.methodList.setModel(listModel);
    }

    private RunPanel getRunpanel(){
        return this;
    }

    public void doInvoke(ContractCall contractCall){
        long currentMili = System.currentTimeMillis();
        logManager.append(sdf.format(new Date()) + " " + currentMili + " " + latestLoadedContractNode + " INV-REQ " + contractCall.toString());
        RpcClientResult result = generateContractResource(latestLoadedContractNode).callContract(contractCall);
        if (result.isSuccess()){
            latestUsedNode = latestLoadedContractNode;
            if (contractCall.getMethodName().startsWith("get")){
                String returnValue = result.getData() == null ? "[NULL]" : result.getData().toString();
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + latestLoadedContractNode + " INV-RES " + returnValue);
                JOptionPane.showMessageDialog(null,
                        "Remote method invoked successfully! Getter method, return value:\n" + returnValue,
                        "INFO",JOptionPane.INFORMATION_MESSAGE);
            }else{
                String txnHash = result.getData() == null ? "-" : result.getData().toString();
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + latestLoadedContractNode + " INV-RES " + txnHash);
                txnHashLabel.setText("-".equals(txnHash) ? "" : txnHash);
                logManager.append(sdf.format(new Date()) + " " + currentMili + " " + latestLoadedContractNode + " [HASH]  " + txnHash);
                JOptionPane.showMessageDialog(null,
                        "Remote method invoked successfully, Please do the further check by clicking the transaction hash.",
                        "INFO",JOptionPane.INFORMATION_MESSAGE);
            }
        }else{
            logManager.append(sdf.format(new Date()) + " " + currentMili + " " + latestLoadedContractNode + " INV-RES " + result.getErrorData());
            JOptionPane.showMessageDialog(null, "Invoke Failed! " + result.getErrorData().getMsg(), "ERROR",JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}
