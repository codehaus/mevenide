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

package org.codehaus.mevenide.netbeans;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.codehaus.mevenide.netbeans.execute.DefaultRunConfig;
import org.codehaus.mevenide.netbeans.execute.MavenJavaExecutor;
import org.codehaus.mevenide.netbeans.execute.RunConfig;
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
    
    private NbMavenProject project;
    private static String[] supported = new String[] {
        ActionProvider.COMMAND_BUILD,
        ActionProvider.COMMAND_CLEAN,
        ActionProvider.COMMAND_REBUILD,
        "javadoc"//, //NOI18N
//        ActionProvider.COMMAND_TEST,
//        ActionProvider.COMMAND_TEST_SINGLE,
//        ActionProvider.COMMAND_RUN,
//        ActionProvider.COMMAND_RUN_SINGLE,
//        ActionProvider.COMMAND_DEBUG,
//        ActionProvider.COMMAND_DEBUG_SINGLE,
//        ActionProvider.COMMAND_DEBUG_TEST_SINGLE
    };
    
    private static Properties mappedGoals;
    static {
        mappedGoals = new Properties();
        mappedGoals.put(ActionProvider.COMMAND_BUILD, "install");
        mappedGoals.put(ActionProvider.COMMAND_CLEAN, "clean:clean");
        mappedGoals.put(ActionProvider.COMMAND_REBUILD, "clean:clean install");
        mappedGoals.put("javadoc", "javadoc:javadoc");
    }
    
    /** Creates a new instance of ActionProviderImpl */
    public ActionProviderImpl(NbMavenProject proj) {
        project = proj;
    }
    
    public String[] getSupportedActions() {
        return supported;
    }
    
    private List getGoalsDefForAction(String actionName, Lookup lookup) {
        String val = mappedGoals.getProperty(actionName);
        String[] vals = val.split(" ");
        return Arrays.asList(vals);
    }
    
    public void invokeAction(String action, Lookup lookup) {
        runGoal(getGoalsDefForAction(action, lookup), lookup);
    }
    
    public void runGoal(List goals, Lookup lookup) {
        runGoal(lookup, new DefaultRunConfig(project, goals));
    }
    
    private void runGoal(Lookup lookup, 
                         RunConfig config) {
        // save all edited files.. maybe finetune for project's files only, however that would fail for multiprojects..
        LifecycleManager.getDefault().saveAll();
        
        // setup executor first..                     
        MavenJavaExecutor exec = new MavenJavaExecutor(config);
        ExecutorTask task = ExecutionEngine.getDefault().execute("Maven", exec, exec.getInputOutput());
        //        RequestProcessor.getDefault().post();
        
        // fire project change on when finishing maven execution, to update the classpath etc. -MEVENIDE-83
        task.addTaskListener(new TaskListener() {
                public void taskFinished(Task task2) {
                    project.firePropertyChange(NbMavenProject.PROP_PROJECT);
                }
        });
    }
    
    public boolean isActionEnabled(String str, Lookup lookup) {
        return true;
    }
    
    
//    /** Find either selected tests or tests which belong to selected source files
//     */
//    private FileObject[] findTestSources(Lookup lookup) {
//        FileObject testSrcDir = FileUtil.toFileObject(new File(project.getTestSrcDirectory()));
//        if (testSrcDir != null) {
//            FileObject[] files = FileUtilities.findSelectedFiles(lookup, testSrcDir, ".java");
//            return files;
//        }
//        return null;
//    }
//    
//   /** Find either selected tests or tests which belong to selected source files
//     */
//    private FileObject[] findSources(Lookup lookup) {
//        FileObject testSrcDir = FileUtil.toFileObject(new File(project.getSrcDirectory()));
//        if (testSrcDir != null) {
//            FileObject[] files = FileUtilities.findSelectedFiles(lookup, testSrcDir, ".java");
//            return files;
//        }
//        return null;
//    }    
    
//    private String extractPackageName(Lookup lookup, FileObject root, boolean test) {
//        FileObject[] fos = test ? findTestSources(lookup) : findSources(lookup);
//        if (fos != null && fos.length == 1) {
//            return extractPackageName(fos[0], root);
//        }
//        return null;
//    }
//    
//    private String extractPackageName(FileObject fo, FileObject root) {
//        String path = FileUtil.getRelativePath(root, fo);
//        path = path.replace('/', '.');
//        path = path.replace('\\', '.');
//        if (path.endsWith(".java")) {
//            path = path.substring(0, path.length() - ".java".length());
//        }
//        return path;
//    }
    
    public Action createBasicMavenAction(String name, String action) {
        return new BasicAction(name, action);
    }
    
//    public Action createCustomMavenAction(String name, String goal) {
//        return new CustomAction(name, goal);
//    }
    
    
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
    
//    private final class CustomAction extends AbstractAction {
//        private String gls;
//        
//        
//        private CustomAction(String name, String goals) {
//            gls = goals;
//            putValue(Action.NAME, name);
//        }
//        
//        public void actionPerformed(java.awt.event.ActionEvent e) {
//            ActionProviderImpl.this.runGoal(gls, ActionProviderImpl.this.project.getLookup());
//        }
//    }
 
}
