/*
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr) - Cross-Systems
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
package org.mevenide.ui.eclipse.launch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jface.wizard.Wizard;
import org.mevenide.core.AbstractRunner;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class LaunchWizard extends Wizard {
	
	private LaunchWizardPage launchPage;
	
	public LaunchWizard() {
		launchPage = new LaunchWizardPage();
		addPage(launchPage);
	}

	public boolean performFinish() {
		try {
            
            launchPage.performFinish();
        
			AbstractRunner.getRunner().run(getOptions(), getGoals());
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	
    private String[] getOptions() {
        Map optionsMap = launchPage.getOptionsMap();
		Map sysProperties = launchPage.getSysProperties();
		
		List optionsList = new ArrayList();
		
		Set optionsKeySet = optionsMap.keySet();
		Iterator optionsIterator = optionsKeySet.iterator();
		while ( optionsIterator.hasNext() ) {
			Character character = (Character) optionsIterator.next();
			boolean hasOption = ((Boolean) optionsMap.get(character)).booleanValue();
			if ( hasOption ) {
				optionsList.add("-" + character.toString());
			}  
		}
		
		Set sysPropertiesKeys = sysProperties.keySet();
		Iterator sysPropertiesIterator = sysPropertiesKeys.iterator();
		while ( sysPropertiesIterator.hasNext() ) {
			String key = (String) sysPropertiesIterator.next();
			String value = (String) sysProperties.get(key);
			optionsList.add(key + "=" + value);
		}
		
		String[] options = new String[optionsList.size()];
		for (int i = 0; i < options.length; i++) {
            options[i] = (String) optionsList.get(i);
        }
		
		return options;
    }

    private String[] getGoals() {
        String goals = launchPage.getGoals();
        StringTokenizer tokenizer = new StringTokenizer(goals, " ");
        String[] tokens = new String[tokenizer.countTokens()];
        int i = 0;
        while ( tokenizer.hasMoreTokens() ) {
        	tokens[i] = tokenizer.nextToken();
        	i++;
        } 
        
        return tokens;
    }

    public boolean performCancel() {
        return super.performCancel();
    }

}
