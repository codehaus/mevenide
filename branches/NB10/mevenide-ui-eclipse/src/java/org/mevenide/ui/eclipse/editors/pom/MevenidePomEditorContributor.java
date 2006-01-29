/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.pom.validation.MarkerHelper;
import org.mevenide.ui.eclipse.pom.validation.ValidationJob;

/**
 * Manages the installation/deinstallation of global actions for the Mevenide
 * POM multi-page editor. Redirects global actions to the main editor, replacing
 * contributors to the individual editors that compose the main editor.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class MevenidePomEditorContributor extends MultiPageEditorActionBarContributor {
    private static final String ID = "org.mevenide.maven.menu.id";

    private static final Log log = LogFactory.getLog(MevenidePomEditorContributor.class);
    
    private IEditorPart activeEditorPart;
    private Action validatePomAction;
    private Action clearMarkersAction;
    private IFile pomFile;
    
    public MevenidePomEditorContributor() {
        super();
        createActions();
    }
    
    
    public void setActiveEditor(IEditorPart part) {
        try {
            pomFile = ((IFileEditorInput) part.getEditorInput()).getFile();
        }
        catch (Exception e) {
            String message = "Unable to initialize pom";  //$NON-NLS-1$
            log.error(message, e);
        }
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
                ITextEditorActionConstants.DELETE,
                getAction(editor, ITextEditorActionConstants.DELETE));
            actionBars.setGlobalActionHandler(
                ITextEditorActionConstants.UNDO,
                getAction(editor, ITextEditorActionConstants.UNDO));
            actionBars.setGlobalActionHandler(
                ITextEditorActionConstants.REDO,
                getAction(editor, ITextEditorActionConstants.REDO));
            actionBars.setGlobalActionHandler(
                ITextEditorActionConstants.CUT,
                getAction(editor, ITextEditorActionConstants.CUT));
            actionBars.setGlobalActionHandler(
                ITextEditorActionConstants.COPY,
                getAction(editor, ITextEditorActionConstants.COPY));
            actionBars.setGlobalActionHandler(
                ITextEditorActionConstants.PASTE,
                getAction(editor, ITextEditorActionConstants.PASTE));
            actionBars.setGlobalActionHandler(
                ITextEditorActionConstants.SELECT_ALL,
                getAction(editor, ITextEditorActionConstants.SELECT_ALL));
            actionBars.setGlobalActionHandler(
                ITextEditorActionConstants.FIND,
                getAction(editor, ITextEditorActionConstants.FIND));
            actionBars.setGlobalActionHandler(
                    IDEActionFactory.BOOKMARK.getId(),
                getAction(editor, IDEActionFactory.BOOKMARK.getId()));
            actionBars.updateActionBars();
        }
    }
    
    private void createActions() {
        createValidatePomAction();
        createClearMarkersAction();
    }
    
    private void createClearMarkersAction() {
        clearMarkersAction = new Action() {
            public void run() {
                try {
                    MarkerHelper.deleteMarkers(pomFile);
                }
                catch (Exception e) {
                    String message = "unable to delete markers";  //$NON-NLS-1$
                    log.error(message, e);
                }
            }
        };
        clearMarkersAction.setText(Mevenide.getResourceString("MevenidePomEditorContributor.ClearMarkers.Action.Text")); //$NON-NLS-1$
        clearMarkersAction.setToolTipText(Mevenide.getResourceString("MevenidePomEditorContributor.ClearMarkers.Action.ToolTip")); //$NON-NLS-1$
        clearMarkersAction.setImageDescriptor(Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.CLEAR_VALIDATE_TOOL));
    }


    private void createValidatePomAction() {
        validatePomAction = new Action() {
            public void run() {
                new ValidationJob(pomFile).schedule();
            }
        };
        validatePomAction.setText(Mevenide.getResourceString("MevenidePomEditorContributor.Validate.Action.Text")); //$NON-NLS-1$
        validatePomAction.setToolTipText(Mevenide.getResourceString("MevenidePomEditorContributor.Validate.Action.ToolTip")); //$NON-NLS-1$
        validatePomAction.setImageDescriptor(Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.VALIDATE_TOOL));
    }

    public void contributeToMenu(IMenuManager manager) {
        IMenuManager menu = new MenuManager(Mevenide.getResourceString("MevenidePomEditorContributor.Menu.Text"), ID); //$NON-NLS-1$
        manager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);
        menu.add(validatePomAction);
        menu.add(clearMarkersAction);
    }
    
    public void contributeToToolBar(IToolBarManager manager) {
        manager.add(new Separator());
        manager.add(validatePomAction);
        manager.add(clearMarkersAction);
    }

    public void updateActions() {
    }
}
