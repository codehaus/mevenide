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
package org.mevenide.ui.eclipse.sync.event;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: SynchronizationConstraintEvent.java,v 1.1 14 mars 2004 Exp gdodinet 
 * 
 */
public class SynchronizationConstraintEvent {
	
	public static final String WRITE_PROPERTIES = "WRITE_PROPERTIES";   //$NON-NLS-1$
	
	private String constraintId;
	private boolean newValue;
	
	//constraintId is cheap but should do for now
	public SynchronizationConstraintEvent(String constraintId, boolean newValue) {
		this.constraintId = constraintId;
		this.newValue = newValue;
	}
	
	public String getConstraintId() {
		return constraintId;
	}
	
	public boolean getNewValue() {
		return newValue;
	}
}
