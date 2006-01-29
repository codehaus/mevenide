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

import org.eclipse.jface.viewers.ViewerSorter;
import org.mevenide.project.ProjectConstants;
import org.mevenide.ui.eclipse.sync.model.Directory;
import org.mevenide.ui.eclipse.sync.model.DirectoryNode;
import org.mevenide.ui.eclipse.sync.model.MavenArtifactNode;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SynchronizatioNodeSorter extends ViewerSorter {
	
	private static final int DEPENDENCY_CATEGORY = 1;
	private static final int SOURCE_DIRECTORY_CATEGORY = 4;
	private static final int ASPECT_DIRECTORY_CATEGORY = 8;
	private static final int UNIT_TEST_DIRECTORY_CATEGORY = 16;
	private static final int RESOURCE_DIRECTORY_CATEGORY = 64;
	private static final int OUTPUT_DIRECTORY_CATEGORY = 92;
	private static final int NON_CATEGORIZED_DIRECTORY_CATEGORY = 127;
	
	public int category(Object element) {
		if ( element instanceof MavenArtifactNode ) {
			return DEPENDENCY_CATEGORY;
		}
		if ( element instanceof DirectoryNode ) {
			Directory directory = (Directory) ((DirectoryNode) element).getData();
			if ( ProjectConstants.MAVEN_ASPECT_DIRECTORY.equals(directory.getType()) ) {
				return ASPECT_DIRECTORY_CATEGORY;
			}
			if ( ProjectConstants.MAVEN_SRC_DIRECTORY.equals(directory.getType()) ) {
				return SOURCE_DIRECTORY_CATEGORY;
			}
			if ( ProjectConstants.MAVEN_TEST_DIRECTORY.equals(directory.getType()) ) {
				return UNIT_TEST_DIRECTORY_CATEGORY;
			}
			if ( ProjectConstants.MAVEN_OUTPUT_DIRECTORY.equals(directory.getType()) ) {
				return OUTPUT_DIRECTORY_CATEGORY;
			}
			if ( ProjectConstants.MAVEN_RESOURCE.equals(directory.getType()) 
					|| ProjectConstants.MAVEN_TEST_RESOURCE.equals(directory.getType())) {
				return RESOURCE_DIRECTORY_CATEGORY;
			}
			return NON_CATEGORIZED_DIRECTORY_CATEGORY;
		}
		return super.category(element);
	}
}
