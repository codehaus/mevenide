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

import org.apache.maven.project.Project;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.pom.entries.OverridableTextEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class FullDescriptionSection extends PageSection {

	private OverridableTextEntry descriptionText;

    public FullDescriptionSection(
    	OverviewPage page, 
    	Composite parent, 
    	FormToolkit toolkit) 
    {
        super(page, parent, toolkit);
		setTitle(Mevenide.getResourceString("FullDescriptionSection.header")); //$NON-NLS-1$
		setDescription(Mevenide.getResourceString("FullDescriptionSection.description")); //$NON-NLS-1$
    }

    public Composite createSectionContent(Composite parent, FormToolkit factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 2 : 1;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);

		final Project pom = getPage().getPomEditor().getPom();
		
		// POM short description textbox
		Button toggle = createOverrideToggle(container, factory, 1, true);
		descriptionText = new OverridableTextEntry(createMultilineText(container, factory), toggle);
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.setDescription((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getDescription();
			}
		};
		descriptionText.addEntryChangeListener(adaptor);
		descriptionText.addOverrideAdaptor(adaptor);
		
		factory.paintBordersFor(container);
		return container;
	}

	public void update(Project pom) {
		setIfDefined(descriptionText, pom.getDescription(), isInherited() ? getParentPom().getDescription() : null);
	}

}
