/*
 * Created on 21 juil. 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.mevenide.ui.eclipse.sync;

import org.eclipse.core.resources.IProject;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * @author gdodinet
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class SynchronizerUtil {
	
	private SynchronizerUtil() {
	}

	public static boolean shouldSynchronizePom(IProject project) {
		boolean shouldCheckTimeStamp = Mevenide.getPlugin().getCheckTimestamp();
		boolean disSynchro = isIdeConfigNewer(project);
		return (shouldCheckTimeStamp && disSynchro) || !shouldCheckTimeStamp;
		
	}

	private static boolean isIdeConfigNewer(IProject project) {
		long pomTimestamp = project.getFile("project.xml").getLocation().toFile().lastModified();
		long dotClasspathTimestamp = project.getFile(".classpath").getLocation().toFile().lastModified();
		return pomTimestamp < dotClasspathTimestamp;
	}
	
	public static boolean shouldSynchronizeProject(IProject project) {
		boolean shouldCheckTimeStamp = Mevenide.getPlugin().getCheckTimestamp();
		boolean disSynchro = !isIdeConfigNewer(project);
		return (shouldCheckTimeStamp && disSynchro) || !shouldCheckTimeStamp;

	}
}
