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
package org.mevenide.ui.eclipse.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.mevenide.sync.ISynchronizer;
import org.mevenide.sync.SynchronizerFactory;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.wizard.SynchronizeWizard;

/**
 * either synchronize pom add .classpath 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SynchronizeAction extends AbstractMevenideAction {
	private static Log log = LogFactory.getLog(SynchronizeAction.class);
	
    public void run(IAction action) {
    	boolean pom = true;
		try {
            if ( action.getId().equals("maven-plugin.Synchronize") ) {
            	pom = false;
            	String mavenHome = Mevenide.getPlugin().getMavenHome();
            	String mavenRepository = Mevenide.getPlugin().getMavenRepository();
            	if ( isNull(mavenHome) || isNull(mavenRepository) ) {
					Mevenide.popUp("Mevenide", "Please set maven preferences before synchronizing");
				}
				else {
					if ( JavaCore.getClasspathVariable("MAVEN_REPO") == null ) {
						JavaCore.setClasspathVariable("MAVEN_REPO", new Path(Mevenide.getPlugin().getMavenRepository()), null);
					}
					SynchronizerFactory.getSynchronizer(ISynchronizer.POM_TO_IDE).synchronize();
				}
			}
			if ( action.getId().equals("maven-plugin.SynchronizePom") ) {
				//show synch wizard
				Wizard wizard = new SynchronizeWizard(currentProject);
				WizardDialog dialog 
					= new WizardDialog(
						PlatformUI.getWorkbench()
								  .getActiveWorkbenchWindow()
								  .getShell(), wizard);
				dialog.create();
				dialog.open();
			}
		}
		catch (Exception e) {
			log.debug("Unable to synchronize " + (pom ? "POM" : "project") + " due to : " + e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);
//		try {
//			if ( !currentProject.hasNature(JavaCore.NATURE_ID) ) {
//				action.setEnabled(false);
//			}
//			else {
//				action.setEnabled(true);
//			}
//		} 
//		catch (Exception e) {
//			log.debug("Unable to disable action '" + action.getText() + "' due to : " + e);
//		}
	}


    private boolean isNull(String strg) {
		return strg == null || strg.trim().equals("");
    }
}