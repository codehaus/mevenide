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

import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.mevenide.project.ProjectConstants;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.model.Directory;
import org.mevenide.ui.eclipse.sync.model.DirectoryNode;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: DirectoryNodeFilter.java,v 1.1 24 avr. 2004 Exp gdodinet 
 * 
 */
public class DirectoryNodeFilter extends ViewerFilter {
	
	static final String APPLY_FILTERS_KEY = "DirectoryNodeFilter.APPLY_FILTERS_KEY"; //$NON-NLS-1$
	static final String APPLY_SOURCE_FILTERS_KEY = "DirectoryNodeFilter.APPLY_SOURCE_FILTERS_KEY"; //$NON-NLS-1$
	static final String APPLY_TEST_FILTERS_KEY = "DirectoryNodeFilter.APPLY_TEST_FILTERS_KEY"; //$NON-NLS-1$
	static final String APPLY_ASPECT_FILTERS_KEY = "DirectoryNodeFilter.APPLY_ASPECT_FILTERS_KEY"; //$NON-NLS-1$
	static final String APPLY_RESOURCE_FILTERS_KEY = "DirectoryNodeFilter.APPLY_RESOURCE_FILTERS_KEY"; //$NON-NLS-1$
	static final String APPLY_OUTPUT_FILTERS_KEY = "DirectoryNodeFilter.APPLY_OUTPUT_FILTERS_KEY"; //$NON-NLS-1$
	
	private boolean filterDirectoryNodes; 
	
	private boolean filterSourceDirectories;
	private boolean filterTestDirectories;
	private boolean filterAspectDirectories;
	private boolean filterResourceDirectories;
	private boolean filterOutputDirectories;
	
	DirectoryNodeFilter() {
		filterDirectoryNodes = getPreferenceStore().getBoolean(APPLY_FILTERS_KEY);
		filterSourceDirectories = getPreferenceStore().getBoolean(APPLY_SOURCE_FILTERS_KEY);
		filterTestDirectories = getPreferenceStore().getBoolean(APPLY_TEST_FILTERS_KEY);
		filterAspectDirectories = getPreferenceStore().getBoolean(APPLY_ASPECT_FILTERS_KEY);
		filterResourceDirectories = getPreferenceStore().getBoolean(APPLY_RESOURCE_FILTERS_KEY);
		filterOutputDirectories = getPreferenceStore().getBoolean(APPLY_OUTPUT_FILTERS_KEY);
	}
	
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if ( !(element instanceof DirectoryNode) ) {
		    return true;
		}
		Directory directory = (Directory) ((DirectoryNode) element).getData();
		if ( filterDirectoryNodes && filterSourceDirectories && ProjectConstants.MAVEN_SRC_DIRECTORY.equals(directory.getType()) ) {
	        return false;
		}
		if ( filterDirectoryNodes && filterTestDirectories && ProjectConstants.MAVEN_TEST_DIRECTORY.equals(directory.getType()) ) {
	        return false;
		}
		if ( filterDirectoryNodes && filterAspectDirectories && ProjectConstants.MAVEN_ASPECT_DIRECTORY.equals(directory.getType()) ) {
	        return false;
		}
		if ( filterDirectoryNodes && filterOutputDirectories && ProjectConstants.MAVEN_OUTPUT_DIRECTORY.equals(directory.getType()) ) {
	        return false;
		}
		if ( filterDirectoryNodes && filterResourceDirectories 
				&& (ProjectConstants.MAVEN_TEST_RESOURCE.equals(directory.getType()) || ProjectConstants.MAVEN_RESOURCE.equals(directory.getType())) ) {
	        return false;
		}
		return true;
	}
	
	
	public void setFilterAspectDirectories(boolean filterAspectDirectories) {
		this.filterAspectDirectories = filterAspectDirectories;
	}
	public void setFilterDirectoryNodes(boolean filterDirectoryNodes) {
		this.filterDirectoryNodes = filterDirectoryNodes;
	}
	public void setFilterResourceDirectories(boolean filterResourceDirectories) {
		this.filterResourceDirectories = filterResourceDirectories;
	}
	public void setFilterSourceDirectories(boolean filterSourceDirectories) {
		this.filterSourceDirectories = filterSourceDirectories;
	}
	public void setFilterTestDirectories(boolean filterTestDirectories) {
		this.filterTestDirectories = filterTestDirectories;
	}
	public void setFilterOutputDirectories(boolean filterOutputDirectories) {
		this.filterOutputDirectories = filterOutputDirectories;
	}

    /**
     * @return the preference store to use in this object
     */
    private IPersistentPreferenceStore getPreferenceStore() {
        return Mevenide.getInstance().getCustomPreferenceStore();
    }
}
