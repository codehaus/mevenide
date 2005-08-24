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
package org.mevenide.ui.eclipse.sync.model.properties;

import org.apache.maven.project.Dependency;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.adapters.properties.AbstractPomPropertySource;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ReadOnlyDependencyPropertySource extends AbstractPomPropertySource {

	public static final String DEPENDENCY_ARTIFACTID = "artifactId"; //$NON-NLS-1$
	public static final String DEPENDENCY_GROUPID = "groupId"; //$NON-NLS-1$
	public static final String DEPENDENCY_VERSION = "version"; //$NON-NLS-1$
	public static final String DEPENDENCY_JAR = "jar"; //$NON-NLS-1$
	public static final String DEPENDENCY_TYPE = "type"; //$NON-NLS-1$
	public static final String DEPENDENCY_URL = "url"; //$NON-NLS-1$

	private Dependency dependency;
	
	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[6];
	{
		descriptors[0] = new PropertyDescriptor(DEPENDENCY_ARTIFACTID, DEPENDENCY_ARTIFACTID);
		descriptors[1] = new PropertyDescriptor(DEPENDENCY_GROUPID, DEPENDENCY_GROUPID);
		descriptors[2] = new PropertyDescriptor(DEPENDENCY_VERSION, DEPENDENCY_VERSION);
		descriptors[3] = new PropertyDescriptor(DEPENDENCY_JAR, DEPENDENCY_JAR );
		descriptors[4] = new PropertyDescriptor(DEPENDENCY_TYPE, DEPENDENCY_TYPE);
		descriptors[5] = new PropertyDescriptor(DEPENDENCY_URL, DEPENDENCY_URL);
	}

	public ReadOnlyDependencyPropertySource(Dependency dependency) {
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
			return valueOrEmptyString(dependency.getType());
		}
		else if (DEPENDENCY_URL.equals(id)) {
			return valueOrEmptyString(dependency.getUrl());
		}
		return null;
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
	}

	public String getLabel(Object o) {
		return dependency.getArtifact() != null ? dependency.getArtifact() : Mevenide.getResourceString("ReadOnlyDependencyPropertySource.Undeclared"); //$NON-NLS-1$
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return dependency;
	}
}
