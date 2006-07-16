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

import org.codehaus.mevenide.netbeans.nodes.MavenProjectNode;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * provider of logical view, meaning the top node in the projects tab.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class LogicalViewProviderImpl implements LogicalViewProvider
{
    private NbMavenProject project;
    /** Creates a new instance of LogicalViewProviderImpl */
    public LogicalViewProviderImpl(NbMavenProject proj) {
        project = proj;
    }
    
    /**
     * create the root node for maven projects..
     */
    public Node createLogicalView()
    {
        return new MavenProjectNode(createLookup(project), project);
    }

    private static Lookup createLookup( NbMavenProject project ) {
        DataFolder rootFolder = DataFolder.findFolder( project.getProjectDirectory() );
        // XXX Remove root folder after FindAction rewrite
        return Lookups.fixed( new Object[] { project, rootFolder } );
    }
    
    /**
     * TODO this is probably good for the Select in Project view action..
     */
    public Node findPath(Node node, Object target) {
        NbMavenProject proj = (NbMavenProject)node.getLookup().lookup(NbMavenProject.class );
        if ( proj == null ) {
            return null;
        }
        
        if ( target instanceof FileObject ) {
            FileObject fo = (FileObject)target;

            Project owner = FileOwnerQuery.getOwner( fo );
            if ( !proj.equals( owner ) ) {
                return null; // Don't waste time if project does not own the fo
            }
            Node[] nodes = node.getChildren().getNodes(true);
            for (int i = 0; i < nodes.length; i++) {
                Node result = PackageView.findPath(nodes[i], target);
                if (result != null) {
                    return result;
                }
            }
        }
        
        return null;
    }
    
}
