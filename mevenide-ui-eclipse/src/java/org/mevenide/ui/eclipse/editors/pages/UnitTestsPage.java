/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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

import java.util.List;

import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.apache.maven.project.UnitTest;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.editors.MevenidePomEditor;

/**
 * Presents a client control for editing information relating to the
 * build process and environment for this project.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class UnitTestsPage extends AbstractPomEditorPage {

	public static final String HEADING = Mevenide.getResourceString("UnitTestsPage.heading");
    
	private IncludesSection includesSection;
	private ExcludesSection excludesSection;
	private ResourcesSection resourcesSection;

    public UnitTestsPage(MevenidePomEditor editor) {
        super(HEADING, editor);
    }

	protected void initializePage(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 10;
		layout.horizontalSpacing = 15;
		parent.setLayout(layout);

		PageWidgetFactory factory = getFactory();
		factory.setBackgroundColor(MevenideColors.WHITE);

		includesSection = new IncludesSection(this);
		IIncludesAdaptor includesAdaptor = new IIncludesAdaptor() {
			public void setIncludes(Object target, List newIncludes) {
				Project pom = (Project) target;
				List includes = getOrCreateUnitTest(pom).getIncludes();
				includes.removeAll(includes);
				includes.addAll(newIncludes);
				getEditor().setModelDirty(true);
			}
	
			public void addInclude(Object target, String include) {
				Project pom = (Project) target;
				getOrCreateUnitTest(pom).addInclude(include);
				getEditor().setModelDirty(true);
			}
	
			public List getIncludes(Object source) {
				Project pom = (Project) source;
				return pom.getBuild() != null 
					? pom.getBuild().getUnitTest() != null
						? pom.getBuild().getUnitTest().getIncludes()
						: null
					: null;
			}
		};
		includesSection.setIncludesAdaptor(includesAdaptor);
		Control control = includesSection.createControl(parent, factory);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		control.setLayoutData(gd);
		
		excludesSection = new ExcludesSection(this);
		IExcludesAdaptor excludesAdaptor = new IExcludesAdaptor() {
			public void setExcludes(Object target, List newExcludes) {
				Project pom = (Project) target;
				List excludes = getOrCreateUnitTest(pom).getExcludes();
				excludes.removeAll(excludes);
				excludes.addAll(newExcludes);
				getEditor().setModelDirty(true);
			}
	
			public void addExclude(Object target, String exclude) {
				Project pom = (Project) target;
				getOrCreateUnitTest(pom).addExclude(exclude);
				getEditor().setModelDirty(true);
			}
	
			public List getExcludes(Object source) {
				Project pom = (Project) source;
				return pom.getBuild() != null 
					? pom.getBuild().getUnitTest() != null
						? pom.getBuild().getUnitTest().getExcludes()
						: null
					: null;
			}
		};
		excludesSection.setExcludesAdaptor(excludesAdaptor);
		control = excludesSection.createControl(parent, factory);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		control.setLayoutData(gd);
		
		resourcesSection = new ResourcesSection(this, "UnitTestResourcesSection");
		IResourceAdaptor adaptor = new IResourceAdaptor() {
			public void setResources(Object target, List resources) {
				Project pom = (Project) target;
				getOrCreateUnitTest(pom).setResources(resources);
				getEditor().setModelDirty(true);
			}
		
			public void addResource(Object target, Resource resource) {
				Project pom = (Project) target;
				getOrCreateUnitTest(pom).addResource(resource);
				getEditor().setModelDirty(true);
			}
		
			public List getResources(Object source) {
				Project pom = (Project) source;
				Build build = pom.getBuild();
				if (build != null) {
					UnitTest unitTest = build.getUnitTest();
					if (unitTest != null) {
						return unitTest.getResources();
					}
				}
				return null;
			}
		};
		resourcesSection.setResourceAdaptor(adaptor);

		control = resourcesSection.createControl(parent, factory);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		control.setLayoutData(gd);
	}

	public void update(Project pom) {
		includesSection.update(pom);
		excludesSection.update(pom);
		resourcesSection.update(pom);
		
		setUpdateNeeded(false);
	}
		
	private UnitTest getOrCreateUnitTest(Project pom) {
		Build build = pom.getBuild();
		if (build == null) {
			build = new Build();
			pom.setBuild(build);
		}
		UnitTest unitTest = build.getUnitTest();
		if (unitTest == null) {
			unitTest = new UnitTest();
			build.setUnitTest(unitTest);
		}
		return unitTest;
	}

}
