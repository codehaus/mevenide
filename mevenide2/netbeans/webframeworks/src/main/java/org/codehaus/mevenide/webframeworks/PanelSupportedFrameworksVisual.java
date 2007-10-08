/* ==========================================================================
 * Copyright 2007 Mevenide Team
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

package org.codehaus.mevenide.webframeworks;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eePlatform;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ServerManager;
import org.netbeans.modules.web.api.webmodule.ExtenderController;
import org.netbeans.modules.web.api.webmodule.ExtenderController.Properties;
import org.netbeans.modules.web.api.webmodule.WebFrameworks;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebFrameworkProvider;
import org.netbeans.modules.web.spi.webmodule.WebModuleExtender;
import org.openide.util.Lookup;

/**
 * 
 * @author mkleint
 */ 
public class PanelSupportedFrameworksVisual extends JPanel implements HelpCtx.Provider, TableModelListener, ListSelectionListener, ChangeListener {
    
    /** All available web extensions */
    public static final int ALL_FRAMEWORKS = 0;
    
    /** Web extensions used in the project */
    public static final int USED_FRAMEWORKS = 1;
    
    /** Web extensions which are not used in the project */
    public static final int UNUSED_FRAMEWORKS = 2;
    
    private List ignoredFrameworks;
    private Map extenders = new IdentityHashMap();

    private FrameworksTableModel model;
    private PanelSupportedFrameworks panel;
    private WizardDescriptor wizardDescriptor;
    private final DefaultComboBoxModel serversModel = new DefaultComboBoxModel();
    private final ExtenderController controller = ExtenderController.create();
    
    /** Creates new form PanelInitProject
     * @param project the web project; if it is null, all available web extensions will be shown
     * @param filter one of the options <code>ALL_FRAMEWORKS</code>, <code>USED_FRAMEWORKS</code>, <code>UNUSED_FRAMEWORKS</code>
     * @param ignoredFrameworks the list of frameworks to be ignored when creating list; null is allowed
     */
    PanelSupportedFrameworksVisual(PanelSupportedFrameworks panel, Project project, int filter, List ignoredFrameworks) {
        this.panel = panel;
        this.ignoredFrameworks = ignoredFrameworks;
        initComponents();

        initServers(getReflectionLastServer());
        model = new FrameworksTableModel();
        jTableFrameworks.setModel(model);
        createFrameworksList(project, filter);

        FrameworksTableCellRenderer renderer = new FrameworksTableCellRenderer();
        renderer.setBooleanRenderer(jTableFrameworks.getDefaultRenderer(Boolean.class));
        jTableFrameworks.setDefaultRenderer(WebFrameworkProvider.class, renderer);
        jTableFrameworks.setDefaultRenderer(Boolean.class, renderer);
        initTableVisualProperties(jTableFrameworks);

        this.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(PanelSupportedFrameworksVisual.class, "ACS_NWP2_Frameworks_A11YDesc"));  // NOI18N        

        // Provide a name in the title bar.
        setName(NbBundle.getMessage(PanelSupportedFrameworksVisual.class, "LBL_NWP2_Frameworks")); //NOI18N
//        putClientProperty ("NewProjectWizard_Title", NbBundle.getMessage(PanelSupportedFrameworksVisual.class, "TXT_NewWebApp")); //NOI18N
    }
    
    private static String getReflectionLastServer() {
        try {
            ClassLoader cl = Lookup.getDefault().lookup(ClassLoader.class);
            return (String) doReflect(cl);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
    
    static Object doReflect(ClassLoader cl) throws Exception {
        Class clazz = cl.loadClass("org.netbeans.modules.web.project.ui.FoldersListSettings"); //NOI18N
        @SuppressWarnings("unchecked") //NOI18N
        Method inst = clazz.getDeclaredMethod("getDefault"); //NOI18N
        @SuppressWarnings("unchecked") //NOI18N
        Method last = clazz.getDeclaredMethod("getLastUsedServer"); //NOI18N
        Object instance = inst.invoke(null);
        return  last.invoke(instance);
    }
    
    /**
     * Init servers model
     * @param selectedServerInstanceID preselected instance or null if non is preselected
     */
    private void initServers(String selectedServerInstanceID) {
        // init the list of server instances
        serversModel.removeAllElements();
        Set<ServerInstanceWrapper> servers = new TreeSet<ServerInstanceWrapper>();
        ServerInstanceWrapper selectedItem = null;
        boolean sjasFound = false;
        for (String serverInstanceID : Deployment.getDefault().getServerInstanceIDs()) {
            String displayName = Deployment.getDefault().getServerInstanceDisplayName(serverInstanceID);
            J2eePlatform j2eePlatform = Deployment.getDefault().getJ2eePlatform(serverInstanceID);
            if (displayName != null && j2eePlatform != null && j2eePlatform.getSupportedModuleTypes().contains(J2eeModule.WAR)) {
                ServerInstanceWrapper serverWrapper = new ServerInstanceWrapper(serverInstanceID, displayName);
                // decide whether this server should be preselected
                if (selectedItem == null || !sjasFound) {
                    if (selectedServerInstanceID != null) {
                        if (selectedServerInstanceID.equals(serverInstanceID)) {
                            selectedItem = serverWrapper;
                        }
                    } else {
                        // preselect the best server ;)
                        String shortName = Deployment.getDefault().getServerID(serverInstanceID);
                        if ("J2EE".equals(shortName)) { // NOI18N
                            selectedItem = serverWrapper;
                            sjasFound = true;
                        }
                        else
                        if ("JBoss4".equals(shortName)) { // NOI18N
                            selectedItem = serverWrapper;
                        }
                    }
                }
                servers.add(serverWrapper);
            }
        }
        for (ServerInstanceWrapper item : servers) {
            serversModel.addElement(item);
        }
        jTableFrameworks.setEnabled(true);
        if (selectedItem != null) {
            // set the preselected item
            serversModel.setSelectedItem(selectedItem);
        } else if (serversModel.getSize() > 0) {
            // set the first item
            serversModel.setSelectedItem(serversModel.getElementAt(0));
        } else {
            jTableFrameworks.setEnabled(false);
        }
    }
    
    private String getSelectedServer() {
        ServerInstanceWrapper serverInstanceWrapper = (ServerInstanceWrapper) serversModel.getSelectedItem();
        if (serverInstanceWrapper == null) {
            return null;
        }
        return serverInstanceWrapper.getServerInstanceID();
    }
    

    private void initTableVisualProperties(JTable table) {
        table.getModel().addTableModelListener(this);
        
        table.setRowSelectionAllowed(true);
        table.getSelectionModel().addListSelectionListener(this);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        table.setTableHeader(null);
        
        table.setRowHeight(jTableFrameworks.getRowHeight() + 4);        
        table.setIntercellSpacing(new java.awt.Dimension(0, 0));        
        // set the color of the table's JViewport
        table.getParent().setBackground(table.getBackground());
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        
        table.getColumnModel().getColumn(0).setMaxWidth(30);
    }

    private void createFrameworksList(Project project, int filter) {
        List<WebFrameworkProvider> frameworks = WebFrameworks.getFrameworks();
        
        if (project == null || filter == ALL_FRAMEWORKS) {
            for (int i = 0; i < frameworks.size(); i++) {
                addFrameworkToModel(frameworks.get(i));
            }
        } else if (filter == USED_FRAMEWORKS) {
            WebModule apiWebModule = WebModule.getWebModule(project.getProjectDirectory());
            for (int i = 0; i < frameworks.size(); i++) {
                WebFrameworkProvider framework = frameworks.get(i);
                if (framework.isInWebModule(apiWebModule)) {
                    addFrameworkToModel(framework);
                }
            }
        } else if (filter == UNUSED_FRAMEWORKS) {
            WebModule apiWebModule = WebModule.getWebModule(project.getProjectDirectory());
            for (int i = 0; i < frameworks.size(); i++) {
                WebFrameworkProvider framework = frameworks.get(i);
                if (!framework.isInWebModule(apiWebModule)) {
                    addFrameworkToModel(framework);
                }
            }
        }
        
    }
    
    private void addFrameworkToModel(WebFrameworkProvider framework) {
        FrameworksTableModel mdl = (FrameworksTableModel) jTableFrameworks.getModel();
        if (ignoredFrameworks == null || !ignoredFrameworks.contains(framework))
            mdl.addItem(new FrameworkModelItem(framework));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableFrameworks = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        jLabelConfig = new javax.swing.JLabel();
        jPanelConfig = new javax.swing.JPanel();
        serverInstanceLabel = new javax.swing.JLabel();
        serverInstanceComboBox = new javax.swing.JComboBox();
        addServerButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(500, 340));
        setRequestFocusEnabled(false);

        jLabel1.setLabelFor(jTableFrameworks);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(PanelSupportedFrameworksVisual.class, "LBL_NWP2_Select_Frameworks")); // NOI18N

        jScrollPane1.setMaximumSize(new java.awt.Dimension(32767, 70));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(22, 70));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(453, 70));

        jTableFrameworks.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTableFrameworks);

        jLabelConfig.setLabelFor(jPanelConfig);

        jPanelConfig.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(serverInstanceLabel, NbBundle.getMessage(PanelSupportedFrameworksVisual.class, "LBL_NWP1_Server")); // NOI18N

        serverInstanceComboBox.setModel(serversModel);
        serverInstanceComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serverInstanceComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(addServerButton, org.openide.util.NbBundle.getMessage(PanelSupportedFrameworksVisual.class, "LBL_AddServer")); // NOI18N
        addServerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addServerButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanelConfig, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(serverInstanceLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(serverInstanceComboBox, 0, 368, Short.MAX_VALUE)))
                        .add(6, 6, 6)
                        .add(addServerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 65, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabelConfig, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(serverInstanceLabel))
                    .add(addServerButton)
                    .add(serverInstanceComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1)
                .add(12, 12, 12)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabelConfig, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelConfig, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 184, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jLabel1.getAccessibleContext().setAccessibleDescription(null);
    }// </editor-fold>//GEN-END:initComponents

    private void serverInstanceComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serverInstanceComboBoxActionPerformed
//        String prevSelectedItem = (String) j2eeSpecComboBox.getSelectedItem();
//        // update the j2ee spec list according to the selected server
//        ServerInstanceWrapper serverInstanceWrapper = (ServerInstanceWrapper) serversModel.getSelectedItem();
//        if (serverInstanceWrapper != null) {
//            J2eePlatform j2eePlatform = Deployment.getDefault().getJ2eePlatform(serverInstanceWrapper.getServerInstanceID());
//            Set supportedVersions = j2eePlatform.getSupportedSpecVersions(J2eeModule.WAR);
//            j2eeSpecComboBox.removeAllItems();
//            if (supportedVersions.contains(J2eeModule.JAVA_EE_5)) {
//                j2eeSpecComboBox.addItem(JAVA_EE_SPEC_50_LABEL);
//            }
//            if (supportedVersions.contains(J2eeModule.J2EE_14)) {
//                j2eeSpecComboBox.addItem(J2EE_SPEC_14_LABEL);
//            }
//            if (supportedVersions.contains(J2eeModule.J2EE_13)) {
//                j2eeSpecComboBox.addItem(J2EE_SPEC_13_LABEL);
//            }
//            if (prevSelectedItem != null) {
//                j2eeSpecComboBox.setSelectedItem(prevSelectedItem);
//            }
//        } else {
//            j2eeSpecComboBox.removeAllItems();
//        }
//        // revalidate the form
//        panel.fireChangeEvent();
    }//GEN-LAST:event_serverInstanceComboBoxActionPerformed

    private void addServerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addServerButtonActionPerformed
        ServerInstanceWrapper serverInstanceWrapper = (ServerInstanceWrapper) serversModel.getSelectedItem();
        String selectedServerInstanceID = null;
        if (serverInstanceWrapper != null) {
            selectedServerInstanceID = serverInstanceWrapper.getServerInstanceID();
        }
//        String lastSelectedJ2eeSpecLevel = (String) j2eeSpecComboBox.getSelectedItem();
        String newServerInstanceID = ServerManager.showAddServerInstanceWizard();
        if (newServerInstanceID != null) {
            selectedServerInstanceID = newServerInstanceID;
            // clear the spec level selection
//            lastSelectedJ2eeSpecLevel = null;
//            j2eeSpecComboBox.setSelectedItem(null);
        }
        // refresh the list of servers
        initServers(selectedServerInstanceID);
        //revalidate..
        valid(wizardDescriptor);
//        if (lastSelectedJ2eeSpecLevel != null) {
//            j2eeSpecComboBox.setSelectedItem(lastSelectedJ2eeSpecLevel);
//        }
    }//GEN-LAST:event_addServerButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addServerButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelConfig;
    private javax.swing.JPanel jPanelConfig;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTableFrameworks;
    private javax.swing.JComboBox serverInstanceComboBox;
    private javax.swing.JLabel serverInstanceLabel;
    // End of variables declaration//GEN-END:variables

    boolean valid(WizardDescriptor wizardDescriptor) {
        setErrorMessage(wizardDescriptor, null);
        wizardDescriptor.putProperty("serverInstanceID", getSelectedServer());
        //TODO how to configure?
        wizardDescriptor.putProperty("j2eeLevel", "1.5"); //NOI18N
        if (getSelectedServer() == null) {
            jTableFrameworks.setEnabled(false);
            String errMsg = NbBundle.getMessage(PanelSupportedFrameworksVisual.class, "MSG_NoServer");
            setErrorMessage(wizardDescriptor, errMsg);
            return false;
        } else {
            jTableFrameworks.setEnabled(true);
            //hack to update extenders..
            read(wizardDescriptor);
        }
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getItem(i).isSelected().booleanValue()) {
                FrameworkModelItem item = model.getItem(i);
                WebModuleExtender extender = (WebModuleExtender) extenders.get(item.getFramework());
                if (extender != null && !extender.isValid()) {
                    setErrorMessage(wizardDescriptor, controller.getErrorMessage());
                    return false;
                }
            }
        }
        return true;

    }
    
       private void setErrorMessage(WizardDescriptor wizardDescriptor, String errorMessage) {
        if (errorMessage == null || errorMessage.length() == 0) {
            errorMessage = " "; // NOI18N
        }
        wizardDescriptor.putProperty("WizardPanel_errorMessage", errorMessage); // NOI18N
    }
 

    void validate (WizardDescriptor settings) throws WizardValidationException {
//        projectLocationPanel.validate (d);
    }
    
    void read (WizardDescriptor settings) {
        
//        if ( bottomPanel != null ) {
//            bottomPanel.readSettings( settings );
//        }        
        wizardDescriptor=settings;
        String serverInstanceID = (String) settings.getProperty("serverInstanceID");
        Properties properties = controller.getProperties();
        properties.setProperty("name", (String) settings.getProperty("name")); // NOI18N
        properties.setProperty("j2eeLevel", (String) settings.getProperty("j2eeLevel")); // NOI18N
        properties.setProperty("serverInstanceID", serverInstanceID); // NOI18N
        properties.setProperty("setSourceLevel", (String) settings.getProperty("setSourceLevel")); // NOI18N
        if (extenders.size() == 0) {
            // Initializing the config panels lazily; should not be done in getComponent(),
            // as that is called too early, even before the wizard properties have been set,
            // thus breaking impls of getComponent() relying on those properties
            createExtenders();
        }
        
        // In the ideal case this should be called before createExtenders();
        // calling it afterwards causes ConfigurationPanel.getComponent() to be called
        // before ConfigurationPanel.update(), which does not make sense (it effectively
        // obtains an empty component first and updates it with data afterwards.
        // Unfortunately existing panels expect to be called that way, so we are stuck with this for now
        if (serverInstanceID != null) {
            for (int i = 0; i < model.getRowCount(); i++) {
                FrameworkModelItem item = model.getItem(i);
                WebModuleExtender extender = (WebModuleExtender) extenders.get(item.getFramework());
                if (extender != null) {
                    extender.update();
                }
            }
        }
        
    }

    void store(WizardDescriptor settings) {
//        if ( bottomPanel != null ) {
//            bottomPanel.storeSettings( settings );
//        }
        
        settings.putProperty(WizardProperties.EXTENDERS, getSelectedExtenders());    //NOI18N
    }

    public List getSelectedExtenders() {
        List selectedExtenders = new LinkedList();
        FrameworksTableModel model = (FrameworksTableModel) jTableFrameworks.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            FrameworkModelItem item = model.getItem(i);
            if (item.isSelected()) {
                WebModuleExtender extender = (WebModuleExtender) extenders.get(item.getFramework());
                if (extender != null) {
                    selectedExtenders.add(extender);
                }
            }
        }
        
        return selectedExtenders;
    }
    
    private void createExtenders() {
        for (int i = 0; i < model.getRowCount(); i++) {
            FrameworkModelItem item = model.getItem(i);
            WebFrameworkProvider framework = item.getFramework();
            WebModuleExtender extender = framework.createWebModuleExtender(null, controller);
            if (extender != null) {
                extender.addChangeListener(this);
                extenders.put(framework, extender);
            }
        }
    }
    
    
    public Component[] getConfigComponents() {
        return new Component[] {jSeparator1, jLabelConfig, jPanelConfig};
    }
    
    /** Help context where to find more about the paste type action.
     * @return the help context for this action
     */
    public HelpCtx getHelpCtx() {
        if (jPanelConfig.getComponentCount()>0){
            for (int i = 0; i < jPanelConfig.getComponentCount(); i++)
                if (jPanelConfig.getComponent(i) instanceof  HelpCtx.Provider)
                    return ((HelpCtx.Provider)jPanelConfig.getComponent(i)).getHelpCtx();
        }
        return null;
    }
    
    public void tableChanged(TableModelEvent e) {
        FrameworksTableModel mdl = (FrameworksTableModel) jTableFrameworks.getModel();
        FrameworkModelItem item = mdl.getItem(jTableFrameworks.getSelectedRow());
        WebFrameworkProvider framework = item.getFramework();
        setConfigPanel(framework, item);
    }
    
    public void valueChanged(javax.swing.event.ListSelectionEvent e) {
        FrameworksTableModel mdl = (FrameworksTableModel) jTableFrameworks.getModel();
        FrameworkModelItem item = mdl.getItem(jTableFrameworks.getSelectedRow());
        WebFrameworkProvider framework = item.getFramework();
        setConfigPanel(framework, item);
    }
    
    private void setConfigPanel(WebFrameworkProvider framework, FrameworkModelItem item) {
        if (extenders.get(framework) != null) {
            String message = MessageFormat.format(NbBundle.getMessage(PanelSupportedFrameworksVisual.class, "LBL_NWP2_ConfigureFramework"), new Object[] {framework.getName()}); //NOI18N
            jLabelConfig.setText(message);
//            jLabelConfig.setEnabled(item.isSelected().booleanValue());
            
            jPanelConfig.removeAll();

            java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
            gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;

            JComponent panelComponent = ((WebModuleExtender) extenders.get(framework)).getComponent();
            jPanelConfig.add(panelComponent, gridBagConstraints);
            
            jLabelConfig.setEnabled(item.isSelected().booleanValue());
            enableComponents(panelComponent, item.isSelected().booleanValue());
            ((WebModuleExtender) extenders.get(framework)).update();
            jPanelConfig.revalidate();
            jPanelConfig.repaint();
        } else {
            jLabelConfig.setText(""); //NOI18N
            jPanelConfig.removeAll();
            jPanelConfig.repaint();
            jPanelConfig.revalidate();
        }
        
        if (panel != null)
            panel.fireChangeEvent();
    }

    private void enableComponents(Container root, boolean enabled) {
        root.setEnabled(enabled);
        for (int i = 0; i < root.getComponentCount(); i++) {
            Component child = root.getComponent(i);
            if (child instanceof Container) {
                enableComponents((Container)child, enabled);
            } else {
                child.setEnabled(enabled);
            }
        }
    }
    
    
    public void stateChanged(javax.swing.event.ChangeEvent e) {
        if (panel != null)
            panel.fireChangeEvent();
    }
    
    public static class FrameworksTableCellRenderer extends DefaultTableCellRenderer {
        private TableCellRenderer booleanRenderer;
        
        @Override
        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
            if (value instanceof WebFrameworkProvider) {
                WebFrameworkProvider item = (WebFrameworkProvider) value;
                return super.getTableCellRendererComponent(table, item.getName(), isSelected, false, row, column);
            } else {
                if (value instanceof Boolean && booleanRenderer != null)
                    return booleanRenderer.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                else
                    return super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
            }
        }
        
        public void setBooleanRenderer(TableCellRenderer booleanRenderer) {
            this.booleanRenderer = booleanRenderer;
        }
    }

    /** 
     * Implements a TableModel.
     */
    public static final class FrameworksTableModel extends AbstractTableModel {
        private DefaultListModel model;
        
        public FrameworksTableModel() {
            model = new DefaultListModel();
        }
        
        public int getColumnCount() {
            return 2;
        }
        
        public int getRowCount() {
            return model.size();
        }
        
        @Override
        public Class getColumnClass(int columnIndex) {
            if (columnIndex == 0)
                return Boolean.class;
            else
                return WebFrameworkProvider.class;
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return (columnIndex == 0);
        }
        
        public Object getValueAt(int row, int column) {
            FrameworkModelItem item = getItem(row);
            switch (column) {
                case 0: return item.isSelected();
                case 1: return item.getFramework();
            }
            return "";
        }
        
        @Override
        public void setValueAt(Object value, int row, int column) {
            FrameworkModelItem item = getItem(row);
            switch (column) {
                case 0: item.setSelected((Boolean) value);break;
                case 1: item.setFramework((WebFrameworkProvider) value);break;
            }
            fireTableCellUpdated(row, column);
        }
        
        private FrameworkModelItem getItem(int index) {
            return (FrameworkModelItem) model.get(index);
        }
        
        void addItem(FrameworkModelItem item){
            model.addElement(item);
        }
    }

    private final class FrameworkModelItem {
        private WebFrameworkProvider framework;
        private Boolean selected;
        
        /** Creates a new instance of BeanFormProperty */
        public FrameworkModelItem(WebFrameworkProvider framework) {
            this.setFramework(framework);
            setSelected(Boolean.FALSE);
        }

        public WebFrameworkProvider getFramework() {
            return framework;
        }

        public void setFramework(WebFrameworkProvider framework) {
            this.framework = framework;
        }

        public Boolean isSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

    }
    
    /**
     * Server instance wrapper represents server instances in the servers combobox.
     * @author sherold
     */
    private static class ServerInstanceWrapper implements Comparable {

        private final String serverInstanceID;
        private final String displayName;

        ServerInstanceWrapper(String serverInstanceID, String displayName) {
            this.serverInstanceID = serverInstanceID;
            this.displayName = displayName;
        }

        public String getServerInstanceID() {
            return serverInstanceID;
        }

        @Override
        public String toString() {
            return displayName;
        }

        public int compareTo(Object o) {
            return toString().compareTo(o.toString());
        }
    }    
}
