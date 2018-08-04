package org.river.nuls.form;

import org.river.nuls.model.ConfigStorage;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private JPanel rootPanel;
    private JCheckBox nulsSyntaxCheckCheckBox;

    public SettingsPanel(ConfigStorage storage){
        setLayout(new BorderLayout());
        add(rootPanel);
        nulsSyntaxCheckCheckBox.setSelected(storage.isNulsSyntaxCheck());
    }

    public boolean isNulsSyntaxCheckSelected(){
        return nulsSyntaxCheckCheckBox.isSelected();
    }
}
