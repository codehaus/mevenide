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

package org.codehaus.mevenide.netbeans.j2ee.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.customizer.ComboBoxUpdater;
import org.codehaus.mevenide.netbeans.api.customizer.ModelHandle;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.mevenide.netbeans.j2ee.MavenDeploymentImpl;
import org.codehaus.mevenide.netbeans.j2ee.POHImpl;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;

/**
 *
 * @author  mkleint
 */
public class EjbRunCustomizerPanel extends javax.swing.JPanel {
    private NbMavenProject project;
    private ModelHandle handle;
    private EjbJar module;

    private ArrayList listeners;

    /**
     * Creates new form EjbRunCustomizerPanel
     */
    public EjbRunCustomizerPanel(ModelHandle handle, NbMavenProject project) {
        initComponents();
        this.handle = handle;
        this.project = project;
        module = EjbJar.getEjbJar(project.getProjectDirectory());
        loadComboModel();
        if (module != null) {
            txtJ2EEVersion.setText(module.getJ2eePlatformVersion());
        }
        initValues();
    }
    
    private void initValues() {
        listeners = new ArrayList();
        listeners.add(new ComboBoxUpdater<Wrapper>(comServer, lblServer) {
            public Wrapper getDefaultValue() {
                Wrapper wr = null;
                String id = handle.getProject().getProperties().getProperty(EjbModuleProviderImpl.ATTRIBUTE_DEPLOYMENT_SERVER_ID);
                if (id != null) {
                    wr = findWrapperByInstance(id);
                }
                if (wr == null) {
                    String str = handle.getProject().getProperties().getProperty(EjbModuleProviderImpl.ATTRIBUTE_DEPLOYMENT_SERVER);
                    if (str != null) {
                        wr = findWrapperByType(str);
                    }
                }
                return wr;
            }
            
            public Wrapper getValue() {
                Wrapper wr = null;
                String id = handle.getNetbeansPrivateProfile(false).getProperties().getProperty(EjbModuleProviderImpl.ATTRIBUTE_DEPLOYMENT_SERVER_ID);
                if (id != null) {
                    wr = findWrapperByInstance(id);
                }
                if (wr == null) {
                    String str = handle.getNetbeansPublicProfile(false).getProperties().getProperty(EjbModuleProviderImpl.ATTRIBUTE_DEPLOYMENT_SERVER);
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
                   handle.getNetbeansPublicProfile().getProperties().remove(EjbModuleProviderImpl.ATTRIBUTE_DEPLOYMENT_SERVER);
                   handle.getNetbeansPrivateProfile().getProperties().remove(EjbModuleProviderImpl.ATTRIBUTE_DEPLOYMENT_SERVER_ID);
               } else {
                   handle.getNetbeansPublicProfile().getProperties().put(EjbModuleProviderImpl.ATTRIBUTE_DEPLOYMENT_SERVER, sID);
                   handle.getNetbeansPrivateProfile().getProperties().setProperty(EjbModuleProviderImpl.ATTRIBUTE_DEPLOYMENT_SERVER_ID, iID);
               }
               handle.markAsModified(handle.getProfileModel());
               handle.markAsModified(handle.getPOMModel());
            }
        });
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
        String[] ids = Deployment.getDefault().getServerInstanceIDs(new Object[] { J2eeModule.EJB });
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
    
    void applyChanges() {
        //#109507 workaround
        POHImpl poh = project.getLookup().lookup(POHImpl.class);
        poh.hackEjbModuleServerChange();
        EjbModuleProviderImpl moduleProvider = project.getLookup().lookup(EjbModuleProviderImpl.class);
        
        moduleProvider.loadPersistedServerId();
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

        lblServer.setText(org.openide.util.NbBundle.getMessage(EjbRunCustomizerPanel.class, "LBL_Server")); // NOI18N

        lblJ2EEVersion.setText(org.openide.util.NbBundle.getMessage(EjbRunCustomizerPanel.class, "LBL_J2EE_Version")); // NOI18N

        txtJ2EEVersion.setEditable(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblJ2EEVersion)
                    .add(lblServer))
                .add(14, 14, 14)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(comServer, 0, 277, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtJ2EEVersion, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))
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
                .addContainerGap(239, Short.MAX_VALUE))
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comServer;
    private javax.swing.JLabel lblJ2EEVersion;
    private javax.swing.JLabel lblServer;
    private javax.swing.JTextField txtJ2EEVersion;
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
                return "<No Server Selected>";
            }
            return Deployment.getDefault().getServerInstanceDisplayName(id);
        }
                
    }
    
}
