package org.mevenide.ui.eclipse.launch.configuration;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.launchVariables.RefreshTab;

public class MavenRunnerTabGroup extends AbstractLaunchConfigurationTabGroup {

	private MavenOptionsTab optionsTab = new MavenOptionsTab();
	private MavenMainTab mainTab = new MavenMainTab();
	private RefreshTab refreshTab = new RefreshTab();
	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog, java.lang.String)
	 */
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
			optionsTab,
			mainTab,
			refreshTab,
		};
		setTabs(tabs);
	}


}
