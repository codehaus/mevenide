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

import org.apache.maven.repository.Artifact;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.mevenide.ui.eclipse.sync.model.MavenArtifactNode;
import org.mevenide.util.StringUtils;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: MavenArtifactNodeFilter.java,v 1.1 24 avr. 2004 Exp gdodinet 
 * 
 */
public class MavenArtifactNodeFilter extends ViewerFilter {
	
	static final String APPLY_FILTERS_KEY = "MavenArtifactNodeFilter.APPLY_FILTERS_KEY";
	static final String GROUP_ID_FILTER = "MavenArtifactNodeFilter.GROUP_ID_FILTER";
	
	private boolean filterMavenArtifacts;
	
	private String groupIdFilter;
	
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if ( filterMavenArtifacts && element instanceof MavenArtifactNode ) {
			if ( !StringUtils.isNull(groupIdFilter) ) {
				String[] groupFilters = org.apache.commons.lang.StringUtils.split(groupIdFilter, ",");
				for (int i = 0; i < groupFilters.length; i++) {
					Artifact artifact = (Artifact) ((MavenArtifactNode) element).getData();
					if ( groupFilters[i].equals(artifact.getDependency().getGroupId()) ) {
						return false;
					}
				}
			}
			else {
				return false;
			}
		}
		return true;
	}
	
	public void setFilterMavenArtifacts(boolean filterMavenArtifacts) {
		this.filterMavenArtifacts = filterMavenArtifacts;
	}
}
