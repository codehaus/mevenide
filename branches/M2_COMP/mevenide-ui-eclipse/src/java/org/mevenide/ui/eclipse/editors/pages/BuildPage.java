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

import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.apache.maven.model.Resource;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.MevenidePomEditor;

/**
 * Presents a client control for editing information relating to the
 * build process and environment for this project.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class BuildPage extends AbstractPomEditorPage {

    private static final String ID = Mevenide.getResourceString("BuildPage.id");
    private static final String TAB = Mevenide.getResourceString("BuildPage.tab.label");
    private static final String HEADING = Mevenide.getResourceString("BuildPage.heading");
    
	private BuildDirectoriesSection directoriesSection;
	private ResourcesSection resourcesSection;

	public BuildPage(MevenidePomEditor editor) {
        super(editor, ID, TAB, HEADING);
    }

    /**
     * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomEditorPage#createPageContent(org.eclipse.swt.widgets.Composite)
     */
    protected void createPageContent(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 10;
		layout.horizontalSpacing = 15;
		parent.setLayout(layout);

		FormToolkit factory = getEditor().getToolkit();

		directoriesSection = new BuildDirectoriesSection(this, parent, factory);
		Control control = directoriesSection.getSection();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		control.setLayoutData(gd);
		addSection(directoriesSection);

		resourcesSection = new ResourcesSection(this, parent, factory, "BuildResourcesSection");
		IResourceAdaptor adaptor = new IResourceAdaptor() {
			public void setResources(Object target, List resources) {
				MavenProject pom = (MavenProject) target;
				getOrCreateBuild(pom).setResources(resources);
				getPomEditor().setModelDirty(true);
			}
		
			public void addResource(Object target, Resource resource) {
				MavenProject pom = (MavenProject) target;
				getOrCreateBuild(pom).addResource(resource);
				getPomEditor().setModelDirty(true);
			}
		
			public List getResources(Object source) {
				MavenProject pom = (MavenProject) source;
				return pom.getModel().getBuild() != null ? pom.getModel().getBuild().getResources() : null;
			}
		
			private Build getOrCreateBuild(MavenProject pom) {
				Build build = pom.getModel().getBuild();
				if (build == null) {
					build = new Build();
					pom.getModel().setBuild(build);
				}
				return build;
			}
		};
		resourcesSection.setResourceAdaptor(adaptor);
		
		control = resourcesSection.getSection();
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		control.setLayoutData(gd);
		addSection(resourcesSection);
	}

}
