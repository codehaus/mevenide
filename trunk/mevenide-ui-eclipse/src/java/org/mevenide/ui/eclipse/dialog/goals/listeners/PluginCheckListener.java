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

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;

import org.mevenide.core.AbstractGoalsManager;


public class PluginCheckListener implements ICheckStateListener {
    private CheckboxTableViewer goalsTable;
    private AbstractGoalsManager goalsManager;
    
    public PluginCheckListener(CheckboxTableViewer goalsTable, AbstractGoalsManager goalsManager) {
        this.goalsTable = goalsTable;
        this.goalsManager = goalsManager;
    }
    
	public void checkStateChanged(CheckStateChangedEvent event) {
        String plugin = (String) event.getElement();
		goalsTable.setAllChecked(event.getChecked());
		if (event.getChecked()) {
			goalsManager.addGoal(plugin, null);
		} else {
			goalsManager.removeGoal(plugin, null);
		}
	}
}