/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.ui.eclipse.sync.view;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Resource;
import org.mevenide.project.dependency.DependencyUtil;
import org.mevenide.project.resource.ResourceUtil;
import org.mevenide.project.source.SourceDirectoryUtil;
import org.mevenide.ui.eclipse.sync.model.DependencyMappingNode;
import org.mevenide.ui.eclipse.sync.model.Directory;
import org.mevenide.ui.eclipse.sync.model.DirectoryMappingNode;
import org.mevenide.ui.eclipse.sync.model.IArtifactMappingNode;


/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DecoratorManager {
    private static final Log log = LogFactory.getLog(DecoratorManager.class);
    
    private static DecoratorManager manager = new DecoratorManager();
    
    public static DecoratorManager getManager() {
        return manager;
    }
    
    public boolean isInherited(IArtifactMappingNode node) {
        try {
            if ( node instanceof DirectoryMappingNode ) {
                if ( node.getArtifact() != null ) {
                    if ( node.getArtifact() instanceof Directory ) {
                        return !SourceDirectoryUtil.isSourceDirectoryPresent(node.getParent().getPrimaryPom(), ((Directory) node.getArtifact()).getPath().replaceAll("\\\\", "/"));
                    }
                    //node instanceof Resource 
                    return !ResourceUtil.isResourcePresent(node.getParent().getPrimaryPom(), ((Resource) node.getArtifact()).getDirectory());
                }
                return false;
            }
            else  {
                //node instanceof DependencyMappingNode
                DependencyMappingNode dependencyMappingNode = (DependencyMappingNode) node;
                if ( dependencyMappingNode.getArtifact() != null ) {
                    return !DependencyUtil.isDependencyPresent(node.getParent().getPrimaryPom(), (Dependency) dependencyMappingNode.getArtifact());
                }
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            return false;
        }
    }
    
    public boolean isMappingComplete(IArtifactMappingNode node) {
        return true;
    }
}
