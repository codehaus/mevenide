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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;
import org.mevenide.ui.eclipse.sync.model.properties.ExcludePropertySource;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ExcludeNode extends AbstractSynchronizationNode implements ISelectableNode, IAdaptable {
	
	private DirectoryNode parentNode;
	
	private String excludePattern;
	
	public ExcludeNode(DirectoryNode parentNode, String excludePattern) {
		this.parentNode = parentNode;
		this.excludePattern = excludePattern;
	}
	
	public ISynchronizationNode getParent() {
		return parentNode;
	}

	public Object getData() {
		return excludePattern;
	}

	public ISynchronizationNode[] getChildren() {
		return null;
	}
	
	public boolean hasChildren() {
		return false;
	}
	
	public boolean select(int direction) {
		return parentNode.select(direction);
	}
	
	public String toString() {
		return excludePattern;
	}
	
	public Object getAdapter(Class adapter) {
        if ( IPropertySource.class.equals(adapter) ) {
            return new ExcludePropertySource(((Directory) parentNode.getData()).getPath(), this.excludePattern);
        }
        return null;
    }
}
