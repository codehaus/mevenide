/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Jeffrey Bonevich (jeff@bonevich.com).  All rights
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Contributor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ContributorPropertySource extends AbstractPomPropertySource {

	private static final Log log = LogFactory.getLog(ContributorPropertySource.class);

	private static final String CONTRIBUTOR_NAME = "name";
	private static final String CONTRIBUTOR_URL = "url";
	private static final String CONTRIBUTOR_DIST = "distribution";
	private static final String CONTRIBUTOR_COMMENTS = "comments";

	private static final String CONTRIBUTOR_DIST_MANUAL = "manual";
	private static final String CONTRIBUTOR_DIST_REPO = "repo";

	private static final Integer CONTRIBUTOR_DIST_EMPTY_INDEX = new Integer(0);
	private static final Integer CONTRIBUTOR_DIST_MANUAL_INDEX = new Integer(1);
	private static final Integer CONTRIBUTOR_DIST_REPO_INDEX = new Integer(2);

	private Contributor contributor;
	
	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[2];
	{
		descriptors[0] = new TextPropertyDescriptor(
			CONTRIBUTOR_NAME,
			CONTRIBUTOR_NAME
		);
		descriptors[1] = new TextPropertyDescriptor(
			CONTRIBUTOR_URL,
			CONTRIBUTOR_URL
		);
	}

	public ContributorPropertySource(Contributor contributor) {
		this.contributor = contributor;
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
		else if (CONTRIBUTOR_URL.equals(id)) {
			return valueOrEmptyString(contributor.getUrl());
		}
		return null;
	}
	
	public boolean isPropertySet(Object id) {
		if (CONTRIBUTOR_NAME.equals(id)) {
			return !isEmpty(contributor.getName());
		}
		else if (CONTRIBUTOR_URL.equals(id)) {
			return !isEmpty(contributor.getUrl());
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
		String idChanged = null, oldValue = null;
		if (CONTRIBUTOR_NAME.equals(id)) {
			idChanged = id.toString();
			oldValue = contributor.getName();
			contributor.setName(newValue);
		}
		else if (CONTRIBUTOR_URL.equals(id)) {
			idChanged = id.toString();
			oldValue = contributor.getUrl();
			contributor.setUrl(newValue);
		}
		if (idChanged != null)
		{
			firePropertyChangeEvent(idChanged, oldValue, newValue);
		}
	}

	public String getLabel(Object o) {
		if (log.isDebugEnabled()) {
			log.debug("getLabel called for " + o);
		}
		return contributor.getName() != null ? contributor.getName() : "[unnamed]";
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return contributor;
	}
}
