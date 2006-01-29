/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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

import org.apache.maven.project.Developer;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mevenide.util.MevenideUtils;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class DeveloperPropertySource extends ContributorPropertySource {

	private static final String DEVELOPER_ID = "id"; //$NON-NLS-1$

	public DeveloperPropertySource(Developer developer) {
		super(developer);
	}

	protected void initializeDescriptors() {
		descriptors = new IPropertyDescriptor[7];
		descriptors[0] = new TextPropertyDescriptor(
			CONTRIBUTOR_NAME,
			CONTRIBUTOR_NAME
		);
		descriptors[1] = new TextPropertyDescriptor(
			DEVELOPER_ID,
			DEVELOPER_ID
		);
		descriptors[2] = new TextPropertyDescriptor(
			CONTRIBUTOR_EMAIL,
			CONTRIBUTOR_EMAIL
		);
		descriptors[3] = new TextPropertyDescriptor(
			CONTRIBUTOR_ORGANIZATION,
			CONTRIBUTOR_ORGANIZATION
		);
		descriptors[4] = new TextPropertyDescriptor(
			CONTRIBUTOR_ROLES,
			CONTRIBUTOR_ROLES
		);
		descriptors[5] = new TextPropertyDescriptor(
			CONTRIBUTOR_URL,
			CONTRIBUTOR_URL
		);
		descriptors[6] = new TextPropertyDescriptor(
			CONTRIBUTOR_TIMEZONE,
			CONTRIBUTOR_TIMEZONE
		);
	}

	public Object getPropertyValue(Object id) {
		if (DEVELOPER_ID.equals(id)) {
			return valueOrEmptyString(contributor.getId());
		}
		return super.getPropertyValue(id);
	}
	
	public boolean isPropertySet(Object id) {
		if (DEVELOPER_ID.equals(id)) {
			return !isEmpty(contributor.getId());
		}
		return super.isPropertySet(id);
	}
	
	/**
	 * @see org.mevenide.ui.eclipse.adapters.properties.ContributorPropertySource#setOtherProperties(java.lang.Object, java.lang.Object)
	 */
	protected String setOtherProperties(Object id, Object value) {
		if (DEVELOPER_ID.equals(id)) {
			String oldValue = contributor.getId();
			if (MevenideUtils.notEquivalent(value, oldValue)) {
				contributor.setId(value.toString());
			}
			return oldValue != null ? oldValue : EMPTY_STR;
		}
		return super.setOtherProperties(id, value);
	}
}
