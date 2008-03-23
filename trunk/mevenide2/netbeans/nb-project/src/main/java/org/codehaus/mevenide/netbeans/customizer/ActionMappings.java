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

import java.awt.Component;
import java.awt.Cursor;
import javax.swing.JList;
import java.awt.Font;
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
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.maven.profiles.Profile;
import org.codehaus.mevenide.netbeans.api.GoalsProvider;
import org.codehaus.mevenide.netbeans.api.customizer.ModelHandle;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.TextValueCompleter;
import org.codehaus.mevenide.netbeans.api.Constants;
import org.codehaus.mevenide.netbeans.api.ModelUtils;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.execute.ActionToGoalUtils;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.plexus.util.StringUtils;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.spi.project.ActionProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author  mkleint@codehaus.org
 */
public class ActionMappings extends javax.swing.JPanel {
    private static final String CUSTOM_ACTION_PREFIX = "CUSTOM-"; //NOI18N
    private NbMavenProject project;
    private ModelHandle handle;
    private HashMap<String, String> titles = new HashMap<String, String>();
    
    private GoalsListener goalsListener;
    private TextValueCompleter goalcompleter;
    private TextValueCompleter profilecompleter;
    private ProfilesListener profilesListener;
    private PropertiesListener propertiesListener;
    private TestListener testListener;
    private RecursiveListener recursiveListener;
    private CheckBoxUpdater commandLineUpdater;
    public static final String PROP_SKIP_TEST="maven.test.skip"; //NOI18N
    private ActionToGoalMapping actionmappings;
    
    private ActionMappings() {
        initComponents();
        lstMappings.setCellRenderer(new Renderer());
        lstMappings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        goalsListener = new GoalsListener();
        profilesListener = new ProfilesListener();
        propertiesListener = new PropertiesListener();
        recursiveListener = new RecursiveListener();
        testListener = new TestListener();
        FocusListener focus = new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (e.getComponent() == txtGoals) {
                    lblHint.setText(NbBundle.getMessage(ActionMappings.class, "ActionMappings.txtGoals.hint"));
                }
                if (e.getComponent() == txtProfiles) {
                    lblHint.setText(NbBundle.getMessage(ActionMappings.class, "ActinMappings.txtProfiles.hint"));
                }
                if (e.getComponent() == txtProperties) {
                    lblHint.setText(NbBundle.getMessage(ActionMappings.class, "ActinMappings.txtProperties.hint"));
                }
            }
            public void focusLost(FocusEvent e) {
                lblHint.setText(""); //NOI18N
            }
        };
        txtGoals.addFocusListener(focus);
        txtProfiles.addFocusListener(focus);
        txtProperties.addFocusListener(focus);
        goalcompleter = new TextValueCompleter(Collections.<String>emptyList(), txtGoals, " "); //NOI18N
        profilecompleter = new TextValueCompleter(Collections.<String>emptyList(), txtProfiles, " "); //NOI18N
        
    }
    
    public ActionMappings(ActionToGoalMapping mapp) {
        this();
        actionmappings = mapp;
        loadMappings();
        btnSetup.setVisible(false);
        cbCommandLine.setVisible(false);
        cbRecursively.setVisible(false);
        clearFields();
        Mnemonics.setLocalizedText(btnAdd, NbBundle.getMessage(ActionMappings.class, "ActionMappings.btnAdd.text2"));
        Mnemonics.setLocalizedText(btnRemove, NbBundle.getMessage(ActionMappings.class, "ActionMappings.btnRemove.text2"));
    }
    
    /** Creates new form ActionMappings */
    public ActionMappings(ModelHandle hand, NbMavenProject proj) {
        this();
        project = proj;
        handle = hand;
        titles.put(ActionProvider.COMMAND_BUILD, org.openide.util.NbBundle.getMessage(ActionMappings.class, "COM_Build_project"));
        titles.put(ActionProvider.COMMAND_CLEAN, org.openide.util.NbBundle.getMessage(ActionMappings.class, "COM_Clean_project"));
        titles.put(ActionProvider.COMMAND_COMPILE_SINGLE, org.openide.util.NbBundle.getMessage(ActionMappings.class, "COM_Compile_file"));
        titles.put(ActionProvider.COMMAND_DEBUG, org.openide.util.NbBundle.getMessage(ActionMappings.class, "COM_Debug_project"));
        titles.put(ActionProvider.COMMAND_DEBUG_SINGLE, org.openide.util.NbBundle.getMessage(ActionMappings.class, "COM_Debug_file"));
        titles.put(ActionProvider.COMMAND_DEBUG_STEP_INTO, null);
        titles.put(ActionProvider.COMMAND_DEBUG_TEST_SINGLE, org.openide.util.NbBundle.getMessage(ActionMappings.class, "COM_Debug_test"));
        titles.put(ActionProvider.COMMAND_REBUILD, org.openide.util.NbBundle.getMessage(ActionMappings.class, "COM_ReBuild_project"));
        titles.put(ActionProvider.COMMAND_RUN, org.openide.util.NbBundle.getMessage(ActionMappings.class, "COM_Run_project"));
        titles.put(ActionProvider.COMMAND_RUN_SINGLE, org.openide.util.NbBundle.getMessage(ActionMappings.class, "COM_Run_file"));
        titles.put(ActionProvider.COMMAND_TEST, org.openide.util.NbBundle.getMessage(ActionMappings.class, "COM_Test_project"));
        titles.put(ActionProvider.COMMAND_TEST_SINGLE, org.openide.util.NbBundle.getMessage(ActionMappings.class, "COM_Test_file"));
        loadMappings();
        btnSetup.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSetup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //TODO when #109538 gets fixed.
                OptionsDisplayer.getDefault().open(); //NOI18N - the id is the name of instance in layers.
            }
            
        });
        commandLineUpdater = new CheckBoxUpdater(cbCommandLine) {
            public Boolean getValue() {
                Profile prof = handle.getNetbeansPrivateProfile(false);
                if (prof != null && prof.getProperties().getProperty(Constants.HINT_USE_EXTERNAL) != null) {
                    return Boolean.valueOf(prof.getProperties().getProperty(Constants.HINT_USE_EXTERNAL));
                }
                String val = handle.getPOMModel().getProperties().getProperty(Constants.HINT_USE_EXTERNAL);
                if (val != null) {
                    return Boolean.valueOf(val);
                }
                return null;
            }

            public Boolean getProjectValue() {
                String val = project.getOriginalMavenProject().getProperties().getProperty(Constants.HINT_USE_EXTERNAL); //NOI18N
                if (val != null) {
                    return Boolean.valueOf(val);
                }
                return null;
            }

            public void setValue(Boolean value) {
                Profile prof = handle.getNetbeansPrivateProfile(false);
                if (prof != null && prof.getProperties().getProperty(Constants.HINT_USE_EXTERNAL) != null) {
                    prof.getProperties().setProperty(Constants.HINT_USE_EXTERNAL, value == null ? "false" : value.toString());
                    handle.markAsModified(handle.getProfileModel());
                    return;
                }
                
                if (value == null || value.booleanValue() == false) {
                    Boolean proj = getProjectValue();
                    if (proj != null && proj.equals(Boolean.TRUE)) {
                        handle.getPOMModel().addProperty(Constants.HINT_USE_EXTERNAL, "false"); //NOI18N
                    } else {
                        handle.getPOMModel().getProperties().remove(Constants.HINT_USE_EXTERNAL);
                    }
                } else {
                    handle.getPOMModel().addProperty(Constants.HINT_USE_EXTERNAL, "true"); //NOI18N
                }
                handle.markAsModified(handle.getPOMModel());
            }

            public boolean getDefaultValue() {
                return false;
            }
        };
        clearFields();
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        //TODO move the list population out of AWT.
        GoalsProvider provider = Lookup.getDefault().lookup(GoalsProvider.class);
        if (provider != null) {
            Set<String> strs = provider.getAvailableGoals();
            try {
                @SuppressWarnings("unchecked")
                List<String> phases = EmbedderFactory.getProjectEmbedder().getLifecyclePhases();
                strs.addAll(phases);
            } catch (Exception e) {
                // oh wel just ignore..
                e.printStackTrace();
            }
            goalcompleter.setValueList(strs);
        }
        if (project != null) {
            List<String> lst = ModelUtils.retrieveAllProfiles(project.getPOMFile());
            profilecompleter.setValueList(lst);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
        cbCommandLine = new javax.swing.JCheckBox();
        lblHint = new javax.swing.JLabel();
        lblMappings = new javax.swing.JLabel();
        btnSetup = new javax.swing.JButton();

        lstMappings.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstMappingsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(lstMappings);

        org.openide.awt.Mnemonics.setLocalizedText(btnAdd, org.openide.util.NbBundle.getMessage(ActionMappings.class, "ActionMappings.btnAdd.text")); // NOI18N
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnRemove, org.openide.util.NbBundle.getMessage(ActionMappings.class, "ActionMappings.btnRemove.text")); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblGoals, org.openide.util.NbBundle.getMessage(ActionMappings.class, "ActionMappings.lblGoals.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblProfiles, org.openide.util.NbBundle.getMessage(ActionMappings.class, "ActionMappings.lblProfiles.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblProperties, org.openide.util.NbBundle.getMessage(ActionMappings.class, "ActionMappings.lblProperties.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cbRecursively, org.openide.util.NbBundle.getMessage(ActionMappings.class, "ActionMappings.cbRecursively.text")); // NOI18N
        cbRecursively.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(cbSkipTests, org.openide.util.NbBundle.getMessage(ActionMappings.class, "ActionMappings.cbSkipTests.text")); // NOI18N
        cbSkipTests.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(cbCommandLine, "&Use external Maven for build execution");
        cbCommandLine.setToolTipText("If checked, will build with external version of Maven. Otherwise builds with embedded Maven.");
        cbCommandLine.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbCommandLine.setMargin(new java.awt.Insets(0, 0, 0, 0));

        lblHint.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        lblMappings.setLabelFor(lstMappings);
        org.openide.awt.Mnemonics.setLocalizedText(lblMappings, org.openide.util.NbBundle.getMessage(ActionMappings.class, "LBL_Actions")); // NOI18N
        lblMappings.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        org.openide.awt.Mnemonics.setLocalizedText(btnSetup, "<html><a href=\"\">Setup external Maven home</a></html>");
        btnSetup.setBorder(null);
        btnSetup.setBorderPainted(false);
        btnSetup.setContentAreaFilled(false);
        btnSetup.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(lblHint, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(btnAdd)
                            .add(btnRemove)))
                    .add(layout.createSequentialGroup()
                        .add(cbCommandLine)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 220, Short.MAX_VALUE)
                        .add(btnSetup, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 210, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(lblMappings)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblGoals)
                            .add(lblProfiles)
                            .add(lblProperties))
                        .add(16, 16, 16)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(cbRecursively)
                                .add(18, 18, 18)
                                .add(cbSkipTests))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtProperties, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
                            .add(txtProfiles, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
                            .add(txtGoals, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {btnAdd, btnRemove}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbCommandLine)
                    .add(btnSetup))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblMappings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(btnAdd)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnRemove))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblGoals)
                    .add(txtGoals, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblProfiles)
                    .add(txtProfiles, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblProperties)
                    .add(txtProperties, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbRecursively)
                    .add(cbSkipTests))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblHint, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
    NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine(org.openide.util.NbBundle.getMessage(ActionMappings.class, "TIT_Add_action"), org.openide.util.NbBundle.getMessage(ActionMappings.class, "LBL_AddAction"));
    Object ret = DialogDisplayer.getDefault().notify(nd);
    if (ret == NotifyDescriptor.OK_OPTION) {
        NetbeansActionMapping nam = new NetbeansActionMapping();
        nam.setDisplayName(nd.getInputText());
        nam.setActionName(CUSTOM_ACTION_PREFIX + nd.getInputText()); 
        getActionMappings().addAction(nam);
        if (handle != null) {
            handle.markAsModified(handle.getActionMappings());
        }
        MappingWrapper wr = new MappingWrapper(nam);
        wr.setUserDefined(true);
        ((DefaultListModel)lstMappings.getModel()).addElement(wr);
        lstMappings.setSelectedIndex(lstMappings.getModel().getSize() - 1);
        lstMappings.ensureIndexIsVisible(lstMappings.getModel().getSize() - 1);
    }
}//GEN-LAST:event_btnAddActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        Object obj = lstMappings.getSelectedValue();
        if (obj == null) {
            return;
        }
        MappingWrapper wr = (MappingWrapper)obj;
        NetbeansActionMapping mapp = wr.getMapping();
        if (mapp != null) {
            if (mapp.getActionName().startsWith(CUSTOM_ACTION_PREFIX)) { 
                ((DefaultListModel)lstMappings.getModel()).removeElement(wr);
            }
            // try removing from model, if exists..
            List lst = getActionMappings().getActions();
            if (lst != null) {
                Iterator it = lst.iterator();
                while (it.hasNext()) {
                    NetbeansActionMapping elem = (NetbeansActionMapping) it.next();
                    if (mapp.getActionName().equals(elem.getActionName())) {
                        it.remove();
                        if (handle != null) {
                            mapp = ActionToGoalUtils.getDefaultMapping(mapp.getActionName(), project);
                        } else {
                            mapp = null;
                        }
                        wr.setMapping(mapp);
                        wr.setUserDefined(false);
                        lstMappingsValueChanged(null);
                        if (handle != null) {
                            handle.markAsModified(handle.getActionMappings());
                        }
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
            if (handle != null && "pom".equals(handle.getProject().getPackaging())) { //NOI18N
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
        DefaultListModel model = new DefaultListModel();
        if (handle != null) {
            addSingleAction(ActionProvider.COMMAND_BUILD, handle, model);
            addSingleAction(ActionProvider.COMMAND_CLEAN, handle, model);
            addSingleAction(ActionProvider.COMMAND_REBUILD, handle, model);
            addSingleAction(ActionProvider.COMMAND_TEST, handle, model);
            addSingleAction(ActionProvider.COMMAND_TEST_SINGLE, handle, model);
            addSingleAction(ActionProvider.COMMAND_RUN, handle, model);
            addSingleAction(ActionProvider.COMMAND_RUN_SINGLE, handle, model);
            addSingleAction(ActionProvider.COMMAND_DEBUG, handle, model);
            addSingleAction(ActionProvider.COMMAND_DEBUG_SINGLE, handle, model);
            addSingleAction(ActionProvider.COMMAND_DEBUG_TEST_SINGLE, handle, model);
        }
        List customs = getActionMappings().getActions();
        if (customs != null) {
            Iterator it = customs.iterator();
            while (it.hasNext()) {
                NetbeansActionMapping elem = (NetbeansActionMapping) it.next();
                if (elem.getActionName().startsWith(CUSTOM_ACTION_PREFIX)) {
                    MappingWrapper wr = new MappingWrapper(elem);
                    model.addElement(wr);
                    wr.setUserDefined(true);
                }
            }
        }
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
        String str = ""; //NOI18N
        if (list != null) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                String elem = (String) it.next();
                str = str + elem + " "; //NOI18N
            }
        }
        return str;
    }
    
    private void clearFields() {
        txtGoals.getDocument().removeDocumentListener(goalsListener);
        txtProfiles.getDocument().removeDocumentListener(profilesListener);
        txtProperties.getDocument().removeDocumentListener(propertiesListener);
        
        txtGoals.setText(""); //NOI18N
        txtProfiles.setText(""); //NOI18N
        txtProperties.setText(""); //NOI18N
        
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
        Font fnt = lblGoals.getFont();
        fnt = fnt.deriveFont(wr != null && wr.isUserDefined() ? Font.BOLD : Font.PLAIN);
        lblGoals.setFont(fnt);
        lblProperties.setFont(fnt);
        lblProfiles.setFont(fnt);
    }
    
    private String createPropertiesList(Properties properties) {
        String str = ""; //NOI18N
        if (properties != null) {
            Iterator it = properties.keySet().iterator();
            while (it.hasNext()) {
                String elem = (String) it.next();
                if (!PROP_SKIP_TEST.equals(elem)) {
                    String val = properties.getProperty(elem);
                    if (val.indexOf(" ") > -1) { //NOI18N
                        val = "\"" + val + "\""; //NOI18N
                    }
                    str = str + elem + "=" + val + " "; //NOI18N
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
                if (PROP_SKIP_TEST.equals(elem)) {
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
    private javax.swing.JButton btnSetup;
    private javax.swing.JCheckBox cbCommandLine;
    private javax.swing.JCheckBox cbRecursively;
    private javax.swing.JCheckBox cbSkipTests;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblGoals;
    private javax.swing.JLabel lblHint;
    private javax.swing.JLabel lblMappings;
    private javax.swing.JLabel lblProfiles;
    private javax.swing.JLabel lblProperties;
    private javax.swing.JList lstMappings;
    private javax.swing.JTextField txtGoals;
    private javax.swing.JTextField txtProfiles;
    private javax.swing.JTextField txtProperties;
    // End of variables declaration//GEN-END:variables
    
    private void writeProperties(final NetbeansActionMapping mapp) {
        String text = txtProperties.getText();
        PropertySplitter split = new PropertySplitter(text);
        String tok = split.nextPair();
        Properties props = new Properties();
        while (tok != null) {
            String[] prp = StringUtils.split(tok, "=", 2); //NOI18N
            if (prp.length == 2) {
                String key = prp[0];
                //in case the user adds -D by mistake, remove it to get a parsable xml file.
                if (key.startsWith("-D")) { //NOI18N
                    key = key.substring("-D".length()); //NOI18N
                }
                props.setProperty(key, prp[1]);
            }
            tok = split.nextPair();
        }
        if (cbSkipTests.isSelected()) {
            props.setProperty(PROP_SKIP_TEST, "true"); //NOI18N
        }
        mapp.setProperties(props);
        if (handle != null) {
            handle.markAsModified(handle.getActionMappings());
        }
    }
    
    private ActionToGoalMapping getActionMappings() {
        assert handle != null || actionmappings != null;
        if (handle != null) {
            return handle.getActionMappings();
        }
        return actionmappings;
    }
    
    private static class Renderer extends DefaultListCellRenderer {
        
    
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                                                      int arg2, boolean arg3,
                                                      boolean arg4) {
            Component supers = super.getListCellRendererComponent(list, value, arg2, arg3, arg4);
            if (supers instanceof JLabel && value instanceof MappingWrapper) {
                MappingWrapper wr = (MappingWrapper)value;
                JLabel lbl = (JLabel)supers;
                if (wr.isUserDefined()) {
                    lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                } else {
                    lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));
                }
            }
            return supers;
        }
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
        
        @Override
        public String toString() {
            if (titles.get(action) != null) {
                return titles.get(action);
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
                    if (handle != null) {
                        handle.markAsModified(handle.getActionMappings());
                    }
                    map.setUserDefined(true);
                    updateColor(map);
                }
            }
            return map;
        }
    }
    
    private class GoalsListener extends TextFieldListener {
        @Override
        protected MappingWrapper doUpdate() {
            MappingWrapper wr = super.doUpdate();
            if (wr != null) {
                String text = txtGoals.getText();
                StringTokenizer tok = new StringTokenizer(text, " "); //NOI18N
                NetbeansActionMapping mapp = wr.getMapping();
                List<String> goals = new ArrayList<String>();
                while (tok.hasMoreTokens()) {
                    String token = tok.nextToken();
                    goals.add(token);
                }
                mapp.setGoals(goals);
                if (handle != null) {
                    handle.markAsModified(handle.getActionMappings());
                }
            }
            return wr;
        }
    }
    
    private class ProfilesListener extends TextFieldListener {
        @Override
        protected MappingWrapper doUpdate() {
            MappingWrapper wr = super.doUpdate();
            if (wr != null) {
                String text = txtProfiles.getText();
                StringTokenizer tok = new StringTokenizer(text, " ,"); //NOI18N
                NetbeansActionMapping mapp = wr.getMapping();
                List<String> profs = new ArrayList<String>();
                while (tok.hasMoreTokens()) {
                    String token = tok.nextToken();
                    profs.add(token);
                }
                mapp.setActivatedProfiles(profs);
                if (handle != null) {
                    handle.markAsModified(handle.getActionMappings());
                }
            }
            return wr;
        }
    }
    
    private class PropertiesListener extends TextFieldListener {
        @Override
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
                    getActionMappings().addAction(mapping);
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
                    
                    getActionMappings().addAction(mapping);
                    map.setUserDefined(true);
                    updateColor(map);
                }
                map.getMapping().setRecursive(cbRecursively.isSelected());
                if (handle != null) {
                    handle.markAsModified(handle.getActionMappings());
                }
            }
        }
        
    }
    
    
}
