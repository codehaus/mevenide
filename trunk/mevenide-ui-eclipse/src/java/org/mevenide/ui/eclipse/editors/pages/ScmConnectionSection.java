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
package org.mevenide.ui.eclipse.editors.pages;

import org.apache.maven.project.Project;
import org.apache.maven.project.Repository;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.entries.*;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ScmConnectionSection extends PageSection {

	private OverridableTextEntry connectionText;
	private OverridableTextEntry developerConnectionText;
	private OverridableTextEntry webAddressText;

    public ScmConnectionSection(RepositoryPage page) {
        super(page);
		setHeaderText(Mevenide.getResourceString("ScmConnectionSection.header"));
		setDescription(Mevenide.getResourceString("ScmConnectionSection.description"));
    }

    public Composite createClient(Composite parent, PageWidgetFactory factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 3 : 2;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		
		final Project pom = getPage().getEditor().getPom();
		
		// Repository connection textbox
		Button toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("ScmConnectionSection.connectionText.label"),
			Mevenide.getResourceString("ScmConnectionSection.connectionText.tooltip"), 
			factory
		);
		connectionText = new OverridableTextEntry(createText(container, factory), toggle);
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				setConnection(pom, (String) value);
			}
			public Object acceptParent() {
				return getConnection(getParentPom());
			}
		};
		connectionText.addEntryChangeListener(adaptor);
		connectionText.addOverrideAdaptor(adaptor);
		
		// Repository developer connection textbox
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("ScmConnectionSection.developerConnectionText.label"),
			Mevenide.getResourceString("ScmConnectionSection.developerConnectionText.tooltip"), 
			factory
		);
		developerConnectionText = new OverridableTextEntry(createText(container, factory), toggle);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				setDeveloperConnection(pom, (String) value);
			}
			public Object acceptParent() {
				return getDeveloperConnection(getParentPom());
			}
		};
		developerConnectionText.addEntryChangeListener(adaptor);
		developerConnectionText.addOverrideAdaptor(adaptor);
		
		// Repository SCM web site address textbox
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("ScmConnectionSection.webAddressText.label"),
			Mevenide.getResourceString("ScmConnectionSection.webAddressText.tooltip"), 
			factory
		);
		webAddressText = new OverridableTextEntry(createText(container, factory), toggle);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				setWebAddress(pom, (String) value);
			}
			public Object acceptParent() {
				return getWebAddress(getParentPom());
			}
		};
		webAddressText.addEntryChangeListener(adaptor);
		webAddressText.addOverrideAdaptor(adaptor);
		
		factory.paintBordersFor(container);
		return container;
	}

	public void update(Project pom) {
		setIfDefined(connectionText, getConnection(pom), getInheritedConnection());
		setIfDefined(developerConnectionText, getDeveloperConnection(pom), getInheritedDeveloperConnection());
		setIfDefined(webAddressText, getWebAddress(pom), getInheritedWebAddress());

		super.update(pom);
	}
	
	private void setConnection(Project pom, String connection) {
		getOrCreateRepository(pom).setConnection(connection);
	}
	
	private String getConnection(Project pom) {
		return pom.getRepository() != null ? pom.getRepository().getConnection() : null;
	}
	
	private String getInheritedConnection() {
		return isInherited() 
			? getConnection(getParentPom())
			: null;
	}

	private void setDeveloperConnection(Project pom, String connection) {
		getOrCreateRepository(pom).setDeveloperConnection(connection);
	}
	
	private String getDeveloperConnection(Project pom) {
		return pom.getRepository() != null ? pom.getRepository().getDeveloperConnection() : null;
	}
	
	private String getInheritedDeveloperConnection() {
		return isInherited() 
			? getDeveloperConnection(getParentPom())
			: null;
	}

	private void setWebAddress(Project pom, String url) {
		getOrCreateRepository(pom).setUrl(url);
	}
	
	private String getWebAddress(Project pom) {
		return pom.getRepository() != null ? pom.getRepository().getUrl() : null;
	}
	
	private String getInheritedWebAddress() {
		return isInherited() 
			? getWebAddress(getParentPom())
			: null;
	}

	private Repository getOrCreateRepository(Project pom) {
		Repository repository = pom.getRepository();
		if (repository == null) {
			repository = new Repository();
			pom.setRepository(repository);
		}
		return repository;
	}

}
