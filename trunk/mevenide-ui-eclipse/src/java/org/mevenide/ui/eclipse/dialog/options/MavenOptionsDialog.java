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
 */
package org.mevenide.ui.eclipse.dialog.options;

import org.apache.maven.MavenConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.mevenide.InvalidOptionException;
import org.mevenide.OptionsRegistry;
import org.mevenide.ui.eclipse.dialog.options.listeners.OptionSelectionListener;


/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class MavenOptionsDialog {
    
    /** Maven options that the user can control in the context of the plugin */
	private char[] optionCheckBoxes = {'E', 'X', 'e', 'o'};
	
	/**
     * @todo FUNCTIONAL add -D options (grid table)
	 * @param parent
	 * @throws InvalidOptionException
	 */
	public Composite getControl(Composite parent) throws InvalidOptionException {
		Composite pageOptionsControl = new Composite(parent, SWT.NULL);
    
		GridLayout pageOptionsLayout = new GridLayout();
		pageOptionsLayout.numColumns = 4;
    
		pageOptionsControl.setLayout(pageOptionsLayout);
    
		for (int i = 0; i < optionCheckBoxes.length; i++) {
			char option = optionCheckBoxes[i];
			createOptionCheckbox(pageOptionsControl, option);
		}
    
        createVersionLabel(pageOptionsControl);
    
		return pageOptionsControl;
	}

    private void createVersionLabel(Composite parent) {
        GridData layout = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END);

        layout.grabExcessVerticalSpace = true;
        
        Text textField = new Text(parent, SWT.READ_ONLY);
        textField.setText("Maven version : " + MavenConstants.MAVEN_VERSION);
        textField.setLayoutData(layout);
    }

	/**
	 * 
	 * create Checkbox for the following maven options :
	 * 	-E, -X, -e, -o
	 * @param option 
	 * @throws InvalidOptionException
	 */
	private void createOptionCheckbox(Composite parent, char option) throws InvalidOptionException {
		GridData buttonDataLayout = new GridData();
		buttonDataLayout.horizontalSpan = 4;
		buttonDataLayout.grabExcessHorizontalSpace = true;
    
		Button optionButton = new Button(parent, SWT.CHECK);
		optionButton.setLayoutData(buttonDataLayout);
		optionButton.setText(OptionsRegistry.getDescription(option));
		optionButton.setToolTipText(new StringBuffer(" -").append(option).toString());
    
		initializeCheckboxState(optionButton);
    
		optionButton.addSelectionListener(new OptionSelectionListener());
	}

	private void initializeCheckboxState(Button button) {
        //@todo IMPLEMENTME init from saved state
	}

}
