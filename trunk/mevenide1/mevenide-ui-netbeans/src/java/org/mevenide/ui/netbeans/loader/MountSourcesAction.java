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
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.mevenide.ui.netbeans.MavenProjectCookie;
import org.mevenide.ui.netbeans.project.FileSystemUtil;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
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
            if (root.exists())
            {
                FileSystemUtil.mountSources(root);
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
