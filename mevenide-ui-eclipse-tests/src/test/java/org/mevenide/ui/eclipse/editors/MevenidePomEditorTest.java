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
package org.mevenide.ui.eclipse.editors;

import junit.framework.TestCase;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.mevenide.ui.eclipse.editors.pom.*;
import org.mevenide.ui.eclipse.mocks.MockEditorInput;
import org.mevenide.ui.eclipse.mocks.MockEditorSite;
import org.mevenide.ui.eclipse.mocks.MockFile;
import org.mevenide.ui.eclipse.mocks.MockFileEditorInput;
import org.mevenide.ui.eclipse.mocks.MockProject;
import org.mevenide.ui.eclipse.mocks.MockWorkspace;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com) 
 * @version $Id$
 */
public class MevenidePomEditorTest extends TestCase {

	private IEditorInput input;
	private IEditorSite site;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		MockWorkspace workspace = new MockWorkspace();
		MockFile file = new MockFile();
		file.setupWorkspace(workspace);
		file.setupPathAndInputStream(MevenidePomEditorTest.class.getResource("/project.xml"));
		file.setupProject(new MockProject("UNITTEST"));
		MockFileEditorInput mockInput = new MockFileEditorInput();
		mockInput.setupFile(file);
		this.input = mockInput;
		this.site = new MockEditorSite();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Constructor for MevenidePomEditorTest.
	 * @param arg0
	 */
	public MevenidePomEditorTest(String arg0) {
		super(arg0);
	}

	/*
	 * Class to test for void init(IEditorSite, IEditorInput)
	 */
	public void testInitIEditorSiteIEditorInput_badInput() {
		MevenidePomEditor editor = new MevenidePomEditor();
		try {
			editor.init(null, new MockEditorInput());
			fail();
		} catch (PartInitException e) {
			assertTrue(true);
		}
	}

	public void testInitIEditorSiteIEditorInput_nullFile() {
		MevenidePomEditor editor = new MevenidePomEditor();
		try {
			editor.init(new MockEditorSite(), new MockFileEditorInput());
			fail();
		} catch (PartInitException e) {
			assertTrue(true);
		}
	}

	public void testInitIEditorSiteIEditorInput() {
		MevenidePomEditor editor = new MevenidePomEditor();
		try {
			editor.init(site, input);
			assertTrue(true);
		} catch (PartInitException e) {
			fail();
		}
	}
	
	public void testIsDirty() {
		MevenidePomEditor editor = new MevenidePomEditor();
		try {
			editor.init(site, input);
			assertTrue(! editor.isDirty());
		} catch (PartInitException e) {
			fail();
		}
	}

	public void testIsDirty_modelDirty() {
		MevenidePomEditor editor = new MevenidePomEditor();
		try {
			editor.init(site, input);
			editor.setModelDirty(true);
			assertTrue(editor.isDirty());
		} catch (PartInitException e) {
			fail();
		}
	}

	public void testIsSaveAsAllowed() {
		MevenidePomEditor editor = new MevenidePomEditor();
		assertTrue(editor.isSaveAsAllowed());
	}

	public void testCreatePages() {
//		MevenidePomEditor editor = new MevenidePomEditor();
//		editor.createPartControl(new Composite(null, SWT.NONE));
//		try {
//			editor.init(site, input);
//			editor.createPages();
//			assertTrue(true);
//		} catch (PartInitException e) {
//			fail();
//		}
	}

	public void testPageChange() {
	}

	public void testGetProject() {
	}

	/*
	 * Class to test for void doSave(IProgressMonitor)
	 */
	public void testDoSaveIProgressMonitor() {
	}

	public void testDoSaveAs() {
	}

	public void testSetModelDirty() {
		MevenidePomEditor editor = new MevenidePomEditor();
		DirtyListener listener = new DirtyListener();
		editor.addPropertyListener(listener);
		editor.setModelDirty(true);
		assertTrue(listener.notifiedOfDirtiness);
	}

	class DirtyListener implements IPropertyListener 
	{
		boolean notifiedOfDirtiness;
		public void propertyChanged(Object source, int propId) {
			notifiedOfDirtiness = (propId == IEditorPart.PROP_DIRTY);
		}
	}
	
	public void testGetCurrentPage() {
	}

	public void testGetPage() {
	}

	public void testUpdateModel() {
	}

	public void testUpdateDocument() {
	}

	/*
	 * Class to test for Object getAdapter(Class)
	 */
	public void testGetAdapterClass() {
	}

	public void testGetContributor() {
	}

	public void testClose() {
	}

	public void testSetPropertySourceSelection() {
	}

}