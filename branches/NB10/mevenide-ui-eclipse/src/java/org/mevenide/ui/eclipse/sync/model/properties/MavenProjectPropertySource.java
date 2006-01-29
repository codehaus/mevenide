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
package org.mevenide.ui.eclipse.sync.model.properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.adapters.properties.AbstractPomPropertySource;

/**
 *
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 */
public class MavenProjectPropertySource extends AbstractPomPropertySource {

	private static final Log log = LogFactory.getLog(MavenProjectPropertySource.class);

	static final String DESCRIPTOR_FILE = "POM Descriptor"; //$NON-NLS-1$
	
	private Project mavenProject;
	
	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[1];

    private String descriptorPath;
	

	public MavenProjectPropertySource(Project mavenProject) {
		this.mavenProject = mavenProject;
        descriptorPath = mavenProject.getFile().getAbsolutePath();
        descriptors[0] = new PropertyDescriptor(DESCRIPTOR_FILE, DESCRIPTOR_FILE);
	}

	public Object getEditableValue() {
		return EMPTY_STR;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (DESCRIPTOR_FILE.equals(id)) {
			return valueOrEmptyString(descriptorPath);
		}
		return EMPTY_STR;
	}
	
	
	public boolean isPropertySet(Object id) {
		if (DESCRIPTOR_FILE.equals(id)) {
			return !isEmpty(descriptorPath);
		}
		return false;
	}
	
	public void resetPropertyValue(Object id) {
		setPropertyValue(id, descriptorPath);
	}

	public void setPropertyValue(Object id, Object value) {
	}

	public String getLabel(Object o) {
		if (log.isDebugEnabled()) {
			log.debug("getLabel called for " + o); //$NON-NLS-1$
		}
		return descriptorPath != null ? descriptorPath : Mevenide.getResourceString("MavenProjectSource.CannotResolve.DescriptorPath"); //$NON-NLS-1$
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return mavenProject;
	}
}
