/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IProjectContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.netbeans.project.classpath.ClassPathProviderImpl;
import org.mevenide.netbeans.project.queries.MavenForBinaryQueryImpl;

import org.mevenide.netbeans.project.queries.MavenSharabilityQueryImpl;
import org.mevenide.project.DefaultProjectContext;
import org.mevenide.properties.IPropertyLocator;
import org.mevenide.properties.IPropertyResolver;
import org.mevenide.properties.resolver.PropertyLocatorFactory;
import org.mevenide.properties.resolver.PropertyResolverFactory;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.openide.filesystems.*;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;


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
    private Image icon;
    private Lookup lookup;
    private PropertyChangeSupport support;
    private Updater updater;
    /** Creates a new instance of MavenProject */
    MavenProject(FileObject projectFO, File projectFile) throws Exception {
        support = new PropertyChangeSupport(this);
        file = projectFile;
        fileObject = projectFO;
        updater = new Updater();
        File projectDir = FileUtil.toFile(fileObject.getParent());
        queryContext = new DefaultQueryContext(projectDir);
        properties = PropertyResolverFactory.getFactory().createContextBasedResolver(queryContext);
        propertyLocator = PropertyLocatorFactory.getFactory().createContextBasedLocator(queryContext);
        IProjectContext prContext = new DefaultProjectContext(queryContext, properties);
        ((DefaultQueryContext)queryContext).initializeProjectContext(prContext);
        locFinder = new LocationFinderAggregator(queryContext);
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
            support.firePropertyChange(new PropertyChangeEvent(this, property, null, null));
        }
    }
    
    public String getDisplayName() {
        String displayName = getOriginalMavenProject().getName();
        if (displayName == null) {
            displayName = "<Maven project with no name>";
        }
        return displayName;
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
    
    Updater getUpdater() {
        return updater;
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
        return getOriginalMavenProject().getId();
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
        String path = properties.getResolvedValue("maven.src.dir");
        if (path == null) {
            logger.warn("Strange thing here. testsrc dir not found.");
            return null;
        }
        File fl = new File(path, "test/java");
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
        String path = properties.getResolvedValue("maven.src.dir");
        if (path == null) {
            logger.warn("Strange thing here. src dir not found.");
            return null;
        }
        // TODO - huh? what is the default location of the aspects? is there any?
        File fl = new File(path, "aspects");
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
        String path = properties.getResolvedValue("maven.src.dir");
        if (path == null) {
            logger.warn("Strange thing here. src dir not found.");
            return null;
        }
        // TODO - huh? what is the default location of the integration tests? is there any?
        File fl = new File(path, "test/integration");
        return  FileUtil.normalizeFile(fl).toURI();
    }       
    
   private URI getDirURI(String path) {
       File parent = FileUtil.toFile(getProjectDirectory());
       File src = new File(parent.getAbsolutePath(), path);
       if (!src.exists()) {
           src = new File(path);
           if (!src.exists()) {
               // the ultimate fallback is the relative path..
               src = new File(parent.getAbsolutePath(), path);
           }
       }
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
        return Lookups.fixed(new Object[] {
            new MavenForBinaryQueryImpl(this),
            new ActionProviderImpl(this),
            new CustomizerProviderImpl(this),
            new LogicalViewProviderImpl(this),
            new ProjectOpenedHookImpl(this),
            new ClassPathProviderImpl(this),
            new MavenSharabilityQueryImpl(this),
//            new MavenFileBuiltQueryImpl(this),
            new SubprojectProviderImpl(this),
            new MavenSourcesImpl(this), 
            new RecommendedTemplatesImpl()
        });
    }
    
   // Private innerclasses ----------------------------------------------------
    
    private final class Info implements ProjectInformation {
        
        private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
        
        Info() {}
        
        void firePropertyChange(String prop) {
            pcs.firePropertyChange(prop, null, null);
        }
        
        public String getName() {
            return MavenProject.this.getName();
        }
        
        public String getDisplayName() {
            return MavenProject.this.getDisplayName();
        }
        
        public Icon getIcon() {
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
        
    }    
 
    
    private class Updater implements FileChangeListener {
        
        private FileObject fileObject;
        
        Updater() {
        }
        
        public void fileAttributeChanged(FileAttributeEvent fileAttributeEvent) {
        }
        
        public void fileChanged(FileEvent fileEvent) {
            firePropertyChange(PROP_PROJECT);
        }
        
        public void fileDataCreated(FileEvent fileEvent) {
            //TODO shall also include the parent of the pom if available..
            String nameExt = fileEvent.getFile().getNameExt();
            if (nameExt.equals("project.xml") ||
                nameExt.equals("project.properties") ||
                nameExt.equals("build.properties")) {
                    File parent = FileUtil.toFile(fileEvent.getFile().getParent());
                    if (parent.equals(queryContext.getUserDirectory()) && nameExt.equals("build.properties")) {
                        fileEvent.getFile().addFileChangeListener(this);
                    }
                    if (parent.equals(queryContext.getProjectDirectory())) {
                        fileEvent.getFile().addFileChangeListener(this);
                    }
                    firePropertyChange(PROP_PROJECT);
            }
        }
        
        public void fileDeleted(FileEvent fileEvent) {
            fileEvent.getFile().removeFileChangeListener(this);
            firePropertyChange(PROP_PROJECT);
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
}
