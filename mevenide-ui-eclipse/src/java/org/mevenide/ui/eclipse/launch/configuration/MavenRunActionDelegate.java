/*
 * Created on 3 août 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.mevenide.ui.eclipse.launch.configuration;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.mevenide.ui.eclipse.actions.AbstractMevenideAction;

/**
 * @author gdodinet
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MavenRunActionDelegate extends AbstractMevenideAction {
	private ISelection selection;
	
	public void run(IAction action) {
		MavenLaunchShortcut shortcut = new MavenLaunchShortcut();
		shortcut.setShowDialog(true);
		shortcut.launch(this.currentProject);
		
	}

	
	
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection; 
		super.selectionChanged(action, selection);
	}

	
	
	
}
