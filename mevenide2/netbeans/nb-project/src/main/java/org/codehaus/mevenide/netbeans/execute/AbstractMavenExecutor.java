/* ==========================================================================
 * Copyright 2007 Mevenide Team
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

package org.codehaus.mevenide.netbeans.execute;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
import org.codehaus.mevenide.netbeans.api.execute.RunUtils;
import org.codehaus.mevenide.netbeans.debug.JPDAStart;
import org.codehaus.mevenide.netbeans.execute.ui.RunGoalsPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.execution.ExecutorTask;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputListener;

/**
 * common code for MAvenExecutors, sharing tabs and actions..
 * @author mkleint
 */
public abstract class AbstractMavenExecutor extends OutputTabMaintainer implements MavenExecutor, Cancellable {
    protected RunConfig config;
    protected ReRunAction rerun;
    protected ReRunAction rerunDebug;
    protected StopAction stop;
    private List<String> messages = new ArrayList<String>();
    private List<OutputListener> listeners = new ArrayList<OutputListener>();

    protected  ExecutorTask task;
    
    public AbstractMavenExecutor(RunConfig conf) {
        super(conf.getExecutionName());
        config = conf;
        
    }
    
    protected final void checkDebuggerListening(RunConfig config, AbstractOutputHandler handler) throws MojoExecutionException, MojoFailureException {
        if ("true".equals(config.getProperties().getProperty("jpda.listen"))) {//NOI18N
            JPDAStart start = new JPDAStart();
            start.setName(config.getProject().getOriginalMavenProject().getArtifactId());
            start.setStopClassName(config.getProperties().getProperty("jpda.stopclass"));//NOI18N
            start.setLog(handler.getLogger());
            String val = start.execute(config.getProject());
            Enumeration en = config.getProperties().propertyNames();
            while (en.hasMoreElements()) {
                String key = (String)en.nextElement();
                String value = config.getProperties().getProperty(key);
                StringBuffer buf = new StringBuffer(value);
                String replaceItem = "${jpda.address}";//NOI18N
                int index = buf.indexOf(replaceItem);
                while (index > -1) {
                    String newItem = val;
                    newItem = newItem == null ? "" : newItem;//NOI18N
                    buf.replace(index, index + replaceItem.length(), newItem);
                    index = buf.indexOf(replaceItem);
                }
                //                System.out.println("setting property=" + key + "=" + buf.toString());
                config.getProperties().setProperty(key, buf.toString());
            }
            config.getProperties().put("jpda.address", val);//NOI18N
        }
    }
    

    public final void setTask(ExecutorTask task) {
        this.task = task;
    }

    
    public final void addInitialMessage(String line, OutputListener listener) {
        messages.add(line);
        listeners.add(listener);
    }
    
    protected final void processInitialMessage() {
        Iterator<String> it1 = messages.iterator();
        Iterator<OutputListener> it2 = listeners.iterator();
        InputOutput ioput = getInputOutput();
        try {
            while (it1.hasNext()) {
                OutputListener ol = it2.next();
                if (ol != null) {
                    ioput.getErr().println(it1.next(), ol, true);
                } else {
                    ioput.getErr().println(it1.next());
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    
    protected final void actionStatesAtStart() {
        rerun.setEnabled(false);
        rerunDebug.setEnabled(false);
        stop.setEnabled(true);
        
    }
    
    protected final void actionStatesAtFinish() {
        rerun.setEnabled(true);
        rerunDebug.setEnabled(true);
        stop.setEnabled(false);
    }
    
    @Override
    protected void reassignAdditionalContext(Iterator vals) {
        rerun = (ReRunAction)vals.next();
        rerunDebug = (ReRunAction)vals.next();
        stop = (StopAction)vals.next();
        rerun.setConfig(config);
        rerunDebug.setConfig(config);
        stop.setExecutor(this);
    }
    
    @Override
    protected final Collection createContext() {
        Collection col = super.createContext();
        col.add(rerun);
        col.add(rerunDebug);
        col.add(stop);
        return col;
    }
    
    @Override
    protected Action[] createNewTabActions() {
        rerun = new ReRunAction(false);
        rerunDebug = new ReRunAction(true);
        stop = new StopAction();
        rerun.setConfig(config);
        rerunDebug.setConfig(config);
        stop.setExecutor(this);
        Action[] actions = new Action[] {
            rerun, 
            rerunDebug,
            stop
        };
        return actions;
    }
    
    static class ReRunAction extends AbstractAction {
        private RunConfig config;
        private boolean debug;
        
        public ReRunAction(boolean debug) {
            this.debug  = debug;
            this.putValue(Action.SMALL_ICON, debug ? 
                new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/netbeans/execute/refreshdebug.png")) : //NOI18N
                new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/netbeans/execute/refresh.png")));//NOI18N
            putValue(Action.NAME, debug ? NbBundle.getMessage(AbstractMavenExecutor.class, "TXT_Rerun_extra") : NbBundle.getMessage(AbstractMavenExecutor.class, "TXT_Rerun"));
            putValue(Action.SHORT_DESCRIPTION, debug ? NbBundle.getMessage(AbstractMavenExecutor.class, "TIP_Rerun_Extra"): NbBundle.getMessage(AbstractMavenExecutor.class, "TIP_Rerun"));
            setEnabled(false);
            
        }
        
        void setConfig(RunConfig config) {
            this.config = config;
        }
        
        public void actionPerformed(ActionEvent e) {
            if (debug) {
                RunGoalsPanel pnl = new RunGoalsPanel();
                DialogDescriptor dd = new DialogDescriptor(pnl, org.openide.util.NbBundle.getMessage(AbstractMavenExecutor.class, "TIT_Run_maven"));
                pnl.readConfig(config);
                Object retValue = DialogDisplayer.getDefault().notify(dd);
                if (retValue == DialogDescriptor.OK_OPTION) {
                    BeanRunConfig newConfig = new BeanRunConfig();
                    newConfig.setExecutionDirectory(config.getExecutionDirectory());
                    newConfig.setExecutionName(config.getExecutionName());
                    newConfig.setTaskDisplayName(config.getTaskDisplayName());
                    newConfig.setProject(config.getProject());
                    pnl.applyValues(newConfig);
                    RunUtils.executeMaven(newConfig);
                }
            } else {
                RunConfig newConfig = config;
                RunUtils.executeMaven(newConfig);
            }
            //TODO the waiting on tasks won't work..
        }
    }
    
    static class StopAction extends AbstractAction {
        private AbstractMavenExecutor exec;
        StopAction() {
            putValue(Action.SMALL_ICON, new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/netbeans/execute/stop.gif"))); //NOi18N
            putValue(Action.NAME, NbBundle.getMessage(AbstractMavenExecutor.class, "TXT_Stop_execution"));
            putValue(Action.SHORT_DESCRIPTION, NbBundle.getMessage(AbstractMavenExecutor.class, "TIP_Stop_Execution"));
            setEnabled(false);
        }
        
        void setExecutor(AbstractMavenExecutor ex) {
            exec = ex;
        }
        public void actionPerformed(ActionEvent e) {
            exec.cancel();
        }
    }
}
