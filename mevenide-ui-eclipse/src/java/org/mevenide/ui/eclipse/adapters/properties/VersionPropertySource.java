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
import org.apache.maven.project.Version;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.MevenideUtils;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class VersionPropertySource extends AbstractPomPropertySource {

	private static final Log log = LogFactory.getLog(VersionPropertySource.class);

	private static final String VERSION_NAME = "name"; //$NON-NLS-1$
	private static final String VERSION_ID = "id"; //$NON-NLS-1$
	private static final String VERSION_TAG = "tag"; //$NON-NLS-1$

	private Version version;
	
	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[3];
	{
		descriptors[0] = new TextPropertyDescriptor(
			VERSION_NAME,
			VERSION_NAME
		);
		descriptors[1] = new TextPropertyDescriptor(
			VERSION_ID,
			VERSION_ID
		);
		descriptors[2] = new TextPropertyDescriptor(
			VERSION_TAG,
			VERSION_TAG
		);
	}

	public VersionPropertySource(Version version) {
		this.version = version;
	}

	public Object getEditableValue() {
		return version.getName();
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (log.isDebugEnabled()) {
			log.debug("getPropertyValue called: " + id); //$NON-NLS-1$
		}
		if (VERSION_NAME.equals(id)) {
			return valueOrEmptyString(version.getName());
		}
		else if (VERSION_ID.equals(id)) {
			return valueOrEmptyString(version.getId());
		}
		else if (VERSION_TAG.equals(id)) {
			return valueOrEmptyString(version.getTag());
		}
		return null;
	}
	
	public boolean isPropertySet(Object id) {
		if (VERSION_NAME.equals(id)) {
			return !isEmpty(version.getName());
		}
		else if (VERSION_ID.equals(id)) {
			return !isEmpty(version.getId());
		}
		else if (VERSION_TAG.equals(id)) {
			return !isEmpty(version.getTag());
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
		if (VERSION_NAME.equals(id)) {
			oldValue = version.getName();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				version.setName(newValue);
				changed = true;
			}
		}
		else if (VERSION_ID.equals(id)) {
			oldValue = version.getId();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				version.setId(newValue);
				changed = true;
			}
		}
		else if (VERSION_TAG.equals(id)) {
			oldValue = version.getTag();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				version.setTag(newValue);
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
			log.debug("getLabel called for " + o); //$NON-NLS-1$
		}
		return version.getName() != null ? version.getName() : Mevenide.getResourceString("AbstractPropertySource.Element.Unknown"); //$NON-NLS-1$
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return version;
	}
}
