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

package org.mevenide.netbeans.project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.project.exec.MavenExecutor;
import org.mevenide.properties.IPropertyResolver;
import org.netbeans.spi.project.ActionProvider;
import org.openide.awt.HtmlBrowser;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class ActionProviderImpl implements ActionProvider
{
    private static final Log logger = LogFactory.getLog(ActionProviderImpl.class);
    private static final Properties defaultIDEGoals;
    
    public static final String COMMAND_MULTIPROJECTBUILD = "multiprojectbuild"; //NOI18N
    public static final String COMMAND_MULTIPROJECTCLEAN = "multiprojectclean"; //NOI18N
    static {
        defaultIDEGoals = new Properties();
        InputStream str = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/mevenide/netbeans/project/exec/execdefaults.properties");
        if (str != null) {
            try {
                defaultIDEGoals.load(str);
            } catch (IOException exc) {
                logger.error("cannot read the default props file", exc);
            } finally {
                try {
                    str.close();
                } catch (IOException exc) {
                }
            }
        } else {
            logger.error("cannot read the default props file");
        }
    }
    
    private MavenProject project;
    private static String[] supported = new String[] {
            ActionProvider.COMMAND_BUILD,
            ActionProvider.COMMAND_CLEAN,
            ActionProvider.COMMAND_REBUILD, 
            "javadoc", //NOI18N
            ActionProvider.COMMAND_TEST,
            ActionProvider.COMMAND_TEST_SINGLE
        };
    /** Creates a new instance of ActionProviderImpl */
    public ActionProviderImpl(MavenProject proj)
    {
        project = proj;
    }
    
    public String[] getSupportedActions()
    {
        return supported;
    }
    
    private String getGoalDefForAction(String actionName) {
        IPropertyResolver res = project.getPropertyResolver();
        String key = "maven.netbeans.exec." + actionName;
        String value = res.getResolvedValue(key);
        if (value == null) {
            value = getDefaultGoalForAction(key);
        }
        return value;
    }
    
    public static String getDefaultGoalForAction(String key) {
        return defaultIDEGoals.getProperty(key);
    }
    
    public void invokeAction(String str, Lookup lookup) throws java.lang.IllegalArgumentException
    {
        String goal = getGoalDefForAction(str);
        if (goal != null) {
            int index = goal.indexOf("%TESTCLASS%");
            if (index != -1) {
                FileObject[] fos = findTestSources(lookup);
                if (fos != null && fos.length == 1) {
                    FileObject testSrcDir = FileUtil.toFileObject(new File(project.getTestSrcDirectory()));
                    String path = FileUtil.getRelativePath(testSrcDir, fos[0]);
                    path = path.replace('/', '.');
                    path = path.replace('\\', '.');
                    if (path.endsWith(".java")) {
                        path = path.substring(0, path.length() - ".java".length());
                    }
                    goal = goal.substring(0, index) + path + goal.substring(index + "%TESTCLASS%".length());
                    System.out.println("goal=" + goal);
                } else {
                    logger.debug("cannot execute:" + goal);
                    return;
                }
            }
            runGoal(goal, lookup);
        } else {
            logger.error("cannot find the action=" + str);
        }
    }
    
    public void runGoal(String goal, Lookup lookup) throws java.lang.IllegalArgumentException {
        MavenExecutor exec = new MavenExecutor(project, goal);
        exec.setNoBanner(MavenSettings.getDefault().isNoBanner());
        exec.setOffline(MavenSettings.getDefault().isOffline());
        ExecutorTask task = ExecutionEngine.getDefault().execute("Maven", exec, exec.getInputOutput());
//        RequestProcessor.getDefault().post();
        
        //-------------------------------------------------------------------------
        // these are temporary.. 
        // need a more general way of checking for opening browser.
        if ("javadoc".equals(goal)) {
            task.addTaskListener(new TaskListener() {
                public void taskFinished(Task task2) {
                    String javadoc = project.getPropertyResolver().getResolvedValue("maven.javadoc.destdir"); //NOI18N
                    if (javadoc == null) {
                        return;
                    }
                    File fil = new File(javadoc, "index.html"); //NOI18N
                    fil = FileUtil.normalizeFile(fil);
                    if (fil.exists()) {
                        try {
                            HtmlBrowser.URLDisplayer.getDefault().showURL(fil.toURI().toURL());
                        } catch (MalformedURLException exc) {
                            logger.error(exc);
                        }   
                    }
                }
            });
        }
        if ("site:generate".equals(goal)) {
            task.addTaskListener(new TaskListener() {
                public void taskFinished(Task task2) {
                    String docs = project.getPropertyResolver().getResolvedValue("maven.docs.dest"); //NOI18N
                    if (docs == null) {
                        return;
                    }
                    File fil = new File(docs, "index.html"); //NOI18N
                    fil = FileUtil.normalizeFile(fil);
                    if (fil.exists()) {
                        try {
                            HtmlBrowser.URLDisplayer.getDefault().showURL(fil.toURI().toURL());
                        } catch (MalformedURLException exc) {
                            logger.error(exc);
                        }   
                    }
                }
            });
        }
    }
    
    public boolean isActionEnabled(String str, Lookup lookup) throws java.lang.IllegalArgumentException
    {   
        if (COMMAND_TEST_SINGLE.equals(str)) {
            FileObject[] fos = findTestSources(lookup);
            return  fos != null && fos.length == 1;
        }
        return true;
    }
    
    
   /** Find either selected tests or tests which belong to selected source files
     */
    private FileObject[] findTestSources(Lookup lookup) {
        FileObject testSrcDir = FileUtil.toFileObject(new File(project.getTestSrcDirectory()));
        if (testSrcDir != null) {
            FileObject[] files = FileUtilities.findSelectedFiles(lookup, testSrcDir, ".java");
            return files;
        }
        return null;
    }        
    
    
    public Action createBasicMavenAction(String name, String action) {
        return new BasicAction(name, action);
    }
    
    public Action createCustomMavenAction(String name, String goal) {
        return new CustomAction(name, goal);
    }    
    
    
//    public Action createMultiProjectAction(String name, String goals) {
//        return new MultiProjectAction(name, goals);
//    }
    
    private class BasicAction extends AbstractAction {
        private String nm;
        private String gls;
        
        
        private BasicAction(String name, String goals) {
            gls = goals;
            putValue(Action.NAME, name);
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            ActionProviderImpl.this.invokeAction(gls, ActionProviderImpl.this.project.getLookup());
        }
    }
    
    private class CustomAction extends AbstractAction {
        private String nm;
        private String gls;
        
        
        private CustomAction(String name, String goals) {
            gls = goals;
            putValue(Action.NAME, name);
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            ActionProviderImpl.this.runGoal(gls, ActionProviderImpl.this.project.getLookup());
        }
    }
    
//    private class MultiProjectAction extends AbstractAction {
//        private String nm;
//        private String gls;
//        
//        
//        private MultiProjectAction(String name, String goals) {
//            gls = goals;
//            putValue(Action.NAME, name);
//        }
//        
//        public void actionPerformed(java.awt.event.ActionEvent e) {
//            if ("clean".equals(gls)) {
//                ActionProviderImpl.this.runGoal("multiproject:clean", ActionProviderImpl.this.project.getLookup());
//                return;
//            }
//            ActionProviderImpl.this.runGoal("multiproject:goal -Dgoal=" + gls, ActionProviderImpl.this.project.getLookup());
//        }
//    }

}
