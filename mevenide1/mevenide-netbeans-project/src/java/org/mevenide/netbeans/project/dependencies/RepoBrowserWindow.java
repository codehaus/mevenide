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

package org.mevenide.netbeans.project.dependencies;

import java.awt.BorderLayout;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.properties.resolver.PropertyResolverFactory;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class RepoBrowserWindow extends TopComponent {
    static RepoBrowserWindow DEFAULT = null;
    /** Creates a new instance of RepoBrowseWindow */
    public RepoBrowserWindow() {
        setLayout(new BorderLayout());
        // kind of ugly, but this is non project based.
        setName("MavenRepositoryBrowser");
        setDisplayName("Maven Repository");
        setIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/RepoBrowser.png"));
        add(new RepositoryExplorerPanel(
                PropertyResolverFactory.getFactory().createContextBasedResolver(
                                DefaultQueryContext.getNonProjectContextInstance()), 
                ConfigUtils.getDefaultLocationFinder()), 
            BorderLayout.CENTER);
    }
    
    public static synchronized RepoBrowserWindow findDefault() {
        if (DEFAULT == null) {
            //If settings file is correctly defined call of WindowManager.findTopComponent() will
            //call TestComponent00.getDefault() and it will set static field component.
            TopComponent tc = WindowManager.getDefault().findTopComponent("mavenrepobrowser"); // NOI18N
            if (tc == null) {
                RepoBrowserWindow.getDefault();
            }
        }
        return DEFAULT;
    }
    /* Singleton accessor reserved for window system ONLY. Used by window system to create
     * OutputWindow instance from settings file when method is given. Use <code>findDefault</code>
     * to get correctly deserialized instance of OutputWindow. */
    public static synchronized RepoBrowserWindow getDefault() {
        if (DEFAULT == null) {
            DEFAULT = new RepoBrowserWindow();
        }
        return DEFAULT;
    }
    
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }
    
    public String preferredID() {
        return "mavenrepobrowser"; //NOI18N
    }
    
    public Object readResolve() throws java.io.ObjectStreamException {
        return getDefault();
    }
    
}
