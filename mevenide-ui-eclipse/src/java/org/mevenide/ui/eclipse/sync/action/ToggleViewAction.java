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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.mevenide.ui.eclipse.sync.event.ISynchronizationDirectionListener;

/**
 * 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ToggleViewAction extends Action implements ISynchronizationDirectionListener {
    private static final Log log = LogFactory.getLog(ToggleViewAction.class);

    private int direction;
    
    public ToggleViewAction(int direction) {
        super(null, AS_RADIO_BUTTON);
		this.direction = direction;
    }
    
    public void directionChanged(int direction) {
        boolean masked = (direction & this.direction) != 0;
        boolean oldValue = isChecked();
        log.debug("directionChanged - oldValue = " + isChecked() + "newValue = " + masked);
        setChecked(masked);
        if ( oldValue != masked ) {
        	firePropertyChange(CHECKED, new Boolean(oldValue), new Boolean(masked));
        }
    }

	public int getDirection() {
		return direction;
	}
}
