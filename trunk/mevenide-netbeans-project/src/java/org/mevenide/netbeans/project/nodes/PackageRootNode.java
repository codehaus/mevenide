/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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

package org.mevenide.netbeans.project.nodes;

import javax.swing.Action;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.openide.actions.FindAction;
import org.openide.actions.NewTemplateAction;
import org.openide.actions.OpenLocalExplorerAction;
import org.openide.loaders.DataFolder;
import org.openide.nodes.AbstractNode;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
class PackageRootNode extends AbstractNode
{
    
    private static Action actions[];
    
    PackageRootNode(DataFolder dir, String name, String displayName)
    {
        super(PackageView.createPackageView(dir.getPrimaryFile()), 
                                            Lookups.fixed( new Object[] { dir } ) );
        setName(name);
        setDisplayName(displayName);
        // can do so, since we depend on it..
        setIconBase("org/netbeans/modules/java/j2seproject/ui/resources/packageRoot");
    }
    
    public Action[] getActions( boolean context )
    {
        
        if ( actions == null )
        {
            actions = new Action[]
            {
                SystemAction.get( OpenLocalExplorerAction.class ),
                SystemAction.get( FindAction.class ),
                null,
                SystemAction.get( NewTemplateAction.class ),
            };
        }
        return actions;
    }
}

