/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Milos Kleint (ca206216@tiscali.cz).  All rights
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
package org.mevenide.ui.netbeans;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import org.mevenide.ui.netbeans.propeditor.ProjectPropEditor;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/** Action sensitive to some cookie that does something useful.
 *
 * @author cenda
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
