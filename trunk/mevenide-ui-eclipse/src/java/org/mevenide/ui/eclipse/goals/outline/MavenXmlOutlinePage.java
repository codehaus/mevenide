/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.ui.eclipse.goals.outline;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.goals.filter.CustomPatternFilter;
import org.mevenide.ui.eclipse.goals.filter.GlobalGoalFilter;
import org.mevenide.ui.eclipse.goals.filter.GoalFilterDialog;
import org.mevenide.ui.eclipse.goals.filter.GoalOriginFilter;
import org.mevenide.ui.eclipse.goals.model.Element;
import org.mevenide.ui.eclipse.goals.model.Goal;
import org.mevenide.ui.eclipse.goals.model.GoalsProvider;
import org.mevenide.ui.eclipse.goals.viewer.GoalsLabelProvider;
import org.mevenide.ui.eclipse.launch.configuration.MavenLaunchShortcut;
import org.mevenide.ui.eclipse.preferences.PreferencesManager;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: MavenXmlOutlinePage.java,v 1.1 30 mars 2004 Exp gdodinet 
 * 
 */
public class MavenXmlOutlinePage extends Page implements IContentOutlinePage {
	private static final Log log = LogFactory.getLog(MavenXmlOutlinePage.class);
	
	private static final String TOGGLE_OFFLINE_ID = "TOGGLE_OFFLINE_ID";
	private static final String TOGGLE_FILTER_ORIGIN_ID = "TOGGLE_FILTER_ORIGIN_ID";
	private static final String TOGGLE_CUSTOM_FILTER_ID = "TOGGLE_CUSTOM_FILTER_ID";
	private static final String RUN_GOAL_ID = "RUN_GOAL_ID";
	private static final String OPEN_FILTER_DIALOG_ID = "OPEN_FILTER_DIALOG_ID";
	
	
	private Composite control;
	
	private TreeViewer goalsViewer;
    private GoalsLabelProvider goalsLabelProvider;
	private GoalsProvider goalsProvider;
	
	private CustomPatternFilter patternFilter;
	private GlobalGoalFilter globalGoalFilter;
	private GoalOriginFilter goalOriginFilter; 
	
	private Menu menu;
	private IAction toggleOfflineAction, 
	                openFilterDialogAction, 
					filterOriginShortcutAction, 
					toggleCustomFilteringAction;
	private IAction runGoalAction;
	
	private boolean runOffline;
	
	private String basedir; 
	private IPath basedirPath;  
	
	public MavenXmlOutlinePage(IFileEditorInput input) {
		this.basedir = new File(input.getFile().getLocation().toOSString()).getParent();
		this.basedirPath = input.getFile().getParent().getFullPath();
	}
	
	public void createControl(Composite parent) {
		try {
			control = new Composite(parent, SWT.NONE);
        	control.setLayout(new GridLayout());
        	
			GridData gridData = new GridData(GridData.FILL_BOTH);
			gridData.grabExcessVerticalSpace = true;
			gridData.grabExcessHorizontalSpace = true;
            gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.BEGINNING;
			control.setLayoutData(gridData);

            goalsViewer = createViewer(control);
            
			configureViewer();
			
			goalsViewer.setInput(Element.NULL_ROOT);
        }
        catch (Exception e) {
            log.error("Unable to create goals TreeViewer host : ", e);
            throw new RuntimeException(e);
        }
	}
	
	private TreeViewer createViewer(Composite parent) throws Exception {
		
    	TreeViewer viewer = new TreeViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL);
    	
    	GridData gridData = new GridData(GridData.FILL_BOTH | SWT.V_SCROLL | SWT.H_SCROLL);
    	gridData.grabExcessVerticalSpace = true;
    	gridData.grabExcessHorizontalSpace = true;
    	gridData.heightHint = 300;
    	viewer.getTree().setLayoutData(gridData);
    	
        return viewer;
    }
	
	private void setupFilters() {
		patternFilter = new CustomPatternFilter();
		goalsViewer.addFilter(patternFilter);

		globalGoalFilter = new GlobalGoalFilter();
		goalsViewer.addFilter(globalGoalFilter);
		
		goalOriginFilter = new GoalOriginFilter();
		goalsViewer.addFilter(goalOriginFilter);
	}

	private void setupProviders() throws Exception {
		goalsProvider = new GoalsProvider();
    	goalsProvider.setBasedir(basedir);
		goalsViewer.setContentProvider(goalsProvider);

		goalsLabelProvider = new GoalsLabelProvider() {
		    public Color getForeground(Object arg0) {
                return MevenideColors.BLACK;
            }
		};
		goalsViewer.setLabelProvider(goalsLabelProvider);
		
	}

	private void configureViewer() throws Exception {
        goalsViewer.getTree().addListener (SWT.MouseDoubleClick, 
        	new Listener () {
        		public void handleEvent (Event event) {
					//shortcut to run selected goal
        			runMaven();
        		}
        	}
        );
        
		setupProviders();
    	setupFilters();
    	setupActions();
    }
	
	private void createContextMenuManager() {
		MenuManager manager= new MenuManager();
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager menuManager) {
				contextualMenuAboutToShow(menuManager);
			}
		});
        menu = manager.createContextMenu(goalsViewer.getTree());
		goalsViewer.getTree().setMenu(menu);
	}

	private void createToolBarManager() {
		IToolBarManager toolBarManager = this.getSite().getActionBars().getToolBarManager();
        toolBarManager.add(this.toggleOfflineAction);
        toolBarManager.add(this.toggleCustomFilteringAction);
        toolBarManager.add(this.filterOriginShortcutAction);
	}
	
	private void createMenuManager() {
		IMenuManager topLevelMenuManager = getSite().getActionBars().getMenuManager();
		topLevelMenuManager.add(openFilterDialogAction);
		topLevelMenuManager.add(runGoalAction);
	}
	
	private void setupActions() {
		createFilterActions();
		createRunActions();
		
		createToolBarManager();
        createMenuManager();
        createContextMenuManager();
	}
	
	private void createRunActions() {
		toggleOfflineAction = new Action(null, Action.AS_CHECK_BOX) {
			public void run() {
				runOffline = isChecked();
				setToolTipText(isChecked() ? "online mode" : "offline mode");
			}
		};
		toggleOfflineAction.setToolTipText(toggleOfflineAction.isChecked() ? "online mode" : "offline mode");
		toggleOfflineAction.setId(TOGGLE_OFFLINE_ID);
		toggleOfflineAction.setImageDescriptor(Mevenide.getImageDescriptor("offline.gif"));
		
		runGoalAction = new Action(null) {
			public void run() {
				runMaven();
			}
		};
		runGoalAction.setText("Run Goal");
		runGoalAction.setToolTipText("Run Goal");
		runGoalAction.setId(RUN_GOAL_ID);
		runGoalAction.setImageDescriptor(Mevenide.getImageDescriptor("run_goal.gif"));
	}

	private void createFilterActions() {
		filterOriginShortcutAction = new Action(null, Action.AS_CHECK_BOX) {
			public void run() {
				goalOriginFilter.setEnable(isChecked());
				setToolTipText(isChecked() ? "Show global goals" : "Hide global goals");
				goalsViewer.refresh(false);
			}
		}; 
		filterOriginShortcutAction.setChecked(goalOriginFilter.isEnabled());
		filterOriginShortcutAction.setToolTipText(filterOriginShortcutAction.isChecked() ? "Show global goals" : "Hide global goals");
		filterOriginShortcutAction.setId(TOGGLE_FILTER_ORIGIN_ID);
		filterOriginShortcutAction.setImageDescriptor(Mevenide.getImageDescriptor("filter_global_goals.gif"));
		
		toggleCustomFilteringAction = new Action(null, Action.AS_CHECK_BOX) {
			public void run() {
				patternFilter.apply(isChecked());
				setToolTipText((isChecked() ? "Disable " : "Enable ") + "custom regex filters");
				PreferencesManager.getManager().setBooleanValue(CustomPatternFilter.APPLY_CUSTOM_FILTERS_KEY, isChecked());
				PreferencesManager.getManager().store();
				goalsViewer.refresh(false); 
			}
		};
		toggleCustomFilteringAction.setChecked(goalOriginFilter.isEnabled());
		toggleCustomFilteringAction.setToolTipText((toggleCustomFilteringAction.isChecked() ? "Disable " : "Enable ") + "custom regex filters");
		toggleCustomFilteringAction.setId(TOGGLE_CUSTOM_FILTER_ID);
		toggleCustomFilteringAction.setImageDescriptor(Mevenide.getImageDescriptor("toggle_regex_filter.gif"));

		openFilterDialogAction = new Action(null) {
			public void run() {
			    openFilterDialog();
			}
		};
		openFilterDialogAction.setText("Filter...");
		openFilterDialogAction.setToolTipText("Open goal filter dialog");
		openFilterDialogAction.setId(OPEN_FILTER_DIALOG_ID);
		openFilterDialogAction.setImageDescriptor(Mevenide.getImageDescriptor("open_filter_dialog.gif"));
	}

	private void contextualMenuAboutToShow(IMenuManager menuManager) {
		Object selection = ((StructuredSelection) goalsViewer.getSelection()).getFirstElement();
	    if ( selection instanceof Goal ) {
		    Goal selectedNode = (Goal) selection;
			menuManager.add(this.runGoalAction);		
		}
	}
	
	private void openFilterDialog() {
		GoalFilterDialog dialog = new GoalFilterDialog();
		int dialogResult = dialog.open();
		
		if ( dialogResult == Window.OK ) {
			
			boolean applyCustomFiltering = dialog.shouldApplyCustomFilters();
			patternFilter.setPatternFilters(dialog.getRegex());
			patternFilter.apply(applyCustomFiltering);
			
			toggleCustomFilteringAction.setChecked(applyCustomFiltering);
			
			globalGoalFilter.setFilteredGoals(dialog.getFilteredGoals());
			
			goalsViewer.refresh(false);
		}
	}
	
	private void runMaven() {
		if ( goalsViewer.getSelection() != null && ((StructuredSelection) goalsViewer.getSelection()).getFirstElement() instanceof Goal) {
			Goal goal = (Goal) ((StructuredSelection) goalsViewer.getSelection()).getFirstElement();
			MavenLaunchShortcut shortcut = new MavenLaunchShortcut();
			shortcut.setShowDialog(false);
			shortcut.setGoalsToRun(goal.getFullyQualifiedName());
			shortcut.setOffline(runOffline);
			shortcut.launch(basedirPath);
		}
	}
	
	public void dispose() {
	}
	
	public Control getControl() {
		return control;
	}
	
	public void setActionBars(IActionBars actionBars) {
		super.setActionBars(actionBars);
	}
	
	public void setFocus() {
	}
	
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
	}
	
	public ISelection getSelection() {
		return goalsViewer.getSelection();
	}
	
	public void setSelection(ISelection selection) {
	}
	
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
	}
	
	public void forceRefresh() {
		try {
			goalsProvider.setBasedir(basedir);
		} 
		catch (Exception e) {
			log.error("unable to refresh goalsProvider", e);
		}
		goalsViewer.refresh(true);
	}
}