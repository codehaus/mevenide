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

import org.apache.maven.project.License;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.MevenideUtils;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class LicensePropertySource extends AbstractPomPropertySource {

	private static final String LICENSE_NAME = "name"; //$NON-NLS-1$
	private static final String LICENSE_URL = "url"; //$NON-NLS-1$
	private static final String LICENSE_DIST = "distribution"; //$NON-NLS-1$
	private static final String LICENSE_COMMENTS = "comments"; //$NON-NLS-1$

	private static final String LICENSE_DIST_MANUAL = "manual"; //$NON-NLS-1$
	private static final String LICENSE_DIST_REPO = "repo"; //$NON-NLS-1$

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
		return license.getName() != null ? license.getName() : Mevenide.getResourceString("AbstractPropertySource.Element.Unnamed"); //$NON-NLS-1$
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return license;
	}
}
