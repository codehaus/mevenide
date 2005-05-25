/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.idea.editor.pom.ui.layer;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

/**
 * A base class for all POM editing panels.
 *
 * <p>Automatically keeps track of the current focusable component. This is a bug fix for IDEA -
 * when a FileEditor has a panel with multiple fields, switching to another editor and returning
 * returns the focus back to the first component. This class fixes that.</p>
 *
 * <p>Also, this class provides a {@link Log} instance, the correct resource bundle and the project
 * and document this panel edits.</p>
 *
 * @author Arik
 */
public abstract class AbstractPomLayerPanel extends JPanel implements Disposable {
    /**
     * Logging.
     */
    protected final Log LOG;

    /**
     * The IDEA project the POM file, that this POM panel edits, belongs to.
     */
    protected final Project project;

    /**
     * The POM file's IDEA document. Changes are propagated from the user interface into this
     * object.
     */
    protected final Document document;

    /**
     * The current focused component.
     */
    protected Component focusedComponent = null;

    /**
     * The focus tracker. This listener is attached to every focusable component added to this
     * panel.
     */
    private final FocusTracker focusTracker = new FocusTracker();

    /**
     * Creates an instance for the given project and POM document.
     *
     * @param pProject     the project that the POM file belongs to
     * @param pPomDocument the POM file's IDEA document
     */
    protected AbstractPomLayerPanel(final Project pProject,
                                    final Document pPomDocument) {

        LOG = LogFactory.getLog(this.getClass());

        project = pProject;
        document = pPomDocument;

        //
        //this will make sure that when the editor is reselected, the correct
        //component will receive the focus
        //
        this.addHierarchyListener(new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent e) {
                if (focusedComponent != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            focusedComponent.requestFocusInWindow();
                        }
                    });
                }
            }
        });
    }

    /**
     * This method should dispose this componet. Usually that would mean removing any registered
     * listeners, dispose of UI objects, etc.
     */
    public void dispose() {
    }

    /**
     * Overriden to attach our focus tracker to the new component, if it's focusable.
     *
     * @param comp        the component
     * @param constraints ignored (simply passed to the super implementation of this method)
     * @param index       ignored (simply passed to the super implementation of this method)
     */
    @Override protected void addImpl(Component comp, Object constraints, int index) {
        super.addImpl(comp, constraints, index);
        if (comp.isFocusable())
            comp.addFocusListener(focusTracker);
    }

    /**
     * A focus listener which keeps track of the currently focused component.
     */
    private class FocusTracker implements FocusListener {
        public void focusGained(FocusEvent e) {
            focusedComponent = e.getComponent();
        }

        public void focusLost(FocusEvent e) {
        }
    }
}
