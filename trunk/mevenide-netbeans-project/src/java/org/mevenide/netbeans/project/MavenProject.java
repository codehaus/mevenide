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
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.apache.maven.project.Project;
import org.mevenide.util.DefaultProjectUnmarshaller;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenProject implements org.netbeans.api.project.Project
{
    private Project project;
    private File file;
    private FileObject fileObject;
    private Image icon;
    private Lookup lookup;
    /** Creates a new instance of MavenProject */
    MavenProject(FileObject projectFO, File projectFile) throws Exception
    {
        DefaultProjectUnmarshaller unmars = new DefaultProjectUnmarshaller();
        file = projectFile;
        fileObject = projectFO;
        project = unmars.parse(new FileReader(projectFile));
        if (project.getId() == null && (project.getPomVersion() == null || project.getExtend() == null)) {
            throw new IOException("Not a maven project file.");
        }
    }
    
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener)
    {
    }
    
    public String getDisplayName()
    {
        String displayName = project.getName();
        if (displayName == null) {
            displayName = "<Maven project with no name>";
        }
        return displayName;
    }
    
    public Project getOriginalMavenProject() {
        return project;
    }
    
    public Image getIcon()
    {
        if (icon == null) {
            icon = Utilities.loadImage("org/mevenide/netbeans/projects/resources/MavenIcon.gif");
        }
        return icon;
    }
    
    public Lookup getLookup()
    {
        if (lookup == null) {
            lookup = createLookup();
        }
        return lookup;
    }
    
    public String getName()
    {
        return project.getId();
    }
    
    public FileObject getProjectDirectory()
    {
        return fileObject.getParent();
    }
    
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener)
    {
    }
   
    private Lookup createLookup() {
        return Lookups.fixed(new Object[] {
            new ActionProviderImpl(this),
            new CustomizerProviderImpl(this),
            new LogicalViewProviderImpl(this),
            
        });
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
    
}
