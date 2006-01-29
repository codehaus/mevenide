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
package org.mevenide.ui.eclipse.goals.model;

import org.mevenide.ui.eclipse.Mevenide;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: Goal.java,v 1.1 7 sept. 2003 Exp gdodinet 
 * 
 */
public class Goal extends Element {
	
    public static final String SEPARATOR = ":"; //$NON-NLS-1$
    
    public static final String DEFAULT_GOAL = Mevenide.getResourceString("Goal.Default");  //$NON-NLS-1$
	
	private Plugin plugin ;
	
	public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

	public String getFullyQualifiedName() {
		return (plugin != null ? plugin.getName() + SEPARATOR : "") + getName(); //$NON-NLS-1$
	}
}
