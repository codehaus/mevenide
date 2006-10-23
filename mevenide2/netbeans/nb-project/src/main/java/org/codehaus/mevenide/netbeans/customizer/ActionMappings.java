/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.customizer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.execute.ActionToGoalUtils;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.plexus.util.StringUtils;
import org.netbeans.spi.project.ActionProvider;

/**
 *
 * @author  mkleint@codehaus.org
 */
public class ActionMappings extends javax.swing.JPanel {
    private NbMavenProject project;
    private ModelHandle handle;
    private HashMap titles = new HashMap();
    
    private GoalsListener goalsListener;
    private ProfilesListener profilesListener;
    private PropertiesListener propertiesListener;
    private TestListener testListener;
    private RecursiveListener recursiveListener;
    
    /** Creates new form ActionMappings */
    public ActionMappings(ModelHandle hand, NbMavenProject proj) {
        initComponents();
        //temporary
        btnAdd.setVisible(false);
        btnRemove.setText("Reset");
        
        project = proj;
        handle = hand;
        titles.put(ActionProvider.COMMAND_BUILD, "Build project");
        titles.put(ActionProvider.COMMAND_CLEAN, "Clean project");
        titles.put(ActionProvider.COMMAND_COMPILE_SINGLE, "Compile file");
        titles.put(ActionProvider.COMMAND_DEBUG, "Debug project");
        titles.put(ActionProvider.COMMAND_DEBUG_SINGLE, "Debug file");
        titles.put(ActionProvider.COMMAND_DEBUG_STEP_INTO, null);
        titles.put(ActionProvider.COMMAND_DEBUG_TEST_SINGLE, "Debug test");
        titles.put(ActionProvider.COMMAND_REBUILD, "Clean and Build project");
        titles.put(ActionProvider.COMMAND_RUN, "Run project");
        titles.put(ActionProvider.COMMAND_RUN_SINGLE, "Run file");
        titles.put(ActionProvider.COMMAND_TEST, "Test project");
        titles.put(ActionProvider.COMMAND_TEST_SINGLE, "Test file");
        loadMappings();
        lstMappings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        goalsListener = new GoalsListener();
        profilesListener = new ProfilesListener();
        propertiesListener = new PropertiesListener();
        recursiveListener = new RecursiveListener();
        testListener = new TestListener();
        FocusListener focus = new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (e.getComponent() == txtGoals) {
                    lblHint.setText("<html>A space-separated list of goals or phases to execute.</html>");
                }
                if (e.getComponent() == txtProfiles) {
                    lblHint.setText("<html>A space- or comma-separated list of profiles to activate during execution.</html>");
                }
                if (e.getComponent() == txtProperties) {
                    lblHint.setText("<html>A space-separated list of properties to set during execution in the form &lt;key&gt;=\"&lt;value&gt;\". " +
                            "If value doesn't contain whitespace, \" can be omited.<br>" +
                            "Additional supported variables include (useful mainly for Run/Debug/Test Single) :" +
                            "<ul>" +
                            "<li><b>className</b> name of selected class eg. String</li>" +
                            "<li><b>classNameWithExtension</b> name of selected class with extension, eg. String.java</li>" +
                            "<li><b>packageClassName</b> name of selected class with package name, eg. java.lang.String</li>" +
                            "<li><b>webpagePath</b> relative path of selected webpage document within src/main/webapp </li>" +
                            "</ul>" + 
                            "</html>");
                }
            }
            public void focusLost(FocusEvent e) {
                lblHint.setText("");
            }
        };
        txtGoals.addFocusListener(focus);
        txtProfiles.addFocusListener(focus);
        txtProperties.addFocusListener(focus);
        clearFields();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        lstMappings = new javax.swing.JList();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        lblGoals = new javax.swing.JLabel();
        txtGoals = new javax.swing.JTextField();
        lblProfiles = new javax.swing.JLabel();
        txtProfiles = new javax.swing.JTextField();
        lblProperties = new javax.swing.JLabel();
        txtProperties = new javax.swing.JTextField();
        cbRecursively = new javax.swing.JCheckBox();
        cbSkipTests = new javax.swing.JCheckBox();
        lblHint = new javax.swing.JLabel();

        lstMappings.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstMappingsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(lstMappings);

        btnAdd.setText("Add Custom...");

        btnRemove.setText("Remove/Reset");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        lblGoals.setText("Execute Goals:");

        lblProfiles.setText("Activate Profiles:");

        lblProperties.setText("Set Properties:");

        cbRecursively.setText("Build Recursively (with modules)");
        cbRecursively.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbRecursively.setMargin(new java.awt.Insets(0, 0, 0, 0));

        cbSkipTests.setText("Skip tests");
        cbSkipTests.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbSkipTests.setMargin(new java.awt.Insets(0, 0, 0, 0));

        lblHint.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lblHint, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(btnRemove)
                            .add(btnAdd)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblGoals)
                            .add(lblProfiles)
                            .add(lblProperties))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtGoals, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                            .add(txtProfiles, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                            .add(txtProperties, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                            .add(cbRecursively)
                            .add(cbSkipTests))))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {btnAdd, btnRemove}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(btnAdd)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnRemove))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblGoals)
                    .add(txtGoals, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblProfiles)
                    .add(txtProfiles, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblProperties)
                    .add(txtProperties, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbRecursively)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbSkipTests)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblHint, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        Object obj = lstMappings.getSelectedValue();
        MappingWrapper wr = (MappingWrapper)obj;
        NetbeansActionMapping mapp = wr.getMapping();
        if (mapp != null) {
            // try removing from model, if exists..
            List lst = handle.getActionMappings().getActions();
            if (lst != null) {
                Iterator it = lst.iterator();
                while (it.hasNext()) {
                    NetbeansActionMapping elem = (NetbeansActionMapping) it.next();
                    if (mapp.getActionName().equals(elem.getActionName())) {
                        it.remove();
                        mapp = ActionToGoalUtils.getDefaultMapping(mapp.getActionName(), project);
                        wr.setMapping(mapp);
                        wr.setUserDefined(false);
                        lstMappingsValueChanged(null);
                        break;
                    }
                }
            }
        }
    }//GEN-LAST:event_btnRemoveActionPerformed
    
    private void lstMappingsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstMappingsValueChanged
        Object obj = lstMappings.getSelectedValue();
        if (obj == null) {
            clearFields();
        } else {
            MappingWrapper wr = (MappingWrapper)obj;
            NetbeansActionMapping mapp = wr.getMapping();
            txtGoals.setEditable(true);
            txtProperties.setEditable(true);
            txtProfiles.setEditable(true);
            cbSkipTests.setEnabled(true);
            
            txtGoals.getDocument().removeDocumentListener(goalsListener);
            txtProfiles.getDocument().removeDocumentListener(profilesListener);
            txtProperties.getDocument().removeDocumentListener(propertiesListener);
            cbRecursively.removeActionListener(recursiveListener);
            cbSkipTests.removeActionListener(testListener);
            
            txtGoals.setText(createSpaceSeparatedList(mapp != null ? mapp.getGoals() : Collections.EMPTY_LIST));
            txtProfiles.setText(createSpaceSeparatedList(mapp != null ? mapp.getActivatedProfiles() : Collections.EMPTY_LIST));
            txtProperties.setText(createPropertiesList(mapp != null ? mapp.getProperties() : new Properties()));
            if ("pom".equals(handle.getProject().getPackaging())) {
                cbRecursively.setEnabled(true);
                cbRecursively.setSelected(mapp != null ? mapp.isRecursive() : true);
            }
            cbSkipTests.setSelected(checkPropertiesList(mapp != null ? mapp.getProperties() : new Properties()));
            
            txtGoals.getDocument().addDocumentListener(goalsListener);
            txtProfiles.getDocument().addDocumentListener(profilesListener);
            txtProperties.getDocument().addDocumentListener(propertiesListener);
            cbRecursively.addActionListener(recursiveListener);
            cbSkipTests.addActionListener(testListener);
            updateColor(wr);
        }
    }//GEN-LAST:event_lstMappingsValueChanged
    
    private void loadMappings() {
        ModelHandle user = handle;
        DefaultListModel model = new DefaultListModel();
        addSingleAction(ActionProvider.COMMAND_BUILD, user, model);
        addSingleAction(ActionProvider.COMMAND_CLEAN, user, model);
        addSingleAction(ActionProvider.COMMAND_REBUILD, user, model);
        addSingleAction(ActionProvider.COMMAND_TEST, user, model);
        addSingleAction(ActionProvider.COMMAND_TEST_SINGLE, user, model);
        addSingleAction(ActionProvider.COMMAND_RUN, user, model);
        addSingleAction(ActionProvider.COMMAND_RUN_SINGLE, user, model);
        addSingleAction(ActionProvider.COMMAND_DEBUG, user, model);
        addSingleAction(ActionProvider.COMMAND_DEBUG_SINGLE, user, model);
        addSingleAction(ActionProvider.COMMAND_DEBUG_TEST_SINGLE, user, model);
        lstMappings.setModel(model);
    }
    
    private void addSingleAction(String action, ModelHandle user, DefaultListModel model) {
        NetbeansActionMapping mapp = null;
        List lst = user.getActionMappings().getActions();
        if (lst != null) {
            Iterator it = lst.iterator();
            while (it.hasNext()) {
                NetbeansActionMapping elem = (NetbeansActionMapping) it.next();
                if (action.equals(elem.getActionName())) {
                    mapp = elem;
                    break;
                }
            }
        }
        boolean userDefined = true;
        if (mapp == null) {
            mapp = ActionToGoalUtils.getDefaultMapping(action, project);
            userDefined = false;
        }
        MappingWrapper wr;
        if (mapp == null) {
            wr = new MappingWrapper(action);
        } else {
            wr = new MappingWrapper(mapp);
        }
        wr.setUserDefined(userDefined);
        model.addElement(wr);
    }
    
    private String createSpaceSeparatedList(List list) {
        String str = "";
        if (list != null) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                String elem = (String) it.next();
                str = str + elem + " ";
            }
        }
        return str;
    }
    
    private void clearFields() {
        txtGoals.getDocument().removeDocumentListener(goalsListener);
        txtProfiles.getDocument().removeDocumentListener(profilesListener);
        txtProperties.getDocument().removeDocumentListener(propertiesListener);
        
        txtGoals.setText("");
        txtProfiles.setText("");
        txtProperties.setText("");
        
        txtGoals.getDocument().addDocumentListener(goalsListener);
        txtProfiles.getDocument().addDocumentListener(profilesListener);
        txtProperties.getDocument().addDocumentListener(propertiesListener);
        
        txtGoals.setEditable(false);
        txtProperties.setEditable(false);
        txtProfiles.setEditable(false);
        updateColor(null);
        cbRecursively.setEnabled(false);
        cbSkipTests.setEnabled(false);
    }
    
    private void updateColor(MappingWrapper wr) {
        Color col = wr != null && wr.isUserDefined() ? TextComponentUpdater.DEFAULT : TextComponentUpdater.INHERITED;
        txtGoals.setBackground(col);
        txtProperties.setBackground(col);
        txtProfiles.setBackground(col);
    }
    
    private String createPropertiesList(Properties properties) {
        String str = "";
        if (properties != null) {
            Iterator it = properties.keySet().iterator();
            while (it.hasNext()) {
                String elem = (String) it.next();
                if (!"maven.test.skip".equals(elem)) {
                    String val = properties.getProperty(elem);
                    if (val.indexOf(" ") > -1) {
                        val = "\"" + val + "\"";
                    }
                    str = str + elem + "=" + val + " ";
                }
            }
        }
        return str;
    }

    private boolean checkPropertiesList(Properties properties) {
        boolean skip = false;
        if (properties != null) {
            Iterator it = properties.keySet().iterator();
            while (it.hasNext()) {
                String elem = (String) it.next();
                if ("maven.test.skip".equals(elem)) {
                    String val = properties.getProperty(elem);
                    skip = Boolean.valueOf(val).booleanValue();
                    break;
                }
            }
        }
        return skip;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnRemove;
    private javax.swing.JCheckBox cbRecursively;
    private javax.swing.JCheckBox cbSkipTests;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblGoals;
    private javax.swing.JLabel lblHint;
    private javax.swing.JLabel lblProfiles;
    private javax.swing.JLabel lblProperties;
    private javax.swing.JList lstMappings;
    private javax.swing.JTextField txtGoals;
    private javax.swing.JTextField txtProfiles;
    private javax.swing.JTextField txtProperties;
    // End of variables declaration//GEN-END:variables

        private void writeProperties(final NetbeansActionMapping mapp) {
            String text = txtProperties.getText();
            Splitter split = new Splitter(text);
            String tok = split.nextPair();
            Properties props = new Properties();
            while (tok != null) {
                String[] prp = StringUtils.split(tok, "=", 2);
                if (prp.length == 2) {
                    props.setProperty(prp[0], prp[1]);
                }
                tok = split.nextPair();
            }
            if (cbSkipTests.isSelected()) {
                props.setProperty("maven.test.skip", "true");
            }
            mapp.setProperties(props);
        }
    
    
    private class MappingWrapper {
        private NetbeansActionMapping mapping;
        private String action;
        private boolean userDefined = false;
        
        public MappingWrapper(String action) {
            this.action = action;
        }
        
        public MappingWrapper(NetbeansActionMapping mapp) {
            action = mapp.getActionName();
            mapping = mapp;
        }
        
        public void setMapping(NetbeansActionMapping mapp) {
            mapping = mapp;
        }
        
        public String getActionName() {
            return action;
        }
        
        public NetbeansActionMapping getMapping() {
            return mapping;
        }
        
        public String toString() {
            if (titles.get(action) != null) {
                return (String)titles.get(action);
            }
            if (mapping != null) {
                if (mapping.getDisplayName() != null) {
                    return mapping.getDisplayName();
                }
                return mapping.getActionName();
            }
            return action;
        }
        
        public boolean isUserDefined() {
            return userDefined;
        }
        
        public void setUserDefined(boolean userDefined) {
            this.userDefined = userDefined;
        }
    }
    
    private abstract class TextFieldListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            doUpdate();
        }
        
        public void removeUpdate(DocumentEvent e) {
            doUpdate();
        }
        
        public void changedUpdate(DocumentEvent e) {
            doUpdate();
        }
        
        protected MappingWrapper doUpdate() {
            MappingWrapper map = (MappingWrapper)lstMappings.getSelectedValue();
            if (map != null) {
                if (!map.isUserDefined()) {
                    NetbeansActionMapping mapping = map.getMapping();
                    if (mapping == null) {
                        mapping = new NetbeansActionMapping();
                        mapping.setActionName(map.getActionName());
                        map.setMapping(mapping);
                    }
                    handle.getActionMappings().addAction(mapping);
                    map.setUserDefined(true);
                    updateColor(map);
                }
            }
            return map;
        }
    }
    
    private class GoalsListener extends TextFieldListener {
        protected MappingWrapper doUpdate() {
            MappingWrapper wr = super.doUpdate();
            if (wr != null) {
                String text = txtGoals.getText();
                StringTokenizer tok = new StringTokenizer(text, " ");
                NetbeansActionMapping mapp = wr.getMapping();
                List goals = new ArrayList();
                while (tok.hasMoreTokens()) {
                    String token = tok.nextToken();
                    goals.add(token);
                }
                mapp.setGoals(goals);
            }
            return wr;
        }
    }
    
    private class ProfilesListener extends TextFieldListener {
        protected MappingWrapper doUpdate() {
            MappingWrapper wr = super.doUpdate();
            if (wr != null) {
                String text = txtProfiles.getText();
                StringTokenizer tok = new StringTokenizer(text, " ,");
                NetbeansActionMapping mapp = wr.getMapping();
                List profs = new ArrayList();
                while (tok.hasMoreTokens()) {
                    String token = tok.nextToken();
                    profs.add(token);
                }
                mapp.setActivatedProfiles(profs);
            }
            return wr;
        }
    }
    
    private class PropertiesListener extends TextFieldListener {
        protected MappingWrapper doUpdate() {
            MappingWrapper wr = super.doUpdate();
            if (wr != null) {
                NetbeansActionMapping mapp = wr.getMapping();
                writeProperties(mapp);
            }
            return wr;
        }

    }
    
    private class TestListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            MappingWrapper map = (MappingWrapper)lstMappings.getSelectedValue();
            if (map != null) {
                if (!map.isUserDefined()) {
                    NetbeansActionMapping mapping = map.getMapping();
                    if (mapping == null) {
                        mapping = new NetbeansActionMapping();
                        mapping.setActionName(map.getActionName());
                    }
                    handle.getActionMappings().addAction(mapping);
                    map.setUserDefined(true);
                    updateColor(map);
                }
                writeProperties(map.getMapping());
            }
        }
        
    }
    
    private class RecursiveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            MappingWrapper map = (MappingWrapper)lstMappings.getSelectedValue();
            if (map != null) {
                if (!map.isUserDefined()) {
                    NetbeansActionMapping mapping = map.getMapping();
                    if (mapping == null) {
                        mapping = new NetbeansActionMapping();
                        mapping.setActionName(map.getActionName());
                    }
                    
                    handle.getActionMappings().addAction(mapping);
                    map.setUserDefined(true);
                    updateColor(map);
                }
                map.getMapping().setRecursive(cbRecursively.isSelected());
            }
        }
        
    }
    
    private class Splitter {
        private String line;
        private char[] quotes;
        private char separator;
        private boolean trim = true;
        private char escape;
        
        private int location = 0;
        private char quoteChar = 0;
        private boolean inQuote = false;
        private boolean escapeNext = false;
        
        Splitter(String line) {
            this(line, new char[] { '"' }, '\\', ' ');
        }
        
        private Splitter(String line, char[] quotes, char escape, char separator) {
            this.line = line;
            this.quotes = quotes;
            this.separator = separator;
            this.trim = trim;
            this.escape = escape;
        }
        
        
        String nextPair() {
            StringBuffer buffer = new StringBuffer();
            if (location >= line.length()) {
                return null;
            }
            //TODO should probably also handle (ignore) spaces before or after the = char somehow
            while (location < line.length()
                    && (line.charAt(location) != separator || inQuote || escapeNext)) {
                char c = line.charAt(location);
                
                if (escapeNext) {
                    buffer.append(c);
                    escapeNext = false;
                } else if (c == escape) {
                    escapeNext = true;
                } else if (inQuote) {
                    if (c == quoteChar) {
                        inQuote = false;
                    } else {
                        buffer.append(c);
                    }
                } else {
                    if (isQuoteChar(c)) {
                        inQuote = true;
                        quoteChar = c;
                    } else {
                        buffer.append(c);
                    }
                }
                location++;
            }
            location++;
            return trim ? buffer.toString().trim() : buffer.toString();
        }
        
        private boolean isQuoteChar(char c) {
            for (int i = 0; i < quotes.length; i++) {
                char quote = quotes[i];
                if (c == quote) return true;
            }
            return false;
        }
    }
    
}