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
package org.mevenide.ui.eclipse.launch.dialog;


import org.eclipse.jface.wizard.Wizard;
import org.mevenide.IOptionsManager;
import org.mevenide.core.AbstractGoalsManager;
import org.mevenide.ui.eclipse.GoalsManager;
import org.mevenide.ui.eclipse.OptionsManager;
import org.mevenide.ui.eclipse.Runner;
import org.mevenide.ui.eclipse.launch.goals.MavenGoalsPage;
import org.mevenide.ui.eclipse.launch.options.MavenOptionsPage;


/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class RunMavenWizard extends Wizard {

	private MavenGoalsPage goalsPage ;
    private MavenOptionsPage optionsPage; 
    
    private AbstractGoalsManager goalsManager ;
    private IOptionsManager optionsManager;
    
    public RunMavenWizard() {
        super();
        
        setWindowTitle("Run Maven");
        
        goalsManager = new GoalsManager();
        optionsManager = new OptionsManager();
        
        optionsPage = new MavenOptionsPage();
        optionsPage.setOptionsManager(optionsManager);
        addPage(optionsPage);
        
        goalsPage = new MavenGoalsPage();
        goalsPage.setGoalsManager(goalsManager);
        addPage(goalsPage);
    }

    public boolean performFinish() {
       	try {
	       
            goalsManager.save();

			String[] goals = goalsManager.getGoalsToRun();
			String[] options = optionsManager.getOptions();
			
			new Runner().run(options, goals);

			return true;
       	}
       	catch ( Exception e ) {
       		e.printStackTrace();
       		return false;
       	}
    }
    
    


}
