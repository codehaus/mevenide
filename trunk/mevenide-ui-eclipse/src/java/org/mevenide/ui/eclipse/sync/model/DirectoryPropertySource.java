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
package org.mevenide.ui.eclipse.sync.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mevenide.project.ProjectConstants;
import org.mevenide.ui.eclipse.editors.properties.AbstractPomPropertySource;
import org.mevenide.util.MevenideUtils;

/**
 *
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 */
public class DirectoryPropertySource extends AbstractPomPropertySource {

	private static final Log log = LogFactory.getLog(DirectoryPropertySource.class);

	private static final String DIRECTORY_PATH = "path";
	private static final String DIRECTORY_TYPE = "type";
	
	private Directory directory;
	
	private static final String[] DIRECTORY_TYPE_VALUES = new String[] {
		ProjectConstants.MAVEN_SRC_DIRECTORY,
		ProjectConstants.MAVEN_TEST_DIRECTORY,
		ProjectConstants.MAVEN_ASPECT_DIRECTORY,
		ProjectConstants.MAVEN_RESOURCE,
		ProjectConstants.MAVEN_TEST_RESOURCE,
	};
	
	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[2];
	{
		descriptors[0] = new TextPropertyDescriptor(
			DIRECTORY_PATH,
			DIRECTORY_TYPE
		);
		descriptors[1] = new ComboBoxPropertyDescriptor(
			DIRECTORY_TYPE,
			DIRECTORY_TYPE,
			DIRECTORY_TYPE_VALUES
		);
		((ComboBoxPropertyDescriptor) descriptors[1]).setLabelProvider(
			new LabelProvider() {
				public String getText(Object element) {
					if (element instanceof Integer) {
						return DIRECTORY_TYPE_VALUES[((Integer) element).intValue()];
					}
					return super.getText(element);
				}
			}
		);
	}
	

	public DirectoryPropertySource(Directory directory) {
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
			log.debug("getPropertyValue called: " + id);
		}
		if (DIRECTORY_PATH.equals(id)) {
			return valueOrEmptyString(directory.getPath());
		}
		if (DIRECTORY_TYPE.equals(id)) {
			return getTypeIndex();
		}
		return EMPTY_STR;
	}
	
	private Integer getTypeIndex() {
	    for ( int u = 0; u < 5; u++ ) {
	        if ( DIRECTORY_TYPE_VALUES[u].equals(directory.getType()) ) {
	            return new Integer(u);
	        }
	    }
	    return new Integer(0);
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
		if (log.isDebugEnabled()) {
			log.debug("setPropertyValue called: " + id + " = " + value);
		}
		if (value == null) return;
		
		String newValue = value.toString();
		String oldValue = null;
		boolean changed = false;
		if (DIRECTORY_PATH.equals(id)) {
			oldValue = directory.getPath();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				directory.setPath(newValue);
				changed = true;
			}
		}
		else if (DIRECTORY_TYPE.equals(id)) {
			newValue = DIRECTORY_TYPE_VALUES[((Integer) value).intValue()];
			oldValue = directory.getType();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				directory.setType(newValue);
				changed = true;
			}
		}
		if (changed)
		{
			firePropertyChangeEvent(id.toString(), oldValue, newValue);
		}
	}

	public String getLabel(Object o) {
		if (log.isDebugEnabled()) {
			log.debug("getLabel called for " + o);
		}
		return directory.getPath() != null ? directory.getPath() : "[unable to resolve Directory path]";
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return directory;
	}
}
