/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.ui.eclipse.goals.viewer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.help.browser.IBrowser;
import org.eclipse.help.internal.browser.BrowserDescriptor;
import org.eclipse.help.internal.browser.BrowserManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.goals.model.Element;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: GoalsPickerDialog.java,v 1.1 8 sept. 2003 Exp gdodinet 
 * 
 */
public class GoalsPickerDialog  extends Dialog {
	private static Log log = LogFactory.getLog(GoalsPickerDialog.class);
	
	private TreeViewer goalsViewer;

	/** dummy implementation of a href-like behavior */ 
	private StyledText pluginHomeURLText;
	
	/** 
     * it could be annoying to check for every urls. 
	 * so we should allow the user to disable the verification
     *  
     */
	private boolean shouldTestUrls = true;

    public StyledText getTextWidget() {
        return pluginHomeURLText;
    }

    public boolean shouldTestUrls() {
        return shouldTestUrls;
    }

	public GoalsPickerDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	}
	
	protected Control createDialogArea(Composite parent) {
        try {
        	Composite composite = new Composite(parent, SWT.NONE);
        	composite.setLayout(new GridLayout());
        	
			GridData gridData = new GridData(GridData.FILL_BOTH);
			gridData.grabExcessVerticalSpace = true;
			gridData.grabExcessHorizontalSpace = true;
			
        	composite.setLayoutData(gridData);

            goalsViewer = GoalsViewer.getViewer(composite);
            
           
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
			
			goalsViewer.getTree().addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
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
	                            	pluginHomeURLText.setText(pluginName + "  (unable to find plugin home)");
	                            }
	                        }
	                        catch (Exception e1) {
	                            pluginHomeURLText.setText(pluginName + "  (unable to find plugin home)");
	                        }
						}
						
					}
				}
			);
			
			goalsViewer.setInput(Element.NULL_ROOT);
			
            return composite;
            
        }
        catch (Exception e) {
            //e.printStackTrace();
            log.error("Unable to instantiate GoalsPickerDialog due to : " + e);
            throw new RuntimeException(e);
        }
    }
    
	/**
	 * @todo maintain a cache of visited urls
     */
    boolean isValidUrl(String url) throws Exception {
		HttpClient httpClient = new HttpClient();
		HttpMethod method = new GetMethod(url);
		int status = httpClient.executeMethod(method);
		//check for 4xx and 5xx return codes
		return !Integer.toString(status).startsWith("4") && !Integer.toString(status).startsWith("5");
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
        	
            if ( browserDecriptors[i].getID().equals("org.eclipse.help.ui.systembrowser") ) {
        		IBrowser browser = browserDecriptors[i].getFactory().createBrowser();
        		browser.displayURL(url);
            }
        	
        }
    }

}