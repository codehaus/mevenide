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

package org.mevenide.ui.eclipse.dialog.options;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.mevenide.InvalidOptionException;
import org.mevenide.ui.eclipse.MavenPlugin;

/**
 * Created on 01 feb. 03	
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 */
public class MavenOptionsPage extends WizardPage {
	MavenOptionsDialog optionsControl ;
		
	public MavenOptionsPage() {
        super(	MavenPlugin.getResourceString("MavenOptionsPage.name"), 
				MavenPlugin.getResourceString("MavenOptionsPage.title"), 
                MavenPlugin.getImageDescriptor("maven-org-64.gif"));
        setDescription(MavenPlugin.getResourceString("MavenOptionsPage.description"));
        //setImageDescriptor(MavenPlugin.getImageDescriptor("sample.gif"));
        optionsControl = new MavenOptionsDialog();
    }

    public void createControl(Composite parent) {
        try {
	        setControl(optionsControl.getControl(parent));
        }
        catch ( InvalidOptionException ex ) {
            //log exception to console for now
            ex.printStackTrace();
        }
    }
   

  
}
