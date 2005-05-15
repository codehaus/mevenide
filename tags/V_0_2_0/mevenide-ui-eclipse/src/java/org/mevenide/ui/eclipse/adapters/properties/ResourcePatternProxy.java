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

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mevenide.util.MevenideUtils;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ResourcePatternProxy extends AbstractPomPropertySource {

	private static final String EXCLUDE_PATTERN = "exclude";
	private static final String INCLUDE_PATTERN = "include";

	private boolean isIncludePattern;
	private String pattern;
	
	private static final IPropertyDescriptor EXCLUDE_DESCRIPTOR = 
		new TextPropertyDescriptor(
			EXCLUDE_PATTERN,
			EXCLUDE_PATTERN
		);
	private static final IPropertyDescriptor INCLUDE_DESCRIPTOR = 
		new TextPropertyDescriptor(
			INCLUDE_PATTERN,
			INCLUDE_PATTERN
		);

	public ResourcePatternProxy(String pattern, boolean isIncludePattern) {
		this.pattern = pattern;
		this.isIncludePattern = isIncludePattern;
	}

	public Object getEditableValue() {
		return pattern;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { isIncludePattern ? INCLUDE_DESCRIPTOR : EXCLUDE_DESCRIPTOR};
	}

	public Object getPropertyValue(Object id) {
		return valueOrEmptyString(pattern);
	}
	
	public boolean isPropertySet(Object id) {
		return !isEmpty(pattern);
	}
	
	public void resetPropertyValue(Object id) {
		setPropertyValue(id, EMPTY_STR);
	}

	public void setPropertyValue(Object id, Object value) {
		if (value == null) return;
		
		String newValue = value.toString();
		String oldValue = null;
		boolean changed = false;
		oldValue = pattern;
		if (MevenideUtils.notEquivalent(newValue, oldValue)) {
			pattern = newValue;
			changed = true;
		}
		if (changed)
		{
			firePropertyChangeEvent(id.toString(), oldValue, newValue);
		}
	}

	public String getLabel(Object o) {
		return pattern;
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return pattern;
	}
}
