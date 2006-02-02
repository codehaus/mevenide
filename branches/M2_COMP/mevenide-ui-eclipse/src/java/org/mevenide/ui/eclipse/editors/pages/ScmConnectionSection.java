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
package org.mevenide.ui.eclipse.editors.pages;

import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.entries.OverridableTextEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ScmConnectionSection extends PageSection {

	private OverridableTextEntry connectionText;
	private OverridableTextEntry developerConnectionText;
	private OverridableTextEntry webAddressText;

    public ScmConnectionSection(
		RepositoryPage page,
	    Composite parent,
	    FormToolkit toolkit)
	{
		super(page, parent, toolkit);
		setTitle(Mevenide.getResourceString("ScmConnectionSection.header"));
		setDescription(Mevenide.getResourceString("ScmConnectionSection.description"));
    }

    public Composite createSectionContent(Composite parent, FormToolkit factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 3 : 2;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		
		final MavenProject pom = getPage().getPomEditor().getPom();
		
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

	public void update(MavenProject pom) {
		setIfDefined(connectionText, getConnection(pom), getInheritedConnection());
		setIfDefined(developerConnectionText, getDeveloperConnection(pom), getInheritedDeveloperConnection());
		setIfDefined(webAddressText, getWebAddress(pom), getInheritedWebAddress());
	}
	
	private void setConnection(MavenProject pom, String connection) {
		getOrCreateRepository(pom).setConnection(connection);
	}
	
	private String getConnection(MavenProject pom) {
		return pom.getModel().getScm() != null ? pom.getModel().getScm().getConnection() : null;
	}
	
	private String getInheritedConnection() {
		return isInherited() 
			? getConnection(getParentPom())
			: null;
	}

	private void setDeveloperConnection(MavenProject pom, String connection) {
		getOrCreateRepository(pom).setDeveloperConnection(connection);
	}
	
	private String getDeveloperConnection(MavenProject pom) {
		return pom.getModel().getScm() != null ? pom.getModel().getScm().getDeveloperConnection() : null;
	}
	
	private String getInheritedDeveloperConnection() {
		return isInherited() 
			? getDeveloperConnection(getParentPom())
			: null;
	}

	private void setWebAddress(MavenProject pom, String url) {
		getOrCreateRepository(pom).setUrl(url);
	}
	
	private String getWebAddress(MavenProject pom) {
		return pom.getModel().getScm() != null ? pom.getModel().getScm().getUrl() : null;
	}
	
	private String getInheritedWebAddress() {
		return isInherited() 
			? getWebAddress(getParentPom())
			: null;
	}

	private Scm getOrCreateRepository(MavenProject pom) {
		Scm repository = pom.getModel().getScm();
		if (repository == null) {
			repository = new Scm();
			pom.getModel().setScm(repository);
		}
		return repository;
	}

}