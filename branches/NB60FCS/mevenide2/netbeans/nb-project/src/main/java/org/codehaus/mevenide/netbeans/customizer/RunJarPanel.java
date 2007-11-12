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

import java.awt.Dialog;
import javax.swing.event.DocumentEvent;
import org.codehaus.mevenide.netbeans.api.customizer.ModelHandle;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Profile;
import org.codehaus.mevenide.netbeans.MavenSourcesImpl;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.Constants;
import org.codehaus.mevenide.netbeans.execute.ActionToGoalUtils;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ActionProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.MouseUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 * panel for displaying the Run Jar project related properties..
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class RunJarPanel extends javax.swing.JPanel {
    private static final String ARTFACTID_JAR = "maven-jar-plugin";//NOI18N
    private static final String ARITFACTID_ASSEMBLY = "maven-assembly-plugin";//NOI18N
    private static final String CONFIGURATION_EL = "configuration";//NOI18N

    private static final String RUN_PARAMS = "netbeans.jar.run.params"; //NOI18N
    private static final String RUN_WORKDIR = "netbeans.jar.run.workdir"; //NOI18N
    private static final String RUN_JVM_PARAMS = "netbeans.jar.run.jvmparams"; //NOI18N
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
        lblMainClass.setFont(lblMainClass.getFont().deriveFont(Font.BOLD));
        List<FileObject> roots = new ArrayList<FileObject>();
        Sources srcs =  ProjectUtils.getSources(project);
        SourceGroup[] grps = srcs.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        for (int i = 0; i < grps.length; i++) {
            SourceGroup sourceGroup = grps[i];
            if (MavenSourcesImpl.NAME_SOURCE.equals(sourceGroup.getName())) {
                roots.add(sourceGroup.getRootFolder());
            }
        }
        grps = srcs.getSourceGroups(MavenSourcesImpl.TYPE_GEN_SOURCES);
        for (int i = 0; i < grps.length; i++) {
            SourceGroup sourceGroup = grps[i];
            roots.add(sourceGroup.getRootFolder());
        }

        btnMainClass.addActionListener(new MainClassListener(roots.toArray(new FileObject[roots.size()]), txtMainClass));
        DocumentListener docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent arg0) {
                applyChanges();
            }

            public void removeUpdate(DocumentEvent arg0) {
                applyChanges();
            }

            public void changedUpdate(DocumentEvent arg0) {
                applyChanges();
            }
        };
        txtMainClass.getDocument().addDocumentListener(docListener);
        txtArguments.getDocument().addDocumentListener(docListener);
        txtVMOptions.getDocument().addDocumentListener(docListener);
        txtWorkDir.getDocument().addDocumentListener(docListener);
    }
    
    private void initValues() {
        run = ActionToGoalUtils.getActiveMapping(ActionProvider.COMMAND_RUN, project);
        debug = ActionToGoalUtils.getActiveMapping(ActionProvider.COMMAND_DEBUG, project);
        isRunCompatible = checkMapping(run);
        isDebugCompatible = checkMapping(debug);
        Profile publicProfile = handle.getNetbeansPublicProfile(false);
        jarPlugin = null;
        assemblyPlugin = null;
        if (publicProfile != null && publicProfile.getBuild() != null) {
            BuildBase bld = publicProfile.getBuild();
            Iterator it = bld.getPlugins().iterator();
            while (it.hasNext()) {
                Plugin elem = (Plugin)it.next();
                if (ARTFACTID_JAR.equals(elem.getArtifactId())) { //NOI18N
                    jarPlugin = elem;
                }
                if (ARITFACTID_ASSEMBLY.equals(elem.getArtifactId())) { //NOI18N
                    assemblyPlugin = elem;
                }
            }
        }
        if (jarPlugin != null) {
            Xpp3Dom conf = (Xpp3Dom)jarPlugin.getConfiguration();
            Xpp3Dom archive = conf.getChild("archive"); //NOI18N
            if (archive != null) {
                Xpp3Dom manifest = archive.getChild("manifest"); //NOI18N
                if (manifest != null) {
                    Xpp3Dom mainClass = manifest.getChild("mainClass"); //NOI18N
                    if (mainClass != null) {
                        oldMainClass = mainClass.getValue();
                    }
                }
            }
        }
        
        if (oldMainClass != null) {
            txtMainClass.setText(oldMainClass);
        } else {
            oldMainClass = ""; //NOI18N
        }
        oldParams = isRunCompatible ? run.getProperties().getProperty(RUN_PARAMS) :
                                      debug.getProperties().getProperty(RUN_PARAMS);
        oldWorkDir = isRunCompatible ? run.getProperties().getProperty(RUN_WORKDIR) :
                                      debug.getProperties().getProperty(RUN_WORKDIR);
        oldVMParams = isRunCompatible ? run.getProperties().getProperty(RUN_JVM_PARAMS) :
                                        debug.getProperties().getProperty(RUN_JVM_PARAMS);
        if (oldParams != null) {
            txtArguments.setText(oldParams);
        } else {
            oldParams = ""; //NOI18N
        }
        if (oldVMParams != null) {
            txtVMOptions.setText(oldVMParams);
        } else {
            oldVMParams = ""; //NOI18N
        }
        if (oldWorkDir != null) {
            txtWorkDir.setText(oldWorkDir);
        } else {
            oldWorkDir = ""; //NOI18N
        }
        
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

        lblMainClass.setLabelFor(txtMainClass);
        org.openide.awt.Mnemonics.setLocalizedText(lblMainClass, org.openide.util.NbBundle.getMessage(RunJarPanel.class, "LBL_MainClass")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnMainClass, org.openide.util.NbBundle.getMessage(RunJarPanel.class, "BTN_Browse")); // NOI18N
        btnMainClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMainClassActionPerformed(evt);
            }
        });

        lblArguments.setLabelFor(txtArguments);
        org.openide.awt.Mnemonics.setLocalizedText(lblArguments, org.openide.util.NbBundle.getMessage(RunJarPanel.class, "LBL_Arguments")); // NOI18N

        lblWorkDir.setLabelFor(txtWorkDir);
        org.openide.awt.Mnemonics.setLocalizedText(lblWorkDir, org.openide.util.NbBundle.getMessage(RunJarPanel.class, "LBL_WorkDir")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnWorkDir, org.openide.util.NbBundle.getMessage(RunJarPanel.class, "BTN_Browse")); // NOI18N
        btnWorkDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWorkDirActionPerformed(evt);
            }
        });

        lblVMOptions.setLabelFor(txtVMOptions);
        org.openide.awt.Mnemonics.setLocalizedText(lblVMOptions, org.openide.util.NbBundle.getMessage(RunJarPanel.class, "LBL_VMOptions")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblHint, org.openide.util.NbBundle.getMessage(RunJarPanel.class, "LBL_VMHint")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblArguments)
                            .add(lblMainClass)
                            .add(lblWorkDir)
                            .add(lblVMOptions))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtVMOptions, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                            .add(txtWorkDir, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                            .add(txtArguments, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                            .add(txtMainClass, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(btnWorkDir)
                            .add(btnMainClass)))
                    .add(layout.createSequentialGroup()
                        .add(87, 87, 87)
                        .add(lblHint)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblMainClass)
                    .add(txtMainClass, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnMainClass))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblArguments)
                    .add(txtArguments, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblWorkDir)
                    .add(txtWorkDir, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnWorkDir))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblVMOptions)
                    .add(txtVMOptions, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblHint)
                .addContainerGap(181, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnWorkDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWorkDirActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        
        String workDir = txtWorkDir.getText();
        if (workDir.equals("")) { //NOI18N
            workDir = FileUtil.toFile(project.getProjectDirectory()).getAbsolutePath();
        }
        chooser.setSelectedFile(new File(workDir));
        chooser.setDialogTitle(org.openide.util.NbBundle.getMessage(RunJarPanel.class, "TIT_SelectWorkingDirectory"));
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) { //NOI18N
            File file = FileUtil.normalizeFile(chooser.getSelectedFile());
            txtWorkDir.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_btnWorkDirActionPerformed

    private void btnMainClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMainClassActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_btnMainClassActionPerformed

    void applyExternalChanges() {
        String newMainClass = txtMainClass.getText().trim();
        if (!newMainClass.equals(oldMainClass)) {
            File assDir = new File(new File(new File(project.getOriginalMavenProject().getBasedir(), "src"), "main"), "assemblies"); //NOI18N
            if (!assDir.exists()) {
                assDir.mkdirs();
            }
            File assembly = new File(assDir, "netbeans-run.xml"); //NOI18N
            if (!assembly.exists()) {
                InputStream instr = null;
                OutputStream outstr = null;
                try {
                    assembly.createNewFile();
                    instr = getClass().getResourceAsStream("/org/codehaus/mevenide/netbeans/execute/netbeans-run.xml"); //NOI18N
                    outstr = new FileOutputStream(assembly);
                    IOUtil.copy(instr, outstr);
                } catch (IOException exc) {
                    ErrorManager.getDefault().notify(exc);
                    NotifyDescriptor nd = new NotifyDescriptor.Message(org.openide.util.NbBundle.getMessage(RunJarPanel.class, "Err_CannotCreate", assembly));
                    DialogDisplayer.getDefault().notify(nd);
                } finally {
                    IOUtil.close(instr);
                    IOUtil.close(outstr);
                }
            }
        }
        
    }
    
    void applyChanges() {
        String newMainClass = txtMainClass.getText().trim();
        if (!newMainClass.equals(oldMainClass)) {
            jarPlugin = checkJarPlugin(jarPlugin, newMainClass);
            assemblyPlugin = checkAssemblyPlugin(assemblyPlugin);
            handle.markAsModified(handle.getPOMModel());
        }
        String newParams = txtArguments.getText().trim();
        if (!newParams.equals(oldParams)) {
            if (isRunCompatible) {
                run.getProperties().setProperty(RUN_PARAMS, newParams);
                ActionToGoalUtils.setUserActionMapping(run, handle.getActionMappings());
                handle.markAsModified(handle.getActionMappings());
            }
            if (isDebugCompatible) {
                debug.getProperties().setProperty(RUN_PARAMS, newParams);
                ActionToGoalUtils.setUserActionMapping(debug, handle.getActionMappings());
                handle.markAsModified(handle.getActionMappings());
            }
        }
        String newVMParams = txtVMOptions.getText().trim();
        if (!newVMParams.equals(oldVMParams)) {
            if (isRunCompatible) {
                run.getProperties().setProperty(RUN_JVM_PARAMS, newVMParams);
                ActionToGoalUtils.setUserActionMapping(run, handle.getActionMappings());
                handle.markAsModified(handle.getActionMappings());
            }
            if (isDebugCompatible) {
                debug.getProperties().setProperty(RUN_JVM_PARAMS, newVMParams);
                ActionToGoalUtils.setUserActionMapping(debug, handle.getActionMappings());
                handle.markAsModified(handle.getActionMappings());
            }
        }
        String newWorkDir = txtWorkDir.getText().trim();
        if (!newWorkDir.equals(oldWorkDir)) {
            if (isRunCompatible) {
                run.getProperties().setProperty(RUN_WORKDIR, newWorkDir);
                ActionToGoalUtils.setUserActionMapping(run, handle.getActionMappings());
                handle.markAsModified(handle.getActionMappings());
            }
            if (isDebugCompatible) {
                debug.getProperties().setProperty(RUN_WORKDIR, newWorkDir);
                ActionToGoalUtils.setUserActionMapping(debug, handle.getActionMappings());
                handle.markAsModified(handle.getActionMappings());
            }
        }
    }

    private boolean checkMapping(NetbeansActionMapping map) {
        Iterator it = map.getGoals().iterator();
        while (it.hasNext()) {
            String goal = (String) it.next();
            if (goal.indexOf("org.codehaus.mevenide:netbeans-run-plugin") > -1) { //NOI18N
                return true;
            }
        }
        return false;
    }

    private Plugin checkJarPlugin(Plugin jarPlugin, String val) {
        if (jarPlugin == null) {
            jarPlugin = new Plugin();
            jarPlugin.setArtifactId(ARTFACTID_JAR); 
            jarPlugin.setGroupId(Constants.GROUP_APACHE_PLUGINS); 
            handle.getNetbeansPublicProfile().getBuild().addPlugin(jarPlugin);
        }
        if (jarPlugin.getConfiguration() == null) {
            jarPlugin.setConfiguration(new Xpp3Dom(CONFIGURATION_EL)); 
        }
        Xpp3Dom configuration = (Xpp3Dom) jarPlugin.getConfiguration();
        Xpp3Dom manifest = getOrCreateXppDomChild(getOrCreateXppDomChild(configuration, "archive"), "manifest"); //NOI18N
        getOrCreateXppDomChild(manifest, "addClasspath").setValue("true"); //NOI18N
        getOrCreateXppDomChild(manifest, "classpathPrefix").setValue("lib"); //NOI18N
        getOrCreateXppDomChild(manifest, "mainClass").setValue(val); //NOI18N
        return jarPlugin;
    }

    private Plugin checkAssemblyPlugin(Plugin assPlugin) {
        if (assPlugin == null) {
            assPlugin = new org.apache.maven.model.Plugin();
            assPlugin.setArtifactId(ARITFACTID_ASSEMBLY); 
            assPlugin.setGroupId(Constants.GROUP_APACHE_PLUGINS); 
//not necessary, can be workarounded in other ways..            assPlugin.setVersion("2.1"); //MEVENIDE-523
            handle.getNetbeansPublicProfile().getBuild().addPlugin(assPlugin);
        }
        //#96834
        assPlugin.flushExecutionMap();
        
        PluginExecution exec = (PluginExecution)assPlugin.getExecutionsAsMap().get("nb"); //NOI18N
        if (exec == null) {
            exec = new PluginExecution();
            exec.setId("nb"); //NOI18N
            assPlugin.addExecution(exec);
        }
        exec.setPhase("package"); //NOI18N
        exec.setGoals(Collections.singletonList("directory")); //NOI18N
        if (exec.getConfiguration() == null) {
            exec.setConfiguration(new Xpp3Dom(CONFIGURATION_EL)); 
        }
        Xpp3Dom configuration = (Xpp3Dom) exec.getConfiguration();
        getOrCreateXppDomChild(configuration, "descriptor").setValue("${basedir}/src/main/assemblies/netbeans-run.xml"); //NOI18N
        getOrCreateXppDomChild(configuration, "finalName").setValue("executable"); //NOI18N
        
        return assPlugin;
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

        // Innercasses -------------------------------------------------------------
    
    private class MainClassListener implements ActionListener /*, DocumentListener */ {
        
        private final JButton okButton;
        private FileObject[] sourceRoots;
        private JTextField mainClassTextField;
        
        MainClassListener( FileObject[] sourceRoots, JTextField mainClassTextField ) {            
            this.sourceRoots = sourceRoots;
            this.mainClassTextField = mainClassTextField;
            this.okButton  = new JButton (NbBundle.getMessage (RunJarPanel.class, "LBL_ChooseMainClass_OK"));
            this.okButton.getAccessibleContext().setAccessibleDescription (NbBundle.getMessage (RunJarPanel.class, "AD_ChooseMainClass_OK"));
        }
        
        // Implementation of ActionListener ------------------------------------
        
        /** Handles button events
         */        
        public void actionPerformed( ActionEvent e ) {
            
            // only chooseMainClassButton can be performed
            
            final MainClassChooser panel = new MainClassChooser (sourceRoots);
            Object[] options = new Object[] {
                okButton,
                DialogDescriptor.CANCEL_OPTION
            };
            panel.addChangeListener (new ChangeListener () {
               public void stateChanged(ChangeEvent e) {
                   if (e.getSource () instanceof MouseEvent && MouseUtils.isDoubleClick (((MouseEvent)e.getSource ()))) {
                       // click button and finish the dialog with selected class
                       okButton.doClick ();
                   } else {
                       okButton.setEnabled (panel.getSelectedMainClass () != null);
                   }
               }
            });
            okButton.setEnabled (false);
            DialogDescriptor desc = new DialogDescriptor (
                panel,
                NbBundle.getMessage (RunJarPanel.class, "LBL_ChooseMainClass_Title" ),
                true, 
                options, 
                options[0], 
                DialogDescriptor.BOTTOM_ALIGN, 
                null, 
                null);
            //desc.setMessageType (DialogDescriptor.INFORMATION_MESSAGE);
            Dialog dlg = DialogDisplayer.getDefault ().createDialog (desc);
            dlg.setVisible (true);
            if (desc.getValue() == options[0]) {
               mainClassTextField.setText (panel.getSelectedMainClass ());
            } 
            dlg.dispose();
        }
        
    }
}
