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
package org.mevenide.ui.jbuilder;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import com.borland.jbuilder.node.XMLFileNode;
import com.borland.primetime.help.HelpTopic;
import com.borland.primetime.properties.PropertyPage;

/**
 * @author Serge Huber
 * @version 1.0
 */
public class MavenPropertyPage extends PropertyPage {
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private JCheckBox debugModeCheckBox = new JCheckBox();
    private JCheckBox offLineModeCheckBox = new JCheckBox();
    private JCheckBox quietModeCheckBox = new JCheckBox();
    private XMLFileNode archiveNode;
    private JLabel debugLabel = new JLabel();
    private JLabel offlineLabel = new JLabel();
    private JLabel quietLabel = new JLabel();

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
                                                 debugModeCheckBox.isSelected());
        MavenPropertyGroup.OFFLINE_MODE.setBoolean(archiveNode,
                                                   offLineModeCheckBox.isSelected());
        MavenPropertyGroup.QUIET_MODE.setBoolean(archiveNode,
                                                   quietModeCheckBox.isSelected());
    }

    public HelpTopic getHelpTopic () {
        return null;
    }

    public void readProperties () {
        debugModeCheckBox.setSelected(MavenPropertyGroup.DEBUG_MODE.
                                      getBoolean(archiveNode));
        offLineModeCheckBox.setSelected(MavenPropertyGroup.OFFLINE_MODE.
                                      getBoolean(archiveNode));
        quietModeCheckBox.setSelected(MavenPropertyGroup.QUIET_MODE.
                                    getBoolean(archiveNode));
    }

    private void jbInit ()
        throws Exception {
        debugModeCheckBox.setText("Debug mode");
        debugModeCheckBox.setMnemonic('d');
        offLineModeCheckBox.setText("Offline mode");
        offLineModeCheckBox.setMnemonic('o');
        quietModeCheckBox.setText("Quiet mode");
        quietModeCheckBox.setMnemonic('q');
        debugLabel.setText(
            "Select the checkbox to start Maven in debug mode");
        offlineLabel.setText("Select the checkbox to start Maven in offline mode");
        quietLabel.setText("Select the checkbox to start Maven in quiet mode");
        this.setLayout(gridBagLayout1);
        add(debugLabel,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 6, 6, 0), 0, 0));
        add(debugModeCheckBox,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 32, 0, 0), 0, 0));
        add(offlineLabel,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(12, 6, 6, 0), 0, 0));
        add(offLineModeCheckBox,   new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 32, 0, 0), 0, 0));
        add(quietLabel,    new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(12, 6, 6, 0), 0, 0));
        add(quietModeCheckBox,   new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 32, 0, 0), 0, 0));
    }
}
