/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
package org.mevenide.ui.netbeans.loader;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.repository.Artifact;
import org.mevenide.ui.netbeans.ArtifactCookie;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Message;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.JarFileSystem;
import org.openide.filesystems.Repository;
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
                //                StatusDisplayer.getDefault().setStatusText("Attempting to mount " + art.getName());
                if (alreadyMounted(art.getFile()))
                {
                    StatusDisplayer.getDefault().setStatusText(art.getName() + " already mounted.");
                } else
                {
                    try
                    {
                        JarFileSystem fs = new JarFileSystem();
                        fs.setJarFile(art.getFile());
                        Repository.getDefault().addFileSystem(fs);
                    } catch (IOException io)
                    {
                        //TODO
                        ErrorManager.getDefault().annotate(io, "Cannot mount");
                    } catch (PropertyVetoException ex)
                    {
                        //TODO
                        ErrorManager.getDefault().annotate(ex, "Cannot mount2");
                    }
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
    
    
    private boolean alreadyMounted(File jarFile)
    {
        Enumeration en = Repository.getDefault().getFileSystems();
        while (en.hasMoreElements())
        {
            FileSystem fs = (FileSystem)en.nextElement();
            if (fs.getSystemName().indexOf(jarFile.getName()) >= 0)
            {
                return true;
            }
        }
        return false;
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
