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
package org.mevenide.ui.eclipse.editors.pom;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PomContentOutlinePage extends ContentOutlinePage {

	private IDocumentProvider documentProvider;
	private MevenidePomEditor editor;
    private IEditorInput editorInput;

    class PomTreeContentProvider implements ITreeContentProvider {

        public Object[] getChildren(Object parentElement) {
            return null;
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return false;
        }

        public Object[] getElements(Object inputElement) {
            return null;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    	
    }
    
    public PomContentOutlinePage(IDocumentProvider provider, MevenidePomEditor pomEditor) {
        super();
        this.documentProvider = provider;
        this.editor = pomEditor;
    }
    
    public void setInput(IEditorInput input) {
    	this.editorInput = input;
    }

    public void createControl(Composite parent) {
        super.createControl(parent);
        
		TreeViewer viewer= getTreeViewer();
		viewer.setContentProvider(new WorkbenchContentProvider());
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		viewer.addSelectionChangedListener(this);
		if (editorInput != null) {
			viewer.setInput(editorInput);
		}
    }

}
