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

import org.apache.maven.project.Dependency;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.views.properties.IPropertySource;
import org.mevenide.ui.eclipse.editors.properties.DependencyPropertySource;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyMappingNode extends AbstractArtifactMappingNode implements IPropertyChangeListener {
    
    private Dependency dirtyDependency;
    
    public String getLabel() {
        if ( (parent.getDirection() & EclipseContainerContainer.OUTGOING) != 0 ) {
            Dependency displayDependency = null;
            if ( dirtyDependency != null ) {
                displayDependency = dirtyDependency ;
            }
            else {
                displayDependency = ((Dependency) resolvedArtifact); 
            }
            return displayDependency.getArtifactId() + "-" + displayDependency.getVersion();
		}
        if ( (parent.getDirection() & EclipseContainerContainer.INCOMING) != 0 ) {
            return ((Dependency) artifact).getGroupId() + ":" + ((Dependency) artifact).getArtifactId(); 
        }
		//NO_CHANGE or CONFLICTING
		if ( artifact != null ) {
			return ((Dependency) artifact).getGroupId() + ":" + ((Dependency) artifact).getArtifactId();
		}
		return "Unresolved";
    }
   
    public Object getAdapter(Class adapter) {
        if ( adapter == IPropertySource.class ) {
            DependencyPropertySource propertySource = null;
            if ( artifact == null ) {
				if ( dirtyDependency != null ) {
					propertySource = new DependencyPropertySource((Dependency) resolvedArtifact);
				}
				else {
	                propertySource = new DependencyPropertySource((Dependency) resolvedArtifact);
				}
            }
			else {
            	propertySource = new DependencyPropertySource((Dependency) artifact);
			}
            propertySource.addPropertyChangeListener(this);
            return propertySource;
        }
        return null;
    }

    public int getChangeDirection() {
        if ( resolvedArtifact == null ) {
            return EclipseContainerContainer.INCOMING;
        }
        if ( artifact == null ) {
            return EclipseContainerContainer.OUTGOING;
        }
        if ( !((Dependency) artifact).getVersion().equals(((Dependency) resolvedArtifact).getVersion()))  {
            //donot compare groupId b/c in most case it may not be resolved
            //|| !dependency.getGroupId().equals(resolvedDependency.getGroupId())
            return EclipseContainerContainer.CONFLICTING;
        }
        return EclipseContainerContainer.NO_CHANGE;
    }
    
    public void setDependency(Dependency d) {
        artifact = d;
    }
    public void setResolvedDependency(Dependency resolvedDependency) {
        this.resolvedArtifact = resolvedDependency;
    }
    public void setIdeEntry(Object ideEntry) {
        this.ideEntry = ideEntry;
    }
    public void propertyChange(PropertyChangeEvent event) {
        setDirtyDependency((Dependency)((DependencyPropertySource)event.getSource()).getSource());
    }
    public Dependency getDirtyDependency() {
        return this.dirtyDependency;
    }
    public void setDirtyDependency(Dependency dirtyDependency) {
        this.dirtyDependency = dirtyDependency;
    }
}
