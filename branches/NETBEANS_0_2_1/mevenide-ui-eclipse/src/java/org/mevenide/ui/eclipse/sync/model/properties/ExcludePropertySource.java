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
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.adapters.properties.AbstractPomPropertySource;

/**
 *
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 */
public class ExcludePropertySource extends AbstractPomPropertySource {

	private static final Log log = LogFactory.getLog(ExcludePropertySource.class);

	public static final String DIRECTORY = "Directory"; //$NON-NLS-1$
	public static final String EXCLUSION_PATTERN = "Exclusion pattern"; //$NON-NLS-1$
	
	private String directory;
	private String exclusionPattern;
	
	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[2];
	{
		descriptors[0] = new PropertyDescriptor(DIRECTORY, DIRECTORY);
		descriptors[1] = new PropertyDescriptor(EXCLUSION_PATTERN, EXCLUSION_PATTERN);
	}
	

	public ExcludePropertySource(String directory, String exclusionPattern) {
		this.directory = directory;
		this.exclusionPattern = exclusionPattern;
	}

	public Object getEditableValue() {
		return directory != null ? directory : EMPTY_STR;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (DIRECTORY.equals(id)) {
			return valueOrEmptyString(directory);
		}
		if (EXCLUSION_PATTERN.equals(id)) {
			return exclusionPattern;
		}
		return EMPTY_STR;
	}
	
	public boolean isPropertySet(Object id) {
		if (DIRECTORY.equals(id)) {
			return !isEmpty(directory);
		}
		if (EXCLUSION_PATTERN.equals(id)) {
			return !isEmpty(exclusionPattern);
		}
		return false;
	}
	
	public void resetPropertyValue(Object id) {
		setPropertyValue(id, directory);
	}

	public void setPropertyValue(Object id, Object value) {
	}

	public String getLabel(Object o) {
		return directory != null ? directory : Mevenide.getResourceString("ExcludePropertySource.CannotResolve.ExcludeDirectory"); //$NON-NLS-1$
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return exclusionPattern;
	}
}
