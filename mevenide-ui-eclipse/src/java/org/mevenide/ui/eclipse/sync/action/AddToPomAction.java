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
package org.mevenide.ui.eclipse.sync.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.MavenProject;
import org.mevenide.ui.eclipse.sync.event.ISynchronizationConstraintListener;
import org.mevenide.ui.eclipse.sync.event.SynchronizationConstraintEvent;
import org.mevenide.ui.eclipse.sync.model.ArtifactNode;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class AddToPomAction extends ArtifactAction implements ISynchronizationConstraintListener {
	private static Log log = LogFactory.getLog(AddToPomAction.class);
	
	private boolean shouldWriteProperties;
	
	public void addEntry(ArtifactNode item, MavenProject project) throws Exception {
		
		item.addTo(project, shouldWriteProperties);
		
		fireArtifactAddedToPom(item, project);
	}
	
	public void constraintsChange(SynchronizationConstraintEvent event) {
		if ( SynchronizationConstraintEvent.WRITE_PROPERTIES.equals(event.getConstraintId()) ) {
			this.shouldWriteProperties = event.getNewValue();
		}
	}

	
	
}
