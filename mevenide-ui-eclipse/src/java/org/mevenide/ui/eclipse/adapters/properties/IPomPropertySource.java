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

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public interface IPomPropertySource extends IPropertySource {
	public Object getSource();
	public void addPropertyChangeListener(IPropertyChangeListener listener);
	public void removePropertyChangeListener(IPropertyChangeListener listener);
}