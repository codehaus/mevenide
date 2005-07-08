/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.mevenide.context.IQueryContext;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.goals.filter.CustomPatternFilter;
import org.mevenide.ui.eclipse.goals.filter.GlobalGoalFilter;
import org.mevenide.ui.eclipse.goals.filter.GoalFilterDialog;
import org.mevenide.ui.eclipse.goals.filter.GoalOriginFilter;
import org.mevenide.ui.eclipse.goals.model.Element;
import org.mevenide.ui.eclipse.goals.model.Goal;
import org.mevenide.ui.eclipse.goals.model.GoalsProvider;
import org.mevenide.ui.eclipse.goals.view.GoalsLabelProvider;
import org.mevenide.ui.eclipse.launch.configuration.MavenLaunchShortcut;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: MavenXmlOutlinePage.java,v 1.1 30 mars 2004 Exp gdodinet 
 * 
 */
public class MavenXmlOutlinePage extends Page implements IContentOutlinePage {
	private static final Log log = LogFactory.getLog(MavenXmlOutlinePage.class);
	
	private static final String TOGGLE_OFFLINE_ID = "TOGGLE_OFFLINE_ID"; //$NON-NLS-1$
	private static final String TOGGLE_FILTER_ORIGIN_ID = "TOGGLE_FILTER_ORIGIN_ID"; //$NON-NLS-1$
	private static final String TOGGLE_CUSTOM_FILTER_ID = "TOGGLE_CUSTOM_FILTER_ID"; //$NON-NLS-1$
	private static final String RUN_GOAL_ID = "RUN_GOAL_ID"; //$NON-NLS-1$
	private static final String OPEN_FILTER_DIALOG_ID = "OPEN_FILTER_DIALOG_ID"; //$NON-NLS-1$
	
	
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
    private IFileEditorInput input;
	
	public MavenXmlOutlinePage(IFileEditorInput input) {
        this.input = input;
	}
	
	public void createControl(Composite parent) {
		try {
			this.control = new Composite(parent, SWT.NONE);
            this.control.setLayout(new GridLayout());
        	
			GridData gridData = new GridData(GridData.FILL_BOTH);
			gridData.grabExcessVerticalSpace = true;
			gridData.grabExcessHorizontalSpace = true;
            gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.BEGINNING;
            this.control.setLayoutData(gridData);

            this.goalsViewer = createViewer(control);
            
			configureViewer();
			
            this.goalsViewer.setInput(Element.NULL_ROOT);
        }
        catch (Exception e) {
            log.error("Unable to create goals TreeViewer host : ", e); //$NON-NLS-1$
            throw new RuntimeException(e);
        }
	}
	
	private TreeViewer createViewer(Composite parent) throws Exception {
		
    	final TreeViewer viewer = new TreeViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL);
    	
    	GridData gridData = new GridData(GridData.FILL_BOTH | SWT.V_SCROLL | SWT.H_SCROLL);
    	gridData.grabExcessVerticalSpace = true;
    	gridData.grabExcessHorizontalSpace = true;
    	gridData.heightHint = 300;
    	viewer.getTree().setLayoutData(gridData);
    	
    	viewer.getTree().addListener (SWT.MouseDoubleClick, 
            	new Listener () {
            		public void handleEvent (Event event) {
            		    Object selection = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
        	            boolean isExpanded = viewer.getExpandedState(selection);
        	            if ( !isExpanded ) {
        	                viewer.expandToLevel(selection, 1);
        	            }
        	            else {
        	                viewer.collapseToLevel(selection, 1);
        	            }
            		}
            	}
            );
    	
        return viewer;
    }
	
	private void setupFilters() {
        this.patternFilter = new CustomPatternFilter();
        this.goalsViewer.addFilter(patternFilter);

        this.globalGoalFilter = new GlobalGoalFilter();
        this.goalsViewer.addFilter(globalGoalFilter);
		
        this.goalOriginFilter = new GoalOriginFilter();
        this.goalsViewer.addFilter(goalOriginFilter);
	}

	private void setupProviders() throws Exception {
        final IProject project = this.input.getFile().getProject();
        IQueryContext context = Mevenide.getInstance().getPOMManager().getQueryContext(project);
        goalsProvider = new GoalsProvider(context);

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
    	
        goalsViewer.getTree().addSelectionListener( new SelectionListener() {
	        public void widgetDefaultSelected(SelectionEvent arg0) { }
	        public void widgetSelected(SelectionEvent arg0) {
	            boolean goalSelected = false;
	            if ( goalsViewer.getSelection() != null && ((StructuredSelection) goalsViewer.getSelection()).getFirstElement() instanceof Goal) {
	                goalSelected = true;
	            }
	            runGoalAction.setEnabled(goalSelected);	            
	        }
        });
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
		toolBarManager.add(this.runGoalAction);
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
				setToolTipText(isChecked() ? Mevenide.getResourceString("MavenXmlOutlinePage.OnlineMode") : Mevenide.getResourceString("MavenXmlOutlinePage.OfflineMode"));  //$NON-NLS-1$//$NON-NLS-2$
			}
		};
		toggleOfflineAction.setToolTipText(toggleOfflineAction.isChecked() ? Mevenide.getResourceString("MavenXmlOutlinePage.OnlineMode") : Mevenide.getResourceString("MavenXmlOutlinePage.OfflineMode")); //$NON-NLS-1$ //$NON-NLS-2$
		toggleOfflineAction.setId(TOGGLE_OFFLINE_ID);
		toggleOfflineAction.setImageDescriptor(Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.OFFLINE));
		
		runGoalAction = new Action(null) {
			public void run() {
				runMaven();
			}
		};
		runGoalAction.setEnabled(false);
		runGoalAction.setText(Mevenide.getResourceString("MavenXmlOutlinePage.RunGoal")); //$NON-NLS-1$
		runGoalAction.setToolTipText(Mevenide.getResourceString("MavenXmlOutlinePage.RunGoal")); //$NON-NLS-1$
		runGoalAction.setId(RUN_GOAL_ID);
		runGoalAction.setImageDescriptor(Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.RUN_GOAL_ENABLED));
		runGoalAction.setDisabledImageDescriptor(Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.RUN_GOAL_DISABLED));
	}

	private void createFilterActions() {
		filterOriginShortcutAction = new Action(null, Action.AS_CHECK_BOX) {
			public void run() {
				goalOriginFilter.setEnable(isChecked());
				setToolTipText(isChecked() ? Mevenide.getResourceString("MavenXmlOutlinePage.GlobalGoals.Show") : Mevenide.getResourceString("MavenXmlOutlinePage.GlobalGoals.Hide")); //$NON-NLS-1$ //$NON-NLS-2$
				goalsViewer.refresh(false);
			}
		}; 
		filterOriginShortcutAction.setChecked(goalOriginFilter.isEnabled());
		filterOriginShortcutAction.setToolTipText(filterOriginShortcutAction.isChecked() ? Mevenide.getResourceString("MavenXmlOutlinePage.GlobalGoals.Show") : Mevenide.getResourceString("MavenXmlOutlinePage.GlobalGoals.Hide"));  //$NON-NLS-1$//$NON-NLS-2$
		filterOriginShortcutAction.setId(TOGGLE_FILTER_ORIGIN_ID);
		filterOriginShortcutAction.setImageDescriptor(Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.FILTER_GOALS));
		
		toggleCustomFilteringAction = new Action(null, Action.AS_CHECK_BOX) {
			public void run() {
				patternFilter.apply(isChecked());
				setToolTipText(isChecked() ? Mevenide.getResourceString("MavenXmlOutlinePage.RegexFilters.Disable") : Mevenide.getResourceString("MavenXmlOutlinePage.RegexFilters.Enable"));  //$NON-NLS-1$//$NON-NLS-2$
                getPreferenceStore().setValue(CustomPatternFilter.APPLY_CUSTOM_FILTERS_KEY, isChecked());
                commitChanges();
				goalsViewer.refresh(false); 
			}
		};
		toggleCustomFilteringAction.setChecked(goalOriginFilter.isEnabled());
		toggleCustomFilteringAction.setToolTipText(toggleCustomFilteringAction.isChecked() ? Mevenide.getResourceString("MavenXmlOutlinePage.RegexFilters.Disable") : Mevenide.getResourceString("MavenXmlOutlinePage.RegexFilters.Enable"));  //$NON-NLS-1$//$NON-NLS-2$
		toggleCustomFilteringAction.setId(TOGGLE_CUSTOM_FILTER_ID);
		toggleCustomFilteringAction.setImageDescriptor(Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.CUSTOM_FILTER));

		openFilterDialogAction = new Action(null) {
			public void run() {
			    openFilterDialog();
			}
		};
		openFilterDialogAction.setText(Mevenide.getResourceString("MavenXmlOutlinePage.FilterDialog.Text")); //$NON-NLS-1$
		openFilterDialogAction.setToolTipText(Mevenide.getResourceString("MavenXmlOutlinePage.FilterDialog.TooltipText")); //$NON-NLS-1$
		openFilterDialogAction.setId(OPEN_FILTER_DIALOG_ID);
		openFilterDialogAction.setImageDescriptor(Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.OPEN_FILTER_DIALOG));
	}

	private void contextualMenuAboutToShow(IMenuManager menuManager) {
		Object selection = ((StructuredSelection) goalsViewer.getSelection()).getFirstElement();
	    if ( selection instanceof Goal ) {
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
			String goalToRun = goal.getFullyQualifiedName();
			if ( goalToRun != null && goalToRun.indexOf(Goal.DEFAULT_GOAL) != -1 ) {
			    goalToRun = goalToRun.substring(0, goalToRun.length() - (Goal.SEPARATOR + Goal.DEFAULT_GOAL).length());
			}
			shortcut.setGoalsToRun(goalToRun);
			shortcut.setOffline(runOffline);
			shortcut.launch(this.input.getFile().getProject().getLocation());
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
            final IProject project = this.input.getFile().getProject();
            IQueryContext context = Mevenide.getInstance().getPOMManager().getQueryContext(project);
            goalsProvider = new GoalsProvider(context);
		} 
		catch (Exception e) {
			log.error("unable to refresh goalsProvider", e); //$NON-NLS-1$
		}
		goalsViewer.refresh(true);
	}

    /**
     * TODO: Describe what commitChanges does.
     */
    private boolean commitChanges() {
        try {
            getPreferenceStore().save();
            return true;
        } catch (IOException e) {
            Mevenide.displayError("Internal MevenIDE Error", "Unable to save preferences.", e);
        }
        return false;
    }

    /**
     * TODO: Describe what getPreferenceStore does.
     * @return
     */
    private IPersistentPreferenceStore getPreferenceStore() {
        return Mevenide.getInstance().getCustomPreferenceStore();
    }
}