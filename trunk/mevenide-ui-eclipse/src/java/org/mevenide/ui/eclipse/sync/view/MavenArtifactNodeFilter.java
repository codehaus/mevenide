/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
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
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.model.MavenArtifactNode;
import org.mevenide.util.StringUtils;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: MavenArtifactNodeFilter.java,v 1.1 24 avr. 2004 Exp gdodinet 
 * 
 */
public class MavenArtifactNodeFilter extends ViewerFilter {
	
	static final String APPLY_FILTERS_KEY = "MavenArtifactNodeFilter.APPLY_FILTERS_KEY"; //$NON-NLS-1$
	static final String GROUP_ID_FILTER = "MavenArtifactNodeFilter.GROUP_ID_FILTER"; //$NON-NLS-1$
	
	private boolean filterMavenArtifacts;
	
	private String groupIdFilter;
	
	MavenArtifactNodeFilter() {
		filterMavenArtifacts = getPreferenceStore().getBoolean(APPLY_FILTERS_KEY);
		groupIdFilter = getPreferenceStore().getString(GROUP_ID_FILTER);
	}
	
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if ( filterMavenArtifacts && element instanceof MavenArtifactNode ) {
			if ( !StringUtils.isNull(groupIdFilter) ) {
				String[] groupFilters = org.apache.commons.lang.StringUtils.split(groupIdFilter, ","); //$NON-NLS-1$
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
	public void setGroupIdFilter(String groupIdFilter) {
		this.groupIdFilter = groupIdFilter;
	}

    /**
     * TODO: Describe what getPreferenceStore does.
     * @return
     */
    private IPersistentPreferenceStore getPreferenceStore() {
        return Mevenide.getInstance().getCustomPreferenceStore();
    }
}
