/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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

package org.mevenide.netbeans.project;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.URI;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.context.IQueryErrorCallback;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.netbeans.project.classpath.ClassPathProviderImpl;
import org.mevenide.netbeans.project.queries.MavenForBinaryQueryImpl;

import org.mevenide.netbeans.project.queries.MavenSharabilityQueryImpl;
import org.mevenide.netbeans.project.queries.MavenTestForSourceImpl;
import org.mevenide.properties.IPropertyLocator;
import org.mevenide.properties.IPropertyResolver;
import org.mevenide.properties.resolver.ProjectWalker2;
import org.mevenide.properties.resolver.PropertyLocatorFactory;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.openide.filesystems.*;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenProject implements Project {
    private static final Log logger = LogFactory.getLog(MavenProject.class);
    
    public static final String PROP_PROJECT = "MavenProject"; //NOI18N
    
    private File file;
    private FileObject fileObject;
    private IPropertyResolver properties;
    private IQueryContext queryContext;
    private ILocationFinder locFinder;
    private IPropertyLocator propertyLocator;
    private ProjectWalker2 walker;
    private Image icon;
    private Lookup lookup;
    private PropertyChangeSupport support;
    private Updater updater1;
    private Updater updater2;
    private Updater updater3;
    private Lookup staticLookup;
    private ProjectLookup completeLookup;
    private Info projectInfo;
    
    /** Creates a new instance of MavenProject */
    MavenProject(FileObject projectFO, File projectFile) throws Exception {
        support = new PropertyChangeSupport(this);
        file = projectFile;
        fileObject = projectFO;
        projectInfo = new Info();
        updater1 = new Updater(true);
        updater2 = new Updater(true, USER_DIR_FILES);
        updater3 = new Updater(false);
        File projectDir = FileUtil.toFile(fileObject.getParent());
        queryContext = new DefaultQueryContext(projectDir, projectInfo);
        properties = queryContext.getResolver();
        propertyLocator = PropertyLocatorFactory.getFactory().createContextBasedLocator(queryContext);
        locFinder = new LocationFinderAggregator(queryContext);
        walker = new ProjectWalker2(queryContext);
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
    
    void firePropertyChange(String property) {
        synchronized (support) {
            projectInfo.reset();
            support.firePropertyChange(new PropertyChangeEvent(this, property, null, null));
        }
    }
    
    public String getDisplayName() {
        String displayName = projectInfo.getDisplayName();
        if (displayName == null) {
            displayName = "<Maven project with no name>";
        }
        return displayName;
    }
    
    public String getShortDescription() {
        String desc = projectInfo.getErrorDescription();
        if (desc == null) {
            desc = getOriginalMavenProject().getShortDescription();
        }
        if (desc == null) {
            desc = "A Maven based project";
        }
        return desc;
    }
    
    public org.apache.maven.project.Project getOriginalMavenProject() {
        return queryContext.getPOMContext().getFinalProject();
    }
    
    public IPropertyResolver getPropertyResolver() {
        return properties;
    }
    
    public ILocationFinder getLocFinder() {
        return locFinder;
    }
    
    public IPropertyLocator getPropertyLocator() {
        return propertyLocator;
    }
    
    public IQueryContext getContext() {
        return queryContext;
    }
    
    public ProjectWalker2 getProjectWalker() {
        return walker;
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
    
    public Image getIcon() {
        if (icon == null) {
            icon = Utilities.loadImage("org/mevenide/netbeans/project/resources/MavenIcon.gif");
        }
        return icon;
    }
    
    public Lookup getLookup() {
        if (lookup == null) {
            lookup = createLookup();
        }
        return lookup;
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
    
    public URI getSrcDirectory() {
        if (getOriginalMavenProject().getBuild() != null) {
            String path = getOriginalMavenProject().getBuild().getSourceDirectory();
            if (path != null) {
                return getDirURI(getPropertyResolver().resolveString(path));
            }
        }
        // this one should not fail
        String path = properties.getResolvedValue("maven.src.dir");
        if (path == null) {
            logger.warn("Strange thing here. src dir not found.");
            return null;
        }
        File fl = new File(path, "java");
        return  FileUtil.normalizeFile(fl).toURI();
    }
    
    public URI getTestSrcDirectory() {
        if (getOriginalMavenProject().getBuild() != null) {
            String path = getOriginalMavenProject().getBuild().getUnitTestSourceDirectory();
            if (path != null) {
                return getDirURI(getPropertyResolver().resolveString(path));
            }
        }
        // this one should not fail
        String path = properties.getResolvedValue("maven.src.dir"); //NOI18N
        if (path == null) {
            logger.warn("Strange thing here. testsrc dir not found.");
            return null;
        }
        File fl = new File(path, "test/java"); //NOI18N
        return FileUtil.normalizeFile(fl).toURI();
    }
    
   public URI getAspectsDirectory() {
        if (getOriginalMavenProject().getBuild() != null) {
            String path = getOriginalMavenProject().getBuild().getAspectSourceDirectory();
            if (path != null) {
                return getDirURI(getPropertyResolver().resolveString(path));
            }
        }
        // this one should not fail
        String path = properties.getResolvedValue("maven.src.dir"); //NOI18N
        if (path == null) {
            logger.warn("Strange thing here. src dir not found.");
            return null;
        }
        // TODO - huh? what is the default location of the aspects? is there any?
        File fl = new File(path, "aspects"); //NOI18N
        return  FileUtil.normalizeFile(fl).toURI();
   }
   
   public URI getIntegrationTestsDirectory() {
       if (getOriginalMavenProject().getBuild() != null) {
           String path = getOriginalMavenProject().getBuild().getIntegrationUnitTestSourceDirectory();
           if (path != null) {
               return getDirURI(getPropertyResolver().resolveString(path));
           }
       }
       // this one should not fail
       String path = properties.getResolvedValue("maven.src.dir"); //NOI18N
       if (path == null) {
           logger.warn("Strange thing here. src dir not found.");
           return null;
       }
       // TODO - huh? what is the default location of the integration tests? is there any?
       File fl = new File(path, "test/integration"); //NOI18N
       return  FileUtil.normalizeFile(fl).toURI();
   }
   
   /**
    * URI denoted by the maven.war.src property in the project context.
    */
   public URI getWebAppDirectory() {
       String path = getPropertyResolver().getResolvedValue("maven.war.src"); //NOI18N
       return path == null ? null : getDirURI(path);
   }
   
   /**
    * URI denoted by the maven.ear.src property in the project context.
    */
   public URI getEarDirectory() {
       String path = getPropertyResolver().getResolvedValue("maven.ear.src"); //NOI18N
       return path == null ? null : getDirURI(path);
   }
   
   private URI getDirURI(String path) {
       File src = FileUtilities.resolveFilePath(FileUtil.toFile(getProjectDirectory()), path);
       return FileUtil.normalizeFile(src).toURI();
   }

    public URI getBuildClassesDir() {
        String path = properties.getResolvedValue("maven.build.dest");
        if (path != null) {
            File fl = new File(path);
            return FileUtil.normalizeFile(fl).toURI();
        }
        logger.warn("maven.build.dest not defined.");
        return null;
    }
    
   public URI getGeneratedSourcesDir() {
        String path = properties.getResolvedValue("maven.build.src");
        if (path != null) {
            File fl = new File(path);
            return FileUtil.normalizeFile(fl).toURI();
        }
        logger.warn("maven.build.src not defined.");
        return null;
    }    
    
    public URI getTestBuildClassesDir() {
        String path = properties.getResolvedValue("maven.test.dest");
        if (path != null) {
            File fl = new File(path);
            return FileUtil.normalizeFile(fl).toURI();
        }
        logger.warn("maven.test.dest not defined.");
        return null;
    }
    
    
    private Lookup createLookup() {
        staticLookup = Lookups.fixed(new Object[] {
            projectInfo,
            new MavenForBinaryQueryImpl(this),
            new ActionProviderImpl(this),
            new CustomizerProviderImpl(this),
            new LogicalViewProviderImpl(this),
            new ProjectOpenedHookImpl(this),
            new ClassPathProviderImpl(this),
            new MavenSharabilityQueryImpl(this),
            new MavenTestForSourceImpl(this),
//            new MavenFileBuiltQueryImpl(this),
            new SubprojectProviderImpl(this),
            new MavenSourcesImpl(this), 
            new RecommendedTemplatesImpl(),
            new WebModuleProviderImpl(this)
        });
        return staticLookup;
//        completeLookup = new ProjectLookup(staticLookup);
//        checkDynamicLookup();
//        return completeLookup;
    }
    
//    private void checkDynamicLookup() {
//        FileObject fo = FileUtilities.getFileObjectForProperty("maven.war.src", getPropertyResolver());
//        if (fo != null) {
//            completeLookup.setDynamicLookup(Lookups.fixed(new Object[] {
//                new WebModuleImpl(this)
//            }));
//        }
//    }
    
    public ProjectInformation getProjectInfo() {
        return projectInfo;
    }
    
   // Private innerclasses ----------------------------------------------------
    
    private final class Info implements ProjectInformation, IQueryErrorCallback {
        
        private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
        
        private String errorName;
        private boolean errorIcon;
        private String errorDescription;
        
        Info() {}
        
        public void reset() {
            firePropertyChange(ProjectInformation.PROP_DISPLAY_NAME);
            pcs.firePropertyChange(ProjectInformation.PROP_ICON, null, getIcon());
        }
        
        void firePropertyChange(String prop) {
            pcs.firePropertyChange(prop, null, null);
        }
        
        public String getName() {
            String toReturn = MavenProject.this.getName();
            return toReturn;
        }
        
        public String getDisplayName() {
            String toReturn = MavenProject.this.getOriginalMavenProject().getName();
            if (errorName != null) {
                return errorName;
            }
            return toReturn;
        }
        
        public Icon getIcon() {
            if (errorIcon) {
                return new ImageIcon(Utilities.mergeImages(MavenProject.this.getIcon(), Utilities.loadImage("org/mevenide/netbeans/project/resources/ResourceNotIncluded.gif"), 0, 0));
            }
            return new ImageIcon(MavenProject.this.getIcon());
        }
        
        public Project getProject() {
            return MavenProject.this;
        }
        
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            pcs.addPropertyChangeListener(listener);
        }
        
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            pcs.removePropertyChangeListener(listener);
        }
        
        /**
         * IQueryErrorCallback based method.
         */
        public void handleError(int errorNumber, Exception exception) {
            System.out.println("handling exception" + exception.getMessage());
            errorIcon = true;
            if (errorNumber == IQueryErrorCallback.ERROR_UNPARSABLE_POM) {
                errorName = "<Non-parseable POM file>";
            } 
            if (errorNumber == IQueryErrorCallback.ERROR_CANNOT_FIND_PARENT_POM) {
                errorName = "<Cannot find parent POM>";
            } 
            if (errorNumber == IQueryErrorCallback.ERROR_CANNOT_FIND_POM) {
                errorName = "<Cannot find POM>";
            } 
            if (exception != null) {
                errorDescription = exception.getMessage();
            }
//            logger.error("error while reading context", exception);
        }
        public void discardError(int errorNumber) {
            errorIcon = false;
            errorName = null;
            errorDescription = null;
        }
        
        public String getErrorDescription() {
            return errorDescription;
        }
    }    
 
    // needs to be binary sorted;
    private static final String[] DEFAULT_FILES = new String[] {
        "build.properties",
        "project.properties",
        "project.xml"
    };
    private static final String[] USER_DIR_FILES = new String[] {
        "build.properties"
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
            MavenProject.this.firePropertyChange(PROP_PROJECT);
        }
        
    }
    
    private class ProjectLookup extends ProxyLookup {
        private Lookup stat;
        public ProjectLookup(Lookup staticLookup) {
            super(new Lookup[] {staticLookup});
            stat = staticLookup;
        }
        
        public final void setDynamicLookup(Lookup look) {
            setLookups(new Lookup[] {stat, look});
        }        
    }
    
}
