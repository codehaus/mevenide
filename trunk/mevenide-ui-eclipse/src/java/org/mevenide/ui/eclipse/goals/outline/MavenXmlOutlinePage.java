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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.mevenide.ui.eclipse.goals.filter.*;
import org.mevenide.ui.eclipse.goals.model.Element;
import org.mevenide.ui.eclipse.goals.model.Goal;
import org.mevenide.ui.eclipse.goals.model.GoalsProvider;
import org.mevenide.ui.eclipse.goals.viewer.GoalsLabelProvider;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: MavenXmlOutlinePage.java,v 1.1 30 mars 2004 Exp gdodinet 
 * 
 */
public class MavenXmlOutlinePage implements IContentOutlinePage {
	private static final Log log = LogFactory.getLog(MavenXmlOutlinePage.class);
	
	private Composite control;
	
	private TreeViewer goalsViewer;
    private GoalsLabelProvider goalsLabelProvider;
	private GoalsProvider goalsProvider;
	
	private CustomPatternFilter patternFilter;
	private GoalOriginFilter originFilter;
	
	private Menu menu;
	private IAction toggleOfflineAction, openFilterDialogAction;
	private IAction runGoalAction;
	
	private String basedir;
	
	public MavenXmlOutlinePage(IFileEditorInput input) {
		this.basedir = new File(input.getFile().getLocation().toOSString()).getParent();
		goalsProvider = new GoalsProvider();
		goalsLabelProvider = new GoalsLabelProvider();
		patternFilter = new CustomPatternFilter();
		originFilter = new GoalOriginFilter();
	}
	
	public void createControl(Composite parent) {
		try {
			control = new Composite(parent, SWT.NONE);
        	control.setLayout(new GridLayout());
        	
			GridData gridData = new GridData(GridData.FILL_BOTH);
			gridData.grabExcessVerticalSpace = true;
			gridData.grabExcessHorizontalSpace = true;
			
        	control.setLayoutData(gridData);

            goalsViewer = getViewer(control);
            
            GridData textGridData = new GridData(GridData.FILL_BOTH);
			gridData.grabExcessHorizontalSpace = true;
			gridData.grabExcessVerticalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.BEGINNING;
			
			configureViewer();
			
			goalsViewer.setInput(Element.NULL_ROOT);
            
        }
        catch (Exception e) {
            log.error("Unable to create goals TreeViewer host : ", e);
            throw new RuntimeException(e);
        }
	}
	
	private TreeViewer getViewer(Composite parent) throws Exception {
		
    	TreeViewer viewer = new TreeViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL);
    	
    	goalsProvider.setBasedir(basedir);
    	
    	viewer.setContentProvider(goalsProvider);
    	viewer.setLabelProvider(goalsLabelProvider);
    	
    	GridData gridData = new GridData(GridData.FILL_BOTH | SWT.V_SCROLL | SWT.H_SCROLL);
    	gridData.grabExcessVerticalSpace = true;
    	gridData.grabExcessHorizontalSpace = true;
    	gridData.heightHint = 300;
    
    	viewer.getTree().setLayoutData(gridData);
    	
    	originFilter.setGoalsGrabber(goalsProvider.getGoalsGrabber());
    	originFilter.setFilterOriginPlugin(false);
    	viewer.addFilter(originFilter);
    	
    	viewer.addFilter(patternFilter);
    	
        return viewer;
    }
	
	private void configureViewer() {
        goalsViewer.getTree().addListener (SWT.MouseHover, 
        	new Listener () {
        		public void handleEvent (Event event) {
					//display description in a tooltip
        			//updateTooltipText(event);
        		}
        	}
        );
        
        goalsViewer.getTree().addListener (SWT.MouseDoubleClick, 
        	new Listener () {
        		public void handleEvent (Event event) {
					//shortcut to run selected goal
        			//runMaven(event)
        		}
        	}
        );
        
        createActions();
        
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
	
	private void createActions() {
		
	}
	
	private void contextualMenuAboutToShow(IMenuManager menuManager) {
		Object selection = ((StructuredSelection) goalsViewer.getSelection()).getFirstElement();
	    if ( selection instanceof Goal ) {
		    Goal selectedNode = (Goal) selection;
			//menuManager.add(runMavenAction);		
		}
	}
	
	public void dispose() {
	}
	
	public Control getControl() {
		return control;
	}
	
	public void setActionBars(IActionBars actionBars) {
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
}