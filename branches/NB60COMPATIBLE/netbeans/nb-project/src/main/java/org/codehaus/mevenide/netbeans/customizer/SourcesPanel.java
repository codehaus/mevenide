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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.IllegalCharsetNameException;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.api.customizer.ModelHandle;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.netbeans.api.java.queries.SourceLevelQuery;
import org.netbeans.api.queries.FileEncodingQuery;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Customizer panel for setting source level and encoding.
 * in future possibly also source roots and resource roots.
 * @author mkleint
 */
public class SourcesPanel extends JPanel {
    
    private static final String COMPILER_ART = "maven-compiler-plugin"; //NOI18N
    private static final String CONFIGURATION_EL = "configuration";//NOI18N
    private static final String RELEASE_VERSION = "RELEASE";//NOI18N
    private static final String RESOURCES_ART = "maven-resources-plugin"; //NOI18N
    private static final String PLUGIN_GR = "org.apache.maven.plugins"; //NOI18N
    private static final String ENCODING = "encoding"; //NOI18N
    private static final String SOURCE_PARAM = "source";//NOI18N
    private static final String TARGET_PARAM = "target";//NOI18N
    
    private String encoding;
    private String defaultEncoding = "UTF-8"; //NOI18N
    private String defaultSourceLevel = "1.3";//NOI18N
    private String sourceLevel;
    private ModelHandle handle;


    public SourcesPanel( ModelHandle handle, NbMavenProject project ) {
        initComponents();
        this.handle = handle;
        FileObject projectFolder = project.getProjectDirectory();
        File pf = FileUtil.toFile( projectFolder );
        txtProjectFolder.setText( pf == null ? "" : pf.getPath() ); // NOI18N
        
        
        comSourceLevel.setEditable(false);
        sourceLevel = SourceLevelQuery.getSourceLevel(project.getProjectDirectory());
        comSourceLevel.setModel(new DefaultComboBoxModel(new String[] {
            "1.3", "1.4", "1.5", "1.6" //NOI18N
        }));
        
        comSourceLevel.setSelectedItem(sourceLevel);
        String enc = PluginPropertyUtils.getPluginProperty(project, 
                    PLUGIN_GR,COMPILER_ART, ENCODING, null);
        Charset chs = null;
        if (enc != null) {
            chs = Charset.forName(enc);
        }
        if (chs == null) {
            String resourceEnc = PluginPropertyUtils.getPluginProperty(project,
                    PLUGIN_GR,
                    RESOURCES_ART, ENCODING, null);
            if (resourceEnc != null) {
                chs = Charset.forName(resourceEnc);
            }
        }
        if (chs != null) {
            encoding = chs.name();
        }
        if (encoding == null) {
            encoding = FileEncodingQuery.getDefaultEncoding().name();
        }
        defaultEncoding = FileEncodingQuery.getDefaultEncoding().name();
        
        comEncoding.setModel(new EncodingModel(encoding));
        comEncoding.setRenderer(new EncodingRenderer());
        
        comSourceLevel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleSourceLevelChange();
            }
        });
        
        comEncoding.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                handleEncodingChange();
            }            
        });
    }
    
    private void handleSourceLevelChange() {
        sourceLevel = (String)comSourceLevel.getSelectedItem();
        checkSourceLevel(handle.getPOMModel(), sourceLevel);
        if (defaultSourceLevel.equals(sourceLevel)) {
            lblSourceLevel.setFont(lblSourceLevel.getFont().deriveFont(Font.PLAIN));
        } else {
            lblSourceLevel.setFont(lblSourceLevel.getFont().deriveFont(Font.BOLD));
        }
    }

    //TODO copied from persistence' CPExtender. have it at one place only..
    private void checkSourceLevel(Model mdl, String sl) {
        String source = PluginPropertyUtils.getPluginProperty(handle.getProject(), 
                PLUGIN_GR,COMPILER_ART,SOURCE_PARAM, 
                "compile"); //NOI18N
        if (source != null && source.contains(sl)) {
            return;
        }
        Plugin plugin = new Plugin();
        plugin.setGroupId(PLUGIN_GR);
        plugin.setArtifactId(COMPILER_ART);
        plugin.setVersion(RELEASE_VERSION);
        Plugin old = null;
        Build bld = mdl.getBuild();
        if (bld != null) {
            old = (Plugin) bld.getPluginsAsMap().get(plugin.getKey());
        } else {
            mdl.setBuild(new Build());
        }
        if (old != null) {
            plugin = old;
        } else {
            mdl.getBuild().addPlugin(plugin);
        }
        Xpp3Dom dom = (Xpp3Dom) plugin.getConfiguration();
        if (dom == null) {
            dom = new Xpp3Dom(CONFIGURATION_EL);
            plugin.setConfiguration(dom);
        }
        Xpp3Dom dom2 = dom.getChild(SOURCE_PARAM);
        if (dom2 == null) {
            dom2 = new Xpp3Dom(SOURCE_PARAM);
            dom.addChild(dom2);
        }
        dom2.setValue(sl);
        
        dom2 = dom.getChild(TARGET_PARAM);
        if (dom2 == null) {
            dom2 = new Xpp3Dom(TARGET_PARAM);
            dom.addChild(dom2);
        }
        dom2.setValue(sl);
        handle.markAsModified(mdl);
    }
    
    
    private void handleEncodingChange () {
        Charset enc = (Charset) comEncoding.getSelectedItem();
        String encName;
        if (enc != null) {
            encName = enc.name();
        }
        else {
            encName = encoding;
        }
        checkEncoding(handle.getPOMModel(), encName);
        if (defaultEncoding.equals(encName)) {
            lblEncoding.setFont(lblEncoding.getFont().deriveFont(Font.PLAIN));
        } else {
            lblEncoding.setFont(lblEncoding.getFont().deriveFont(Font.BOLD));
        }
    }
    
    //TODO copied from persistence' CPExtender. have it at one place only..
    private void checkEncoding(Model mdl, String enc) {
        String source = PluginPropertyUtils.getPluginProperty(handle.getProject(), 
                PLUGIN_GR,COMPILER_ART, 
                ENCODING, null);
        if (source != null && source.contains(enc)) {
            return;
        }
        Plugin plugin = new Plugin();
        plugin.setGroupId(PLUGIN_GR);
        plugin.setArtifactId(COMPILER_ART);
        plugin.setVersion(RELEASE_VERSION);
        Plugin plugin2 = new Plugin();
        plugin2.setGroupId(PLUGIN_GR);
        plugin2.setArtifactId(RESOURCES_ART);
        plugin2.setVersion(RELEASE_VERSION);
        Plugin old = null;
        Plugin old2 = null;
        Build bld = mdl.getBuild();
        if (bld != null) {
            old = (Plugin) bld.getPluginsAsMap().get(plugin.getKey());
            old2 = (Plugin) bld.getPluginsAsMap().get(plugin2.getKey());
        } else {
            mdl.setBuild(new Build());
        }
        if (old != null) {
            plugin = old;
        } else {
            mdl.getBuild().addPlugin(plugin);
        }
        if (old2 != null) {
            plugin2 = old2;
        } else {
            mdl.getBuild().addPlugin(plugin2);
        }
        Xpp3Dom dom = (Xpp3Dom) plugin.getConfiguration();
        if (dom == null) {
            dom = new Xpp3Dom(CONFIGURATION_EL);
            plugin.setConfiguration(dom);
        }
        Xpp3Dom dom2 = dom.getChild(ENCODING);
        if (dom2 == null) {
            dom2 = new Xpp3Dom(ENCODING);
            dom.addChild(dom2);
        }
        dom2.setValue(enc);
        
        dom = (Xpp3Dom) plugin2.getConfiguration();
        if (dom == null) {
            dom = new Xpp3Dom(CONFIGURATION_EL);
            plugin2.setConfiguration(dom);
        }
        dom2 = dom.getChild(ENCODING);
        if (dom2 == null) {
            dom2 = new Xpp3Dom(ENCODING);
            dom.addChild(dom2);
        }
        dom2.setValue(enc);
        handle.markAsModified(mdl);
    }
    

    private static class EncodingRenderer extends DefaultListCellRenderer {
        
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            assert value instanceof Charset; 
            return super.getListCellRendererComponent(list, ((Charset)value).displayName(), index, isSelected, cellHasFocus);
        }
    }
    
    private static class EncodingModel extends DefaultComboBoxModel {
        
        public EncodingModel (String originalEncoding) {
            Charset defEnc = null;
            for (Charset c : Charset.availableCharsets().values()) {
                if (c.name().equals(originalEncoding)) {
                    defEnc = c;
                }
                addElement(c);
            }
            if (defEnc == null) {
                //Create artificial Charset to keep the original value
                //May happen when the project was set up on the platform
                //which supports more encodings
                try {
                    defEnc = new UnknownCharset (originalEncoding);
                    addElement(defEnc);
                } catch (IllegalCharsetNameException e) {
                    //The source.encoding property is completely broken
                    Logger.getLogger(this.getClass().getName()).info("IllegalCharsetName: " + originalEncoding); //NOI18N
                }
            }
            if (defEnc == null) {
                defEnc = FileEncodingQuery.getDefaultEncoding();
            }
            setSelectedItem(defEnc);
        }
    }
    
    private static class UnknownCharset extends Charset {
        
        UnknownCharset (String name) {
            super (name, new String[0]);
        }
    
        public boolean contains(Charset c) {
            throw new UnsupportedOperationException();
        }

        public CharsetDecoder newDecoder() {
            throw new UnsupportedOperationException();
        }

        public CharsetEncoder newEncoder() {
            throw new UnsupportedOperationException();
        }
}
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblProjectFolder = new javax.swing.JLabel();
        txtProjectFolder = new javax.swing.JTextField();
        sourceRootsPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lblSourceLevel = new javax.swing.JLabel();
        comSourceLevel = new javax.swing.JComboBox();
        lblEncoding = new javax.swing.JLabel();
        comEncoding = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        lblProjectFolder.setLabelFor(txtProjectFolder);
        org.openide.awt.Mnemonics.setLocalizedText(lblProjectFolder, org.openide.util.NbBundle.getBundle(SourcesPanel.class).getString("CTL_ProjectFolder")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(lblProjectFolder, gridBagConstraints);

        txtProjectFolder.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(txtProjectFolder, gridBagConstraints);
        txtProjectFolder.getAccessibleContext().setAccessibleDescription(null);

        sourceRootsPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.45;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(sourceRootsPanel, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblSourceLevel.setLabelFor(comSourceLevel);
        org.openide.awt.Mnemonics.setLocalizedText(lblSourceLevel, org.openide.util.NbBundle.getMessage(SourcesPanel.class, "TXT_SourceLevel")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        jPanel1.add(lblSourceLevel, gridBagConstraints);

        comSourceLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1.4", "1.5" }));
        comSourceLevel.setMinimumSize(this.comSourceLevel.getPreferredSize());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(comSourceLevel, gridBagConstraints);
        comSourceLevel.getAccessibleContext().setAccessibleName(null);
        comSourceLevel.getAccessibleContext().setAccessibleDescription(null);

        lblEncoding.setLabelFor(comEncoding);
        org.openide.awt.Mnemonics.setLocalizedText(lblEncoding, org.openide.util.NbBundle.getMessage(SourcesPanel.class, "TXT_Encoding")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 12);
        jPanel1.add(lblEncoding, gridBagConstraints);

        comEncoding.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        jPanel1.add(comEncoding, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comEncoding;
    private javax.swing.JComboBox comSourceLevel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblEncoding;
    private javax.swing.JLabel lblProjectFolder;
    private javax.swing.JLabel lblSourceLevel;
    private javax.swing.JPanel sourceRootsPanel;
    private javax.swing.JTextField txtProjectFolder;
    // End of variables declaration//GEN-END:variables
    
}
