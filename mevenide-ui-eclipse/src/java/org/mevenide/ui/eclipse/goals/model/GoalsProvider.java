/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.ui.eclipse.goals.model;

import java.io.File;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mevenide.Environment;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.goals.manager.GoalsGrabbersManager;
import org.mevenide.ui.eclipse.Mevenide;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: GoalsProvider.java,v 1.1 7 sept. 2003 Exp gdodinet 
 * 
 */
public class GoalsProvider implements ITreeContentProvider {

	private String basedir;
	private IGoalsGrabber goalsGrabber;
	
    public String getBasedir() {
        return basedir;
    }

    public void setBasedir(String basedir) throws Exception {
        this.basedir = basedir;
		String mavenLocalHome = Mevenide.getPlugin().getMavenLocalHome();
		Environment.setMavenLocalHome(mavenLocalHome);
        goalsGrabber = GoalsGrabbersManager.getGoalsGrabber(new File(basedir, "project.xml").getAbsolutePath());
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
