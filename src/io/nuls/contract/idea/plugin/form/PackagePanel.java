package io.nuls.contract.idea.plugin.form;

import com.intellij.openapi.project.Project;
import io.nuls.contract.idea.plugin.logic.LogManager;
import io.nuls.contract.idea.plugin.model.ConfigStorage;
import io.nuls.contract.idea.plugin.toolwindow.ui.NulsToolWindowPanel;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class PackagePanel extends JPanel {
    private JLabel filePathLabel;
    private JTextField jarFilePath;
    private JTextPane hexCode;
    private JLabel openFile;
    private JButton nextButton;
    private JLabel packageLabel;
    private JLabel hexCodeLabel;
    private JPanel rootPanel;
    private JLabel message;
    private JLabel copyHexCode;

    private final Project project;
    private final NulsToolWindowPanel nulsToolWindowPanel;
    private final LogManager logManager;

    private String hexCodeStr = "";


    public PackagePanel(Project project, NulsToolWindowPanel nulsToolWindowPanel, LogManager logManager) {
        this.project = project;
        this.nulsToolWindowPanel = nulsToolWindowPanel;
        this.logManager = logManager;
        setLayout(new BorderLayout());
        add(rootPanel);

        initInfo();
        initListeners();
    }

    private void initInfo() {
        ConfigStorage storage = ConfigStorage.getInstance(project);
        if (storage.getJarFilePath() != null) {
            jarFilePath.setText(storage.getJarFilePath());
        }
        jarFilePath.setEditable(false);
        hexCode.setEditable(false);

        initHexCode();
        /*while (!status) {
            status = initHexCode();
        }*/
    }

    private void initHexCode() {
        String code = "";
        try {
            code = Hex.encodeHexString(FileUtils.readFileToByteArray(FileUtils.getFile(jarFilePath.getText())));
//            hexCode.setText(code);
            setHexCodeAddNewline(code);
        } catch (Exception e) {
        }

        if (StringUtils.isNotEmpty(code)) {
            message.setText("");
            message.setForeground(Color.BLACK);
            hexCodeStr = code;
            copyHexCode.setVisible(true);
        } else {
            message.setText("<html>Failed to generate Hex encoding. Please confirm that the package is completed.</html>");
            message.setForeground(Color.RED);
            hexCodeStr = "";
            copyHexCode.setVisible(false);
        }
    }

    private void setHexCodeAddNewline(String code) {
        if (StringUtils.isEmpty(code)) {
            return;
        }
        StringBuffer sb = new StringBuffer();

        boolean isContinue = true;
        while (isContinue) {
            int length = code.length();
            if (length > 50) {
                sb.append(code.substring(0, 50) + "\n");
                code = code.substring(50, length);
            } else {
                sb.append(code.substring(0, length));
                isContinue = false;
            }
        }

        hexCode.setText(sb.toString());
    }

    private void initListeners() {
        nextButton.addActionListener(actionEvent -> {
            if (null != hexCode.getText() && hexCode.getText().length() > 0) {
                //打开发布合约
                nulsToolWindowPanel.replaceContentPanel(new RunPanel(project, nulsToolWindowPanel, nulsToolWindowPanel.getLogManager()));
            } else {
                JOptionPane.showMessageDialog(null,
                        "The package is wrong, so we can not proceed with the next step.",
                        "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        });

        openFile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //点击事件，打开jarFilePath所在文件夹
                try {
                    String path = project.getBasePath() + "/out/artifacts/contract/";
                    Desktop.getDesktop().open(new File(path));
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(null,
                            "Folder open failed. Please copy and open.",
                            "info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        message.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                initInfo();
            }
        });

        copyHexCode.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Clipboard clipbd = getToolkit().getSystemClipboard();
                StringSelection clipString = new StringSelection(hexCodeStr);
                clipbd.setContents(clipString, clipString);
                JOptionPane.showMessageDialog(null,
                        "copy success",
                        "info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

}
