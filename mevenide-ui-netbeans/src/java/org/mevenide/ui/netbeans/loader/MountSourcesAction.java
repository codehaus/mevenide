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
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.mevenide.ui.netbeans.MavenProjectCookie;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.Repository;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.CookieAction;


/**
 * Action that attempts to mount all the sources defined in the POM.
 * Temporary solution - creation of Maven project environment is preffered.
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class MountSourcesAction extends CookieAction
{
    private static Log logger = LogFactory.getLog(MountSourcesAction.class);
    
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
                        MavenProjectCookie cook = (MavenProjectCookie)nodes[i].getCookie(MavenProjectCookie.class);
                        if (cook != null)
                        {
                            mountSources(cook);
                        }
                    }
                }
            });
        }
    }
    
    private void mountSources(MavenProjectCookie cookie)
    {
        Project proj = cookie.getMavenProject();
        if (proj == null)
        {
            //TODO report something?
            return;
        }
        Build build = proj.getBuild();
        if (build == null)
        {
            // probably not a complete POM
            //TODO do something?
            return;
        }
        File projFileDir = cookie.getProjectFile().getParentFile();
        try
        {
            build.resolveDirectories(projFileDir);
            
            mount(build.getSourceDirectory());
            mount(build.getUnitTestSourceDirectory());
            mount(build.getAspectSourceDirectory());
            mount(build.getIntegrationUnitTestSourceDirectory());
        } catch (IOException exc)
        {
            logger.error("Error while resolving absolute paths, maybe not on local fs", exc);
        } catch (PropertyVetoException exc2)
        {
            logger.debug("vetoed, shoudl not happen, since it's not mounted", exc2);
        }
        //        if (!allMounted)
        //        {
        //            NotifyDescriptor desc = new Message("Some of the dependencies could not be mounted", NotifyDescriptor.ERROR_MESSAGE);
        //            DialogDisplayer.getDefault().notify(desc);
        //        }
    }
    
    private void mount(String absPath) throws IOException, PropertyVetoException
    {
        if (absPath != null) {
            File root = new File(absPath);
            if (root.exists() && !alreadyMounted(root))
            {
                LocalFileSystem fs = new LocalFileSystem();
                fs.setRootDirectory(root);
                Repository.getDefault().addFileSystem(fs);
            }
        }
    }
    
    private boolean alreadyMounted(File rootFile)
    {
        Enumeration en = Repository.getDefault().getFileSystems();
        while (en.hasMoreElements())
        {
            FileSystem fs = (FileSystem)en.nextElement();
            File rootFOFile = FileUtil.toFile(fs.getRoot());
            if (rootFOFile != null && rootFOFile.equals(rootFile))
            {
                return true;
            }
        }
        return false;
    }
    
    public String getName()
    {
        return NbBundle.getMessage(MountSourcesAction.class, "LBL_MountSourcesAction"); //NOI18N
    }
    
    protected String iconResource()
    {
        return "org/mevenide/ui/netbeans/loader/MountSourcesActionIcon.gif"; //NOI18N
    }
    
    public HelpCtx getHelpCtx()
    {
        return new HelpCtx("org.mevenide.ui.netbeans"); // TODO helpset //NOI18N
        // If you will provide context help then use:
        // return new HelpCtx(MountDependenciesAction.class);
    }
    
    
}
