/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Jeffrey Bonevich (jeff@bonevich.com).  All rights
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
package org.mevenide.ui.eclipse.editors.properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Branch;
import org.apache.maven.project.Contributor;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Developer;
import org.apache.maven.project.License;
import org.apache.maven.project.MailingList;
import org.apache.maven.project.Resource;
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
		if (object instanceof Resource) {
			return new ResourcePropertySource((Resource) object);
		}
		log.error("Unable to create a PropertySource for " + object);
		return null;
	}

}
