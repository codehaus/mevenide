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
package org.mevenide.ui.eclipse.editors.pom.pages;

import java.util.List;

import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.apache.maven.project.SourceModification;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.pom.MevenidePomEditor;

/**
 * Presents a client control for editing information relating to the
 * build process and environment for this project.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 */
public class BuildPage extends AbstractPomEditorPage {

    private static final String ID = Mevenide.getResourceString("BuildPage.id");
    private static final String TAB = Mevenide.getResourceString("BuildPage.tab.label");
    private static final String HEADING = Mevenide.getResourceString("BuildPage.heading");
    
	private BuildDirectoriesSection directoriesSection;
	private ResourcesSection resourcesSection;
	private SourceModificationsSection sourceModificationsSection;
	
	public BuildPage(MevenidePomEditor editor) {
        super(editor, ID, TAB, HEADING);
    }

    /**
     * @see org.mevenide.ui.eclipse.editors.pom.pages.AbstractPomEditorPage#createPageContent(org.eclipse.swt.widgets.Composite)
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
				Project pom = (Project) target;
				getOrCreateBuild(pom).setResources(resources);
				getPomEditor().setModelDirty(true);
			}
		
			public void addResource(Object target, Resource resource) {
				Project pom = (Project) target;
				getOrCreateBuild(pom).addResource(resource);
				getPomEditor().setModelDirty(true);
			}
		
			public List getResources(Object source) {
				Project pom = (Project) source;
				return pom.getBuild() != null ? pom.getBuild().getResources() : null;
			}
		
			private Build getOrCreateBuild(Project pom) {
				Build build = pom.getBuild();
				if (build == null) {
					build = new Build();
					pom.setBuild(build);
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
		
		sourceModificationsSection = new SourceModificationsSection(this, parent, factory, "BuildSourceModificationsSection");
		ISourceModificationAdaptor sourceModificationsSectionAdaptor = new ISourceModificationAdaptor() {
			public void setSourceModifications(Object target, List sourceModifications) {
			    Project pom = (Project) target;
			    getOrCreateBuild(pom).setSourceModification(sourceModifications);
				getPomEditor().setModelDirty(true);
			}
		
			public void addSourceModification(Object target, SourceModification sourceModification) {
				Project pom = (Project) target;
				getOrCreateBuild(pom).addSourceModification(sourceModification);
				getPomEditor().setModelDirty(true);
			}
		
			public List getSourceModifications(Object source) {
				Project pom = (Project) source;
				return pom.getBuild() != null ? pom.getBuild().getSourceModifications() : null;
			}
		
			private Build getOrCreateBuild(Project pom) {
				Build build = pom.getBuild();
				if (build == null) {
					build = new Build();
					pom.setBuild(build);
				}
				return build;
			}
		};
		sourceModificationsSection.setSourceModificationAdaptor(sourceModificationsSectionAdaptor);
		
		Control sourceModificationControl = sourceModificationsSection.getSection();
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		sourceModificationControl.setLayoutData(gd);
		addSection(sourceModificationsSection);
	}

}
