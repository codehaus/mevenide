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


package org.mevenide.netbeans.project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.mevenide.netbeans.api.project.MavenProject;
import org.mevenide.netbeans.project.exec.DefaultRunConfig;
import org.mevenide.netbeans.project.exec.MavenJavaExecutor;
import org.mevenide.netbeans.project.exec.ProjectRunContext;
import org.mevenide.netbeans.project.exec.RunConfig;
import org.mevenide.netbeans.project.output.AttachDebuggerOutputHook;
import org.mevenide.netbeans.api.output.OutputProcessor;
import org.mevenide.netbeans.project.output.DefaultOutputProcessorFactory;
import org.mevenide.properties.IPropertyLocator;
import org.mevenide.properties.IPropertyResolver;
import org.netbeans.spi.project.ActionProvider;
import org.openide.LifecycleManager;
import org.openide.awt.StatusDisplayer;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.InputOutput;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class ActionProviderImpl implements ActionProvider {
    private static final Logger LOGGER = Logger.getLogger(ActionProviderImpl.class.getName());
    private static final Properties defaultIDEGoals = new Properties();
    
    public static final String COMMAND_MULTIPROJECTBUILD = "multiprojectbuild"; //NOI18N
    public static final String COMMAND_MULTIPROJECTCLEAN = "multiprojectclean"; //NOI18N
    
    private MavenProject project;
    private static String[] supported = new String[] {
        ActionProvider.COMMAND_BUILD,
        ActionProvider.COMMAND_CLEAN,
        ActionProvider.COMMAND_REBUILD,
        "javadoc", //NOI18N
        ActionProvider.COMMAND_TEST,
        ActionProvider.COMMAND_TEST_SINGLE,
        ActionProvider.COMMAND_RUN,
        ActionProvider.COMMAND_RUN_SINGLE,
        ActionProvider.COMMAND_DEBUG,
        ActionProvider.COMMAND_DEBUG_SINGLE,
        ActionProvider.COMMAND_DEBUG_TEST_SINGLE
    };
    /** Creates a new instance of ActionProviderImpl */
    public ActionProviderImpl(MavenProject proj) {
		if (defaultIDEGoals.size() == 0) {
			synchronized(defaultIDEGoals) {
				if (defaultIDEGoals.size() == 0) {				
					initDefaultGoals();
				}
			}
		}
        project = proj;
    }
    
    public String[] getSupportedActions() {
        // allow compile single to be hooked..
        if (project.getPropertyResolver().getResolvedValue(
                 "maven.netbeans.exec." + COMMAND_COMPILE_SINGLE) != null) {
            ArrayList arr = new ArrayList(Arrays.asList(supported));
            arr.add(ActionProvider.COMMAND_COMPILE_SINGLE);
            return (String[])arr.toArray(new String[arr.size()]);
        }
        return supported;
    }
    
    private String getGoalDefForAction(String actionName, Lookup lookup) {
        IPropertyResolver res = project.getPropertyResolver();
        String key = "maven.netbeans.exec.";
        if (COMMAND_DEBUG_SINGLE.equals(actionName)) {
            FileObject[] fos = FileUtilities.extractFileObjectsfromLookup(lookup);
            FileObject fo =  FileUtilities.findTestForFile(project, fos[0]);
            if (fo == fos[0]) {
                // debugging test..
                key = key + COMMAND_DEBUG_TEST_SINGLE;
            } else {
                // debugging non-test..
                key = key + COMMAND_DEBUG_SINGLE;
            }
        } else if (COMMAND_RUN_SINGLE.equals(actionName)) {
            FileObject[] fos = FileUtilities.extractFileObjectsfromLookup(lookup);
            FileObject fo =  FileUtilities.findTestForFile(project, fos[0]);
            if (fo == fos[0]) {
                // running test..
                key = key + COMMAND_TEST_SINGLE;
            } else {
                // running non-test..
                key = key + COMMAND_RUN_SINGLE;
            }
        } else {
            key = key + actionName;
        }
        String value = res.getResolvedValue(key);
        if (value == null) {
            value = getDefaultGoalForAction(key);
        }
        return value;
    }
    
    public static String getDefaultGoalForAction(String key) {
        return defaultIDEGoals.getProperty(key);
    }
    
    public void invokeAction(String str, Lookup lookup) {
        String goal = getGoalDefForAction(str, lookup);
        if (goal != null) {
            int index = goal.indexOf("%TESTCLASS%");
            if (index != -1) {
                FileObject testFile = FileUtilities.findTestForFile(project, FileUtilities.extractFileObjectsfromLookup(lookup)[0]);
                String path = extractPackageName(testFile, FileUtil.toFileObject(new File(project.getTestSrcDirectory())));
                if (path != null) {
                    goal = goal.substring(0, index) + path + goal.substring(index + "%TESTCLASS%".length()); //NOI18N
                } else {
                    StatusDisplayer.getDefault().setStatusText("Cannot execute Maven goal:" + goal);
                    LOGGER.fine("cannot execute:" + goal);
                    return;
                }
            }
            index = goal.indexOf("%CLASS%");
            if (index != -1) {
                String path = extractPackageName(lookup, FileUtil.toFileObject(new File(project.getSrcDirectory())), false);
                if (path != null) {
                    goal = goal.substring(0, index) + path + goal.substring(index + "%CLASS%".length());
                } else {
                    StatusDisplayer.getDefault().setStatusText("Cannot execute Maven goal:" + goal); //NOI18N
                    LOGGER.fine("cannot execute:" + goal);
                    return;
                }
            }
//            if (ActionProvider.COMMAND_RUN.equals(str) || ActionProvider.COMMAND_RUN_SINGLE.equals(str)) {
//                OutputFilter filter = new RunOutputFilter();
//                InputOutput io = IOProvider.getDefault().getIO("Run-" + project.getDisplayName(), true);
//                runGoal(goal, lookup, filter, filter, io);
//            }
            if (ActionProvider.COMMAND_DEBUG.equals(str) 
                || ActionProvider.COMMAND_DEBUG_SINGLE.equals(str) 
                || ActionProvider.COMMAND_DEBUG_TEST_SINGLE.equals(str)) {
                String attach = project.getPropertyResolver().getResolvedValue("maven.netbeans.debug.attach");
                if (attach == null || "true".equals(attach)) {
                    runDebuggedGoal(goal, lookup);
                } else {
                    runGoal(goal, lookup);
                }
            } else {
                runGoal(goal, lookup);
            }
        } else {
            LOGGER.severe("cannot find the action=" + str);
        }
    }
    
    private void runDebuggedGoal(String goal, Lookup lookup) {
        IPropertyResolver resolv = project.getPropertyResolver();
        String host = resolv.getResolvedValue("maven.netbeans.debug.address");
        if (host == null) {
            host = "localhost";
        }
        String portStr = resolv.getResolvedValue("maven.netbeans.debug.port");
        if (portStr == null) {
            portStr = "8888";
        }
        String delStr = resolv.getResolvedValue("maven.netbeans.debug.delay");
        if (delStr == null) {
            delStr = "5000";
        }
        try {
            int port = Integer.parseInt(portStr);
            int delay = Integer.parseInt(delStr);
            OutputProcessor filter = new AttachDebuggerOutputHook(delay, host, port);
            Set procs = DefaultOutputProcessorFactory.getAllProcessors(project);
            procs.add(filter);
            runGoal(goal, lookup, procs, null, new DefaultRunConfig());
        } catch (NumberFormatException exc) {
            LOGGER.log(Level.SEVERE, "Cannot parse", exc);
        }
    }

    public ExecutorTask runGoal(String goal, Lookup lookup) {
        return runGoal(goal, lookup, new DefaultRunConfig());
    }
    
    public ExecutorTask runGoal(String goal, Lookup lookup, RunConfig config) {
        return runGoal(goal, lookup, DefaultOutputProcessorFactory.getAllProcessors(project), null, config);
    }
    
    private ExecutorTask runGoal(String goal, Lookup lookup, 
                         Set processors, 
                         InputOutput io, RunConfig config) {
        // save all edited files.. maybe finetune for project's files only, however that would fail for multiprojects..
        LifecycleManager.getDefault().saveAll();
        boolean ok = MavenModule.checkMavenHome(project.getLocFinder());
        if (!ok) {
            return null;
        }
        MavenModule.copyMevenidePlugin(project.getLocFinder());
        // setup executor first..                     
        MavenJavaExecutor exec = new MavenJavaExecutor(new ProjectRunContext(project), goal, processors, config);
        int meterLoc = project.getPropertyLocator().getPropertyLocation("maven.download.meter"); //NOI18N
        if (    meterLoc == IPropertyLocator.LOCATION_NOT_DEFINED 
            ||  meterLoc == IPropertyLocator.LOCATION_DEFAULTS) {
            exec.setDownloadMeter(MavenSettings.getDefault().getDownloader());
        } else {
            exec.setDownloadMeter(project.getPropertyResolver().getResolvedValue("maven.download.meter")); //NOI18N
        }
        exec.setCustomInputOutput(io);
        ExecutorTask task = ExecutionEngine.getDefault().execute("Maven", exec, exec.getInputOutput());
        //        RequestProcessor.getDefault().post();
        
        // fire project change on when finishing maven execution, to update the classpath etc. -MEVENIDE-83
        task.addTaskListener(new TaskListener() {
                public void taskFinished(Task task2) {
                    project.firePropertyChange(MavenProject.PROP_PROJECT);
                }
        });
        return task;
    }
    
    public boolean isActionEnabled(String str, Lookup lookup) {
        if (COMMAND_TEST_SINGLE.equals(str) || COMMAND_DEBUG_TEST_SINGLE.equals(str)) {
            FileObject[] fos = FileUtilities.extractFileObjectsfromLookup(lookup);
            boolean found = fos != null && fos.length == 1;
            if (found) {
                FileObject test = FileUtilities.findTestForFile(project, fos[0]);
                found =  test != fos[0] && test != null;
            }
            return found;
        } 
        if (COMMAND_RUN_SINGLE.equals(str) || COMMAND_COMPILE_SINGLE.equals(str)) {
            FileObject[] fos = findSources(lookup);
            boolean found = fos != null && fos.length == 1;
            if (!found && COMMAND_RUN_SINGLE.equals(str)) {
                fos = findTestSources(lookup);
                found = fos != null && fos.length == 1;
            }
            return found;
        }
        if (COMMAND_DEBUG_SINGLE.equals(str)) {
            FileObject[] fos = findSources(lookup);
            boolean found = fos != null && fos.length == 1;
            if (!found) {
                fos = findTestSources(lookup);
                found = fos != null && fos.length == 1;
            } 
            return found;
        }
        if (COMMAND_RUN.equals(str) || COMMAND_DEBUG.equals(str)) {
            String mainClass = project.getPropertyResolver().getResolvedValue("maven.jar.mainclass");
            if ((mainClass == null) || (mainClass.length() == 0)) {
                return false;
            }
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
    
   /** Find either selected tests or tests which belong to selected source files
     */
    private FileObject[] findSources(Lookup lookup) {
        FileObject testSrcDir = FileUtil.toFileObject(new File(project.getSrcDirectory()));
        if (testSrcDir != null) {
            FileObject[] files = FileUtilities.findSelectedFiles(lookup, testSrcDir, ".java");
            return files;
        }
        return null;
    }    
    
    private String extractPackageName(Lookup lookup, FileObject root, boolean test) {
        FileObject[] fos = test ? findTestSources(lookup) : findSources(lookup);
        if (fos != null && fos.length == 1) {
            return extractPackageName(fos[0], root);
        }
        return null;
    }
    
    private String extractPackageName(FileObject fo, FileObject root) {
        String path = FileUtil.getRelativePath(root, fo);
        path = path.replace('/', '.');
        path = path.replace('\\', '.');
        if (path.endsWith(".java")) {
            path = path.substring(0, path.length() - ".java".length());
        }
        return path;
    }
    
    public Action createBasicMavenAction(String name, String action) {
        return new BasicAction(name, action);
    }
    
    public Action createCustomMavenAction(String name, String goal) {
        return new CustomAction(name, goal);
    }
    
 
    private void initDefaultGoals() {
        InputStream str = getClass().getClassLoader()
        .getResourceAsStream("org/mevenide/netbeans/project/exec/execdefaults.properties");
        if (str != null) {
            try {
                defaultIDEGoals.load(str);
            } catch (IOException exc) {
                LOGGER.log(Level.SEVERE, "cannot read the default props file", exc);
            } finally {
                try {
                    str.close();
                } catch (IOException exc) {
                    LOGGER.log(Level.SEVERE, "cannot read the default props file2", exc);
                }
            }
        } else {
            LOGGER.log(Level.SEVERE, "cannot read the default props file");
        }
    }
    
    //    public Action createMultiProjectAction(String name, String goals) {
    //        return new MultiProjectAction(name, goals);
    //    }
    
    private final class BasicAction extends AbstractAction {
        private String gls;
        
        
        private BasicAction(String name, String goals) {
            gls = goals;
            putValue(Action.NAME, name);
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            ActionProviderImpl.this.invokeAction(gls, ActionProviderImpl.this.project.getLookup());
        }
    }
    
    private final class CustomAction extends AbstractAction {
        private String gls;
        
        
        private CustomAction(String name, String goals) {
            gls = goals;
            putValue(Action.NAME, name);
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            ActionProviderImpl.this.runGoal(gls, ActionProviderImpl.this.project.getLookup());
        }
    }
 
}
