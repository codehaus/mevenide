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

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.mevenide.ui.eclipse.adapters.properties.PropertyProxy;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ReadOnlyPropertyProxy extends PropertyProxy {
    
    private static final String PROPERTY_NAME = "name";
	private static final String PROPERTY_VALUE = "value";
	
    private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[2]; 
    {
		descriptors[0] = new PropertyDescriptor(PROPERTY_NAME, PROPERTY_NAME);
		descriptors[1] = new PropertyDescriptor(PROPERTY_VALUE, PROPERTY_VALUE);
	}
    
    public ReadOnlyPropertyProxy(String name, String value) {
        super(name, value);
    }
    
    public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}
}
