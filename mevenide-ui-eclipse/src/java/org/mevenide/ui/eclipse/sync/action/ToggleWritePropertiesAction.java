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
package org.mevenide.ui.eclipse.sync.action;

import org.eclipse.jface.action.Action;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: ToggleWritePropertiesAction.java,v 1.1 14 mars 2004 Exp gdodinet 
 * 
 */
public class ToggleWritePropertiesAction extends Action {
	
	public ToggleWritePropertiesAction() {
        super(null, AS_CHECK_BOX);
	}
	
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		super.firePropertyChange(propertyName, oldValue, newValue);
		if ( CHECKED.equals(propertyName) ) {
			String tooltip;
			if ( ((Boolean) newValue).booleanValue() ) {
				tooltip = "Do not override project.properties";
			}
			else {
				tooltip = "Override project.properties";
			}
			setToolTipText(tooltip);
		}
	}
}
