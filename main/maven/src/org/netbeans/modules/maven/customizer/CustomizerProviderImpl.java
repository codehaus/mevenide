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

package org.netbeans.modules.maven.customizer;

import org.netbeans.modules.maven.api.customizer.ModelHandle;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.Model;
import org.apache.maven.profiles.ProfilesRoot;
import org.apache.maven.project.MavenProject;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.ProjectProfileHandler;
import org.netbeans.modules.maven.configurations.ConfigurationProviderEnabler;
import org.netbeans.modules.maven.configurations.M2ConfigProvider;
import org.netbeans.modules.maven.configurations.M2Configuration;
import org.netbeans.modules.maven.embedder.MavenSettingsSingleton;
import org.netbeans.modules.maven.embedder.writer.WriterUtils;
import org.netbeans.modules.maven.execute.UserActionGoalProvider;
import hidden.org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.JDOMFactory;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.maven.MavenProjectPropsImpl;
import org.netbeans.modules.maven.execute.model.ActionToGoalMapping;
import org.netbeans.modules.maven.execute.model.io.jdom.NetbeansBuildActionJDOMWriter;
import org.netbeans.modules.maven.execute.model.io.xpp3.NetbeansBuildActionXpp3Reader;
import org.netbeans.spi.project.ui.CustomizerProvider;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * maven implementation of CustomizerProvider, handles the general workflow,
 *for panel creation depegates to M2CustomizerPanelProvider instances.
 * @author Milos Kleint 
 */
public class CustomizerProviderImpl implements CustomizerProvider {
    
    private final NbMavenProjectImpl project;
    private ModelHandle handle;
    
    
    public CustomizerProviderImpl(NbMavenProjectImpl project) {
        this.project = project;
    }
    
    public void showCustomizer() {
        showCustomizer( null );
    }
    
    
    public void showCustomizer( String preselectedCategory ) {
        showCustomizer( preselectedCategory, null );
    }
    
    public void showCustomizer( String preselectedCategory, String preselectedSubCategory ) {
        project.getLookup().lookup(MavenProjectPropsImpl.class).startTransaction();
        try {
            init();
            OptionListener listener = new OptionListener();
            Lookup context = Lookups.fixed(new Object[] { project, handle});
            Dialog dialog = ProjectCustomizer.createCustomizerDialog("Projects/org-netbeans-modules-maven/Customizer", //NOI18N
                                             context, 
                                             preselectedCategory, listener, null );
            dialog.addWindowListener( listener );
            listener.setDialog(dialog);
            dialog.setTitle( MessageFormat.format(
                    org.openide.util.NbBundle.getMessage(CustomizerProviderImpl.class, "TIT_Project_Properties"),
                    new Object[] { ProjectUtils.getInformation(project).getDisplayName() } ) );
            
            dialog.setVisible(true);
        } catch (FileNotFoundException ex) {
            //TODO
            ex.printStackTrace();
        } catch (IOException ex) {
            //TODO
            ex.printStackTrace();
        } catch (XmlPullParserException ex) {
            //TODO
            ex.printStackTrace();
        } 
    }
    
    private void init() throws XmlPullParserException, FileNotFoundException, IOException {
        Model model = project.getEmbedder().readModel(project.getPOMFile());
        ProfilesRoot prof = MavenSettingsSingleton.createProfilesModel(project.getProjectDirectory());
        UserActionGoalProvider usr = project.getLookup().lookup(org.netbeans.modules.maven.execute.UserActionGoalProvider.class);
        Map<String, ActionToGoalMapping> mapps = new HashMap<String, ActionToGoalMapping>();
        NetbeansBuildActionXpp3Reader reader = new NetbeansBuildActionXpp3Reader();
        ActionToGoalMapping mapping = reader.read(new StringReader(usr.getRawMappingsAsString()));
        mapps.put(M2Configuration.DEFAULT, mapping);
        List<ModelHandle.Configuration> configs = new ArrayList<ModelHandle.Configuration>();
        ModelHandle.Configuration active = null;
        boolean configEnabled = project.getLookup().lookup(ConfigurationProviderEnabler.class).isConfigurationEnabled();
        if (configEnabled) {
            M2ConfigProvider provider = project.getLookup().lookup(M2ConfigProvider.class);
            M2Configuration act = provider.getActiveConfiguration();
            M2Configuration defconfig = provider.getDefaultConfig();
            mapps.put(defconfig.getId(), reader.read(new StringReader(defconfig.getRawMappingsAsString())));
            ModelHandle.Configuration c = ModelHandle.createDefaultConfiguration();
            configs.add(c);
            if (act.equals(defconfig)) {
                active = c;
            }
            
            for (M2Configuration config : provider.getSharedConfigurations()) {
                mapps.put(config.getId(), reader.read(new StringReader(config.getRawMappingsAsString())));
                c = ModelHandle.createCustomConfiguration(config.getId());
                c.setActivatedProfiles(config.getActivatedProfiles());
                c.setShared(true);
                configs.add(c);
                if (act.equals(config)) {
                    active = c;
                }
            }
            for (M2Configuration config : provider.getNonSharedConfigurations()) {
                mapps.put(config.getId(), reader.read(new StringReader(config.getRawMappingsAsString())));
                c = ModelHandle.createCustomConfiguration(config.getId());
                c.setActivatedProfiles(config.getActivatedProfiles());
                c.setShared(false);
                configs.add(c);
                if (act.equals(config)) {
                    active = c;
                }
            }
            for (M2Configuration config : provider.getProfileConfigurations()) {
                mapps.put(config.getId(), reader.read(new StringReader(config.getRawMappingsAsString())));
                c = ModelHandle.createProfileConfiguration(config.getId());
                configs.add(c);
                if (act.equals(config)) {
                    active = c;
                }
            }
            
        } else {
            configs.add(ModelHandle.createDefaultConfiguration());
            active = configs.get(0);
            ProjectProfileHandler profileHandler = project.getLookup().lookup(ProjectProfileHandler.class);
            for (String profile : profileHandler.getAllProfiles()) {
                configs.add(ModelHandle.createProfileConfiguration(profile));
            }
        }

        handle = ACCESSOR.createHandle(model, prof, project.getOriginalMavenProject(), mapps, configs, active);
        handle.setConfigurationsEnabled(configEnabled);
    }
    
    public static ModelAccessor ACCESSOR = null;

    static {
        // invokes static initializer of ModelHandle.class
        // that will assign value to the ACCESSOR field above
        Class c = ModelHandle.class;
        try {
            Class.forName(c.getName(), true, c.getClassLoader());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    
    
    
    public static abstract class ModelAccessor {
        
        public abstract ModelHandle createHandle(Model model, ProfilesRoot prof, MavenProject proj, Map<String, ActionToGoalMapping> mapp, 
                List<ModelHandle.Configuration> configs, ModelHandle.Configuration active);
        
    }
    /** Listens to the actions on the Customizer's option buttons */
    private class OptionListener extends WindowAdapter implements ActionListener {
        private Dialog dialog;
        
        OptionListener() {
        }
        
        void setDialog(Dialog dlg) {
            dialog = dlg;
        }
        
        // Listening to OK button ----------------------------------------------
        
        public void actionPerformed( ActionEvent e ) {
            if ( dialog != null ) {
                dialog.setVisible(false);
                dialog.dispose();
                try {
                    project.getProjectDirectory().getFileSystem().runAtomicAction(new FileSystem.AtomicAction() {
                        public void run() throws IOException {
                            project.getLookup().lookup(MavenProjectPropsImpl.class).commitTransaction();
                            writeAll(handle, project);
                        }
                    });
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                    //TODO error reporting on wrong model save
                }
            }
        }
        
        // Listening to window events ------------------------------------------
        
        @Override
        public void windowClosed( WindowEvent e) {
            //TODO where to put elsewhere?
            project.getLookup().lookup(MavenProjectPropsImpl.class).cancelTransaction();
        }
        
        @Override
        public void windowClosing(WindowEvent e) {
            if ( dialog != null ) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        }

    }
    
    static interface SubCategoryProvider {
        public void showSubCategory(String name);
    }

   public static void writeAll(ModelHandle handle, NbMavenProjectImpl project) throws IOException {
        if (handle.isModified(handle.getPOMModel())) {
            WriterUtils.writePomModel(FileUtil.toFileObject(project.getPOMFile()), handle.getPOMModel());
        }
        if (handle.isModified(handle.getProfileModel())) {
            WriterUtils.writeProfilesModel(project.getProjectDirectory(), handle.getProfileModel());
        }
        if (handle.isModified(handle.getActionMappings())) {
            writeNbActionsModel(project.getProjectDirectory(), handle.getActionMappings(), M2Configuration.getFileNameExt(M2Configuration.DEFAULT));
        }
        project.getLookup().lookup(ConfigurationProviderEnabler.class).enableConfigurations(handle.isConfigurationsEnabled());
        if (handle.isConfigurationsEnabled()) {
            M2ConfigProvider prv = project.getLookup().lookup(M2ConfigProvider.class);
            
            if (handle.isModified(handle.getConfigurations())) {
                List<M2Configuration> shared = new ArrayList<M2Configuration>();
                List<M2Configuration> nonshared = new ArrayList<M2Configuration>();
                for (ModelHandle.Configuration mdlConf : handle.getConfigurations()) {
                    if (!mdlConf.isDefault() && !mdlConf.isProfileBased()) {
                        M2Configuration c = new M2Configuration(mdlConf.getId(), project);
                        c.setActivatedProfiles(mdlConf.getActivatedProfiles());
                        if (mdlConf.isShared()) {
                            shared.add(c);
                        } else {
                            nonshared.add(c);
                        }
                    }
                }
                prv.setConfigurations(shared, nonshared, true);
            }
            
            //TODO we need to set the configurations for the case of non profile configs
            String id = handle.getActiveConfiguration() != null ? handle.getActiveConfiguration().getId() : M2Configuration.DEFAULT;
            for (M2Configuration m2 : prv.getConfigurations()) {
                if (id.equals(m2.getId())) {
                    prv.setActiveConfiguration(m2);
                }
            }
            //save action mappings for configurations..
            for (ModelHandle.Configuration c : handle.getConfigurations()) {
                if (handle.isModified(handle.getActionMappings(c))) {
                    writeNbActionsModel(project.getProjectDirectory(), handle.getActionMappings(c), M2Configuration.getFileNameExt(c.getId()));
                }
                
            }
        }
   }
    
   public static void writeNbActionsModel(final FileObject pomDir, final ActionToGoalMapping mapping, final String path) throws IOException {
        pomDir.getFileSystem().runAtomicAction(new FileSystem.AtomicAction() {
            public void run() throws IOException {
                JDOMFactory factory = new DefaultJDOMFactory();
                
                InputStream inStr = null;
                FileLock lock = null;
                OutputStreamWriter outStr = null;
                try {
                    Document doc;
                    FileObject fo = pomDir.getFileObject(path);
                    if (fo == null) {
                        fo = pomDir.createData(path);
                        doc = factory.document(factory.element("actions")); //NOI18N
                    } else {
                        //TODO..
                        inStr = fo.getInputStream();
                        SAXBuilder builder = new SAXBuilder();
                        doc = builder.build(inStr);
                        inStr.close();
                        inStr = null;
                    }
                    lock = fo.lock();
                    NetbeansBuildActionJDOMWriter writer = new NetbeansBuildActionJDOMWriter();
                    String encoding = mapping.getModelEncoding() != null ? mapping.getModelEncoding() : "UTF-8"; //NOI18N
                    outStr = new OutputStreamWriter(fo.getOutputStream(lock), encoding);
                    Format form = Format.getRawFormat().setEncoding(encoding);
                    form = form.setLineSeparator(System.getProperty("line.separator")); //NOI18N
                    writer.write(mapping, doc, outStr, form);
                } catch (JDOMException exc){
                    throw (IOException) new IOException("Cannot parse the nbactions.xml by JDOM.").initCause(exc); //NOI18N
                } finally {
                    IOUtil.close(inStr);
                    IOUtil.close(outStr);
                    if (lock != null) {
                        lock.releaseLock();
                    }
                    
                }
            }
        });
    }
    
}
