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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.mevenide.core.AbstractGoalsManager;


public class PluginSelectionListener implements ISelectionChangedListener {
    private CheckboxTableViewer goalsTable;
    private AbstractGoalsManager goalsManager;
    
    public PluginSelectionListener(CheckboxTableViewer goalsTable, AbstractGoalsManager goalsManager) {
		this.goalsTable = goalsTable;
        this.goalsManager = goalsManager;
	}
    
	public void selectionChanged(SelectionChangedEvent event) {
		Object plugin = ((StructuredSelection) event.getSelection()).getFirstElement();
        goalsTable.setInput(plugin);
		String[] checked = goalsManager.getGoals((String) plugin);
		goalsTable.setCheckedElements(checked);
	}
}