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
package org.mevenide.ui.eclipse.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *
 */
public class MevenidePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private PreferencesManager manager;
    private MevenidePreferenceDialog dialog;
    
    public MevenidePreferencePage() {
        super(Mevenide.getResourceString("MavenPreferencePage.title"));
        //setImageDescriptor(MavenPlugin.getImageDescriptor("sample.gif"));
		manager = new PreferencesManager();
		manager.loadPreferences();
        dialog = new MevenidePreferenceDialog(manager, this);
       	
    }

	

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		//shouldnot be necessary
		dialog.setJavaHome(manager.getValue("java.home"));
		dialog.setMavenHome(manager.getValue("maven.home"));
		dialog.setMavenRepo(manager.getValue("maven.repo"));
		dialog.setPomTemplateLocation(manager.getValue("pom.template.location"));
		dialog.setDefaultGoals(manager.getValue("maven.launch.defaultgoals"));
		
		dialog.setCheckTimestamp(manager.getBooleanValue("mevenide.checktimestamp"));
		
		return dialog.createContent(composite);
	}
  
  
    public boolean performOk() {
       if ( dialog.canFinish() ) {
        	return false;
        }
        else {
            return finish();
        }
    }
    
	private boolean finish() {
		dialog.update();
		
		manager.setValue("maven.home", dialog.getMavenHome());
		manager.setValue("maven.local.home", dialog.getMavenLocalHome());
		manager.setValue("java.home", dialog.getJavaHome());
		manager.setValue("maven.repo", dialog.getMavenRepo());
		//manager.setValue("maven.plugins.dir", dialog.getPluginsInstallDir());
		manager.setValue("pom.template.location", dialog.getPomTemplateLocation());
		manager.setValue("maven.launch.defaultgoals", dialog.getDefaultGoals());
		manager.setBooleanValue("mevenide.checktimestamp", dialog.getCheckTimestamp());
		
		Mevenide.getPlugin().initEnvironment();
		
		return manager.store();
	}
	
	
	
	public void init(IWorkbench workbench) {
    }
}
    
    