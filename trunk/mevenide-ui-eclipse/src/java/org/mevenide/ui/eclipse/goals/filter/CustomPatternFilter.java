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
package org.mevenide.ui.eclipse.goals.filter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.mevenide.ui.eclipse.goals.model.Element;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: CustomPatternFilter.java,v 1.1 31 mars 2004 Exp gdodinet 
 * 
 */
public class CustomPatternFilter extends ViewerFilter {
	private List customFilters = new ArrayList();
	
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if ( !(element instanceof Element) ) {
			return false;
		}
		if ( customFilters.size() > 0 ) {
			for (int j = 0; j < customFilters.size(); j++) {
				if ( ((Element) element).getFullyQualifiedName().startsWith((String) customFilters.get(j)) 
						|| ((Element) element).getName().startsWith((String) customFilters.get(j)) ) {
					return false;
				}
			}
		}
		return true;
	}
	

}
