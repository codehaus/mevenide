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
package org.mevenide.ui.eclipse.dialog.goals;



import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import org.mevenide.core.AbstractGoalsManager;
import org.mevenide.ui.eclipse.MavenPlugin;

/**
 * just poor delegation to MavenGoalsDialog 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class MavenGoalsPage extends WizardPage {
	/** instance of MavenGoalsDialog that encapsulates all ui stuff */
	private MavenGoalsDialog dialog = null;
	
    public MavenGoalsPage() {
        super(	MavenPlugin.getResourceString("MavenGoalsPage.name"), 
			   	MavenPlugin.getResourceString("MavenGoalsPage.title"), 
			   	MavenPlugin.getImageDescriptor("maven-org-64.gif"));
        setDescription(MavenPlugin.getResourceString("MavenGoalsPage.description"));
        dialog = new MavenGoalsDialog();
    }

    /**
     * @see org.eclipse.jface.dialogs.DialogPage#createConrol()
     */
    public void createControl(Composite parent) {
        try {
            setControl(dialog.getControl(parent));
        }
        catch ( Exception ex ) {
            //log exception to console for now
            ex.printStackTrace();
        }
    }
    
    /** 
     * just a delagation
     * @param manager
     */
    public void setGoalsManager(AbstractGoalsManager manager) {
        dialog.setGoalsManager(manager);
    }

} 