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
package org.mevenide.ui.eclipse.dialog.goals.listeners;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.mevenide.IGoalsGrabber;
import org.mevenide.ui.eclipse.dialog.goals.model.MevenideProvider;


public class RefreshListener extends SelectionAdapter {
    private CheckboxTableViewer pluginsTable;
    private IGoalsGrabber goalsGrabber;
   
    public RefreshListener(CheckboxTableViewer pluginsTable, IGoalsGrabber goalsGrabber) {
		this.pluginsTable = pluginsTable;
        this.goalsGrabber = goalsGrabber;
	}
    
	public void widgetSelected(SelectionEvent e) {
		try {
            goalsGrabber.load();
            pluginsTable.setInput(MevenideProvider.ROOT);
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}