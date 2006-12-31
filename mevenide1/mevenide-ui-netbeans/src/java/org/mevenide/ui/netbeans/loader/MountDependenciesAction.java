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
package org.mevenide.ui.netbeans.loader;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.repository.Artifact;
import org.mevenide.ui.netbeans.ArtifactCookie;
import org.mevenide.ui.netbeans.project.FileSystemUtil;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Message;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.CookieAction;


/** Action sensitive to some cookie that does something useful.
 *
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class MountDependenciesAction extends CookieAction
{
    protected Class[] cookieClasses()
    {
        return new Class[]
        { ArtifactCookie.class };
    }
    
    protected int mode()
    {
        return MODE_ALL;
        // return MODE_ALL;
    }
    
    protected void performAction(final Node[] nodes)
    {
        if (nodes != null && nodes.length > 0)
        {
            RequestProcessor.getDefault().post(new Runnable()
            {
                public void run()
                {
                    for (int i= 0; i < nodes.length; i++)
                    {
                        ArtifactCookie cook = (ArtifactCookie)nodes[i].getCookie(ArtifactCookie.class);
                        if (cook != null)
                        {
                            mountDependencies(cook);
                        }
                    }
                }
            });
        }
    }
    
    private void mountDependencies(ArtifactCookie cookie)
    {
        List lst = cookie.getArtifacts();
        if (lst == null) return;
        Iterator it = lst.iterator();
        boolean allMounted = true;
        while (it.hasNext())
        {
            Artifact art = (Artifact)it.next();
            if (art.exists())
            {
                try
                {
                    FileSystemUtil.mountDependency(art);
                } catch (IOException io)
                {
                    //TODO
                    ErrorManager.getDefault().annotate(io, "Cannot mount");
                    allMounted = false;
                } catch (PropertyVetoException pve)
                {
                    //TODO
                    ErrorManager.getDefault().annotate(pve, "Cannot mount");
                    allMounted = false;
                }
            } else
            {
                allMounted = false;
            }
        }
        if (!allMounted)
        {
            NotifyDescriptor desc = new Message("Some of the dependencies could not be mounted", NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(desc);
        }
    }
    
 
    

    
    public String getName()
    {
        return NbBundle.getMessage(MountDependenciesAction.class, "LBL_MountDependenciesAction"); //NOI18N
    }
    
    protected String iconResource()
    {
        return "org/mevenide/ui/netbeans/loader/MountDependenciesActionIcon.gif"; //NOI18N
    }
    
    public HelpCtx getHelpCtx()
    {
        return new HelpCtx("org.mevenide.ui.netbeans"); // TODO helpset //NOI18N
        // If you will provide context help then use:
        // return new HelpCtx(MountDependenciesAction.class);
    }
    
    
}
