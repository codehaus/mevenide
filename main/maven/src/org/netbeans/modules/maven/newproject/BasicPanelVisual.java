/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.newproject;

import java.io.File;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.Document;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.embedder.MavenEmbedder;
import org.netbeans.modules.maven.indexer.api.RepositoryIndexer;
import org.netbeans.modules.maven.indexer.api.RepositoryInfo;
import org.netbeans.modules.maven.indexer.api.RepositoryPreferences;
import org.netbeans.modules.maven.api.archetype.Archetype;
import org.netbeans.modules.maven.embedder.EmbedderFactory;
import org.netbeans.modules.maven.embedder.exec.ProgressTransferListener;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.awt.Mnemonics;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author mkleint
 */
public class BasicPanelVisual extends JPanel implements DocumentListener {
    
    public static final String PROP_PROJECT_NAME = "projectName"; //NOI18N
    
    private static final String ERROR_MSG = "WizardPanel_errorMessage"; //NOI18N
    
    private BasicWizardPanel panel;

    private String lastProjectName = ""; //NOI18N

    private boolean changedPackage = false;
    
    private ArchetypeProviderImpl ngprovider;
    
    /** Creates new form PanelProjectLocationVisual */
    public BasicPanelVisual(BasicWizardPanel panel) {
        initComponents();
        this.panel = panel;
        // Register listener on the textFields to make the automatic updates
        projectNameTextField.getDocument().addDocumentListener(this);
        projectLocationTextField.getDocument().addDocumentListener(this);
        txtArtifactId.getDocument().addDocumentListener(this);
        txtGroupId.getDocument().addDocumentListener(this);
        txtVersion.getDocument().addDocumentListener(this);
        txtPackage.getDocument().addDocumentListener(this);
        tblAdditionalProps.setVisible(false);
        lblAdditionalProps.setVisible(false);
        jScrollPane1.setVisible(false);
    }
    
    
    public String getProjectName() {
        return this.projectNameTextField.getText();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        projectNameLabel = new javax.swing.JLabel();
        projectNameTextField = new javax.swing.JTextField();
        projectLocationLabel = new javax.swing.JLabel();
        projectLocationTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        createdFolderLabel = new javax.swing.JLabel();
        createdFolderTextField = new javax.swing.JTextField();
        lblArtifactId = new javax.swing.JLabel();
        txtArtifactId = new javax.swing.JTextField();
        lblGroupId = new javax.swing.JLabel();
        txtGroupId = new javax.swing.JTextField();
        lblVersion = new javax.swing.JLabel();
        txtVersion = new javax.swing.JTextField();
        lblPackage = new javax.swing.JLabel();
        txtPackage = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        pnlAdditionals = new javax.swing.JPanel();
        lblAdditionalProps = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAdditionalProps = new javax.swing.JTable();

        projectNameLabel.setLabelFor(projectNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectNameLabel, org.openide.util.NbBundle.getMessage(BasicPanelVisual.class, "LBL_ProjectName")); // NOI18N

        projectLocationLabel.setLabelFor(projectLocationTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectLocationLabel, org.openide.util.NbBundle.getMessage(BasicPanelVisual.class, "LBL_ProjectLocation")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(BasicPanelVisual.class, "BTN_Browse")); // NOI18N
        browseButton.setActionCommand("BROWSE");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        createdFolderLabel.setLabelFor(createdFolderTextField);
        org.openide.awt.Mnemonics.setLocalizedText(createdFolderLabel, org.openide.util.NbBundle.getMessage(BasicPanelVisual.class, "LBL_ProjectFolder")); // NOI18N

        createdFolderTextField.setEditable(false);

        lblArtifactId.setLabelFor(txtArtifactId);
        org.openide.awt.Mnemonics.setLocalizedText(lblArtifactId, org.openide.util.NbBundle.getMessage(BasicPanelVisual.class, "LBL_ArtifactId")); // NOI18N

        txtArtifactId.setEditable(false);

        lblGroupId.setLabelFor(txtGroupId);
        org.openide.awt.Mnemonics.setLocalizedText(lblGroupId, org.openide.util.NbBundle.getMessage(BasicPanelVisual.class, "LBL_GroupId")); // NOI18N

        txtGroupId.setText("com.mycompany");

        lblVersion.setLabelFor(txtVersion);
        org.openide.awt.Mnemonics.setLocalizedText(lblVersion, org.openide.util.NbBundle.getMessage(BasicPanelVisual.class, "LBL_Version")); // NOI18N

        txtVersion.setText("1.0-SNAPSHOT");

        lblPackage.setLabelFor(txtPackage);
        org.openide.awt.Mnemonics.setLocalizedText(lblPackage, org.openide.util.NbBundle.getMessage(BasicPanelVisual.class, "LBL_Package")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(BasicPanelVisual.class, "LBL_Optional")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblAdditionalProps, "jLabel2");

        tblAdditionalProps.setModel(createPropModel());
        tblAdditionalProps.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(tblAdditionalProps);
        tblAdditionalProps.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        org.jdesktop.layout.GroupLayout pnlAdditionalsLayout = new org.jdesktop.layout.GroupLayout(pnlAdditionals);
        pnlAdditionals.setLayout(pnlAdditionalsLayout);
        pnlAdditionalsLayout.setHorizontalGroup(
            pnlAdditionalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAdditionalsLayout.createSequentialGroup()
                .add(lblAdditionalProps)
                .addContainerGap(402, Short.MAX_VALUE))
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
        );
        pnlAdditionalsLayout.setVerticalGroup(
            pnlAdditionalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAdditionalsLayout.createSequentialGroup()
                .add(lblAdditionalProps)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(projectNameLabel)
                    .add(projectLocationLabel)
                    .add(createdFolderLabel)
                    .add(lblPackage)
                    .add(lblVersion)
                    .add(lblGroupId)
                    .add(lblArtifactId))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, projectNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, projectLocationTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, createdFolderTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                    .add(txtPackage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                    .add(txtVersion, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                    .add(txtGroupId, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                    .add(txtArtifactId, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(browseButton)
                    .add(jLabel1)))
            .add(pnlAdditionals, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(projectNameLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(projectLocationLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(createdFolderLabel))
                    .add(layout.createSequentialGroup()
                        .add(projectNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(projectLocationTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(browseButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(createdFolderTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(54, 54, 54)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtArtifactId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblArtifactId))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtGroupId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblGroupId))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtVersion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblVersion))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtPackage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblPackage)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAdditionals, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        String command = evt.getActionCommand();
        if ("BROWSE".equals(command)) { //NOI18N
            JFileChooser chooser = new JFileChooser();
            FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
            chooser.setDialogTitle(NbBundle.getMessage(BasicPanelVisual.class, "TIT_Select_Project_Location"));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            String path = this.projectLocationTextField.getText();
            if (path.length() > 0) {
                File f = new File(path);
                if (f.exists()) {
                    chooser.setSelectedFile(f);
                }
            }
            if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
                File projectDir = chooser.getSelectedFile();
                projectLocationTextField.setText(FileUtil.normalizeFile(projectDir).getAbsolutePath());
            }
            panel.fireChangeEvent();
        }
        
    }//GEN-LAST:event_browseButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JLabel createdFolderLabel;
    private javax.swing.JTextField createdFolderTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAdditionalProps;
    private javax.swing.JLabel lblArtifactId;
    private javax.swing.JLabel lblGroupId;
    private javax.swing.JLabel lblPackage;
    private javax.swing.JLabel lblVersion;
    private javax.swing.JPanel pnlAdditionals;
    private javax.swing.JLabel projectLocationLabel;
    private javax.swing.JTextField projectLocationTextField;
    private javax.swing.JLabel projectNameLabel;
    private javax.swing.JTextField projectNameTextField;
    private javax.swing.JTable tblAdditionalProps;
    private javax.swing.JTextField txtArtifactId;
    private javax.swing.JTextField txtGroupId;
    private javax.swing.JTextField txtPackage;
    private javax.swing.JTextField txtVersion;
    // End of variables declaration//GEN-END:variables
    
    @Override
    public void addNotify() {
        super.addNotify();
        //same problem as in 31086, initial focus on Cancel button
        projectNameTextField.requestFocus();
        tblAdditionalProps.setVisible(false);
        lblAdditionalProps.setVisible(false);
        jScrollPane1.setVisible(false);
    }
    
    boolean valid(WizardDescriptor wizardDescriptor) {
        
        if (projectNameTextField.getText().length() == 0) {
            wizardDescriptor.putProperty(ERROR_MSG,
                    NbBundle.getMessage(BasicPanelVisual.class, "ERR_Project_Name_is_not_valid"));
            return false; // Display name not specified
        }
        File f = FileUtil.normalizeFile(new File(projectLocationTextField.getText()).getAbsoluteFile());
        if (!f.isDirectory()) {
            String message = NbBundle.getMessage(BasicPanelVisual.class, "ERR_Project_Folder_is_not_valid_path");
            wizardDescriptor.putProperty(ERROR_MSG, message); //NOI18N
            return false;
        }
        final File destFolder = FileUtil.normalizeFile(new File(createdFolderTextField.getText()).getAbsoluteFile());
        
        File projLoc = destFolder;
        while (projLoc != null && !projLoc.exists()) {
            projLoc = projLoc.getParentFile();
        }
        if (projLoc == null || !projLoc.canWrite()) {
            wizardDescriptor.putProperty(ERROR_MSG, //NOI18N
                    NbBundle.getMessage(BasicPanelVisual.class, "ERR_Project_Folder_cannot_be_created"));
            return false;
        }
        
        if (FileUtil.toFileObject(projLoc) == null) {
            String message = NbBundle.getMessage(BasicPanelVisual.class, "ERR_Project_Folder_is_not_valid_path");
            wizardDescriptor.putProperty(ERROR_MSG, message); //NOI18N
            return false;
        }
        
        File[] kids = destFolder.listFiles();
        if (destFolder.exists() && kids != null && kids.length > 0) {
            // Folder exists and is not empty
            wizardDescriptor.putProperty(ERROR_MSG,
                    NbBundle.getMessage(BasicPanelVisual.class, "ERR_Project_Folder_exists"));
            return false;
        }
        if (txtArtifactId.getText().trim().length() == 0) {
            wizardDescriptor.putProperty(ERROR_MSG, 
                    NbBundle.getMessage(BasicPanelVisual.class, "ERR_Require_artifactId"));
            return false;
        }
        if (txtGroupId.getText().trim().length() == 0) {
            wizardDescriptor.putProperty(ERROR_MSG, 
                    NbBundle.getMessage(BasicPanelVisual.class, "ERR_require_groupId"));
            return false;
        }
        if (txtVersion.getText().trim().length() == 0) {
            wizardDescriptor.putProperty(ERROR_MSG, 
                    NbBundle.getMessage(BasicPanelVisual.class, "ERR_require_version"));
            return false;
        }
        wizardDescriptor.putProperty(ERROR_MSG, ""); //NOI18N
        return true;
    }
    
    void store(WizardDescriptor d) {
        String name = projectNameTextField.getText().trim();
        String folder = createdFolderTextField.getText().trim();
        
        d.putProperty("projdir", new File(folder)); //NOI18N
        d.putProperty("name", name); //NOI18N
        d.putProperty("artifactId", txtArtifactId.getText().trim()); //NOI18N
        d.putProperty("groupId", txtGroupId.getText().trim()); //NOI18N
        d.putProperty("version", txtVersion.getText().trim()); //NOI18N
        d.putProperty("package", txtPackage.getText().trim()); //NOI18N
        if (tblAdditionalProps.isVisible()) {
            TableModel mdl = tblAdditionalProps.getModel();
            HashMap<String, String> map = new HashMap<String, String>();
            for (int i = 0; i < mdl.getRowCount(); i++) {
                map.put((String)mdl.getValueAt(i, 0), (String)mdl.getValueAt(i, 1));
            }
            d.putProperty("additionalProps", map); //NOI18N
        }
    }
    
    void read(WizardDescriptor settings) {
        File projectLocation = (File) settings.getProperty("projdir"); //NOI18N
        if (projectLocation == null || projectLocation.getParentFile() == null || !projectLocation.getParentFile().isDirectory()) {
            projectLocation = ProjectChooser.getProjectsFolder();
        } else {
            projectLocation = projectLocation.getParentFile();
        }
        this.projectLocationTextField.setText(projectLocation.getAbsolutePath());
        
        String projectName = (String) settings.getProperty("name"); //NOI18N

        if(projectName == null) {
            int baseCount = 1;
            String formatter = NbBundle.getMessage(BasicPanelVisual.class,"TXT_MavenProjectName");
            while ((projectName = validFreeProjectName(projectLocation, formatter, baseCount)) == null) {
                baseCount++;                
            }
        }
        
        this.projectNameTextField.setText(projectName);
        this.projectNameTextField.selectAll();
        ngprovider = (ArchetypeProviderImpl)settings.getProperty(MavenWizardIterator.PROPERTY_CUSTOM_CREATOR);
        final Archetype arch = (Archetype)settings.getProperty(ChooseArchetypePanel.PROP_ARCHETYPE);
        lblAdditionalProps.setText(NbBundle.getMessage(BasicPanelVisual.class, "TXT_Checking1"));
        lblAdditionalProps.setVisible(true);
        tblAdditionalProps.setVisible(false);
        jScrollPane1.setVisible(false);
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                prepareAdditionalProperties(arch);
            }
        });
    }
    
    private void prepareAdditionalProperties(Archetype arch) {
        final DefaultTableModel dtm = new DefaultTableModel();
        dtm.addColumn(NbBundle.getMessage(BasicPanelVisual.class, "COL_Key"));
        dtm.addColumn(NbBundle.getMessage(BasicPanelVisual.class, "COL_Value"));
        try {
            Artifact art = downloadArchetype(arch);
            File fil = art.getFile();
            if (fil.exists()) {
                Map<String, String> props = ngprovider.getAdditionalProperties(art);
                for (String key : props.keySet()) {
                    String defVal = props.get(key);
                    dtm.addRow(new Object[] {key, defVal == null ? "" : defVal });
                }
            }
        } catch (ArtifactResolutionException ex) {
            //#143026
            Logger.getLogger( BasicPanelVisual.class.getName()).log( Level.FINE, "Cannot download archetype", ex);
        } catch (ArtifactNotFoundException ex) {
            //#143026
            Logger.getLogger( BasicPanelVisual.class.getName()).log( Level.FINE, "Cannot download archetype", ex);
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (dtm.getRowCount() > 0) {
                    Mnemonics.setLocalizedText(lblAdditionalProps, NbBundle.getMessage(BasicPanelVisual.class, "TXT_Checking2"));
                    lblAdditionalProps.setVisible(true);
                    jScrollPane1.setVisible(true);
                    tblAdditionalProps.setModel(dtm);
                    tblAdditionalProps.setVisible(true);
                } else {
                    tblAdditionalProps.setVisible(false);
                    lblAdditionalProps.setVisible(false);
                    jScrollPane1.setVisible(false);
                }
            }
        });
    }

    private Artifact downloadArchetype(Archetype arch) throws ArtifactResolutionException, ArtifactNotFoundException {
        MavenEmbedder online = EmbedderFactory.getOnlineEmbedder();
        Artifact art = online.createArtifact(
                arch.getGroupId(), 
                arch.getArtifactId(), 
                arch.getVersion(), 
                "jar", //NOI18N
                "maven-archetype"); //NOI18N
        Artifact pom = online.createArtifact(
                arch.getGroupId(), 
                arch.getArtifactId(), 
                arch.getVersion(), 
                "pom", //NOI18N
                "pom"); //NOI18N
        
        //hack to get the right extension for the right packaging without the plugin.
        art.setArtifactHandler(new ArtifactHandler() {
            public String getExtension() {
                return "jar"; //NOI18N
            }
            public String getDirectory() {
                return null;
            }
            public String getClassifier() {
                return null;
            }
            public String getPackaging() {
                return "maven-archetype"; //NOI18N
            }
            public boolean isIncludesDependencies() {
                return false;
            }
            public String getLanguage() {
                return "java"; //NOI18N
            }
            public boolean isAddedToClasspath() {
                return false;
            }
        });
        List repos;
        if (arch.getRepository() == null) {
            repos = Collections.singletonList(EmbedderFactory.createRemoteRepository(online, "http://repo1.maven.org/maven2", "central"));//NOI18N
        } else {
            
            repos = Collections.singletonList(EmbedderFactory.createRemoteRepository(online, arch.getRepository(), "custom-repo"));//NOI18N
        }
                    AggregateProgressHandle hndl = AggregateProgressFactory.createHandle(NbBundle.getMessage(BasicPanelVisual.class, "Handle_Download"), 
                            new ProgressContributor[] {
                                AggregateProgressFactory.createProgressContributor("zaloha") },  //NOI18N
                            null, null);
        ProgressTransferListener.setAggregateHandle(hndl);
        try {
            hndl.start();
            online.resolve(pom, repos, online.getLocalRepository());
            online.resolve(art, repos, online.getLocalRepository());
            
            RepositoryInfo info = RepositoryPreferences.getInstance().getRepositoryInfoById(RepositoryPreferences.LOCAL_REPO_ID);
            if (info != null) {
                System.out.println("updating repository");
                RepositoryIndexer.updateIndexWithArtifacts(info, Collections.singletonList(art));
            }
        } finally {
            hndl.finish();
            ProgressTransferListener.clearAggregateHandle();
        }
        return art;
    }
    
    private TableModel createPropModel() {
        return new DefaultTableModel();
    }
    
    void validate(WizardDescriptor d) throws WizardValidationException {
        // nothing to validate
    }
    
    // Implementation of DocumentListener --------------------------------------
    
    public void changedUpdate(DocumentEvent e) {
        updateTexts(e);
        if (this.projectNameTextField.getDocument() == e.getDocument()) {
            firePropertyChange(PROP_PROJECT_NAME,null,this.projectNameTextField.getText());
        }
    }
    
    public void insertUpdate(DocumentEvent e) {
        updateTexts(e);
        if (this.projectNameTextField.getDocument() == e.getDocument()) {
            firePropertyChange(PROP_PROJECT_NAME,null,this.projectNameTextField.getText());
        }
    }
    
    public void removeUpdate(DocumentEvent e) {
        updateTexts(e);
        if (this.projectNameTextField.getDocument() == e.getDocument()) {
            firePropertyChange(PROP_PROJECT_NAME,null,this.projectNameTextField.getText());
        }
    }
    
    /** Handles changes in the Project name and project directory, */
    private void updateTexts(DocumentEvent e) {
        
        Document doc = e.getDocument();
        
        if (doc == projectNameTextField.getDocument() || doc == projectLocationTextField.getDocument()) {
            // Change in the project name
            
            String projectName = projectNameTextField.getText();
            String projectFolder = projectLocationTextField.getText();
            
            //if (projectFolder.trim().length() == 0 || projectFolder.equals(oldName)) {
            createdFolderTextField.setText(projectFolder + File.separatorChar + projectName);
            //}
            
        }
        
        if (projectNameTextField.getDocument() == doc && 
                (txtArtifactId.getText().trim().length() == 0 || lastProjectName.equals(txtArtifactId.getText().trim()))) {
            txtArtifactId.setText(projectNameTextField.getText());
            lastProjectName = projectNameTextField.getText().trim();
        }
        
        if (!changedPackage && (projectNameTextField.getDocument() == doc || txtGroupId.getDocument() == doc)) {
            txtPackage.getDocument().removeDocumentListener(this);
            txtPackage.setText(txtGroupId.getText() + "." + projectNameTextField.getText()); //NOI18N
            txtPackage.getDocument().addDocumentListener(this);
        }
        
        if (txtPackage.getDocument() == doc) {
            changedPackage = txtPackage.getText().trim().length() != 0;
        }
        
        panel.fireChangeEvent(); // Notify that the panel changed
    }
    
    private String validFreeProjectName (final File parentFolder, final String formater, final int index) {
        String name = MessageFormat.format (formater, new Object[]{new Integer (index)});                
        File file = new File (parentFolder, name);
        return file.exists() ? null : name;
    }
    
    
}
