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
package org.mevenide.ui.eclipse.nature;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PatternBasedMavenLaunchAction extends Action {
    
    private ActionDefinitions definition;
    
    public PatternBasedMavenLaunchAction(ActionDefinitions definition) {
        this.definition = definition;
        setText(definition.toString());
        setToolTipText(definition.getGoalList());
        setEnabled(definition.isEnabled());
    }
    
	public void run(IAction action) {
	    //TODO
	}

}
