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
package org.mevenide.ui.eclipse.sync.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Resource;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.ui.views.properties.IPropertySource;
import org.mevenide.ui.eclipse.editors.properties.ResourcePropertySource;
import org.mevenide.ui.eclipse.util.SourceDirectoryTypeUtil;
import org.mevenide.util.MevenideUtils;


/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DirectoryMappingNode extends AbstractArtifactMappingNode {
    private static Log log = LogFactory.getLog(DirectoryMappingNode.class);
    
    private boolean conflicting;
    private boolean overrideSameValue;
    
    public Object getAdapter(Class adapter) {
        if ( adapter == IPropertySource.class ) {
			if ( artifact instanceof Resource ) {
				return new ResourcePropertySource((Resource) artifact);
			}
			else {
				//instanceof Directory
				DirectoryPropertySource directoryPropertySource = null;
				if ( artifact != null ) {
					directoryPropertySource = new DirectoryPropertySource((Directory) artifact);
				}
				else {
					directoryPropertySource = new DirectoryPropertySource((Directory) resolvedArtifact);
				}
				return directoryPropertySource;
			}
		}
        return null;
    }
    
   
    public String getLabel() {
    	if ( resolvedArtifact != null ) {
            return SourceDirectoryTypeUtil.stripBasedir(((Directory) resolvedArtifact).getDisplayPath());
        }
        if ( artifact instanceof Resource ) {
            return SourceDirectoryTypeUtil.stripBasedir(((Resource) artifact).getDirectory());
        }
        if ( artifact instanceof Directory ) {
            return SourceDirectoryTypeUtil.stripBasedir(((Directory) artifact).getDisplayPath());   
        }
        return "Unresolved";
    }
    
    public void setResolvedDirectory(Directory directory) {
        this.resolvedArtifact = directory;
    }
    
	/**
	 * either a Resource or Directory 
	 */
	public void setArtifact(Object object) {
		this.artifact = object;
	}

    public void setIdeEntry(IClasspathEntry entry) {
        this.ideEntry = entry;
    }
   
    public void setParent(DirectoryMappingNodeContainer container) {
        this.parent = container;
    }

	public int getChangeDirection() {
		if ( conflicting || overrideSameValue ) {
			return EclipseContainerContainer.CONFLICTING;
		}
		
        if ( artifact == null ) {
        	return EclipseContainerContainer.OUTGOING;
		}
        
        if ( resolvedArtifact == null ) {
			return EclipseContainerContainer.INCOMING;
		}
		
		if ( artifact instanceof Directory 
				&& MevenideUtils.notEquivalent(((Directory) artifact).getType(), ((Directory) resolvedArtifact).getType()) ) {
			return EclipseContainerContainer.CONFLICTING;
		} 
        return EclipseContainerContainer.NO_CHANGE;
		
    }

	public boolean isConflicting() {
		return conflicting;
	}

	public void setConflicting(boolean conflicting) {
		this.conflicting = conflicting;
	}

	public boolean isOverrideSameValue() {
		return overrideSameValue;
	}

	public void setOverrideSameValue(boolean overrideSameValue) {
		this.overrideSameValue = overrideSameValue;
	}

}
