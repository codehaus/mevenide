package org.mevenide.ui.eclipse.launch.configuration;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class MavenRunnerTabGroup extends AbstractLaunchConfigurationTabGroup {

	private MavenOptionsTab tab = new MavenOptionsTab();
	private MavenMainTab mainTab = new MavenMainTab();
	
	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog, java.lang.String)
	 */
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
			mainTab,
			tab,
		};
		setTabs(tabs);
	}


	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		tab.performApply(configuration);
	}
}
