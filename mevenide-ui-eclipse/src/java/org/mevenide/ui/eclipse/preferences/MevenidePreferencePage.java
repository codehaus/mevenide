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
import org.mevenide.ui.eclipse.MevenidePreferenceKeys;

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
		
		//*should*not be necessary
		dialog.setJavaHome(manager.getValue(MevenidePreferenceKeys.JAVA_HOME_PREFERENCE_KEY));
		dialog.setMavenHome(manager.getValue(MevenidePreferenceKeys.MAVEN_HOME_PREFERENCE_KEY));
		dialog.setMavenRepo(manager.getValue(MevenidePreferenceKeys.MAVEN_REPO_PREFERENCE_KEY));
		dialog.setPomTemplateLocation(manager.getValue(MevenidePreferenceKeys.POM_TEMPLATE_LOCATION_PREFERENCE_KEY));
		dialog.setHeapSize(manager.getIntValue(MevenidePreferenceKeys.JAVA_HEAP_SIZE_PREFERENCE_KEY));
		dialog.setDefaultGoals(manager.getValue(MevenidePreferenceKeys.DEFAULT_GOALS_PREFERENCE_KEY));
		dialog.setCheckTimestamp(manager.getBooleanValue(MevenidePreferenceKeys.MEVENIDE_CHECKTIMESTAMP_PREFERENCE_KEY));
		
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
		
		manager.setValue(
			MevenidePreferenceKeys.MAVEN_HOME_PREFERENCE_KEY, 
			dialog.getMavenHome()
		);
		manager.setValue(
			MevenidePreferenceKeys.MAVEN_LOCAL_HOME_PREFERENCE_KEY, 
			dialog.getMavenLocalHome()
		);
		manager.setValue(
			MevenidePreferenceKeys.JAVA_HOME_PREFERENCE_KEY, 
			dialog.getJavaHome()
		);
		manager.setValue(
			MevenidePreferenceKeys.MAVEN_REPO_PREFERENCE_KEY, 
			dialog.getMavenRepo()
		);
		manager.setValue(
			MevenidePreferenceKeys.POM_TEMPLATE_LOCATION_PREFERENCE_KEY, 
			dialog.getPomTemplateLocation()
		);
		manager.setValue(
			MevenidePreferenceKeys.DEFAULT_GOALS_PREFERENCE_KEY, 
			dialog.getDefaultGoals()
		);
		manager.setBooleanValue(
			MevenidePreferenceKeys.MEVENIDE_CHECKTIMESTAMP_PREFERENCE_KEY, 
			dialog.getCheckTimestamp()
		);
		
		if ( dialog.getHeapSize() != 0 ) {
			manager.setIntValue(MevenidePreferenceKeys.JAVA_HEAP_SIZE_PREFERENCE_KEY, dialog.getHeapSize());
		}
		
		Mevenide.getPlugin().initEnvironment();
		
		return manager.store();
	}
	
	
	
	public void init(IWorkbench workbench) {
    }
}
    
    