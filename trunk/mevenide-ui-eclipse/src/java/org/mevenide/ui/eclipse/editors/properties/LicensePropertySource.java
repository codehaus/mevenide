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
package org.mevenide.ui.eclipse.editors.properties;

import org.apache.maven.project.License;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mevenide.util.MevenideUtils;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class LicensePropertySource extends AbstractPomPropertySource {

	private static final String LICENSE_NAME = "name";
	private static final String LICENSE_URL = "url";
	private static final String LICENSE_DIST = "distribution";
	private static final String LICENSE_COMMENTS = "comments";

	private static final String LICENSE_DIST_MANUAL = "manual";
	private static final String LICENSE_DIST_REPO = "repo";

	private static final Integer LICENSE_DIST_EMPTY_INDEX = new Integer(0);
	private static final Integer LICENSE_DIST_MANUAL_INDEX = new Integer(1);
	private static final Integer LICENSE_DIST_REPO_INDEX = new Integer(2);

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
		descriptors[2] = new ComboBoxPropertyDescriptor(
			LICENSE_DIST,
			LICENSE_DIST,
			new String[] {EMPTY_STR, LICENSE_DIST_MANUAL, LICENSE_DIST_REPO}
		);
		((ComboBoxPropertyDescriptor) descriptors[2]).setLabelProvider(
			new LabelProvider() {
				public String getText(Object element) {
					if (element instanceof Integer) {
						return getDistributionForIndex((Integer) element);
					}
					return super.getText(element);
				}
			}
		);
		descriptors[3] = new TextPropertyDescriptor(
			LICENSE_COMMENTS,
			LICENSE_COMMENTS
		);
	}

	public LicensePropertySource(License license) {
		this.license = license;
	}

	public Object getEditableValue() {
		return license.getName();
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (LICENSE_NAME.equals(id)) {
			return valueOrEmptyString(license.getName());
		}
		else if (LICENSE_URL.equals(id)) {
			return valueOrEmptyString(license.getUrl());
		}
		else if (LICENSE_DIST.equals(id)) {
			return getIndexOfDistribution();
		}
		else if (LICENSE_COMMENTS.equals(id)) {
			return valueOrEmptyString(license.getComments());
		}
		return null;
	}
	
	private Integer getIndexOfDistribution() {
		String dist = license.getDistribution();
		if (LICENSE_DIST_MANUAL.equals(dist)) {
			return LICENSE_DIST_MANUAL_INDEX;
		}
		if (LICENSE_DIST_REPO.equals(dist)) {
			return LICENSE_DIST_REPO_INDEX;
		}
		return LICENSE_DIST_EMPTY_INDEX;
	}
	
	public boolean isPropertySet(Object id) {
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
	
	public void resetPropertyValue(Object id) {
		setPropertyValue(id, EMPTY_STR);
	}

	public void setPropertyValue(Object id, Object value) {
		if (value == null) return;
		
		String newValue = value.toString();
		String oldValue = null;
		boolean changed = false;
		if (LICENSE_NAME.equals(id)) {
			oldValue = license.getName();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				license.setName(newValue);
				changed = true;
			}
		}
		else if (LICENSE_URL.equals(id)) {
			oldValue = license.getUrl();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				license.setUrl(newValue);
				changed = true;
			}
		}
		else if (LICENSE_DIST.equals(id)) {
			oldValue = license.getDistribution();
			newValue = getDistributionForIndex((Integer) value);
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				license.setDistribution(newValue);
				changed = true;
			}
		}
		else if (LICENSE_COMMENTS.equals(id)) {
			oldValue = license.getComments();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				license.setComments(newValue);
				changed = true;
			}
		}
		if (changed)
		{
			firePropertyChangeEvent(id.toString(), oldValue, newValue);
		}
	}

	private String getDistributionForIndex(Integer index) {
		if (LICENSE_DIST_MANUAL_INDEX.equals(index)) {
			return LICENSE_DIST_MANUAL;
		}
		if (LICENSE_DIST_REPO_INDEX.equals(index)) {
			return LICENSE_DIST_REPO;
		}
		return EMPTY_STR;
	}

	public String getLabel(Object o) {
		return license.getName() != null ? license.getName() : "[unnamed]";
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return license;
	}
}
