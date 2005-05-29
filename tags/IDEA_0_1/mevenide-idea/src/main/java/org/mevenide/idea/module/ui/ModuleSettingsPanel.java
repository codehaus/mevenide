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
package org.mevenide.idea.module.ui;

import com.intellij.openapi.module.Module;
import com.intellij.util.ui.Tree;
import org.mevenide.goals.grabber.DefaultGoalsGrabber;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.idea.Res;
import org.mevenide.idea.module.ModuleLocationFinder;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.idea.util.ui.tree.checkbox.TreeCheckBoxEditor;
import org.mevenide.idea.util.ui.tree.checkbox.TreeCheckBoxRenderer;
import org.mevenide.idea.util.goals.grabber.CustomGoalsGrabber;

import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.ArrayList;

/**
 * @author Arik
 */
public class ModuleSettingsPanel extends JPanel {
    private static final Res RES = Res.getInstance(ModuleSettingsPanel.class);

    private final Module module;
    private IGoalsGrabber goalsGrabber = null;

    private final Tree goalsTree = new Tree();
    private final JTextArea topLabel = new JTextArea(RES.get("top.label"));
    private GoalsSelectionTreeModel selectedGoalsModel;

    public ModuleSettingsPanel(final Module pModule) {
        super(new GridBagLayout());
        module = pModule;
        init();
    }

    private void init() {
        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        //
        //configure the label
        //
        topLabel.setFocusable(false);
        topLabel.setEditable(false);

        //
        //configure the goals tree
        //
        goalsTree.setEditable(true);
        goalsTree.setCellRenderer(new TreeCheckBoxRenderer());
        goalsTree.setCellEditor(new TreeCheckBoxEditor());
        goalsTree.setRowHeight(20);
        goalsTree.setRootVisible(false);
        goalsTree.setShowsRootHandles(true);
    }

    private void layoutComponents() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1;
        add(topLabel, c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(10, 10, 10, 10);
        c.weighty = 1;
        add(new JScrollPane(goalsTree), c);
    }

    public void refreshGoals() throws Exception {
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
        if(moduleSettings.getQueryContext() == null)
            goalsGrabber = new CustomGoalsGrabber("Not a Maven project");
        else {
            final ModuleLocationFinder finder = new ModuleLocationFinder(module);
            goalsGrabber = new DefaultGoalsGrabber(finder);
        }

        goalsGrabber.refresh();
    }

    public Collection<String> getSelectedGoals() {
        if(selectedGoalsModel == null)
            return new ArrayList<String>(0);

        return selectedGoalsModel.getSelectedGoals();
    }

    public void setSelectedGoals(final Collection<String> pGoals) {
        if(goalsGrabber == null)
            throw new IllegalStateException(RES.get("goals.not.loaded"));

        selectedGoalsModel = new GoalsSelectionTreeModel(goalsGrabber);
        selectedGoalsModel.setSelectedGoals(pGoals);
        goalsTree.setModel(selectedGoalsModel);
    }
}
