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
import org.apache.maven.project.Branch;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mevenide.util.MevenideUtils;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class BranchPropertySource extends AbstractPomPropertySource {

	private static final Log log = LogFactory.getLog(BranchPropertySource.class);

	private static final String BRANCH_TAG = "tag";

	private Branch branch;
	
	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[1];
	{
		descriptors[0] = new TextPropertyDescriptor(
			BRANCH_TAG,
			BRANCH_TAG
		);
	}

	public BranchPropertySource(Branch branch) {
		this.branch = branch;
	}

	public Object getEditableValue() {
		return branch.getTag();
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (log.isDebugEnabled()) {
			log.debug("getPropertyValue called: " + id);
		}
		if (BRANCH_TAG.equals(id)) {
			return valueOrEmptyString(branch.getTag());
		}
		return null;
	}
	
	public boolean isPropertySet(Object id) {
		if (BRANCH_TAG.equals(id)) {
			return !isEmpty(branch.getTag());
		}
		return false;
	}
	
	public void resetPropertyValue(Object id) {
		setPropertyValue(id, EMPTY_STR);
	}

	public void setPropertyValue(Object id, Object value) {
		if (log.isDebugEnabled()) {
			log.debug("setPropertyValue called: " + id + " = " + value);
		}
		if (value == null) return;
		
		String newValue = value.toString();
		String oldValue = null;
		boolean changed = false;
		if (BRANCH_TAG.equals(id)) {
			oldValue = branch.getTag();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				branch.setTag(newValue);
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
		return branch.getTag() != null ? branch.getTag() : "[unknown]";
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return branch;
	}
}
