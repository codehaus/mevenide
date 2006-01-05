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
package org.mevenide.ui.eclipse.goals.view;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.goals.model.Goal;
import org.mevenide.ui.eclipse.goals.model.Plugin;

/**  
 * 
 * needed to display nice icons
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: GoalsLabelProvider.java,v 1.1 7 sept. 2003 Exp gdodinet 
 * 
 */
public class GoalsLabelProvider extends LabelProvider implements IColorProvider {
	
    private Image plugin16 = Mevenide.getInstance().getImageRegistry().get(IImageRegistry.PLUGIN_OBJ);
    private Image goal16 = Mevenide.getInstance().getImageRegistry().get(IImageRegistry.GOAL_OBJ);
    
    public Image getImage(Object arg0) {
    	if ( arg0 instanceof Plugin )
			return plugin16;
        if ( arg0 instanceof Goal )
			return goal16;
        return null;
    }
    
    public Color getForeground(Object arg0) {
    	if ( arg0 instanceof Plugin ) {
    		//make explicit that plugins are not checkable
    		return MevenideColors.GREY;
    	}
        return MevenideColors.BLACK;
    }
    
    public Color getBackground(Object arg0) {
        return MevenideColors.WHITE;
    }

}
