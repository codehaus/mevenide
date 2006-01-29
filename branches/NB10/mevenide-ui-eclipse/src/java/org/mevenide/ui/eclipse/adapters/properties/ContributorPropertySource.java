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

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Contributor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.MevenideUtils;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ContributorPropertySource extends AbstractPomPropertySource {

	private static final Log log = LogFactory.getLog(ContributorPropertySource.class);

	protected static final String CONTRIBUTOR_NAME = "name"; //$NON-NLS-1$
	protected static final String CONTRIBUTOR_EMAIL = "email"; //$NON-NLS-1$
	protected static final String CONTRIBUTOR_ORGANIZATION = "organization"; //$NON-NLS-1$
	protected static final String CONTRIBUTOR_ROLES = "roles"; //$NON-NLS-1$
	protected static final String CONTRIBUTOR_URL = "url"; //$NON-NLS-1$
	protected static final String CONTRIBUTOR_TIMEZONE = "timezone"; //$NON-NLS-1$

	protected Contributor contributor;
	
	protected IPropertyDescriptor[] descriptors;
	
	public ContributorPropertySource(Contributor contributor) {
		this.contributor = contributor;
		initializeDescriptors();
	}

	protected void initializeDescriptors() {
		descriptors = new IPropertyDescriptor[6];
		descriptors[0] = new TextPropertyDescriptor(
			CONTRIBUTOR_NAME,
			CONTRIBUTOR_NAME
		);
		descriptors[1] = new TextPropertyDescriptor(
			CONTRIBUTOR_EMAIL,
			CONTRIBUTOR_EMAIL
		);
		descriptors[2] = new TextPropertyDescriptor(
			CONTRIBUTOR_ORGANIZATION,
			CONTRIBUTOR_ORGANIZATION
		);
		descriptors[3] = new TextPropertyDescriptor(
			CONTRIBUTOR_ROLES,
			CONTRIBUTOR_ROLES
		);
		descriptors[4] = new TextPropertyDescriptor(
			CONTRIBUTOR_URL,
			CONTRIBUTOR_URL
		);
		descriptors[5] = new TextPropertyDescriptor(
			CONTRIBUTOR_TIMEZONE,
			CONTRIBUTOR_TIMEZONE
		);
	}

	public Object getEditableValue() {
		return contributor.getName();
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (log.isDebugEnabled()) {
			log.debug("getPropertyValue called: " + id); //$NON-NLS-1$
		}
		if (CONTRIBUTOR_NAME.equals(id)) {
			return valueOrEmptyString(contributor.getName());
		}
		else if (CONTRIBUTOR_EMAIL.equals(id)) {
			return valueOrEmptyString(contributor.getEmail());
		}
		else if (CONTRIBUTOR_ORGANIZATION.equals(id)) {
			return valueOrEmptyString(contributor.getOrganization());
		}
		else if (CONTRIBUTOR_ROLES.equals(id)) {
			return getRolesString();
		}
		else if (CONTRIBUTOR_URL.equals(id)) {
			return valueOrEmptyString(contributor.getUrl());
		}
		else if (CONTRIBUTOR_TIMEZONE.equals(id)) {
			return valueOrEmptyString(contributor.getTimezone());
		}
		return null;
	}
	
	private String getRolesString() {
		Set roles = contributor.getRoles();
		if (roles != null) {
			Iterator itr = roles.iterator();
			return StringUtils.join(itr, ","); //$NON-NLS-1$
		}
		return EMPTY_STR;
	}

	public boolean isPropertySet(Object id) {
		if (CONTRIBUTOR_NAME.equals(id)) {
			return !isEmpty(contributor.getName());
		}
		else if (CONTRIBUTOR_EMAIL.equals(id)) {
			return !isEmpty(contributor.getEmail());
		}
		else if (CONTRIBUTOR_ORGANIZATION.equals(id)) {
			return !isEmpty(contributor.getOrganization());
		}
		else if (CONTRIBUTOR_ROLES.equals(id)) {
			return contributor.getRoles() != null && !contributor.getRoles().isEmpty();
		}
		else if (CONTRIBUTOR_URL.equals(id)) {
			return !isEmpty(contributor.getUrl());
		}
		else if (CONTRIBUTOR_TIMEZONE.equals(id)) {
			return !isEmpty(contributor.getTimezone());
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
		if (CONTRIBUTOR_NAME.equals(id)) {
			oldValue = contributor.getName();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				contributor.setName(newValue);
				changed = true;
			}
		}
		else if (CONTRIBUTOR_EMAIL.equals(id)) {
			oldValue = contributor.getEmail();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				contributor.setEmail(newValue);
				changed = true;
			}
		}
		else if (CONTRIBUTOR_ORGANIZATION.equals(id)) {
			oldValue = contributor.getOrganization();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				contributor.setOrganization(newValue);
				changed = true;
			}
		}
		else if (CONTRIBUTOR_ROLES.equals(id)) {
			oldValue = getRolesString();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				updateRoles(newValue);
				changed = true;
			}
		}
		else if (CONTRIBUTOR_URL.equals(id)) {
			oldValue = contributor.getUrl();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				contributor.setUrl(newValue);
				changed = true;
			}
		}
		else if (CONTRIBUTOR_TIMEZONE.equals(id)) {
			oldValue = contributor.getTimezone();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				contributor.setTimezone(newValue);
				changed = true;
			}
		}
		oldValue = setOtherProperties(id, value);
		if (changed || oldValue != null)
		{
			firePropertyChangeEvent(id.toString(), oldValue, newValue);
		}
	}

	protected String setOtherProperties(Object id, Object value) {
		return null;
	}

	private void updateRoles(String newValue) {
		Set originalRoles = contributor.getRoles();
		contributor.getRoles().removeAll(originalRoles);
		String[] roles = newValue.split("\\s*,\\s*"); //$NON-NLS-1$
		for (int i = 0; i < roles.length; i++) {
			contributor.addRole(roles[i]);
		}
	}

	public String getLabel(Object o) {
		if (log.isDebugEnabled()) {
			log.debug("getLabel called for " + o); //$NON-NLS-1$
		}
		return contributor.getName() != null ? contributor.getName() : Mevenide.getResourceString("AbstractPropertySource.Element.Unknown"); //$NON-NLS-1$
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return contributor;
	}
}
