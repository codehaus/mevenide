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
 *       "This product includes software licensed under 
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
package org.mevenide.ui.eclipse.editors.pages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.License;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class LicensePropertySource implements IPropertySource, IAdaptable, IWorkbenchAdapter {

	private static final Log log = LogFactory.getLog(LicensePropertySource.class);

	private static final String LICENSE_NAME = "name";
	private static final String LICENSE_URL = "url";
	private static final String LICENSE_DIST = "distribution";
	private static final String LICENSE_COMMENTS = "comments";

	private static final String EMPTY_STR = "";
	
	private License license;
	
	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[4];
	{
		descriptors[0] = new TextPropertyDescriptor(
			LICENSE_NAME,
			LICENSE_NAME
		);
		descriptors[1] = new TextPropertyDescriptor(
			LICENSE_URL,
			LICENSE_URL
		);
		descriptors[2] = new TextPropertyDescriptor(
			LICENSE_DIST,
			LICENSE_DIST
		);
		descriptors[3] = new TextPropertyDescriptor(
			LICENSE_COMMENTS,
			LICENSE_COMMENTS
		);
	}

	public LicensePropertySource(License license) {
		this.license = license;
		if (log.isDebugEnabled()) {
			log.debug("created a LicensePropertySource");
		}
	}

	public Object getEditableValue() {
		if (log.isDebugEnabled()) {
			log.debug("getEditableValue called");
		}
		return license.getName();
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		if (log.isDebugEnabled()) {
			log.debug("getPropertyDescriptors called");
		}
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (log.isDebugEnabled()) {
			log.debug("getPropertyValue called: " + id);
		}
		if (LICENSE_NAME.equals(id)) {
			return valueOrEmptyString(license.getName());
		}
		else if (LICENSE_URL.equals(id)) {
			return valueOrEmptyString(license.getUrl());
		}
		else if (LICENSE_DIST.equals(id)) {
			return valueOrEmptyString(license.getDistribution());
		}
		else if (LICENSE_COMMENTS.equals(id)) {
			return valueOrEmptyString(license.getComments());
		}
		return null;
	}
	
	private String valueOrEmptyString(String value) {
		return value != null ? value : EMPTY_STR;
	}

	public boolean isPropertySet(Object id) {
		if (log.isDebugEnabled()) {
			log.debug("isPropertySet called: " + id);
		}
		if (LICENSE_NAME.equals(id)) {
			return !isEmpty(license.getName());
		}
		else if (LICENSE_URL.equals(id)) {
			return !isEmpty(license.getUrl());
		}
		else if (LICENSE_DIST.equals(id)) {
			return !isEmpty(license.getDistribution());
		}
		else if (LICENSE_COMMENTS.equals(id)) {
			return !isEmpty(license.getComments());
		}
		return false;
	}
	
	private boolean isEmpty(String value) {
		return value == null || EMPTY_STR.equals(value);
	}

	public void resetPropertyValue(Object id) {
		if (log.isDebugEnabled()) {
			log.debug("resetPropertyValue called: " + id);
		}
		if (LICENSE_NAME.equals(id)) {
			license.setName(EMPTY_STR);
		}
		else if (LICENSE_URL.equals(id)) {
			license.setUrl(EMPTY_STR);
		}
		else if (LICENSE_DIST.equals(id)) {
			license.setDistribution(EMPTY_STR);
		}
		else if (LICENSE_COMMENTS.equals(id)) {
			license.setComments(EMPTY_STR);
		}
	}

	public void setPropertyValue(Object id, Object value) {
		if (log.isDebugEnabled()) {
			log.debug("setPropertyValue called: " + id + " = " + value);
		}
		if (value == null) return;
		
		String v = value.toString();
		
		if (LICENSE_NAME.equals(id)) {
			license.setName(v);
		}
		else if (LICENSE_URL.equals(id)) {
			license.setUrl(v);
		}
		else if (LICENSE_DIST.equals(id)) {
			license.setDistribution(v);
		}
		else if (LICENSE_COMMENTS.equals(id)) {
			license.setComments(v);
		}
	}

	public Object getAdapter(Class adapter) {
		if (log.isDebugEnabled()) {
			log.debug("getAdapter called");
		}
		if (IPropertySource.class.equals(adapter)) {
			return this;
		}
		if (IWorkbenchAdapter.class.equals(adapter)) {
			return this;
		}
		return null;
	}

	public Object[] getChildren(Object o) {
		if (log.isDebugEnabled()) {
			log.debug("getChildren called");
		}
		return null;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		if (log.isDebugEnabled()) {
			log.debug("getImageDescriptor called");
		}
		return null;
	}

	public String getLabel(Object o) {
		if (log.isDebugEnabled()) {
			log.debug("getLabel called");
		}
		return license.getName() != null ? license.getName() : "[unnamed]";
	}

	public Object getParent(Object o) {
		if (log.isDebugEnabled()) {
			log.debug("getParent called");
		}
		return null;
	}

}
