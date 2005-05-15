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

import java.util.ArrayList;
import java.util.List;

import org.mevenide.ui.eclipse.sync.event.ISynchronizationNodeListener;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class AbstractSynchronizationNode implements ISynchronizationNode {
	
	private List synchronizationNodesListener = new ArrayList();
	
	
	public void addNodeListener(ISynchronizationNodeListener node) {
		synchronizationNodesListener.add(node);
	}
	
	public void removeNodeListener(ISynchronizationNodeListener node) {
		synchronizationNodesListener.remove(node);
	}
	
	public void setSynchronizationNodesListener(List synchronizationNodesListener) {
		this.synchronizationNodesListener = synchronizationNodesListener;
	}
	
	protected List getSynchronizationNodesListener() {
		return synchronizationNodesListener;
	}
}
