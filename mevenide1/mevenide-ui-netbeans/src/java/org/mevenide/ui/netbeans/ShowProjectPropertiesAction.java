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
package org.mevenide.ui.netbeans;

import java.util.Iterator;
import java.util.Set;
import org.mevenide.ui.netbeans.propeditor.ProjectPropEditor;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/** Action sensitive to some cookie that does something useful.
 *
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class ShowProjectPropertiesAction extends CookieAction
{
    
    protected Class[] cookieClasses()
    {
        return new Class[]
            { MavenProjectCookie.class };
    }
    
    protected int mode()
    {
        return MODE_EXACTLY_ONE;
        // return MODE_ALL;
    }
    
    protected void performAction(Node[] nodes)
    {
        // do work based on the current node selection, e.g.:
        // SourceCookie cookie = (SourceCookie)nodes[0].getCookie(SourceCookie.class);
        // etc.
        if (nodes != null && nodes.length > 0) {
            for (int i= 0; i < nodes.length; i++)
            {
                MavenProjectCookie cook = (MavenProjectCookie)nodes[i].getCookie(MavenProjectCookie.class);
                if (cook != null)
                {
                    DataObject dobj = (DataObject)nodes[i].getCookie(DataObject.class);
                    
                    //ProjectPropEditor component = new ProjectPropEditor(dobj);
                    ProjectPropEditor component = findOpenComponent(dobj);
                    if (component.isOpened())
                    {
                        component.requestFocus();
                    } else {
                        component.open(WindowManager.getDefault().getCurrentWorkspace());
                    }
                }
            }
        }
    }

    private ProjectPropEditor findOpenComponent(DataObject dobj)
    {
        Set set = TopComponent.getRegistry().getOpened();
        Iterator it = set.iterator();
        while (it.hasNext())
        {
            TopComponent tc = (TopComponent)it.next();
            
            DataObject ob = (DataObject)tc.getLookup().lookup(DataObject.class);
            if (ob != null && ob.equals(dobj))
            {
                if (tc instanceof ProjectPropEditor)
                {
                    return (ProjectPropEditor)tc;
                }
            }
        }
        return new ProjectPropEditor(dobj);
    }
    
    public String getName()
    {
        return NbBundle.getMessage(ShowProjectPropertiesAction.class, "ShowProjectProperties.label");
    }
    
    protected String iconResource()
    {
        return "org/mevenide/ui/netbeans/ShowProjectPropertiesActionIcon.gif";
    }
    
    public HelpCtx getHelpCtx()
    {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx(ShowProjectPropertiesActionAction.class);
    }
    
    /** Perform special enablement check in addition to the normal one.
     * protected boolean enable(Node[] nodes) {
     * if (!super.enable(nodes)) return false;
     * if (...) ...;
     * }
     */
    
    /** Perform extra initialization of this action's singleton.
     * PLEASE do not use constructors for this purpose!
     * protected void initialize() {
     * super.initialize();
     * putProperty(Action.SHORT_DESCRIPTION, NbBundle.getMessage(ShowProjectPropertiesActionAction.class, "HINT_Action"));
     * }
     */
    
}
