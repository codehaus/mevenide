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
package org.mevenide.ui.eclipse.goals.viewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.help.browser.IBrowser;
import org.eclipse.help.internal.browser.BrowserDescriptor;
import org.eclipse.help.internal.browser.BrowserManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.goals.model.Element;
import org.mevenide.ui.eclipse.goals.model.Goal;
import org.mevenide.ui.eclipse.goals.model.GoalFactory;
import org.mevenide.ui.eclipse.goals.model.GoalsProvider;
import org.mevenide.ui.eclipse.goals.model.Plugin;

/**
 * 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: GoalsPickerDialog.java,v 1.1 8 sept. 2003 Exp gdodinet 
 * 
 */
public class GoalsPickerDialog  extends Dialog {
	private static final String HTTP_SERVER_ERROR = "5";
    private static final String HTTP_CLIENT_ERROR = "4";
    private static Log log = LogFactory.getLog(GoalsPickerDialog.class);
	
	private CheckboxTreeViewer goalsViewer;

	/** dummy implementation of a href-like behavior */ 
	private StyledText pluginHomeURLText;
	
	private List checkedItems = new ArrayList();

	private List visitedUrls = new ArrayList();
	private List notFoundUrls = new ArrayList();
	
	/** 
     * it could be annoying to check for every urls. 
	 * so we should allow the user to disable the verification
     * verification disabled by default
     */
	private boolean shouldTestUrls = false;

    private GoalsProvider goalsProvider;

	private Text goalsOrderText; 
	private String goalsOrder;
	
    public StyledText getTextWidget() {
        return pluginHomeURLText;
    }

    public boolean shouldTestUrls() {
        return shouldTestUrls;
    }

	public GoalsPickerDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		super.setBlockOnOpen(true);
	}
	
	protected Control createDialogArea(Composite parent) {
        try {
        	Composite composite = new Composite(parent, SWT.NONE);
        	composite.setLayout(new GridLayout());
        	
			GridData gridData = new GridData(GridData.FILL_BOTH);
			gridData.grabExcessVerticalSpace = true;
			gridData.grabExcessHorizontalSpace = true;
			
        	composite.setLayoutData(gridData);

            goalsViewer = getViewer(composite);
            
            GridData textGridData = new GridData(GridData.FILL_BOTH);
			gridData.grabExcessHorizontalSpace = true;
			gridData.grabExcessVerticalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.BEGINNING;
			
			pluginHomeURLText = new StyledText(composite, SWT.READ_ONLY);			
			pluginHomeURLText.setLayoutData(textGridData);
			pluginHomeURLText.setForeground(MevenideColors.DARK_BLUE);
			pluginHomeURLText.setCaret(null);
			pluginHomeURLText.setFont(parent.getFont());
			pluginHomeURLText.setBackground(composite.getBackground());
			pluginHomeURLText.setCursor(null);
			
			pluginHomeURLText.addMouseListener(new HyperLinkMouseListener(this));

			pluginHomeURLText.addModifyListener(
				new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						String url = ((StyledText) e.getSource()).getText();
						if ( !shouldTestUrls ) {
							pluginHomeURLText.setCursor(new Cursor(null, SWT.CURSOR_HAND));
						}
						else {
							try {
	                            if ( isValidUrl(url) ) {
	                            	pluginHomeURLText.setCursor(new Cursor(null, SWT.CURSOR_HAND));
	                            }
	                            else {
	                            	pluginHomeURLText.setCursor(null);
	                            }
	                        }
	                        catch (Exception e1) {
	                            pluginHomeURLText.setCursor(null);
	                        }
						}
                    }
				}
			);
			
			configureViewer();
			
			setInput(Element.NULL_ROOT);
			
			new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			createGoalsOrderingComposite(composite);
			
			initOrderGoalsText();

            return composite;
            
        }
        catch (Exception e) {
            //e.printStackTrace();
            log.error("Unable to instantiate GoalsPickerDialog due to : " + e);
            throw new RuntimeException(e);
        }
    }
    
	private void initOrderGoalsText() {

	}

    private void setInput(Object obj) {
		goalsViewer.setInput(Element.NULL_ROOT);
		goalsViewer.setGrayed(goalsProvider.getChildren(Element.NULL_ROOT), true);
    }
    
	private void createGoalsOrderingComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout orderLayout = new GridLayout();
        orderLayout.numColumns = 2;
        composite.setLayout(orderLayout);

        GridData orderGridData = new GridData(GridData.FILL_BOTH);
        orderGridData.grabExcessVerticalSpace = true;
        orderGridData.grabExcessHorizontalSpace = true;
        composite.setLayoutData(orderGridData);

        goalsOrderText = 
			new Text(
				composite, 
				SWT.READ_ONLY | SWT.BORDER | SWT.WRAP | SWT.MULTI
			);
        GridData orderTextGridData = new GridData(GridData.FILL_BOTH);
        orderTextGridData.grabExcessVerticalSpace = true;
        orderTextGridData.grabExcessHorizontalSpace = true;
		orderTextGridData.verticalSpan = 2;
		goalsOrderText.setLayoutData(orderTextGridData);
		
		final Button goalsOrderButton = new Button(composite, SWT.PUSH);
		goalsOrderButton.setText(Mevenide.getResourceString("GoalsPickerDialog.goals.order"));
		String text = goalsOrderText.getText();
		boolean orderButtonEnabled = text != null && !text.trim().equals("");
        goalsOrderButton.setEnabled(orderButtonEnabled);

        goalsOrderText.addModifyListener(
        	new ModifyListener() {
        		public void modifyText(ModifyEvent e) {
                	String text = goalsOrderText.getText();
					boolean orderButtonEnabled = text != null && !text.trim().equals("");
	        		goalsOrderButton.setEnabled(orderButtonEnabled);
        		}
        	}
        );
       
        goalsOrderButton.addSelectionListener(
    		new SelectionAdapter() {
    			public void widgetSelected(SelectionEvent e) {
					
	                GoalsOrderDialog dialog = new GoalsOrderDialog(getShell(), StringUtils.split(goalsOrder));
					
					int ok = dialog.open();
					
					if (ok == Window.OK) {
						Object[] targets = dialog.getTargets();
						String newOrder = "";
						for (int i = 0; i < targets.length -1; i++) {
							newOrder += targets[i] + " ";
						}
						newOrder += targets[targets.length-1];
						goalsOrderText.setText(newOrder);
						goalsOrder = goalsOrderText.getText();
						log.debug("New order : " + goalsOrder); 
					}
    			}
		    }
        );

		if ( goalsOrder != null ) {
			goalsOrderText.setText(goalsOrder);
			//@todo check goals and expand parent also...
			String[] goals = StringUtils.split(goalsOrder);
			for (int i = 0; i < goals.length; i++) {
				Goal goal = GoalFactory.newGoal(goals[i]);
				goalsViewer.setExpandedState(goal.getPlugin(), true);
				goalsViewer.setChecked(goal, true);
		    }
		}
		
		goalsViewer.setGrayed(goalsProvider.getChildren(Element.NULL_ROOT), true);
    }

    private void updateCheckedItems(CheckStateChangedEvent e) {
		  
		boolean isSelectionChecked = ((CheckboxTreeViewer) e.getSource()).getChecked(e.getElement());
	
		if ( e.getElement() instanceof Goal ) {
			Goal goal = (Goal) e.getElement();
			updateCheckedGoal(isSelectionChecked, goal);
		}
		else {
			Plugin plugin = (Plugin) e.getElement();
			//it is way too confusing when plugins are checkable. indeed when 
			//theres a default goal, both the default and the plugin should be 
			//checkable, thus we got the goal multiple times. I think its best
 			//to just disable plugins.
  
			//updateCheckedPlugin(isSelectionChecked, plugin);
			
			//prevent user to check a plugin	
			goalsViewer.setChecked(plugin, false);
		}
		
		String newOrder = "";
		for (int i = 0; i < checkedItems.size(); i++) {
            newOrder += " " + checkedItems.get(i); 
        }
		goalsOrderText.setText(newOrder);
		
		goalsOrder = newOrder;
	}

	private void updateCheckedGoal(boolean isSelectionChecked, Goal goal) {
        String fullyQualifiedGoalName = goal.getPlugin().getName();
        if ( !goal.getName().equals(Goal.DEFAULT_GOAL) ) {
        	fullyQualifiedGoalName += ":" + goal.getName();
        }
        if ( isSelectionChecked ) {
        	checkedItems.add(fullyQualifiedGoalName);
        }
        else {
        	checkedItems.remove(fullyQualifiedGoalName);
        }
    }

    private void updateCheckedPlugin(boolean isSelectionChecked, Plugin plugin) {
        String pluginName = plugin.getName();
        String[] goals = goalsProvider.getGoalsGrabber().getGoals(pluginName);
        if ( goals != null && goals.length > 0 ) {
        	if ( !Arrays.asList(goals).contains(Goal.DEFAULT_GOAL) ) {
        		goalsViewer.setChecked(pluginName, false);
        	}
        	else {
        		if ( isSelectionChecked ) {
        			checkedItems.add(pluginName);
        		}
        		else {
        			checkedItems.remove(pluginName);
        		}
        	}
        }
    }

    private void updateStyledTextWidgetHyperlink(SelectionEvent e) {
		TreeItem item = (TreeItem) e.item;
		
		String urlPrefix = Mevenide.getResourceString("maven.plugins.url.prefix");
		log.debug("Looked up urlPrefix = " + urlPrefix);
		String pluginName = "";
		if ( item.getParentItem() == null ) {
			pluginName = item.getText();
		}
		else {
			pluginName = item.getParentItem().getText();
		}
		if ( !shouldTestUrls ) {
			pluginHomeURLText.setText(urlPrefix + pluginName);
		}
		else {
			try {
                if ( isValidUrl(urlPrefix + pluginName) ) {
                	pluginHomeURLText.setText(urlPrefix + pluginName);
                }
                else {
                	pluginHomeURLText.setText(pluginName + " " + Mevenide.getResourceString("GoalsPickerDialog.plugin.home.notfound"));
                }
            }
            catch (Exception e1) {
                pluginHomeURLText.setText(pluginName + " " + Mevenide.getResourceString("GoalsPickerDialog.plugin.home.notfound"));
            }
		}
	}

	private void updateTooltipText(Event event) {
        final Tree tree = goalsViewer.getTree();
 
		//update tooltip 
        //there should a smarter way to enable tooltip on a treeviewer 
		Rectangle clientArea = tree.getClientArea ();
		Point pt = new Point (event.x, event.y);
		TreeItem item = tree.getItem(pt);
		if ( item != null ) {
			if ( item.getData() instanceof Plugin ) {
				String tooltip = item.getText() + " plugin";
				Plugin plugin = (Plugin) item.getData();
				String[] goals = goalsProvider.getGoalsGrabber().getGoals(plugin.getName());
				if ( goals != null && goals.length > 0 ) {
					if ( !Arrays.asList(goals).contains(Goal.DEFAULT_GOAL) ) {
						tooltip += Mevenide.getResourceString("GoalsPickerDialog.no.default.goal");
					}
				}
				tree.setToolTipText(tooltip);
			}
			if ( item.getData() instanceof Goal ) {
				Goal goal = (Goal) item.getData();
				if ( Goal.DEFAULT_GOAL.equals(goal.getName()) ) {
					tree.setToolTipText("default " + goal.getPlugin().getName() + " goal");
				}
				else {
					tree.setToolTipText("goal " + goal.getPlugin().getName() + ":" + goal.getName());
				}
			}
		}
	}

	private void configureViewer() {
        goalsViewer.getTree().addSelectionListener(
        	new SelectionAdapter() {
        		public void widgetSelected(SelectionEvent e) {
					updateStyledTextWidgetHyperlink(e);
        		}
        	}
        );
        
		goalsViewer.addCheckStateListener(
        	new ICheckStateListener() {
				public void checkStateChanged(CheckStateChangedEvent event) {
                	updateCheckedItems(event);
        		}
        	}
        );
        
        goalsViewer.getTree().addListener (SWT.MouseHover, 
        	new Listener () {
        		public void handleEvent (Event event) {
        			updateTooltipText(event);
        		}
        	}
        );
        
        goalsViewer.getTree().addListener (SWT.MouseDoubleClick, 
            	new Listener () {
            		public void handleEvent (Event event) {
            		    Object selection = ((IStructuredSelection) goalsViewer.getSelection()).getFirstElement();
        	            boolean isExpanded = goalsViewer.getExpandedState(selection);
        	            if ( !isExpanded ) {
        	                goalsViewer.expandToLevel(selection, 1);
        	            }
        	            else {
        	                goalsViewer.collapseToLevel(selection, 1);
        	            }
            		}
            	}
            );
    }

    boolean isValidUrl(String url) throws Exception {
    	if ( visitedUrls.contains(url) ) {
    		return !notFoundUrls.contains(url);
    	}
		HttpClient httpClient = new HttpClient();
		HttpMethod method = new GetMethod(url);
		int status = httpClient.executeMethod(method);
		//check for 4xx and 5xx return codes
		visitedUrls.add(url);
		boolean fileExists = !Integer.toString(status).startsWith(HTTP_CLIENT_ERROR) && !Integer.toString(status).startsWith(HTTP_SERVER_ERROR);
		if ( !fileExists ) {
			notFoundUrls.add(url);
		}
		return fileExists;
    }

    private CheckboxTreeViewer getViewer(Composite parent) throws Exception {
    	String basedir = Mevenide.getInstance().getCurrentDir();
    	
    	CheckboxTreeViewer viewer = new CheckboxTreeViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL);
    	
    	goalsProvider = new GoalsProvider();
        GoalsLabelProvider goalsLabelProvider = new GoalsLabelProvider();
    	goalsProvider.setBasedir(basedir);
    	
    	viewer.setContentProvider(goalsProvider);
    	viewer.setLabelProvider(goalsLabelProvider);
    	
    	GridData gridData = new GridData(GridData.FILL_BOTH | SWT.V_SCROLL | SWT.H_SCROLL);
    	gridData.grabExcessVerticalSpace = true;
    	gridData.grabExcessHorizontalSpace = true;
    	gridData.heightHint = 300;
    
    	viewer.getTree().setLayoutData(gridData);
    	
        return viewer;
    }


	public String getOrderedGoals() {
		return goalsOrder;
	}

    public void setGoalsOrder(String goalsOrder) {
        this.goalsOrder = goalsOrder;
    }

}

class HyperLinkMouseListener extends MouseAdapter {
	private static Log log = LogFactory.getLog(HyperLinkMouseListener.class); 
	private StyledText text;
	private GoalsPickerDialog goalsPickerDialog;
	HyperLinkMouseListener(GoalsPickerDialog goalsPickerDialog) {
		this.text = goalsPickerDialog.getTextWidget();
		this.goalsPickerDialog = goalsPickerDialog;
	}

	public void mouseDown(MouseEvent e) {
	    try {

			
			String url = text.getText();
			
			if ( !goalsPickerDialog.shouldTestUrls() ) {
				displayURL(url);
			}		
			else {
				if ( goalsPickerDialog.isValidUrl(url) ) {
    	        	displayURL(url);
				}
			}

        }
        catch (Exception e1) {
            log.error("Unable to launch browser due to : " + e);
        }		
    }

    private void displayURL(String url) throws Exception {
        BrowserDescriptor[] browserDecriptors = BrowserManager.getInstance().getBrowserDescriptors();
        for (int i = 0; i < browserDecriptors.length; i++) {
        	
            if ( browserDecriptors[i].getID().equals(Mevenide.getResourceString("GoalsPickerDialog.browser.id")) ) {
        		IBrowser browser = browserDecriptors[i].getFactory().createBrowser();
        		browser.displayURL(url);
            }
        	
        }
    }

}