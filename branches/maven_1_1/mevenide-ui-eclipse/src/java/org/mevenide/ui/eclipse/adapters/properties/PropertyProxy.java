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
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.MevenideUtils;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PropertyProxy extends AbstractPomPropertySource {

	private static final Log log = LogFactory.getLog(PropertyProxy.class);
	
	public static final String PROPERTY_NAME = "name"; //$NON-NLS-1$
	public static final String PROPERTY_VALUE = "value"; //$NON-NLS-1$

	private String name;
	private String value;
	
	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[2];
	{
		descriptors[0] = new TextPropertyDescriptor(
			PROPERTY_NAME,
			PROPERTY_NAME
		);
		descriptors[1] = new TextPropertyDescriptor(
			PROPERTY_VALUE,
			PROPERTY_VALUE
		);
	}

	public PropertyProxy(String property) {
		this(MevenideUtils.resolveProperty(property));
	}

	private PropertyProxy(String[] property) {
		this(property[0], property[1]);
	}

	public PropertyProxy(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public Object getEditableValue() {
		return value;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (log.isDebugEnabled()) {
			log.debug("getPropertyValue called: " + id); //$NON-NLS-1$
		}
		if (PROPERTY_NAME.equals(id)) {
			return valueOrEmptyString(name);
		}
		else if (PROPERTY_VALUE.equals(id)) {
			return valueOrEmptyString(value);
		}
		return null;
	}
	
	public boolean isPropertySet(Object id) {
		if (PROPERTY_NAME.equals(id)) {
			return !isEmpty(name);
		}
		else if (PROPERTY_VALUE.equals(id)) {
			return !isEmpty(value);
		}
		return false;
	}
	
	public void resetPropertyValue(Object id) {
		setPropertyValue(id, EMPTY_STR);
	}

	public void setPropertyValue(Object id, Object valueObj) {
		if (log.isDebugEnabled()) {
			log.debug("setPropertyValue called: " + id + " = " + valueObj);  //$NON-NLS-1$//$NON-NLS-2$
		}
		if (valueObj == null) return;
		
		String newValue = valueObj.toString();
		String oldValue = null;
		boolean changed = false;
		if (PROPERTY_NAME.equals(id)) {
			oldValue = name;
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				this.name = newValue;
				changed = true;
			}
		}
		else if (PROPERTY_VALUE.equals(id)) {
			oldValue = value;
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				this.value = newValue;
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
		return name != null ? name : Mevenide.getResourceString("AbstractPropertySource.Element.Unknown"); //$NON-NLS-1$
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return name + MevenideUtils.PROPERTY_SEPARATOR + value;
	}
}
