/* 
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 */
package org.mevenide.ui.eclipse;

import org.mevenide.Environment;
import org.mevenide.MevenideException;
import org.mevenide.core.AbstractRunner;
import org.mevenide.core.ArgumentsManager;
import org.mevenide.ui.eclipse.jdt.launching.VMLauncherUtility;

//import com.werken.forehead.Forehead;

/**
 * @author Gilles Dodinet  
 * @version $Id$
 */
public class Runner extends AbstractRunner {
	MavenPlugin plugin = MavenPlugin.getPlugin();

	public Runner() throws MevenideException {
		super();
	}

    /**
	 * @see org.mevenide.core.AbstractRunner#getEffectiveDirectory()
	 */
	protected String getBasedir() {
        return plugin.getCurrentDir();
	}

	/**
	 * @see org.mevenide.core.AbstractRunner#initEnvironment()
	 */
	protected void initEnvironment() {
        Environment.setMavenHome(plugin.getMavenHome()); 
        Environment.setJavaHome(plugin.getJavaHome());
	}

	/**
     * @param options
	 * @param goals
	 * @throws Exception
	 */
	protected void launchVM(String[] options, String[] goals) throws Exception {
	    //System.out.println("launching vm");
       
	    VMLauncherUtility.runVM(
			"com.werken.forehead.Forehead",
			ArgumentsManager.getMavenClasspath(),
		    ArgumentsManager.getVMArgs(this),
	        getMavenArgs(options, goals));
	}

}
