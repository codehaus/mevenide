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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.SourceModification;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.MevenideUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SourceModificationPropertySource extends AbstractPomPropertySource {

    	private static final Log log = LogFactory.getLog(SourceModificationPropertySource.class);

    	private static final String CLASSNAME = "className"; //$NON-NLS-1$
    	
       	private SourceModification sourceModification;
    	
    	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[1];
    	{
    		descriptors[0] = new TextPropertyDescriptor(
    			CLASSNAME,
    			CLASSNAME
    		);
    	}

    	public SourceModificationPropertySource(SourceModification sourceModification) {
    		this.sourceModification = sourceModification;
    	}

    	public Object getEditableValue() {
    		return sourceModification.getClassName();
    	}

    	public IPropertyDescriptor[] getPropertyDescriptors() {
    		return descriptors;
    	}

    	public Object getPropertyValue(Object id) {
    		if (log.isDebugEnabled()) {
    			log.debug("getPropertyValue called: " + id); //$NON-NLS-1$
    		}
    		if (CLASSNAME.equals(id)) {
    			return valueOrEmptyString(sourceModification.getClassName());
    		}
    		return null;
    	}
    	
    	public boolean isPropertySet(Object id) {
    		if (CLASSNAME.equals(id)) {
    			return !isEmpty(sourceModification.getClassName());
    		}
    		return false;
    	}
    	
    	public void resetPropertyValue(Object id) {
    		setPropertyValue(id, EMPTY_STR);
    	}

    	public void setPropertyValue(Object id, Object value) {
    		if (log.isDebugEnabled()) {
    			log.debug("setPropertyValue called: " + id + " = " + value); //$NON-NLS-1$ //$NON-NLS-2$
    		}
    		if (value == null) return;
    		
    		String newValue = value.toString();
    		String oldValue = null;
    		boolean changed = false;
    		if (CLASSNAME.equals(id)) {
    			oldValue = sourceModification.getClassName();
    			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
    			    sourceModification.setClassName(newValue);
    				changed = true;
    			}
    		}
    		if (changed)
    		{
    			firePropertyChangeEvent(id.toString(), oldValue, newValue);
    		}
    	}

    	public String getLabel(Object o) {
    		if (log.isDebugEnabled()) {
    			log.debug("getLabel called for " + o); //$NON-NLS-1$
    		}
    		return sourceModification.getClassName() != null ? sourceModification.getClassName() : Mevenide.getResourceString("AbstractPropertySource.Element.ClassNameNotSet"); //$NON-NLS-1$
    	}

    	/**
    	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
    	 */
    	public Object getSource() {
    		return sourceModification;
    	}
    }

