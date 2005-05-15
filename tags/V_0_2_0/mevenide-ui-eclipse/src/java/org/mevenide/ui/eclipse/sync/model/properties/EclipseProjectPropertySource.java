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
package org.mevenide.ui.eclipse.sync.model.properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.mevenide.ui.eclipse.adapters.properties.AbstractPomPropertySource;

/**
 *
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 */
public class EclipseProjectPropertySource extends AbstractPomPropertySource {

	private static final Log log = LogFactory.getLog(EclipseProjectPropertySource.class);

	static final String PROJECT = "Project";
	
	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[1];

    private String projectPath;
	

	public EclipseProjectPropertySource(IProject eclipseProject) {
		projectPath = eclipseProject.getLocation().toString();
        descriptors[0] = new PropertyDescriptor(PROJECT, PROJECT);
	}

	public Object getEditableValue() {
		return EMPTY_STR;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (PROJECT.equals(id)) {
			return valueOrEmptyString(projectPath);
		}
		return EMPTY_STR;
	}
	
	
	public boolean isPropertySet(Object id) {
		if (PROJECT.equals(id)) {
			return !isEmpty(projectPath);
		}
		return false;
	}
	
	public void resetPropertyValue(Object id) {
		setPropertyValue(id, projectPath);
	}

	public void setPropertyValue(Object id, Object value) {
	}

	public String getLabel(Object o) {
		if (log.isDebugEnabled()) {
			log.debug("getLabel called for " + o);
		}
		return projectPath != null ? projectPath : "[unable to resolve Descriptor path]";
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return projectPath;
	}
}
