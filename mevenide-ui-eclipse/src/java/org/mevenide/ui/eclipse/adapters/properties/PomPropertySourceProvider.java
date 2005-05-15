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
package org.mevenide.ui.eclipse.adapters.properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Branch;
import org.apache.maven.project.Contributor;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Developer;
import org.apache.maven.project.License;
import org.apache.maven.project.MailingList;
import org.apache.maven.project.Resource;
import org.apache.maven.project.SourceModification;
import org.apache.maven.project.Version;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

/**
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PomPropertySourceProvider implements IPropertySourceProvider {

	private static final Log log = LogFactory.getLog(PomPropertySourceProvider.class);

	public PomPropertySourceProvider() {
	}

	public IPropertySource getPropertySource(Object object) {
		if (log.isDebugEnabled()) {
			log.debug("getPropertySource: looking for source for " + object);
		}
		if (object instanceof IPropertySource) {
			return (IPropertySource) object;
		}
		return getPomPropertySource(object);
	}
	
	public IPomPropertySource getPomPropertySource(Object object) {
		if (object instanceof IPomPropertySource) {
			return (IPomPropertySource) object;
		}
		if (object instanceof License) {
			return new LicensePropertySource((License) object);
		}
		if (object instanceof Contributor && !(object instanceof Developer) ) {
			return new ContributorPropertySource((Contributor) object);
		}
		if (object instanceof Developer) {
			return new DeveloperPropertySource((Developer) object);
		}
		if (object instanceof MailingList) {
			return new MailingListPropertySource((MailingList) object);
		}
		if (object instanceof Version) {
			return new VersionPropertySource((Version) object);
		}
		if (object instanceof Branch) {
			return new BranchPropertySource((Branch) object);
		}
		if (object instanceof Dependency) {
			return new DependencyPropertySource((Dependency) object);
		}
		if ( object instanceof SourceModification ) {
		    return new SourceModificationPropertySource((SourceModification) object);
		}
		if (object instanceof Resource) {
			return new ResourcePropertySource((Resource) object);
		}
		log.error("Unable to create a PropertySource for " + object);
		return null;
	}

}
