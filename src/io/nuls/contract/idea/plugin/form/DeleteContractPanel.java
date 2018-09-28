package io.nuls.contract.idea.plugin.form;

import com.intellij.openapi.project.Project;
import feign.Feign;
import feign.gson.GsonEncoder;
import feign.jaxrs.JAXRSContract;
import io.nuls.contract.idea.plugin.logic.LogManager;
import io.nuls.contract.idea.plugin.model.ConfigStorage;
import io.nuls.contract.idea.plugin.model.NulsContract;
import io.nuls.contract.idea.plugin.model.TreeItem;
import io.nuls.contract.idea.plugin.toolwindow.ui.NulsToolWindowPanel;
import io.nuls.contract.idea.plugin.util.JsonFormater;
import io.nuls.contract.rpc.form.ContractDelete;
import io.nuls.contract.rpc.model.ContractTransactionDto;
import io.nuls.contract.rpc.resource.ContractResource;
import io.nuls.contract.rpc.resource.ResultfGsonDecoder;
import io.nuls.kernel.model.RpcClientResult;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeleteContractPanel extends JPanel {
    private JPanel rootPanel;
    private JLabel nodeLabel;
    private JComboBox nodeComboBox;
    private JLabel contractLabel;
    private JComboBox contractComboBox;
    private JLabel accountLabel;
    private JComboBox accountComboBox;
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
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

    public DeleteContractPanel(Project project, NulsToolWindowPanel nulsToolWindowPanel, LogManager logManager, NulsContract contract) {
        this.project = project;
        this.nulsToolWindowPanel = nulsToolWindowPanel;
        this.logManager = logManager;
        this.contract = contract;
        refreshComboBoxes();
        setLayout(new BorderLayout());
        add(rootPanel);
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
        contractComboBox.addItem(contract);
        if (nodeSelected != null) {
            nodeComboBox.setSelectedItem(nodeSelected);
        }
        if (accountSelected != null) {
            accountComboBox.setSelectedItem(accountSelected);
        }
        if (contract != null) {
            contractComboBox.setSelectedItem(contract);
        }
        removeContract();
    }

    private void removeContract() {
        String nodeAddress = nodeComboBox.getSelectedItem() == null ? "" : nodeComboBox.getSelectedItem().toString();
        String accountAddress = accountComboBox.getSelectedItem() == null ? "" : accountComboBox.getSelectedItem().toString();
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
        NulsContract contract = (NulsContract) contractComboBox.getSelectedItem();
        if (contract == null || StringUtils.isEmpty(contract.getAddress())) {
            contractError.setText("No Contract Selected! ");
            return;
        } else {
            contractError.setText("");
        }

        /*String password = ((NulsAccount) accountComboBox.getSelectedItem()).getPassword();
        if (StringUtils.isEmpty(password)) {
            int result = JOptionPane.showConfirmDialog(null,
                    "该账户没有密码，确认交易么?",
                    "确认密码",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.NO_OPTION) {
                return;
            }
        } else {
            JPasswordField pwd = new JPasswordField();
            Object[] message = {"请输入密码:", pwd};

            int result = JOptionPane.showConfirmDialog(null, message, "确认密码", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
            password = pwd.getText();
        }*/


        JPasswordField pwd = new JPasswordField();
        Object[] messagePwd = {"请输入密码:", pwd};
        int resultPwd = JOptionPane.showConfirmDialog(null, messagePwd, "删除合约后将无法找回，请再次确认", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resultPwd == JOptionPane.CANCEL_OPTION) {
            return;
        }
        String password = pwd.getText();
        /*String remark = JOptionPane.showInputDialog(null,
                "Are you sure? This action will permanently delete this contract. \nRemark:\n",
                "删除合约后将无法找回，请再次确认",
                JOptionPane.PLAIN_MESSAGE);
        if (remark == null) {
            return;
        }*/

        ContractDelete delete = new ContractDelete();
        delete.setContractAddress(contract.getAddress());
        delete.setSender(accountAddress);
        delete.setPassword(password);
        delete.setRemark(null);
        long currentMili = System.currentTimeMillis();
        logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEL-REQ " + delete.toString());
        RpcClientResult result = generateContractResource(nodeAddress).deleteContract(delete);
        if (result.isSuccess()) {
            latestUsedNode = nodeAddress;
            String txnHash = result.getData().toString();
            logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEL-RES " + txnHash);
            txnHashLabel.setText(txnHash);
            logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " [HASH]  " + txnHash);

            // 更新ToolWindow
//            nulsToolWindowPanel.removeTreeItem(((NulsContract) contractComboBox.getSelectedItem()));

            message.setText("Confirming......");
            message.setForeground(Color.BLACK);

            new Thread() {
                public void run() {
                    obtainResult();
                }
            }.start();

        } else {
            logManager.append(sdf.format(new Date()) + " " + currentMili + " " + nodeAddress + " DEL-RES " + result.getErrorData());
//            JOptionPane.showMessageDialog(null, "Delete Failed! " + result.getErrorData().getMsg(), "ERROR",JOptionPane.ERROR_MESSAGE);
            message.setText("Delete Failed! " + result.getErrorData().getMsg());
            message.setForeground(Color.RED);

            return;
        }
    }

    private void initListener() {
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

    private ContractResource generateContractResource(String nodeAddress) {
        return Feign.builder()
                .encoder(new GsonEncoder())
                .decoder(new ResultfGsonDecoder())
                .contract(new JAXRSContract())
                .target(ContractResource.class, "http://" + nodeAddress + "/api");
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
            message.setText("更新结果失败，请重新点击txnHash查询");
            message.setForeground(Color.RED);
            return true;
        }
    }
}
