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
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IProjectContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.project.DefaultProjectContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.netbeans.project.classpath.ClassPathProviderImpl;
import org.mevenide.netbeans.project.queries.MavenFileBuiltQueryImpl;
import org.mevenide.netbeans.project.queries.MavenSharabilityQueryImpl;
import org.mevenide.netbeans.project.queries.MavenForBinaryQueryImpl;
import org.mevenide.properties.IPropertyResolver;
import org.mevenide.properties.resolver.PropertyResolverFactory;
import org.mevenide.util.JDOMProjectUnmarshaller;
import org.netbeans.spi.project.ProjectInformation;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenProject implements org.netbeans.api.project.Project {
    private static final Log logger = LogFactory.getLog(MavenProject.class);
    
    public static final String PROP_PROJECT = "MavenProject"; //NOI18N
    
    private File file;
    private FileObject fileObject;
    private IPropertyResolver properties;
    private IQueryContext queryContext;
    private ILocationFinder locFinder;
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
        locFinder = new LocationFinderAggregator(queryContext);
        IProjectContext prContext = new DefaultProjectContext(queryContext, properties);
        ((DefaultQueryContext)queryContext).initializeProjectContext(prContext);
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
    
    public Project getOriginalMavenProject() {
        return queryContext.getPOMContext().getFinalProject();
    }
    
    public IPropertyResolver getPropertyResolver() {
        return properties;
    }
    
    public ILocationFinder getLocFinder() {
        return locFinder;
    }
    
    IQueryContext getContext() {
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
                File parent = FileUtil.toFile(getProjectDirectory());
                File src = new File(parent.getAbsolutePath() + "/" + path);
                return FileUtil.normalizeFile(src).toURI();
            }
        }
        // this one should not fail
        String path = properties.getResolvedValue("maven.src.dir");
        if (path == null) {
            logger.warn("Strange thing here. src dir not found.");
            return null;
        }
        File fl = new File(path, "java");
        return fl.toURI();
    }
    
    public URI getTestSrcDirectory() {
        if (getOriginalMavenProject().getBuild() != null) {
            String path = getOriginalMavenProject().getBuild().getUnitTestSourceDirectory();
            if (path != null) {
                File parent = FileUtil.toFile(getProjectDirectory());
                File src = new File(parent.getAbsolutePath() + "/" + path);
                return FileUtil.normalizeFile(src).toURI();
            }
        }
        // this one should not fail
        String path = properties.getResolvedValue("maven.src.dir");
        if (path == null) {
            logger.warn("Strange thing here. testsrc dir not found.");
            return null;
        }
        File fl = new File(path, "test/java");
        return fl.toURI();
    }
    
    public URI getBuildClassesDir() {
        String path = properties.getResolvedValue("maven.build.dest");
        if (path != null) {
            File fl = new File(path);
            return fl.toURI();
        }
        logger.warn("maven.build.dest not defined.");
        return null;
    }
    
    public URI getTestBuildClassesDir() {
        String path = properties.getResolvedValue("maven.test.dest");
        if (path != null) {
            File fl = new File(path);
            return fl.toURI();
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
            new MavenFileBuiltQueryImpl(this),
            new SubprojectProviderImpl(this)
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
        
        public org.netbeans.api.project.Project getProject() {
            return MavenProject.this;
        }
        
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            pcs.addPropertyChangeListener(listener);
        }
        
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            pcs.removePropertyChangeListener(listener);
        }
        
    }    
    //    private Lookup createLookup(ExtensibleMetadataProvider emp) {
    //        SubprojectProvider spp = refHelper.createSubprojectProvider();
    //        FileBuiltQueryImplementation fileBuilt = new GlobFileBuiltQuery(helper, new String[] {
    //            "${src.dir}/*.java", // NOI18N
    //            "${test.src.dir}/*.java", // NOI18N
    //        }, new String[] {
    //            "${build.classes.dir}/*.class", // NOI18N
    //            "${build.test.classes.dir}/*.class", // NOI18N
    //        });
    //        return Lookups.fixed(new Object[] {
    //            emp,
    //            spp,
    //            new J2SEActionProvider( this, helper ),
    //            new J2SEPhysicalViewProvider(this, helper, spp),
    //            new J2SECustomizerProvider( this, helper, refHelper ),
    //            new ClassPathProviderImpl(helper),
    //            new CompiledSourceForBinaryQuery(helper),
    //            new JavadocForBinaryQueryImpl(helper),
    //            new AntArtifactProviderImpl(),
    //            new ProjectXmlSavedHookImpl(),
    //            new ProjectOpenedHookImpl(),
    //            new UnitTestForSourceQueryImpl(helper),
    //            fileBuilt,
    //        });
    //    }
    //
    //    public void addPropertyChangeListener(PropertyChangeListener listener) {
    //        pcs.addPropertyChangeListener(listener);
    //    }
    //
    //    public void removePropertyChangeListener(PropertyChangeListener listener) {
    //        pcs.removePropertyChangeListener(listener);
    //    }
    //
    
    private class Updater implements FileChangeListener {
        
        private FileObject fileObject;
        
        Updater() {
        }
        
        public void fileAttributeChanged(org.openide.filesystems.FileAttributeEvent fileAttributeEvent) {
        }
        
        public void fileChanged(org.openide.filesystems.FileEvent fileEvent) {
            firePropertyChange(PROP_PROJECT);
        }
        
        public void fileDataCreated(org.openide.filesystems.FileEvent fileEvent) {
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
        
        public void fileDeleted(org.openide.filesystems.FileEvent fileEvent) {
            fileEvent.getFile().removeFileChangeListener(this);
            firePropertyChange(PROP_PROJECT);
        }
        
        public void fileFolderCreated(org.openide.filesystems.FileEvent fileEvent) {
            firePropertyChange(PROP_PROJECT);
        }
        
        public void fileRenamed(org.openide.filesystems.FileRenameEvent fileRenameEvent) {
        }
        
    }
}
