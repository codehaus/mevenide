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

package org.mevenide.netbeans.project.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
class MavenProjectChildren extends Children.Keys
{
    
    private static final Object KEY_SOURCE_DIR = "srcDir"; // NOI18N
    private static final Object KEY_TEST_SOURCE_DIR = "testSrcDir"; // NOI18N
    private static final Object KEY_ASPECT_SOURCE_DIR = "aspectSrcDir"; // NOI18N
    private static final Object KEY_INTEG_TEST_SOURCE_DIR = "integrationTestSrcDir"; // NOI18N
    private static final Object KEY_JELLY_SCRIPT = "jellyScript"; //NOI18N
    
    private MavenProject project;
    
    public MavenProjectChildren(MavenProject project)
    {
        this.project = project;
    }
    
    protected void addNotify()
    {
        super.addNotify();
        List list = new ArrayList();
        Project proj = project.getOriginalMavenProject();
        Build build = proj.getBuild();
        if (build != null) {
            if (build.getAspectSourceDirectory() != null) {
                list.add(KEY_ASPECT_SOURCE_DIR);
            }
            if (build.getUnitTestSourceDirectory() != null) {
                list.add(KEY_TEST_SOURCE_DIR);
            }
            if (build.getSourceDirectory() != null) {
                list.add(KEY_SOURCE_DIR);
            }
            if (build.getIntegrationUnitTestSourceDirectory() != null) {
                list.add(KEY_INTEG_TEST_SOURCE_DIR);
            }
        }
        FileObject fo = project.getProjectDirectory().getFileObject("plugin", "jelly");
        if (fo != null) {
            list.add(KEY_JELLY_SCRIPT);
        }
        setKeys(list);
    }
    
    protected void removeNotify()
    {
        setKeys(Collections.EMPTY_SET);
        super.removeNotify();
    }
    
    protected Node[] createNodes(Object key)
    {
        Node n = null;
        Project proj = project.getOriginalMavenProject();
        if (key == KEY_SOURCE_DIR)
        {
            String relPath = proj.getBuild().getSourceDirectory();
            if (relPath == null) {
                //TODO better visual representation for the sources when not defined..
                n = null;
            } else {
                n = new PackageRootNode( getFolder(relPath), "srcDir", "Sources" ); // NOI18N
            }
        } 
        else if (key == KEY_TEST_SOURCE_DIR)
        {
            String relPath = proj.getBuild().getUnitTestSourceDirectory();
            if (relPath == null) {
                n = null;
            } else {
                n = new PackageRootNode( getFolder(relPath), "testSrcDir", "Test Sources" ); // NOI18N
            }
        }
        else if (key == KEY_ASPECT_SOURCE_DIR)
        {
            String relPath = proj.getBuild().getAspectSourceDirectory();
            if (relPath == null) {
                n = null;
            } else {
                n = new PackageRootNode( getFolder(relPath), "aspectSrcDir", "Aspect Sources" ); // NOI18N
            }
        }
        else if (key == KEY_ASPECT_SOURCE_DIR)
        {
            String relPath = proj.getBuild().getIntegrationUnitTestSourceDirectory();
            if (relPath == null) {
                n = null;
            } else {
                n = new PackageRootNode( getFolder(relPath), "integrationSrcDir", "Integration Test Sources" ); // NOI18N
            }
        }
        else if (key == KEY_JELLY_SCRIPT) {
            n = new PluginScriptNode(project.getProjectDirectory());
        }
        return n == null ? new Node[0] : new Node[] {n};
    }
    
    private DataFolder getFolder(String relPath) {
        FileObject folder = FileUtilities.findFolder(project.getProjectDirectory(), relPath);
        if (folder != null) {
            return DataFolder.findFolder(folder);
        } 
        //TODO - create the folder if it doesn't exist? and do it here? I'd rather do it when opening project..
        return null;
    }

}
