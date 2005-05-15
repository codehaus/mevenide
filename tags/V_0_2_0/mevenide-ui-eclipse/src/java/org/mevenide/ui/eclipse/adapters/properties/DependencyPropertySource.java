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

import org.apache.maven.project.Dependency;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mevenide.util.MevenideUtils;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class DependencyPropertySource extends AbstractPomPropertySource {

	public static final String DEPENDENCY_ARTIFACTID = "artifactId";
	public static final String DEPENDENCY_GROUPID = "groupId";
	public static final String DEPENDENCY_VERSION = "version";
	public static final String DEPENDENCY_JAR = "jar";
	public static final String DEPENDENCY_TYPE = "type";
	public static final String DEPENDENCY_URL = "url";

	private static final String DEPENDENCY_TYPE_JAR = "jar";
	private static final String DEPENDENCY_TYPE_EJB = "ejb";
	private static final String DEPENDENCY_TYPE_PLUGIN = "plugin";
	private static final String DEPENDENCY_TYPE_ASPECT = "aspect";
	private static final String DEPENDENCY_TYPE_WAR = "war";
	
	private static final String[] DEPENDENCY_TYPES = new String[] {
		DEPENDENCY_TYPE_JAR, 
		DEPENDENCY_TYPE_EJB, 
		DEPENDENCY_TYPE_PLUGIN,
		DEPENDENCY_TYPE_ASPECT,
		DEPENDENCY_TYPE_WAR
	};

	private Dependency dependency;
	
	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[6];
	{
		descriptors[0] = new TextPropertyDescriptor(
			DEPENDENCY_ARTIFACTID,
			DEPENDENCY_ARTIFACTID
		);
		descriptors[1] = new TextPropertyDescriptor(
			DEPENDENCY_GROUPID,
			DEPENDENCY_GROUPID
		);
		descriptors[2] = new TextPropertyDescriptor(
			DEPENDENCY_VERSION,
			DEPENDENCY_VERSION
		);
		descriptors[3] = new TextPropertyDescriptor(
			DEPENDENCY_JAR,
			DEPENDENCY_JAR
		);
		descriptors[4] = new ComboBoxPropertyDescriptor(
			DEPENDENCY_TYPE,
			DEPENDENCY_TYPE,
			DEPENDENCY_TYPES
		);
		((ComboBoxPropertyDescriptor) descriptors[4]).setLabelProvider(
			new LabelProvider() {
				public String getText(Object element) {
					if (element instanceof Integer) {
						return getTypeForIndex(((Integer) element).intValue());
					}
					return super.getText(element);
				}
			}
		);
		descriptors[5] = new TextPropertyDescriptor(
			DEPENDENCY_URL,
			DEPENDENCY_URL
		);
	}

	public DependencyPropertySource(Dependency dependency) {
		this.dependency = dependency;
	}

	public Object getEditableValue() {
		return dependency.getArtifact();
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (DEPENDENCY_ARTIFACTID.equals(id)) {
			return valueOrEmptyString(dependency.getArtifactId());
		}
		else if (DEPENDENCY_GROUPID.equals(id)) {
			return valueOrEmptyString(dependency.getGroupId());
		}
		else if (DEPENDENCY_VERSION.equals(id)) {
			return valueOrEmptyString(dependency.getVersion());
		}
		else if (DEPENDENCY_JAR.equals(id)) {
			return valueOrEmptyString(dependency.getJar());
		}
		else if (DEPENDENCY_TYPE.equals(id)) {
			return getIndexOfType();
		}
		else if (DEPENDENCY_URL.equals(id)) {
			return valueOrEmptyString(dependency.getUrl());
		}
		return null;
	}
	
	private Integer getIndexOfType() {
		String type = dependency.getType();
		for (int i = 0; i < DEPENDENCY_TYPES.length; i++) {
			if (DEPENDENCY_TYPES[i].equals(type)) {
				return new Integer(i);
			}
		}
		return new Integer(0);
	}
	
	public boolean isPropertySet(Object id) {
		if (DEPENDENCY_ARTIFACTID.equals(id)) {
			return !isEmpty(dependency.getArtifactId());
		}
		else if (DEPENDENCY_GROUPID.equals(id)) {
			return !isEmpty(dependency.getUrl());
		}
		else if (DEPENDENCY_VERSION.equals(id)) {
			return !isEmpty(dependency.getVersion());
		}
		else if (DEPENDENCY_JAR.equals(id)) {
			return !isEmpty(dependency.getJar());
		}
		else if (DEPENDENCY_TYPE.equals(id)) {
			return !isEmpty(dependency.getType());
		}
		else if (DEPENDENCY_URL.equals(id)) {
			return !isEmpty(dependency.getUrl());
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
		if (DEPENDENCY_ARTIFACTID.equals(id)) {
			oldValue = dependency.getArtifactId();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				dependency.setArtifactId(newValue);
				changed = true;
			}
		}
		else if (DEPENDENCY_GROUPID.equals(id)) {
			oldValue = dependency.getGroupId();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				dependency.setGroupId(newValue);
				changed = true;
			}
		}
		else if (DEPENDENCY_VERSION.equals(id)) {
			oldValue = dependency.getVersion();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				dependency.setVersion(newValue);
				changed = true;
			}
		}
		else if (DEPENDENCY_JAR.equals(id)) {
			oldValue = dependency.getJar();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				dependency.setJar(newValue);
				changed = true;
			}
		}
		else if (DEPENDENCY_TYPE.equals(id)) {
			oldValue = dependency.getType();
			newValue = getTypeForIndex(((Integer) value).intValue());
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				dependency.setType(newValue);
				changed = true;
			}
		}
		else if (DEPENDENCY_URL.equals(id)) {
			oldValue = dependency.getUrl();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				dependency.setUrl(newValue);
				changed = true;
			}
		}
		if (changed)
		{
			firePropertyChangeEvent(id.toString(), oldValue, newValue);
		}
	}

	private String getTypeForIndex(int index) {
		if (index < DEPENDENCY_TYPES.length) {
			return DEPENDENCY_TYPES[index];
		}
		return EMPTY_STR;
	}

	public String getLabel(Object o) {
		return dependency.getArtifact() != null ? dependency.getArtifact() : "[undeclared]";
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return dependency;
	}
}
