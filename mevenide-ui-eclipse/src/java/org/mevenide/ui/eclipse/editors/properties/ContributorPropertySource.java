/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software contributord under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.ui.eclipse.editors.properties;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Contributor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mevenide.util.MevenideUtils;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ContributorPropertySource extends AbstractPomPropertySource {

	private static final Log log = LogFactory.getLog(ContributorPropertySource.class);

	protected static final String CONTRIBUTOR_NAME = "name";
	protected static final String CONTRIBUTOR_EMAIL = "email";
	protected static final String CONTRIBUTOR_ORGANIZATION = "organization";
	protected static final String CONTRIBUTOR_ROLES = "roles";
	protected static final String CONTRIBUTOR_URL = "url";
	protected static final String CONTRIBUTOR_TIMEZONE = "timezone";

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
			log.debug("getPropertyValue called: " + id);
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
			return StringUtils.join(itr, ",");
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
			log.debug("setPropertyValue called: " + id + " = " + value);
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
		String[] roles = newValue.split("\\s*,\\s*");
		for (int i = 0; i < roles.length; i++) {
			contributor.addRole(roles[i]);
		}
	}

	public String getLabel(Object o) {
		if (log.isDebugEnabled()) {
			log.debug("getLabel called for " + o);
		}
		return contributor.getName() != null ? contributor.getName() : "[unknown]";
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return contributor;
	}
}
