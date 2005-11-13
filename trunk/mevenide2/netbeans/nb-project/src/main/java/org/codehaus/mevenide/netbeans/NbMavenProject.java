/* ==========================================================================
 * Copyright 2005 Mevenide Team
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

package org.codehaus.mevenide.netbeans;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.project.DefaultMavenProjectBuilder;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl;
import org.codehaus.mevenide.netbeans.queries.MavenForBinaryQueryImpl;
import org.codehaus.mevenide.netbeans.queries.MavenSharabilityQueryImpl;
import org.codehaus.mevenide.netbeans.queries.MavenTestForSourceImpl;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.openide.ErrorManager;
import org.openide.filesystems.*;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;



/**
 * the ultimate source for all maven project like.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public final class NbMavenProject implements Project {
    
    public static final String PROP_PROJECT = "MavenProject"; //NOI18N
    
    private FileObject fileObject;
    private File projectFile;
    private Image icon;
    private Lookup lookup;
    private PropertyChangeSupport support;
    private Updater updater1;
    private Updater updater2;
    private Updater updater3;
    private Sources sources;
    private MavenProject project;
    private MavenEmbedder embedder;

    private Info projectInfo;
    
    /** 
     * Creates a new instance of MavenProject, should never be called by user code.
     * but only by MavenProjectFactory!!!
     */
    public NbMavenProject(FileObject projectFO, File projectFile) throws Exception {
        this.projectFile = projectFile;
        support = new PropertyChangeSupport(this);
        fileObject = projectFO;
        projectInfo = new Info();
        updater1 = new Updater(true);
        updater2 = new Updater(true, USER_DIR_FILES);
        updater3 = new Updater(false);
        File projectDir = FileUtil.toFile(fileObject.getParent());
        embedder = EmbedderFactory.getProjectEmbedder();
        //TODO maybe just be offline when reading project?
        
    }
    
    
    
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        synchronized (support) {
            support.addPropertyChangeListener(propertyChangeListener);
        }
    }
    
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        synchronized (support) {
            support.removePropertyChangeListener(propertyChangeListener);
        }
    }
    
    public MavenProject getOriginalMavenProject() {
        if (project == null) {
            try {
                try {
                    project = embedder.readProjectWithDependencies(projectFile);
                } catch (ArtifactResolutionException ex) {
                    project = embedder.readProject(projectFile);
                } catch (ArtifactNotFoundException ex) {
                    project = embedder.readProject(projectFile);
                }
            } catch (ProjectBuildingException ex) {
                ex.printStackTrace();
            }
        }
        // TODO
        return project;
    }
    
    public void firePropertyChange(String property) {
        synchronized (support) {
            project = null;
            projectInfo.reset();
            support.firePropertyChange(new PropertyChangeEvent(this, property, null, null));
        }
    }
    
    public String getDisplayName() {
        String displayName = projectInfo.getDisplayName();
        if (displayName == null) {
            displayName = "<Maven2 project with no name>";
        }
        return displayName;
    }
    
    public String getShortDescription() {
        String desc = null;
        if (desc == null) {
            desc = getOriginalMavenProject().getDescription();
        }
        if (desc == null) {
            desc = "A Maven2 based project";
        }
        return desc;
    }
    
    Updater getProjectFolderUpdater() {
        return updater1;
    }
    
    Updater getUserFolderUpdater() {
        return updater2;
    }
    
    Updater getFileUpdater() {
        return updater3;
    }
    
    private Image getIcon() {
        if (icon == null) {
            icon = Utilities.loadImage("org/codehaus/mevenide/netbeans/Maven2Icon.gif");
        }
        return icon;
    }
        
    public String getName() {
        String toReturn = getOriginalMavenProject().getId();
        if (toReturn == null) {
            toReturn = getProjectDirectory().getName() + " <No Project ID>";
        }
        return toReturn;
    }
    
    public Action createRefreshAction() {
        return new RefreshAction();
    }
    
    public FileObject getProjectDirectory() {
        return fileObject.getParent();
    }
    
    public FileObject getHomeDirectory() {
        String homeStr = System.getProperty("user.home");
        FileObject fo = FileUtil.toFileObject(new File(homeStr));
        FileObject home = fo.getFileObject(".m2");
        if (home == null) {
            try {
                home = fo.createFolder(".m2");
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        return home;
    }
    
    /**
     * TODO temporary.. how to figure correctly the root path to repository
     */
    public FileObject getRepositoryRoot() {
        FileObject parent = getHomeDirectory();
        return parent.getFileObject("repository");
    }
    
    public String getArtifactRelativeRepositoryPath() {
        return getArtifactRelativeRepositoryPath(getOriginalMavenProject().getArtifact());
    }
    
    public String getArtifactRelativeRepositoryPath(Artifact artifact) {
        embedder.setLocalRepositoryDirectory(FileUtil.toFile(getRepositoryRoot()));
        String toRet = embedder.getLocalRepository().pathOf(artifact);
        //TODO this is more or less a hack..
        // if packaging is nbm, the path suggests the extension to be nbm.. override that to be jar
        return toRet.substring(0 , toRet.length() - artifact.getType().length()) + "jar";
    }
    
    public MavenEmbedder getEmbedder() {
        return embedder;
    }
    
   
   private URI getDirURI(String path) {
       String pth = path.trim();
       pth = pth.replaceFirst("^\\./", "");
       pth = pth.replaceFirst("^\\.\\\\", "");
       File src = FileUtilities.resolveFilePath(FileUtil.toFile(getProjectDirectory()), pth);
       return FileUtil.normalizeFile(src).toURI();
   }

    
    public synchronized Lookup getLookup() {
        if (lookup == null) {
            lookup = createBasicLookup();
            // now in the creation of extended lookup by MavenLookupProvider
            // it can happen that someone is using the project's lookup to find some 
            // basic stuff. That would create a cycle..
            // obviously one cannot call getlookup() in the basic lookup setup..
            lookup = createCompleteLookups();
        }
        return lookup;
    }   
    private Lookup createBasicLookup() {
        Lookup staticLookup = Lookups.fixed(new Object[] {
            projectInfo,
            new MavenForBinaryQueryImpl(this),
            new ActionProviderImpl(this),
//            new CustomizerProviderImpl(this),
            new LogicalViewProviderImpl(this),
            new ProjectOpenedHookImpl(this),
            new ClassPathProviderImpl(this),
            new MavenSharabilityQueryImpl(this),
            new MavenTestForSourceImpl(this),
////            new MavenFileBuiltQueryImpl(this),
            new SubprojectProviderImpl(this),
            new MavenSourcesImpl(this), 
            new RecommendedTemplatesImpl()//,
//            new MavenSourceLevelImpl(this)
                    
        });
        return staticLookup;
    }
    
    private Lookup createCompleteLookups() {
        Collection toReturn = new ArrayList();
        // add the static lookup that acts as complete instance as of now..
        toReturn.add(lookup);
        
        Lookup.Template template = new Lookup.Template(AdditionalM2LookupProvider.class);
        Lookup.Result result = Lookup.getDefault().lookup(template);
        Collection col = result.allInstances();
        Iterator it = col.iterator();
        while (it.hasNext()) {
            AdditionalM2LookupProvider prov = (AdditionalM2LookupProvider)it.next();
            toReturn.add(prov.createMavenLookup(this));
        }
        Lookup[] lookups = new Lookup[toReturn.size()];
        lookups = (Lookup[])toReturn.toArray(lookups);
        ProxyLookup look = new ProxyLookup(lookups);
        return look;
    }
    
   // Private innerclasses ----------------------------------------------------
    
    private final class Info implements ProjectInformation {
        
        private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
        
        Info() {}
        
        public void reset() {
            firePropertyChange(ProjectInformation.PROP_DISPLAY_NAME);
            pcs.firePropertyChange(ProjectInformation.PROP_ICON, null, getIcon());
        }
        
        void firePropertyChange(String prop) {
            pcs.firePropertyChange(prop, null, null);
        }
        
        public String getName() {
            String toReturn = NbMavenProject.this.getName();
            return toReturn;
        }
        
        public String getDisplayName() {
            String toReturn = NbMavenProject.this.getOriginalMavenProject().getName();
            if (toReturn == null) {
                toReturn = "<No name defined>";
            }
            toReturn = toReturn + " (" + NbMavenProject.this.getOriginalMavenProject().getPackaging() + ")";
            return toReturn;
        }
        
        public Icon getIcon() {
            return new ImageIcon(NbMavenProject.this.getIcon());
        }
        
        public Project getProject() {
            return NbMavenProject.this;
        }
        
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            pcs.addPropertyChangeListener(listener);
        }
        
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            pcs.removePropertyChangeListener(listener);
        }
        
    }    
 
    // needs to be binary sorted;
    private static final String[] DEFAULT_FILES = new String[] {
        "pom.xml"
    };
    private static final String[] USER_DIR_FILES = new String[] {
        "settings.xml"
    };

    
    private class Updater implements FileChangeListener {
        
//        private FileObject fileObject;
        private boolean isFolder;
        private String[] filesToWatch;
        Updater(boolean folder) {
            this(folder, DEFAULT_FILES);
        }
        
        Updater(boolean folder, String[] toWatch) {
            isFolder = folder;
            filesToWatch = toWatch;
        }
        
        public void fileAttributeChanged(FileAttributeEvent fileAttributeEvent) {
        }
        
        public void fileChanged(FileEvent fileEvent) {
            if (!isFolder) {
                String nameExt = fileEvent.getFile().getNameExt();
                if (Arrays.binarySearch(filesToWatch, nameExt) != -1)  {
                    firePropertyChange(PROP_PROJECT);
                }
            }
        }
        
        public void fileDataCreated(FileEvent fileEvent) {
            //TODO shall also include the parent of the pom if available..
            if (isFolder) {
                String nameExt = fileEvent.getFile().getNameExt();
                if (Arrays.binarySearch(filesToWatch, nameExt) != -1) {
                    fileEvent.getFile().addFileChangeListener(getFileUpdater());
                    firePropertyChange(PROP_PROJECT);
                }
            }
        }
        
        public void fileDeleted(FileEvent fileEvent) {
            if (!isFolder) {
                fileEvent.getFile().removeFileChangeListener(getFileUpdater());
                firePropertyChange(PROP_PROJECT);
            }
        }
        
        public void fileFolderCreated(FileEvent fileEvent) {
            firePropertyChange(PROP_PROJECT);
        }
        
        public void fileRenamed(FileRenameEvent fileRenameEvent) {
        }
        
    }
    
    private static final class RecommendedTemplatesImpl 
                        implements RecommendedTemplates, PrivilegedTemplates {
                            
        private static final String[] APPLICATION_TYPES = new String[] { 
            "java-classes",         // NOI18N
            "java-main-class",      // NOI18N
            "java-forms",           // NOI18N
            "gui-java-application", // NOI18N
            "java-beans",           // NOI18N
            "oasis-XML-catalogs",   // NOI18N
            "XML",                  // NOI18N
            //"ant-script",           // NOI18N
            //"ant-task",             // NOI18N
            "servlet-types",     // NOI18N
            "web-types",         // NOI18N
            "junit",                // NOI18N
            "MIDP",              // NOI18N
            "maven-docs",
            "simple-files"          // NOI18N
        };
        
        private static final String[] PRIVILEGED_NAMES = new String[] {
            "Templates/Classes/Class.java", // NOI18N
            "Templates/Classes/Package", // NOI18N
            "Templates/Classes/Interface.java", // NOI18N
            "Templates/GUIForms/JPanel.java", // NOI18N
            "Templates/GUIForms/JFrame.java", // NOI18N
        };
        
        RecommendedTemplatesImpl() {
        }
        
        public String[] getRecommendedTypes() {
            return APPLICATION_TYPES;
        }
        
        public String[] getPrivilegedTemplates() {
            return PRIVILEGED_NAMES;
        }
        
    }   
    
    private class RefreshAction extends AbstractAction {
        public RefreshAction() {
            putValue(Action.NAME, "Reload Project");
        }
        
        public void actionPerformed(java.awt.event.ActionEvent event) {
            NbMavenProject.this.firePropertyChange(PROP_PROJECT);
        }
        
    }
    
}
