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
package org.mevenide.ui.eclipse.goals.model;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mevenide.goals.grabber.DefaultGoalsGrabber;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.goals.manager.GoalsGrabbersManager;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: GoalsProvider.java,v 1.1 7 sept. 2003 Exp gdodinet 
 * 
 */
public class GoalsProvider implements ITreeContentProvider {
	
    private static final Log log = LogFactory.getLog(GoalsProvider.class);

    private static final String POM_NAME = "project.xml"; //$NON-NLS-1$
	
	private String basedir;
	
	private IGoalsGrabber goalsGrabber;
	private IGoalsGrabber defaultGrabber;
	
	public GoalsProvider() {
		try {
			goalsGrabber = new DefaultGoalsGrabber();
			defaultGrabber = new DefaultGoalsGrabber();
		} 
		catch (Exception e) {
			log.debug(e);
		}
	}
	
    public String getBasedir() {
        return basedir;
    }

    public void setBasedir(String basedir) throws Exception {
        this.basedir = basedir;
		try {
            goalsGrabber = GoalsGrabbersManager.getGoalsGrabber(new File(basedir, POM_NAME).getAbsolutePath());
        }
        catch (Exception e) {
            goalsGrabber = defaultGrabber;
            log.error("Unable to set basedir. It is highly probable that maven.xml is badly formed."); //$NON-NLS-1$
        }
    }

    public Object[] getChildren(Object parent) {
    	if ( parent == Element.NULL_ROOT ) {
    		Plugin[] plugins = new Plugin[goalsGrabber.getPlugins().length];
    		
    		for (int i = 0; i < plugins.length; i++) {
    			Plugin plugin = new Plugin();
    			plugin.setName(goalsGrabber.getPlugins()[i]);
                plugins[i] = plugin;
            } 
      	    return plugins;
    	}
    	if ( parent instanceof Plugin ) {
			String pluginName = ((Plugin) parent).getName();
			Goal[] goals = new Goal[goalsGrabber.getGoals(pluginName).length];
			for (int i = 0; i < goals.length; i++) {
				Goal goal = new Goal();
				goal.setName(goalsGrabber.getGoals(pluginName)[i]);
				goal.setPlugin((Plugin) parent);
				goals[i] = goal;
			} 
			return goals;
    	}
      	else return null;
    }

    public Object getParent(Object element) {
    	if ( element instanceof Plugin ) {
    		return Element.NULL_ROOT;
    	}
    	if ( element instanceof Goal ) {
    		return ((Goal) element).getPlugin();
    	}
        return null;
    }

    public boolean hasChildren(Object arg0) {
        return 
        	( arg0  == Element.NULL_ROOT && goalsGrabber.getPlugins().length > 0 )
        	|| ( arg0 instanceof Plugin && goalsGrabber.getGoals(((Plugin) arg0).getName()).length > 0);
    }

    public Object[] getElements(Object arg0) {
        return getChildren(arg0);
    }

    public void dispose() {
        
    }

    public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
        
    }
    

    public IGoalsGrabber getGoalsGrabber() {
        return goalsGrabber;
    }

}
