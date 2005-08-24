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
package org.mevenide.ui.jbuilder;

import javax.swing.Action;

import com.borland.primetime.ide.Browser;
import com.borland.primetime.ide.BrowserAction;
import com.borland.primetime.ide.ContextActionProvider;
import com.borland.primetime.ide.ProjectView;
import com.borland.primetime.node.Node;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>
 * There are several local context menus, and this class will
 * add entries to the local menu in the Project View, and also
 * to the local menu in the "source" window in the Content pane.
 * </p>
 * @author Serge Huber
 * @version 1.0
 */
public class MavenGoalMenu
    implements ContextActionProvider {

    /**
     * When the OpenTool add-ins are loaded, two ContextActionProviders are
     * registered, one for the Project Pane and one for the EditorPane.
     *
     * @param major byte
     * @param minor byte
     */
    public static void initOpenTool (byte major, byte minor) {
        MavenGoalMenu menu = new MavenGoalMenu();

        // Two different ways to register ContextActionProviders:
        // - implement the interface in the global class and
        //   register the global class as the provider.
        // - implement the interface in a local class and
        //   register the local class as the provider.

        // Register the global class as the provider
        ProjectView.registerContextActionProvider(menu);
    }

    /**
     * The action that will get added to the Project View local
     * menu.  The actionPerformed function is triggered by
     * selecting "View in Notepad" from the local menu after a
     * right click on a batch file node in the Project View.
     */
    public static final Action ACTION_VIEW_NOTEPAD = new BrowserAction(
        "Attain Goal") {
        public void actionPerformed (Browser browser) {
            // Get the node currently selected in the Project View
            Node node = browser.getProjectView().getSelectedNode();
            // We only want batch files.
            if (node instanceof MavenGoalNode) {
                MavenGoalNode batchNode = (MavenGoalNode) node;
                try {
                    // Make Notepad show the file
                    // String path = batchNode.getUrl().getFileObject().getAbsolutePath();
                    // Runtime.getRuntime().exec("notepad " + path);
                } catch (Exception ex) {
                }
            }
        }
    };

    /**
     * Part of the ContextActionProvider interface. If the node is a single node
     * that belongs to a batch file, the appropriate action is returned that is
     * able to deal with a batch file.
     *
     * @param browser current browser
     * @param nodes The node that is currently selected.
     * @return An action appropriate for a batch file.
     */
    public Action getContextAction (Browser browser, Node[] nodes) {
        if (nodes.length == 1 && nodes[0] instanceof MavenGoalNode)
            return ACTION_VIEW_NOTEPAD;
        return null;
    }

}
