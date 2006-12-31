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

import org.mevenide.netbeans.api.project.MavenProject;
import org.mevenide.netbeans.project.nodes.MavenProjectNode;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class LogicalViewProviderImpl implements LogicalViewProvider
{
    private MavenProject project;
    /** Creates a new instance of LogicalViewProviderImpl */
    public LogicalViewProviderImpl(MavenProject proj)
    {
        project = proj;
    }
    
    public Node createLogicalView()
    {
        return new MavenProjectNode(createLookup(project), project);
    }

    private static Lookup createLookup( MavenProject project ) {
        DataFolder rootFolder = DataFolder.findFolder( project.getProjectDirectory() );
        // XXX Remove root folder after FindAction rewrite
        return Lookups.fixed( new Object[] { project, rootFolder } );
    }
    
    public Node findPath(Node node, Object target) {
        MavenProject proj = (MavenProject)node.getLookup().lookup( MavenProject.class );
        if ( proj == null ) {
            return null;
        }
        
        if ( target instanceof FileObject ) {
            FileObject fo = (FileObject)target;

            Project owner = FileOwnerQuery.getOwner( fo );
            if ( !proj.equals( owner ) ) {
                return null; // Don't waste time if project does not own the fo
            }

//            Sources sources = ProjectUtils.getSources( project );
//            SourceGroup[] groups = sources.getSourceGroups( JavaProjectConstants.SOURCES_TYPE_JAVA );
//            for( int i = 0; i < groups.length; i++ ) {
//                FileObject groupRoot = groups[i].getRootFolder();
//                if ( FileUtil.isParentOf( groupRoot, fo ) && groups[i].contains( fo ) ) {
//                    // The group contains the object
//
//                    String relPath = FileUtil.getRelativePath( groupRoot, fo );
//                    int lastSlashIndex = relPath.lastIndexOf( '/' ); // NOI18N
//                    
//                    String[] path = null;
//                    if ( lastSlashIndex == -1 ) {
//                        path = new String[] { groups[i].getRootFolder().getNameExt(),
//                                              fo.getName() };
//                    }
//                    else {
//                        String packageName = relPath.substring( 0, lastSlashIndex ).replace( '/', '.' ); // NOI18N
//                        path = new String[] { groups[i].getRootFolder().getNameExt(),
//                                              packageName, 
//                                              fo.getName() };			
//                    } 
//                    try {
//                        return NodeOp.findPath( root, path );
//                    }
//                    catch ( NodeNotFoundException e ) {
//                        return null;
//                    }
//                }
//            }                
        }

        return null;
    }
    
}
