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

import java.io.File;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.mevenide.core.AbstractGoalsGrabber;
import org.mevenide.core.AbstractGoalsManager;
import org.mevenide.core.IGoalsGrabber;
import org.mevenide.ui.eclipse.MavenPlugin;
import org.mevenide.ui.eclipse.dialog.goals.listeners.GoalCheckListener;
import org.mevenide.ui.eclipse.dialog.goals.listeners.PluginCheckListener;
import org.mevenide.ui.eclipse.dialog.goals.listeners.DeselectAllListener;
import org.mevenide.ui.eclipse.dialog.goals.listeners.RefreshListener;
import org.mevenide.ui.eclipse.dialog.goals.listeners.SelectAllListener;
import org.mevenide.ui.eclipse.dialog.goals.listeners.PluginSelectionListener;
import org.mevenide.ui.eclipse.dialog.goals.model.GoalsProvider;
import org.mevenide.ui.eclipse.dialog.goals.model.MevenideProvider;
import org.mevenide.ui.eclipse.dialog.goals.model.PluginsProvider;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 * @todo FUNCTIONAL gray state and bidirectionnal dependencies between plugins and related goals 
 */
public class MavenGoalsDialog {

	private CheckboxTableViewer pluginsTableViewer = null;
	private CheckboxTableViewer goalsTableViewer = null;

	private IGoalsGrabber goalsGrabber;
	private AbstractGoalsManager goalsManager;

	public MavenGoalsDialog() {
		try {
			File xmlGoals = new File(MavenPlugin.getPlugin().getEffectiveDirectory(), "maven-goals.xml");
			goalsGrabber = AbstractGoalsGrabber.getGrabber(xmlGoals.getAbsolutePath());
		} 
        catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Composite getControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;

		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(layout);
        
        createPluginsTable(container);
        createGoalsTable(container);
        
        addTableEventListeners();
           
		createRefreshButton(container);
        createSelectionButtons(container);

		initState();

		return container;
	}

    private void addTableEventListeners() {
        goalsTableViewer.addCheckStateListener(new GoalCheckListener(goalsTableViewer, goalsManager));
    }

	private void initState() {
		pluginsTableViewer.setCheckedElements(getSavedState());
        pluginsTableViewer.addSelectionChangedListener(new PluginSelectionListener(goalsTableViewer, goalsManager));
        pluginsTableViewer.addCheckStateListener(new PluginCheckListener(goalsTableViewer, goalsManager));
	}

	private void createSelectionButtons(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		
        layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		
        composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button selectAllButton = new Button(composite, SWT.PUSH);
		selectAllButton.setText("Select all");
		selectAllButton.addSelectionListener(new SelectAllListener(goalsTableViewer, goalsManager));
		selectAllButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		Button deselectAllButton = new Button(composite, SWT.PUSH);
		deselectAllButton.setText("Deselect all");
		deselectAllButton.addSelectionListener(new DeselectAllListener(goalsTableViewer, goalsManager));
		deselectAllButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	}

	private void createRefreshButton(Composite parent) {
		GridData btnDataLayout = new GridData();
		Button refreshButton = new Button(parent, SWT.PUSH);
		refreshButton.setText("Refresh");

		refreshButton.setLayoutData(btnDataLayout);
		refreshButton.addSelectionListener(new RefreshListener(pluginsTableViewer, goalsGrabber));
	}

	private void createGoalsTable(Composite parent) {
		GridData subGoalsDataLayout = new GridData(GridData.FILL_BOTH);
		subGoalsDataLayout.heightHint = 20;

		Table goalsTable = new Table(parent, SWT.MULTI | SWT.CHECK | SWT.BORDER);

		goalsTable.setLayoutData(subGoalsDataLayout);

		goalsTableViewer = new CheckboxTableViewer(goalsTable);

		GoalsProvider goalsProvider = new GoalsProvider(goalsGrabber);
        goalsTableViewer.setContentProvider(goalsProvider);
		goalsTableViewer.setLabelProvider(goalsProvider);
	}

	private void createPluginsTable(Composite parent) {
		//little hack to render the table correctly (heightHint && FILL_BOTH)
		GridData primaryGoalsDataLayout = new GridData(GridData.FILL_BOTH);
		primaryGoalsDataLayout.heightHint = 20;

		Table pluginsTable = new Table(parent, SWT.MULTI | SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

        pluginsTable.setLayoutData(primaryGoalsDataLayout);

		pluginsTableViewer = new CheckboxTableViewer(pluginsTable);

		PluginsProvider pluginsProvider = new PluginsProvider(goalsGrabber);

		pluginsTableViewer.setContentProvider(pluginsProvider);
		pluginsTableViewer.setLabelProvider(pluginsProvider);
		pluginsTableViewer.setInput(MevenideProvider.ROOT);
	}

    public AbstractGoalsManager getGoalsManager() {
		return goalsManager;
	}

    /**
     * @return String[] the previously checked plugins
     */
	private String[] getSavedState() {
        //@todo IMPLEMENTME
		return new String[0];
	}

	/**
	 * IOC pattern
	 */
	public void setGoalsManager(AbstractGoalsManager manager) {
		goalsManager = manager;
	}

}
