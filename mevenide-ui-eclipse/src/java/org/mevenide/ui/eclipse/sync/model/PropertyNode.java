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

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: PropertyNode.java,v 1.1 12 avr. 2004 Exp gdodinet 
 * 
 */
public class PropertyNode extends AbstractSynchronizationNode implements ISelectableNode {
	
	private MavenArtifactNode parentNode;
	
	private String key;
	private String value;
	
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
		// TODO Auto-generated method stub
		return null;
	}
	public Object getData() {
		// TODO Auto-generated method stub
		return null;
	}
	public ISynchronizationNode getParent() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
