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
package org.mevenide.ui.eclipse.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *
 */
public class MevenidePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private PreferencesManager manager;
    private MevenidePreferenceDialog dialog;
    
    public MevenidePreferencePage() {
        super(Mevenide.getResourceString("MavenPreferencePage.title"));
        //setImageDescriptor(MavenPlugin.getImageDescriptor("sample.gif"));
		manager = new PreferencesManager();
		manager.loadPreferences();
        dialog = new MevenidePreferenceDialog(manager);
       	
    }

	

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		dialog.setJavaHome(manager.getValue("java.home"));
		dialog.setMavenHome(manager.getValue("maven.home"));
		dialog.setMavenRepo(manager.getValue("maven.repo"));
		
		dialog.setCheckTimestamp(manager.getBooleanValue("mevenide.checktimestamp"));
		
		return dialog.createContent(composite);
	}
  
  
    public boolean performOk() {
       if ( dialog.canFinish() ) {
        	return false;
        }
        else {
            return finish();
        }
    }
    
	private boolean finish() {
		dialog.update();
		
		manager.setValue("maven.home", dialog.getMavenHome());
		manager.setValue("java.home", dialog.getJavaHome());
		manager.setValue("maven.repo", dialog.getMavenRepo());
		manager.setBooleanValue("mevenide.checktimestamp", dialog.getCheckTimestamp());
		
		return manager.store();
	}
	
	
	
	public void init(IWorkbench workbench) {
    }
}
    