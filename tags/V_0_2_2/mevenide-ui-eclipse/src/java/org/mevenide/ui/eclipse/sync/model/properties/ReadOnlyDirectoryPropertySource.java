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
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.mevenide.project.ProjectConstants;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.adapters.properties.AbstractPomPropertySource;
import org.mevenide.ui.eclipse.sync.model.Directory;

/**
 *
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 */
public class ReadOnlyDirectoryPropertySource extends AbstractPomPropertySource {

	private static final Log log = LogFactory.getLog(ReadOnlyDirectoryPropertySource.class);

	public static final String DIRECTORY_PATH = "path"; //$NON-NLS-1$
	public static final String DIRECTORY_TYPE = "type"; //$NON-NLS-1$
	
	private Directory directory;
	
	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[2];
	{
		descriptors[0] = new PropertyDescriptor(DIRECTORY_PATH, DIRECTORY_TYPE);
		descriptors[1] = new PropertyDescriptor(DIRECTORY_TYPE, DIRECTORY_TYPE);
		
	}
	

	public ReadOnlyDirectoryPropertySource(Directory directory) {
		this.directory = directory;
	}

	public Object getEditableValue() {
		return directory.getType() != null ? directory.getType() : EMPTY_STR;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (log.isDebugEnabled()) {
			log.debug("getPropertyValue called: " + id); //$NON-NLS-1$
		}
		if (DIRECTORY_PATH.equals(id)) {
			return valueOrEmptyString(directory.getPath());
		}
		if (DIRECTORY_TYPE.equals(id)) {
		    return valueOrEmptyString(directory.getType());
		}
		return EMPTY_STR;
	}
	
	public boolean isPropertySet(Object id) {
		if (DIRECTORY_PATH.equals(id)) {
			return !isEmpty(directory.getPath());
		}
		if (DIRECTORY_TYPE.equals(id)) {
			return !isEmpty(directory.getType());
		}
		return false;
	}
	
	public void resetPropertyValue(Object id) {
		setPropertyValue(id, ProjectConstants.MAVEN_RESOURCE);
	}

	public void setPropertyValue(Object id, Object value) {
	}

	public String getLabel(Object o) {
		if (log.isDebugEnabled()) {
			log.debug("getLabel called for " + o); //$NON-NLS-1$
		}
		return directory.getPath() != null ? directory.getPath() : Mevenide.getResourceString("ReadOnlyDirectoryPropertySource.CannotResolveDirectoryPath"); //$NON-NLS-1$
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return directory;
	}
}
