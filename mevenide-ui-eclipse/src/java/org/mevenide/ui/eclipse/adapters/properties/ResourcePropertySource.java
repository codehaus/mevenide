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
package org.mevenide.ui.eclipse.adapters.properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Resource;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.MevenideUtils;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ResourcePropertySource extends AbstractPomPropertySource {

	private static final Log log = LogFactory.getLog(ResourcePropertySource.class);

	private static final String RESOURCE_DIRECTORY = "directory"; //$NON-NLS-1$
	private static final String RESOURCE_TARGETPATH = "targetPath"; //$NON-NLS-1$
	private static final String RESOURCE_FILTERING = "filtering"; //$NON-NLS-1$

	private static final String RESOURCE_FILTERING_TRUE = "true"; //$NON-NLS-1$
	private static final String RESOURCE_FILTERING_FALSE = "false"; //$NON-NLS-1$
	
	private static final String[] RESOURCE_FILTERING_VALUES = new String[] {
		RESOURCE_FILTERING_TRUE,
		RESOURCE_FILTERING_FALSE
	};

	private Resource resource;
	
	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[3];
	{
		descriptors[0] = new TextPropertyDescriptor(
			RESOURCE_DIRECTORY,
			RESOURCE_DIRECTORY
		);
		descriptors[1] = new TextPropertyDescriptor(
			RESOURCE_TARGETPATH,
			RESOURCE_TARGETPATH
		);
		descriptors[2] = new ComboBoxPropertyDescriptor(
			RESOURCE_FILTERING,
			RESOURCE_FILTERING,
			RESOURCE_FILTERING_VALUES
		);
		((ComboBoxPropertyDescriptor) descriptors[2]).setLabelProvider(
			new LabelProvider() {
				public String getText(Object element) {
					if (element instanceof Integer) {
						return getBooleanForIndex(((Integer) element).intValue());
					}
					return super.getText(element);
				}
			}
		);
	}

	public ResourcePropertySource(Resource resource) {
		this.resource = resource;
	}

	public Object getEditableValue() {
		return resource.getDirectory();
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (log.isDebugEnabled()) {
			log.debug("getPropertyValue called: " + id); //$NON-NLS-1$
		}
		if (RESOURCE_DIRECTORY.equals(id)) {
			return valueOrEmptyString(resource.getDirectory());
		}
		if (RESOURCE_TARGETPATH.equals(id)) {
			return valueOrEmptyString(resource.getTargetPath());
		}
		if (RESOURCE_FILTERING.equals(id)) {
			return getIndexOfType();
		}
		return null;
	}
	
	public boolean isPropertySet(Object id) {
		if (RESOURCE_DIRECTORY.equals(id)) {
			return !isEmpty(resource.getDirectory());
		}
		if (RESOURCE_TARGETPATH.equals(id)) {
			return !isEmpty(resource.getTargetPath());
		}
		if (RESOURCE_FILTERING.equals(id)) {
			return resource.getFiltering();
		}
		return false;
	}
	
	public void resetPropertyValue(Object id) {
		setPropertyValue(id, EMPTY_STR);
	}

	public void setPropertyValue(Object id, Object value) {
		if (log.isDebugEnabled()) {
			log.debug("setPropertyValue called: " + id + " = " + value);  //$NON-NLS-1$//$NON-NLS-2$
		}
		if (value == null) return;
		
		String newValue = value.toString();
		String oldValue = null;
		boolean changed = false;
		if (RESOURCE_DIRECTORY.equals(id)) {
			oldValue = resource.getDirectory();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				resource.setDirectory(newValue);
				changed = true;
			}
		}
		else if (RESOURCE_TARGETPATH.equals(id)) {
			oldValue = resource.getTargetPath();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				resource.setTargetPath(newValue);
				changed = true;
			}
		}
		else if (RESOURCE_FILTERING.equals(id)) {
			newValue = getBooleanForIndex(((Integer) value).intValue());
			oldValue = Boolean.toString(resource.getFiltering());
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				boolean filtering = Boolean.valueOf(newValue).booleanValue();
				resource.setFiltering(filtering);
				changed = true;
			}
		}
		if (changed)
		{
			firePropertyChangeEvent(id.toString(), oldValue, newValue);
		}
	}

	private Integer getIndexOfType() {
		return resource.getFiltering() ? new Integer(0) : new Integer(1);
	}
	
	private String getBooleanForIndex(int index) {
		return index == 0 ? RESOURCE_FILTERING_TRUE : RESOURCE_FILTERING_FALSE;	
	}

	public String getLabel(Object o) {
		if (log.isDebugEnabled()) {
			log.debug("getLabel called for " + o); //$NON-NLS-1$
		}
		return resource.getDirectory() != null ? resource.getDirectory() : Mevenide.getResourceString("AbstractPropertySource.Element.Unknown"); //$NON-NLS-1$
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return resource;
	}
}
