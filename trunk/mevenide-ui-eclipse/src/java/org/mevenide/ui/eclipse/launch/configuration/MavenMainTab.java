/*
 * Created on 4 août 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.mevenide.ui.eclipse.launch.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsMainTab;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;

/**
 * @author gdodinet
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MavenMainTab extends ExternalToolsMainTab {
	private static Log log = LogFactory.getLog(MavenMainTab.class);
	
	public MavenMainTab() {
		super(false);
	}
	
	public void createControl(Composite parent) {
		Font font = parent.getFont();
		
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);

		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 2;
		topLayout.makeColumnsEqualWidth = false;
		composite.setLayout(topLayout);		
		GridData gd = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gd);
		composite.setFont(font);
		
		createWorkDirectoryComponent(composite);
		
		workDirectoryField.addModifyListener(
			new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setDirty(true);
					updateLaunchConfigurationDialog();
				}
			}
		);
		
	}
	
	public void initializeFrom(ILaunchConfiguration configuration) {
		updateWorkingDirectory(configuration);

	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		String workingDirectory= workDirectoryField.getText().trim();
		if (workingDirectory.length() == 0) {
			configuration.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, (String)null);
		} else {
			configuration.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, workingDirectory);
		}
	}
	
	public boolean isValid(ILaunchConfiguration launchConfig) {
		return true;
	}
	protected String getWorkingDirectoryLabel() {
		return "Base Directory";
	}
}
