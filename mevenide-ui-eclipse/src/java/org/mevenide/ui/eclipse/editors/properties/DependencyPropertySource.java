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
 *       "This product includes software dependencyd under 
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

	private static final String DEPENDENCY_ARTIFACTID = "artifactId";
	private static final String DEPENDENCY_GROUPID = "groupId";
	private static final String DEPENDENCY_VERSION = "version";
	private static final String DEPENDENCY_JAR = "jar";
	private static final String DEPENDENCY_TYPE = "type";
	private static final String DEPENDENCY_URL = "url";

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
