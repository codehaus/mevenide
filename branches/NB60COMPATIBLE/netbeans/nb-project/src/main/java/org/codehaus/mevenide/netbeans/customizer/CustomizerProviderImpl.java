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

import org.codehaus.mevenide.netbeans.api.customizer.ModelHandle;
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
import org.apache.maven.model.Model;
import org.apache.maven.profiles.ProfilesRoot;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.embedder.MavenSettingsSingleton;
import org.codehaus.mevenide.netbeans.embedder.writer.WriterUtils;
import org.codehaus.mevenide.netbeans.execute.UserActionGoalProvider;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.model.io.jdom.NetbeansBuildActionJDOMWriter;
import org.codehaus.mevenide.netbeans.execute.model.io.xpp3.NetbeansBuildActionXpp3Reader;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.JDOMFactory;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.CustomizerProvider;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * maven implementation of CustomizerProvider, handles the general workflow,
 *for panel creation depegates to M2CustomizerPanelProvider instances.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class CustomizerProviderImpl implements CustomizerProvider {
    
    private final NbMavenProject project;
    private ModelHandle handle;
    
    
    public CustomizerProviderImpl(NbMavenProject project) {
        this.project = project;
    }
    
    public void showCustomizer() {
        showCustomizer( null );
    }
    
    
    public void showCustomizer( String preselectedCategory ) {
        showCustomizer( preselectedCategory, null );
    }
    
    public void showCustomizer( String preselectedCategory, String preselectedSubCategory ) {
        try {
            init();
            OptionListener listener = new OptionListener();
            Lookup context = Lookups.fixed(new Object[] { project, handle});
            Dialog dialog = ProjectCustomizer.createCustomizerDialog("Projects/org-codehaus-mevenide-netbeans/Customizer", //NOI18N
                                             context, 
                                             preselectedCategory, listener, null );
            dialog.addWindowListener( listener );
            listener.setDialog(dialog);
            dialog.setTitle( MessageFormat.format(
                    "{0}", // NOI18N
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
        UserActionGoalProvider usr = project.getLookup().lookup(org.codehaus.mevenide.netbeans.execute.UserActionGoalProvider.class);
        ActionToGoalMapping mapping = new NetbeansBuildActionXpp3Reader().read(new StringReader(usr.getRawMappingsAsString()));
        handle = ACCESSOR.createHandle(model, prof, project.getOriginalMavenProject(), mapping);
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
        
        public abstract ModelHandle createHandle(Model model, ProfilesRoot prof, MavenProject proj, ActionToGoalMapping mapp);
        
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
                            if (handle.isModified(handle.getPOMModel())) {
                                WriterUtils.writePomModel(FileUtil.toFileObject(project.getPOMFile()), handle.getPOMModel());
                            }
                            if (handle.isModified(handle.getProfileModel())) {
                                WriterUtils.writeProfilesModel(project.getProjectDirectory(), handle.getProfileModel());
                            }
                            if (handle.isModified(handle.getActionMappings())) {
                                writeNbActionsModel(project.getProjectDirectory(), handle.getActionMappings());
                            }
                        }
                    });
                } catch (IOException ex) {
                    ex.printStackTrace();
                    //TODO
                }
            }
        }
        
        // Listening to window events ------------------------------------------
        
        @Override
        public void windowClosed( WindowEvent e) {
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
    
    
   public static void writeNbActionsModel(final FileObject pomDir, final ActionToGoalMapping mapping) throws IOException {
        pomDir.getFileSystem().runAtomicAction(new FileSystem.AtomicAction() {
            public void run() throws IOException {
                JDOMFactory factory = new DefaultJDOMFactory();
                
                InputStream inStr = null;
                FileLock lock = null;
                OutputStreamWriter outStr = null;
                try {
                    Document doc;
                    FileObject fo = pomDir.getFileObject(UserActionGoalProvider.FILENAME);
                    if (fo == null) {
                        fo = pomDir.createData(UserActionGoalProvider.FILENAME);
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
                    writer.write(mapping, doc, outStr, form);
                } catch (JDOMException exc){
                    throw (IOException) new IOException("Cannot parse the nbactions.xml by JDOM.").initCause(exc);
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
