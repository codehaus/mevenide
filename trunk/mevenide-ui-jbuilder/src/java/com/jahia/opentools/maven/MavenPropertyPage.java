/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package com.jahia.opentools.maven;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.borland.jbuilder.node.XMLFileNode;
import com.borland.primetime.help.HelpTopic;
import com.borland.primetime.properties.PropertyPage;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004 Jahia Ltd</p>
 * <p>Company: Jahia Ltd</p>
 * @author Serge Huber
 * @version 1.0
 */
public class MavenPropertyPage extends PropertyPage {
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private JCheckBox obfuscateCheckBox = new JCheckBox();
    private XMLFileNode archiveNode;
    private JLabel label = new JLabel();
    private JLabel scriptLabel = new JLabel();
    private JTextField scriptTextField = new JTextField();

    public MavenPropertyPage (XMLFileNode xmlFileNode) {
        try {
            this.archiveNode = xmlFileNode;
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeProperties () {
        MavenPropertyGroup.DEBUG_MODE.setBoolean(archiveNode,
                                                 obfuscateCheckBox.isSelected());
        MavenPropertyGroup.MAVEN_HOME.setValue(archiveNode,
                                               scriptTextField.getText());
    }

    public HelpTopic getHelpTopic () {
        return null;
    }

    public void readProperties () {
        obfuscateCheckBox.setSelected(MavenPropertyGroup.DEBUG_MODE.
                                      getBoolean(archiveNode));
        scriptTextField.setText(MavenPropertyGroup.MAVEN_HOME.
                                getValue(archiveNode));
    }

    private void jbInit ()
        throws Exception {
        obfuscateCheckBox.setText("Maven");
        obfuscateCheckBox.setMnemonic('m');
        label.setText(
            "Select the checkbox if you want to start Maven in debug mode");
        scriptLabel.setText("Maven home directory :");
        this.setLayout(gridBagLayout1);
        add(label, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0,
                                          GridBagConstraints.CENTER,
                                          GridBagConstraints.HORIZONTAL,
                                          new Insets(0, 0, 6, 0), 0, 0));
        add(obfuscateCheckBox, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));
        add(scriptLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                                                GridBagConstraints.CENTER,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(0, 0, 0, 0), 0, 0));
        add(scriptTextField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(0, 6, 0, 0), 0, 0));
        add(new JPanel(), new GridBagConstraints(1, 5, 1, 1, 0.0, 1.0,
                                                 GridBagConstraints.CENTER,
                                                 GridBagConstraints.VERTICAL,
                                                 new Insets(0, 0, 0, 0), 0, 0));
    }
}
