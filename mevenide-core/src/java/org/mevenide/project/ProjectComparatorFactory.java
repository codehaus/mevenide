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
package org.mevenide.project;

import java.util.Hashtable;
import java.util.Map;

import org.apache.maven.project.MavenProject;

/**
 * A factory for obtaining instances of ProjectComparator.  Clients asking for the
 * comparator for equivalent projects will be given the same instance.
 * 
 * @author Jeffrey Bonevich <jeff@bonevich.com>
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 */
public class ProjectComparatorFactory {

	private static Map comparatorMap = new Hashtable();
	
	private ProjectComparatorFactory() {}
	
	/**
	 * Get the ProjectComparator instance appropriate for this POM project.  An
	 * appropriate instance is one configured for the POM with the same id as the
	 * project supplied as an argument.
	 */
	public static ProjectComparator getComparator(MavenProject project) {
		if (project == null) {
			return null;
		}
		ProjectComparator comparator = (ProjectComparator) comparatorMap.get(getComparatorKey(project));
		if (comparator == null) {
			comparator = new ProjectComparator(project);
				
			// put new comparator in map with project as key
			// @fixme: this will be problematic if the id of the project changes
			comparatorMap.put(getComparatorKey(project), comparator);
		}
		return comparator;
	}
	
	/**
	 * Internal method to update the comparator instance for a project that has
	 * been changed.  The same comparator instance is disassociated with the old
	 * project, and associated with the new one.
	 */
	protected static void updateComparator(MavenProject originalProject, MavenProject newProject) {
		ProjectComparator comparator = getComparator(originalProject);
		if (comparator != null && newProject != null) {
			comparatorMap.remove(getComparatorKey(originalProject));
			comparatorMap.put(getComparatorKey(newProject), comparator);
		} else {
			getComparator(newProject);
		}
	}
	
	private static Object getComparatorKey(MavenProject project) {
		if ( project == null ) {
			return null;
	    }
		return project.getGroupId() + ":" + project.getArtifactId();
	}
	    
}