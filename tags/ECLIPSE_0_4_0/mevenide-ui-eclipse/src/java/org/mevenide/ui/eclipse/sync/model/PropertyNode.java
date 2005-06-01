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
import org.apache.maven.repository.Artifact;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.views.properties.IPropertySource;
import org.mevenide.ui.eclipse.adapters.properties.PropertyProxy;
import org.mevenide.ui.eclipse.sync.model.properties.ReadOnlyPropertyProxy;
import org.mevenide.util.MevenideUtils;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: PropertyNode.java,v 1.1 12 avr. 2004 Exp gdodinet 
 * 
 */
public class PropertyNode extends AbstractSynchronizationNode implements ISelectableNode, IAdaptable, IPropertyChangeListener {
	
	private MavenArtifactNode parentNode;
	
	private String key;
	private String value;
	
	public PropertyNode(MavenArtifactNode parentNode, String key, String value) {
		this.parentNode = parentNode;
		this.key = key;
		this.value = value;
	}
	
	public boolean select(int direction) {
		return ((MavenArtifactNode) getParent()).select(direction);
	}
	
	public boolean equals(Object obj) {
		if ( !(obj instanceof PropertyNode) ) {
			return false;
		}
		PropertyNode node = (PropertyNode) obj;
		return key.equals(node.key) 
		       && value.equals(node.value)
			   && parentNode.equals(node.parentNode);
	}
	public ISynchronizationNode[] getChildren() {
		return null;
	}
	public Object getData() {
		return toString();
	}
	public ISynchronizationNode getParent() {
		return parentNode;
	}
	public boolean hasChildren() {
		return false;
	}
	public String toString() {
		return key + MevenideUtils.PROPERTY_SEPARATOR + value;
	}
	
	public Object getAdapter(Class adapter) {
        if ( IPropertySource.class.equals(adapter) ) {
            if ( ((MavenArtifactNode) getParent()).getDirection() == ISelectableNode.INCOMING_DIRECTION ) {
                return new ReadOnlyPropertyProxy(this.key, this.value);
            }
            if ( ((MavenArtifactNode) getParent()).getDirection() == ISelectableNode.OUTGOING_DIRECTION ) {
                PropertyProxy proxy = new PropertyProxy(this.key, this.value);
                proxy.addPropertyChangeListener(this); 
                return proxy;
            }
        }
        return null;
    }
    
    public void propertyChange(PropertyChangeEvent event) {
    	String oldKey = null;
        if ( PropertyProxy.PROPERTY_NAME.equals(event.getProperty()) ) {
			key = (String) event.getNewValue();
			oldKey = (String) event.getOldValue();
		}
		if ( PropertyProxy.PROPERTY_VALUE.equals(event.getProperty()) ) {
			value = (String) event.getNewValue();
			oldKey = key;
		}
		Dependency dependency = ((Artifact) parentNode.getData()).getDependency();
		dependency.resolvedProperties().remove(oldKey);
		dependency.resolvedProperties().put(key, value);
		propagateNodeChangeEvent();
    }
    
    protected void propagateNodeChangeEvent() {
    	((EclipseProjectNode) getParent().getParent().getParent()).fireNodeChanged(this);
	}
}
