/* ==========================================================================
 * Copyright 2005-2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.j2ee.web;

import org.codehaus.mevenide.netbeans.j2ee.POHImpl;
import org.codehaus.mevenide.netbeans.api.customizer.ModelHandle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.Constants;
import org.codehaus.mevenide.netbeans.customizer.ComboBoxUpdater;
import org.codehaus.mevenide.netbeans.api.customizer.ModelHandle;
import org.codehaus.mevenide.netbeans.execute.ActionToGoalUtils;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.mevenide.netbeans.j2ee.MavenDeploymentImpl;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.spi.project.ActionProvider;

/**
 *
 * @author  mkleint
 */
public class WebRunCustomizerPanel extends javax.swing.JPanel {
    private Project project;
    private ModelHandle handle;
    private WebModule module;
    private WebModuleProviderImpl moduleProvider;

    private ArrayList listeners;

    private NetbeansActionMapping run;

    private NetbeansActionMapping debug;

    private boolean isRunCompatible;

    private boolean isDebugCompatible;

    private String oldUrl;
    
    /** Creates new form WebRunCustomizerPanel */
    public WebRunCustomizerPanel(ModelHandle handle, Project project) {
        initComponents();
        this.handle = handle;
        this.project = project;
        module = WebModule.getWebModule(project.getProjectDirectory());
        moduleProvider = project.getLookup().lookup(WebModuleProviderImpl.class);
        assert moduleProvider != null;
        assert module != null;
        loadComboModel();
        if (module != null) {
            txtJ2EEVersion.setText(module.getJ2eePlatformVersion());
        }
        initValues();
        txtContextPath.setText(moduleProvider.getWebModuleImplementation().getContextPath());
    }
    
    private void initValues() {
        listeners = new ArrayList();
        listeners.add(new ComboBoxUpdater<Wrapper>(comServer, lblServer) {
            public Wrapper getDefaultValue() {
                Wrapper wr = null;
                String id = handle.getProject().getProperties().getProperty(Constants.HINT_DEPLOY_J2EE_SERVER_ID);
                if (id != null) {
                    wr = findWrapperByInstance(id);
                }
                if (wr == null) {
                    String str = handle.getProject().getProperties().getProperty(Constants.HINT_DEPLOY_J2EE_SERVER);
                    if (str != null) {
                        wr = findWrapperByType(str);
                    }
                }
                return wr;
            }
            
            public Wrapper getValue() {
                Wrapper wr = null;
                String id = handle.getNetbeansPrivateProfile(false).getProperties().getProperty(Constants.HINT_DEPLOY_J2EE_SERVER_ID);
                if (id != null) {
                    wr = findWrapperByInstance(id);
                }
                if (wr == null) {
                    String str = handle.getNetbeansPublicProfile(false).getProperties().getProperty(Constants.HINT_DEPLOY_J2EE_SERVER);
                    if (str != null) {
                        wr = findWrapperByType(str);
                    }
                }
                return wr;
            }
            
            public void setValue(Wrapper wr) {
                if (wr == null) {
                    return;
                }
               String sID = wr.getServerID();
               String iID = wr.getServerInstanceID();
               if (MavenDeploymentImpl.DEV_NULL.equals(iID)) {
                   handle.getNetbeansPublicProfile().getProperties().remove(Constants.HINT_DEPLOY_J2EE_SERVER);
                   handle.getNetbeansPrivateProfile().getProperties().remove(Constants.HINT_DEPLOY_J2EE_SERVER_ID);
               } else {
                   handle.getNetbeansPublicProfile().getProperties().setProperty(Constants.HINT_DEPLOY_J2EE_SERVER, sID);
                   handle.getNetbeansPrivateProfile().getProperties().setProperty(Constants.HINT_DEPLOY_J2EE_SERVER_ID, iID);
               }
               handle.markAsModified(handle.getProfileModel());
               handle.markAsModified(handle.getPOMModel());
            }
        });
        //TODO remove the NbMavenProject dependency
        run = ActionToGoalUtils.getActiveMapping(ActionProvider.COMMAND_RUN, project.getLookup().lookup(NbMavenProject.class));
        debug = ActionToGoalUtils.getActiveMapping(ActionProvider.COMMAND_DEBUG, project.getLookup().lookup(NbMavenProject.class));
        isRunCompatible = checkMapping(run);
        isDebugCompatible = checkMapping(debug);
        oldUrl = isRunCompatible ? run.getProperties().getProperty("netbeans.deploy.clientUrlPart") : //NOI18N
                                      debug.getProperties().getProperty("netbeans.deploy.clientUrlPart"); //NOI18N
        if (oldUrl != null) {
            txtRelativeUrl.setText(oldUrl);
        } else {
            oldUrl = ""; //NOI18N
        }
        
    }
    
    private Wrapper findWrapperByInstance(String instanceId) {
        for (int i = 0; i < comServer.getModel().getSize(); i++) {
            Wrapper wr = (Wrapper)comServer.getModel().getElementAt(i);
            if (instanceId.equals(wr.getServerInstanceID())) {
                return wr;
            }
        }
        return null;
    }
    
    private Wrapper findWrapperByType(String serverId) {
        for (int i = 0; i < comServer.getModel().getSize(); i++) {
            Wrapper wr = (Wrapper)comServer.getModel().getElementAt(i);
            if (serverId.equals(wr.getServerID())) {
                return wr;
            }
        }
        return null;
    }
    
    private void loadComboModel() {
        String[] ids = Deployment.getDefault().getServerInstanceIDs();
        Collection<Wrapper> col = new ArrayList<Wrapper>();
//        Wrapper selected = null;
        col.add(new Wrapper(MavenDeploymentImpl.DEV_NULL));
        for (int i = 0; i < ids.length; i++) {
            Wrapper wr = new Wrapper(ids[i]);
            col.add(wr);
//            if (selectedId.equals(ids[i])) {
//                selected = wr;
//            }
            
        }
        comServer.setModel(new DefaultComboBoxModel(col.toArray()));
//        if (selected != null) {
//            comServer.setSelectedItem(selected);
//        }
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblServer = new javax.swing.JLabel();
        comServer = new javax.swing.JComboBox();
        lblJ2EEVersion = new javax.swing.JLabel();
        txtJ2EEVersion = new javax.swing.JTextField();
        lblContextPath = new javax.swing.JLabel();
        txtContextPath = new javax.swing.JTextField();
        cbBrowser = new javax.swing.JCheckBox();
        lblHint1 = new javax.swing.JLabel();
        lblRelativeUrl = new javax.swing.JLabel();
        txtRelativeUrl = new javax.swing.JTextField();
        lblHint2 = new javax.swing.JLabel();

        lblServer.setText(org.openide.util.NbBundle.getMessage(WebRunCustomizerPanel.class, "LBL_Server")); // NOI18N

        lblJ2EEVersion.setText(org.openide.util.NbBundle.getMessage(WebRunCustomizerPanel.class, "LBL_J2EE_Version")); // NOI18N

        txtJ2EEVersion.setEditable(false);

        lblContextPath.setText(org.openide.util.NbBundle.getMessage(WebRunCustomizerPanel.class, "LBL_Context_Path")); // NOI18N

        cbBrowser.setText(org.openide.util.NbBundle.getMessage(WebRunCustomizerPanel.class, "LBL_Display_on_Run")); // NOI18N
        cbBrowser.setMargin(new java.awt.Insets(0, 0, 0, 0));

        lblHint1.setText(org.openide.util.NbBundle.getMessage(WebRunCustomizerPanel.class, "LBL_Hint1")); // NOI18N

        lblRelativeUrl.setText(org.openide.util.NbBundle.getMessage(WebRunCustomizerPanel.class, "LBL_Relative_URL")); // NOI18N

        lblHint2.setText(org.openide.util.NbBundle.getMessage(WebRunCustomizerPanel.class, "LBL_Hint2")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbBrowser)
                    .add(lblHint1)
                    .add(layout.createSequentialGroup()
                        .add(lblRelativeUrl)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(layout.createSequentialGroup()
                                .add(lblHint2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 140, Short.MAX_VALUE))
                            .add(txtRelativeUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblContextPath)
                            .add(lblJ2EEVersion)
                            .add(lblServer))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(comServer, 0, 277, Short.MAX_VALUE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtJ2EEVersion, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .add(txtContextPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblServer)
                    .add(comServer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblJ2EEVersion)
                    .add(txtJ2EEVersion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblContextPath)
                    .add(txtContextPath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(20, 20, 20)
                .add(cbBrowser)
                .add(16, 16, 16)
                .add(lblHint1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblRelativeUrl)
                    .add(txtRelativeUrl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblHint2)
                .addContainerGap(102, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private boolean checkMapping(NetbeansActionMapping map) {
        if (map == null) {
            return false;
        }
        Iterator it = map.getGoals().iterator();
        while (it.hasNext()) {
            String goal = (String) it.next();
            if (goal.indexOf("org.codehaus.mevenide:netbeans-deploy-plugin") > -1) { //NOI18N
                return true;
            }
        }
        return false;
    }

    void applyChanges() {
        String newUrl = txtRelativeUrl.getText().trim();
        if (!newUrl.equals(oldUrl)) {
            if (isRunCompatible) {
                run.getProperties().setProperty("netbeans.deploy.clientUrlPart", newUrl); //NOI18N
                ActionToGoalUtils.setUserActionMapping(run, handle.getActionMappings());
            }
            if (isDebugCompatible) {
                debug.getProperties().setProperty("netbeans.deploy.clientUrlPart", newUrl); //NOI18N
                ActionToGoalUtils.setUserActionMapping(debug, handle.getActionMappings());
            }
        }
        //#109507 workaround
        POHImpl poh = project.getLookup().lookup(POHImpl.class);
        poh.hackWebModuleServerChange();
        moduleProvider = project.getLookup().lookup(WebModuleProviderImpl.class);
        //---
        moduleProvider.loadPersistedServerId();
        moduleProvider.getWebModuleImplementation().setContextPath(txtContextPath.getText().trim());
        handle.markAsModified(handle.getActionMappings());
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbBrowser;
    private javax.swing.JComboBox comServer;
    private javax.swing.JLabel lblContextPath;
    private javax.swing.JLabel lblHint1;
    private javax.swing.JLabel lblHint2;
    private javax.swing.JLabel lblJ2EEVersion;
    private javax.swing.JLabel lblRelativeUrl;
    private javax.swing.JLabel lblServer;
    private javax.swing.JTextField txtContextPath;
    private javax.swing.JTextField txtJ2EEVersion;
    private javax.swing.JTextField txtRelativeUrl;
    // End of variables declaration//GEN-END:variables

    private class Wrapper {
        private String id;
        
        public Wrapper(String serverid) {
            id = serverid;
        }
        
        public String getServerInstanceID() {
            return id;
        }
        
        public String getServerID() {
            if (MavenDeploymentImpl.DEV_NULL.equals(id)) {
                return MavenDeploymentImpl.DEV_NULL;
            }
            return Deployment.getDefault().getServerID(id);
        }
        
        @Override
        public String toString() {
            if (MavenDeploymentImpl.DEV_NULL.equals(id)) {
                return org.openide.util.NbBundle.getMessage(WebRunCustomizerPanel.class, "MSG_No_Server");
            }
            return Deployment.getDefault().getServerInstanceDisplayName(id);
        }
                
    }
    
}
