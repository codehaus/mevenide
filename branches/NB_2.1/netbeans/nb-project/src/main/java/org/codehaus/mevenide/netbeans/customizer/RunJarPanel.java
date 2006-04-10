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

package org.codehaus.mevenide.netbeans.customizer;

import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.JFileChooser;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Profile;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.execute.ActionToGoalUtils;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.netbeans.spi.project.ActionProvider;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileUtil;

/**
 * panel for displaying the Run Jar project related properties..
 * @author  mkleint
 */
public class RunJarPanel extends javax.swing.JPanel implements M2CustomizerPanelProvider.Panel {

    private ModelHandle handle;
    private NbMavenProject project;
    private NetbeansActionMapping run;
    private NetbeansActionMapping debug;
    private boolean isRunCompatible = true;
    private boolean isDebugCompatible = true;
    private String oldMainClass;
    private String oldParams;
    private String oldVMParams;
    private String oldWorkDir;
    private Plugin jarPlugin;
    private Plugin assemblyPlugin;
    
    public RunJarPanel(ModelHandle handle, NbMavenProject project) {
        initComponents();
        this.handle = handle;
        this.project = project;
        initValues();
        lblMainClass.setFont(lblMainClass.getFont().deriveFont((int)Font.BOLD));
    }
    
    private void initValues() {
        ActionToGoalMapping mappings = handle.getActionMappings();
        run = ActionToGoalUtils.getActiveMapping(ActionProvider.COMMAND_RUN, project);
        debug = ActionToGoalUtils.getActiveMapping(ActionProvider.COMMAND_DEBUG, project);
        isRunCompatible = checkMapping(run);
        isDebugCompatible = checkMapping(debug);
        Profile publicProfile = handle.getNetbeansPublicProfile();
        BuildBase bld = publicProfile.getBuild();
        jarPlugin = null;
        assemblyPlugin = null;
        if (bld != null) {
            Iterator it = bld.getPlugins().iterator();
            while (it.hasNext()) {
                Plugin elem = (Plugin)it.next();
                if ("maven-jar-plugin".equals(elem.getArtifactId())) {
                    jarPlugin = elem;
                }
                if ("maven-assembly-plugin".equals(elem.getArtifactId())) {
                    assemblyPlugin = elem;
                }
            }
        }
        if (jarPlugin != null) {
            Xpp3Dom conf = (Xpp3Dom)jarPlugin.getConfiguration();
            Xpp3Dom archive = conf.getChild("archive");
            if (archive != null) {
                Xpp3Dom manifest = archive.getChild("manifest");
                if (manifest != null) {
                    Xpp3Dom mainClass = manifest.getChild("mainClass");
                    if (mainClass != null) {
                        oldMainClass = mainClass.getValue();
                    }
                }
            }
        }
        
        if (oldMainClass != null) {
            txtMainClass.setText(oldMainClass);
        } else {
            oldMainClass = "";
        }
        oldParams = isRunCompatible ? run.getProperties().getProperty("netbeans.jar.run.params") :
                                      debug.getProperties().getProperty("netbeans.jar.run.params");
        oldVMParams = isRunCompatible ? run.getProperties().getProperty("netbeans.jar.run.jvmparams") :
                                        debug.getProperties().getProperty("netbeans.jar.run.jvmparams");
        if (oldParams != null) {
            txtArguments.setText(oldParams);
        } else {
            oldParams = "";
        }
        if (oldVMParams != null) {
            txtVMOptions.setText(oldVMParams);
        } else {
            oldVMParams = "";
        }
        
                                      
//        listeners = new ArrayList();
//        try {
//            listeners.add(new ReflectionTextComponentUpdater("getGroupId", "setGroupId", mdl, project, txtGroupId));
//            listeners.add(new ReflectionTextComponentUpdater("getArtifactId", "setArtifactId", mdl, project, txtArtifactId));
//            listeners.add(new ReflectionTextComponentUpdater("getVersion", "setVersion", mdl, project, txtVersion));
//            listeners.add(new ReflectionTextComponentUpdater("getName", "setName", mdl, project, txtName));
//            listeners.add(new ReflectionTextComponentUpdater("getPackaging", "setPackaging", mdl, project, txtPackaging));
//            listeners.add(new ReflectionTextComponentUpdater("getDescription", "setDescription", mdl, project, taDescription));
//        } catch (NoSuchMethodException ex) {
//            ex.printStackTrace();
//        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblMainClass = new javax.swing.JLabel();
        txtMainClass = new javax.swing.JTextField();
        btnMainClass = new javax.swing.JButton();
        lblArguments = new javax.swing.JLabel();
        txtArguments = new javax.swing.JTextField();
        lblWorkDir = new javax.swing.JLabel();
        txtWorkDir = new javax.swing.JTextField();
        btnWorkDir = new javax.swing.JButton();
        lblVMOptions = new javax.swing.JLabel();
        txtVMOptions = new javax.swing.JTextField();
        lblHint = new javax.swing.JLabel();

        lblMainClass.setText("Main Class:");

        btnMainClass.setText("Browse...");
        btnMainClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMainClassActionPerformed(evt);
            }
        });

        lblArguments.setText("Arguments:");

        lblWorkDir.setText("Working Directory:");

        btnWorkDir.setText("Browse...");
        btnWorkDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWorkDirActionPerformed(evt);
            }
        });

        lblVMOptions.setText("VM Options:");

        lblHint.setText("(e.g. -Xms10m)");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblMainClass)
                    .add(lblArguments)
                    .add(lblWorkDir)
                    .add(lblVMOptions))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblHint)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 57, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtVMOptions, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtWorkDir, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtArguments, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtMainClass, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btnMainClass)
                    .add(btnWorkDir))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(lblMainClass)
                        .add(txtMainClass, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(btnMainClass))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblArguments)
                    .add(txtArguments, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblWorkDir)
                    .add(btnWorkDir)
                    .add(txtWorkDir, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblVMOptions)
                    .add(txtVMOptions, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblHint)
                .addContainerGap(161, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnWorkDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWorkDirActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        
        String workDir = txtWorkDir.getText();
        if (workDir.equals("")) {
            workDir = FileUtil.toFile(project.getProjectDirectory()).getAbsolutePath();
        }
        chooser.setSelectedFile(new File(workDir));
        chooser.setDialogTitle("Select Working Directory");
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) { //NOI18N
            File file = FileUtil.normalizeFile(chooser.getSelectedFile());
            txtWorkDir.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_btnWorkDirActionPerformed

    private void btnMainClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMainClassActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_btnMainClassActionPerformed

    public void applyChanges() {
        String newMainClass = txtMainClass.getText().trim();
        if (!newMainClass.equals(oldMainClass)) {
            jarPlugin = checkJarPlugin(jarPlugin, newMainClass);
            assemblyPlugin = checkAssemblyPlugin(assemblyPlugin);
        }
        String newParams = txtArguments.getText().trim();
        if (!newParams.equals(oldParams)) {
            if (isRunCompatible) {
                run.getProperties().setProperty("netbeans.jar.run.params", newParams);
                ActionToGoalUtils.setUserActionMapping(run, handle.getActionMappings());
            }
            if (isDebugCompatible) {
                debug.getProperties().setProperty("netbeans.jar.run.params", newParams);
                ActionToGoalUtils.setUserActionMapping(debug, handle.getActionMappings());
            }
        }
        String newVMParams = txtVMOptions.getText().trim();
        if (!newVMParams.equals(oldVMParams)) {
            if (isRunCompatible) {
                run.getProperties().setProperty("netbeans.jar.run.jvmparams", newVMParams);
                ActionToGoalUtils.setUserActionMapping(run, handle.getActionMappings());
            }
            if (isDebugCompatible) {
                debug.getProperties().setProperty("netbeans.jar.run.jvmparams", newVMParams);
                ActionToGoalUtils.setUserActionMapping(debug, handle.getActionMappings());
            }
        }
    }

    private boolean checkMapping(NetbeansActionMapping map) {
        Iterator it = map.getGoals().iterator();
        while (it.hasNext()) {
            String goal = (String) it.next();
            if (goal.indexOf("org.codehaus.mevenide:netbeans-run-plugin") > -1) {
                return true;
            }
        }
        return false;
    }

    private Plugin checkJarPlugin(Plugin jarPlugin, String val) {
        if (jarPlugin == null) {
            jarPlugin = new Plugin();
            jarPlugin.setArtifactId("maven-jar-plugin");
            jarPlugin.setGroupId("org.apache.maven.plugins");
            handle.getNetbeansPublicProfile().getBuild().addPlugin(jarPlugin);
        }
        if (jarPlugin.getConfiguration() == null) {
            jarPlugin.setConfiguration(new Xpp3Dom("configuration"));
        }
        Xpp3Dom configuration = (Xpp3Dom) jarPlugin.getConfiguration();
        Xpp3Dom manifest = getOrCreateXppDomChild(getOrCreateXppDomChild(configuration, "archive"), "manifest");
        getOrCreateXppDomChild(manifest, "addClasspath").setValue("true");
        getOrCreateXppDomChild(manifest, "classpathPrefix").setValue("lib");
        getOrCreateXppDomChild(manifest, "mainClass").setValue(val);
        return jarPlugin;
    }

    private Plugin checkAssemblyPlugin(Plugin assemblyPlugin) {
        if (assemblyPlugin == null) {
            assemblyPlugin = new Plugin();
            assemblyPlugin.setArtifactId("maven-assembly-plugin");
            assemblyPlugin.setGroupId("org.apache.maven.plugins");
            handle.getNetbeansPublicProfile().getBuild().addPlugin(assemblyPlugin);
        }
        PluginExecution exec = (PluginExecution)assemblyPlugin.getExecutionsAsMap().get("nb");
        if (exec == null) {
            exec = new PluginExecution();
            exec.setId("nb");
            assemblyPlugin.addExecution(exec);
        }
        exec.setPhase("package");
        exec.setGoals(Collections.singletonList("directory"));
        if (exec.getConfiguration() == null) {
            exec.setConfiguration(new Xpp3Dom("configuration"));
        }
        Xpp3Dom configuration = (Xpp3Dom) exec.getConfiguration();
        getOrCreateXppDomChild(configuration, "descriptor").setValue("${basedir}/src/main/assemblies/netbeans-run.xml");
        getOrCreateXppDomChild(configuration, "finalName").setValue("executable");
        File assDir = new File(new File(new File(project.getOriginalMavenProject().getBasedir(), "src"), "main"), "assemblies");
        if (!assDir.exists()) {
            assDir.mkdirs();
        }
        File assembly = new File(assDir, "netbeans-run.xml");
        if (!assembly.exists()) {
            InputStream instr = null;
            OutputStream outstr = null;
            try {
                assembly.createNewFile();
                instr = getClass().getResourceAsStream("/org/codehaus/mevenide/netbeans/execute/netbeans-run.xml");
                outstr = new FileOutputStream(assembly);
                IOUtil.copy(instr, outstr);
            } catch (IOException exc) {
                ErrorManager.getDefault().notify(exc);
                NotifyDescriptor nd = new NotifyDescriptor.Message("Cannot create assembly file at " + assembly);
                DialogDisplayer.getDefault().notify(nd);
            } finally {
                IOUtil.close(instr);
                IOUtil.close(outstr);
            }
        }
        return assemblyPlugin;
    }
    
    private Xpp3Dom getOrCreateXppDomChild(Xpp3Dom parent, String name) {
        Xpp3Dom child = parent.getChild(name);
        if (child == null) {
            child = new Xpp3Dom(name);
            parent.addChild(child);
        }
        return child;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMainClass;
    private javax.swing.JButton btnWorkDir;
    private javax.swing.JLabel lblArguments;
    private javax.swing.JLabel lblHint;
    private javax.swing.JLabel lblMainClass;
    private javax.swing.JLabel lblVMOptions;
    private javax.swing.JLabel lblWorkDir;
    private javax.swing.JTextField txtArguments;
    private javax.swing.JTextField txtMainClass;
    private javax.swing.JTextField txtVMOptions;
    private javax.swing.JTextField txtWorkDir;
    // End of variables declaration//GEN-END:variables

    
}
