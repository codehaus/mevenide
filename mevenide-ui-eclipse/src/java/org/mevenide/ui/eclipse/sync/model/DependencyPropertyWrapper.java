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
package org.mevenide.ui.eclipse.sync.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.views.properties.IPropertySource;
import org.mevenide.ui.eclipse.editors.properties.PropertyProxy;


/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyPropertyWrapper implements IPropertyChangeListener, IAdaptable {
	private static final Log log = LogFactory.getLog(DependencyPropertyWrapper.class); 
	
	private Dependency dependency;
	private String formattedProperty;
	
	public DependencyPropertyWrapper(Dependency dependency, String formattedProperty) {
		log.debug("formatted property = " + formattedProperty);
		this.dependency = dependency;
		this.formattedProperty = formattedProperty;
	}
	
	public Object getAdapter(Class adapter) {
		if ( adapter == IPropertySource.class ) {
			return new PropertyProxy(formattedProperty);
		}
		return null;
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		setFormattedProperty((String) ((PropertyProxy) event.getSource()).getSource());
    }

	public String getFormattedProperty() {
		return formattedProperty;
	}
	
	public void setFormattedProperty(String formattedProperty) {
		this.formattedProperty = formattedProperty;
	}
	
	public Dependency getDependency() {
		return dependency;
	}
}
