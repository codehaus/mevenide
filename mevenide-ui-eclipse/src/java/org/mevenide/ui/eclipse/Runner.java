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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
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
	Mevenide plugin = Mevenide.getPlugin();

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
	protected void initEnvironment() throws Exception  {
		if ( plugin.getMavenHome() == null || plugin.getMavenHome().trim().equals("") ) { 
			MessageBox dialog = new MessageBox (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.OK);
			dialog.setText ("Mevenide");
			dialog.setMessage ("Please set preferences before running Maven.");
			dialog.open ();
			throw new Exception("Maven Home has not been set");
	    }
	    else {
		    plugin.initEnvironment();
	    }
	}

	/**
     * @param options
	 * @param goals
	 * @throws Exception
	 */
	protected void launchVM(String[] options, String[] goals) throws Exception {
	
	    VMLauncherUtility.runVM(
			"com.werken.forehead.Forehead",
			ArgumentsManager.getMavenClasspath(),
		    ArgumentsManager.getVMArgs(this),
	        getMavenArgs(options, goals));
		
	}

}
