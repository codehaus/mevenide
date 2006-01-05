/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.mevenide.ui.eclipse.sync.model.EclipseProjectNode;
import org.mevenide.ui.eclipse.sync.model.ISelectableNode;
import org.mevenide.ui.eclipse.sync.model.ISynchronizationNode;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: SynchronizationNodeFilter.java,v 1.1 15 avr. 2004 Exp gdodinet 
 * 
 */
public class SynchronizationNodeFilter extends ViewerFilter {
	private int direction;
	
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		//let instances pass if they're instances of nether ISynchronizationNode nor ISelectableNode
		if ( !(element instanceof ISynchronizationNode || element instanceof ISelectableNode) ) {
			return true;
		}
		//top node should always pass through the filter 
		if ( element instanceof EclipseProjectNode ) {
			return true;
		}
		//else visit nodes 
		ISelectableNode node = (ISelectableNode) element;
		return node.select(direction);
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
}
