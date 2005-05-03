/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.idea.editor.pom.ui;

import com.intellij.openapi.editor.Document;
import org.apache.maven.project.Project;
import org.mevenide.context.IQueryContext;
import org.mevenide.idea.editor.pom.ui.layer.PomLayerPanel;

import javax.swing.*;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Arik
 */
public class PomPanel extends JPanel {
    private final com.intellij.openapi.project.Project project;
    private final IQueryContext queryContext;
    private final Document pomDocument;

    private final PomLayersPanel layersPanel;
    private final JComboBox layersCombo;
    private final JLabel layersLabel = new JLabel("POM layers:", JLabel.TRAILING);
    private final JButton applyButton = new JButton("Apply");

    public PomPanel(final com.intellij.openapi.project.Project pProject,
                    final Document pPomDocument,
                    final IQueryContext pQueryContext) {
        super(new GridBagLayout());

        project = pProject;
        queryContext = pQueryContext;
        pomDocument = pPomDocument;

        layersPanel = new PomLayersPanel();
        layersCombo = new JComboBox();

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        //
        //initialize the layers combo box
        //
        layersCombo.setModel(new PomLayersComboBoxModel(queryContext));
        layersCombo.setRenderer(new PomLayersComboBoxRenderer());
        layersCombo.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent pEvent) {
                final int index = layersCombo.getSelectedIndex();
                layersPanel.setLayer(index < 0 ? -1 : index);
            }
        });

        //
        //initialize the layers combo box label
        //
        layersLabel.setLabelFor(layersCombo);
    }

    public void addApplyAction(final Runnable pRunnable) {
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pRunnable.run();
            }
        });
    }

    private void layoutComponents() {

        //
        //create the layers combo box
        //
        final Box layersBox = Box.createHorizontalBox();
        layersBox.add(layersLabel);
        layersBox.add(Box.createHorizontalStrut(5));
        layersBox.add(layersCombo);
        layersBox.add(Box.createHorizontalGlue());
        layersBox.add(applyButton);

        //
        //create the global box
        //
        final Box box = Box.createVerticalBox();
        box.add(Box.createVerticalStrut(5));
        box.add(layersBox);
        box.add(Box.createVerticalStrut(5));
        box.add(layersPanel);

        //
        //create the global surrounding box
        //
        GridBagConstraints c = new GridBagConstraints(
                0,                          //grid x
                0,                          //grid y
                1,                          //grid width
                1,                          //grid height
                1,                          //weight x
                1,                          //weight y
                GridBagConstraints.CENTER,  //anchor
                GridBagConstraints.BOTH,    //fill
                new Insets(5, 5, 5, 5),     //insets
                0,                          //pad x
                0                           //pad y
        );
        add(box, c);
    }

    private class PomLayersPanel extends JPanel {
        private final CardLayout layersLayout;

        public PomLayersPanel() {
            super(new CardLayout());

            layersLayout = (CardLayout) getLayout();
            final Project[] layers = queryContext.getPOMContext().getProjectLayers();
            for (int i = 0; i < layers.length; i++) {
                final Project layer = layers[i];
                final JPanel layerPanel = new PomLayerPanel(project, layer, pomDocument);
                add(layerPanel, i + "");
            }

            add(new JPanel(), "-1");
        }

        private void setLayer(final int pIndex) {
            layersLayout.show(this, pIndex + "");
        }
    }
}
