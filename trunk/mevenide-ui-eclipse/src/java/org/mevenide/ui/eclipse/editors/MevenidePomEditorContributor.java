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
package org.mevenide.ui.eclipse.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Manages the installation/deinstallation of global actions for the Mevenide
 * POM multi-page editor. Redirects global actions to the main editor, replacing
 * contributors to the individual editors that compose the main editor.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class MevenidePomEditorContributor extends MultiPageEditorActionBarContributor {
    private IEditorPart activeEditorPart;
    private Action sampleAction;
    
    public MevenidePomEditorContributor() {
        super();
        createActions();
    }
    
    /**
     * Returns the action registed with the given text editor.
     * @return IAction or null if editor is null.
     */
    protected IAction getAction(ITextEditor editor, String actionID) {
        return (editor == null ? null : editor.getAction(actionID));
    }
    
    public void setActivePage(IEditorPart part) {
        if (activeEditorPart == part)
            return;

        activeEditorPart = part;

        IActionBars actionBars = getActionBars();
        if (actionBars != null) {

            ITextEditor editor = (part instanceof ITextEditor) ? (ITextEditor) part : null;

            actionBars.setGlobalActionHandler(
                IWorkbenchActionConstants.DELETE,
                getAction(editor, IWorkbenchActionConstants.DELETE));
            actionBars.setGlobalActionHandler(
                IWorkbenchActionConstants.UNDO,
                getAction(editor, IWorkbenchActionConstants.UNDO));
            actionBars.setGlobalActionHandler(
                IWorkbenchActionConstants.REDO,
                getAction(editor, IWorkbenchActionConstants.REDO));
            actionBars.setGlobalActionHandler(
                IWorkbenchActionConstants.CUT,
                getAction(editor, IWorkbenchActionConstants.CUT));
            actionBars.setGlobalActionHandler(
                IWorkbenchActionConstants.COPY,
                getAction(editor, IWorkbenchActionConstants.COPY));
            actionBars.setGlobalActionHandler(
                IWorkbenchActionConstants.PASTE,
                getAction(editor, IWorkbenchActionConstants.PASTE));
            actionBars.setGlobalActionHandler(
                IWorkbenchActionConstants.SELECT_ALL,
                getAction(editor, IWorkbenchActionConstants.SELECT_ALL));
            actionBars.setGlobalActionHandler(
                IWorkbenchActionConstants.FIND,
                getAction(editor, IWorkbenchActionConstants.FIND));
            actionBars.setGlobalActionHandler(
                IWorkbenchActionConstants.BOOKMARK,
                getAction(editor, IWorkbenchActionConstants.BOOKMARK));
            actionBars.updateActionBars();
        }
    }
    
    private void createActions() {
        sampleAction = new Action() {
            public void run() {
                MessageDialog.openInformation(null, "Mevenide", "Sample Action Executed");
            }
        };
        sampleAction.setText("Sample Action");
        sampleAction.setToolTipText("Sample Action tool tip");
        sampleAction.setImageDescriptor(
            PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
            	ISharedImages.IMG_OBJS_TASK_TSK
            )
        );
    }
    
    public void contributeToMenu(IMenuManager manager) {
        IMenuManager menu = new MenuManager("Editor &Menu");
        manager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);
        menu.add(sampleAction);
    }
    
    public void contributeToToolBar(IToolBarManager manager) {
        manager.add(new Separator());
        manager.add(sampleAction);
    }

    public void updateActions() {
    }
}
