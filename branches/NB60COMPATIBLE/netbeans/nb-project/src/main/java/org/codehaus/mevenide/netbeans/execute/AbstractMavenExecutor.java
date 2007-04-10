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
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
import org.codehaus.mevenide.netbeans.api.execute.RunUtils;
import org.codehaus.mevenide.netbeans.execute.ui.RunGoalsPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.execution.ExecutorTask;
import org.openide.util.Cancellable;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 * common code for MAvenExecutors, sharing tabs and actions..
 * @author mkleint
 */
public abstract class AbstractMavenExecutor implements MavenExecutor, Cancellable {
    protected RunConfig config;
    protected InputOutput io;
    protected ReRunAction rerun;
    protected ReRunAction rerunDebug;
    protected StopAction stop;

    /**
     * All tabs which were used for some process which has now ended.
     * These are closed when you start a fresh process.
     * Map from tab to tab display name.
     */
    protected static final Map freeTabs = new WeakHashMap();
    protected  ExecutorTask task;
    
    public AbstractMavenExecutor(RunConfig conf) {
        config = conf;
        
    }

    public final void setTask(ExecutorTask task) {
        this.task = task;
    }

    public final InputOutput getInputOutput() {
        if (io == null) {
            io = createInputOutput();
        }
        return io;
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
    
    protected final void markFreeTab(InputOutput ioput) {
            synchronized (freeTabs) {
                Collection col = new ArrayList();
                col.add(config.getExecutionName());
                col.add(rerun);
                col.add(rerunDebug);
                col.add(stop);
                freeTabs.put(ioput, col);
            }
        
    }
    
    private InputOutput createInputOutput() {
        synchronized (freeTabs) {
            Iterator it = freeTabs.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                InputOutput free = (InputOutput)entry.getKey();
                Iterator vals = ((Collection)entry.getValue()).iterator();
                String freeName = (String)vals.next();
                if (io == null && freeName.equals(config.getExecutionName())) {
                    // Reuse it.
                    io = free;
                    rerun = (ReRunAction)vals.next();
                    rerunDebug = (ReRunAction)vals.next();
                    stop = (StopAction)vals.next();
                    rerun.setConfig(config);
                    rerunDebug.setConfig(config);
                    stop.setExecutor(this);
                    try {
                        io.getOut().reset();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    // useless: io.flushReader();
                } else {
                    // Discard it.
                    free.closeInputOutput();
                }
            }
            freeTabs.clear();
        }
        //                }
        if (io == null) {
            rerun = new ReRunAction(false);
            rerunDebug = new ReRunAction(true);
            stop = new StopAction();
            Action[] actions = new Action[] {
                rerun, 
                rerunDebug,
                stop
            };
            io = IOProvider.getDefault().getIO(config.getExecutionName(), actions);
            rerun.setConfig(config);
            rerunDebug.setConfig(config);
            stop.setExecutor(this);
        }
        return io;
    }
    
    static class ReRunAction extends AbstractAction {
        private RunConfig config;
        private boolean debug;
        
        public ReRunAction(boolean debug) {
            this.debug  = debug;
            this.putValue(Action.SMALL_ICON, debug ? 
                new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/netbeans/execute/refreshdebug.png")) :
                new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/netbeans/execute/refresh.png")));
            putValue(Action.NAME, debug ? "Re-run with different parameters" : "Re-run the goals.");
            putValue(Action.SHORT_DESCRIPTION, debug ? "Re-run with different parameters": "Re-run the goals.");
            setEnabled(false);
            
        }
        
        void setConfig(RunConfig config) {
            this.config = config;
        }
        
        public void actionPerformed(ActionEvent e) {
            if (debug) {
                RunGoalsPanel pnl = new RunGoalsPanel();
                DialogDescriptor dd = new DialogDescriptor(pnl, "Run Maven");
                pnl.readConfig(config);
                Object retValue = DialogDisplayer.getDefault().notify(dd);
                if (retValue == DialogDescriptor.OK_OPTION) {
                    BeanRunConfig newConfig = new BeanRunConfig();
                    newConfig.setExecutionDirectory(config.getExecutionDirectory());
                    newConfig.setExecutionName(config.getExecutionName());
                    newConfig.setProject(config.getProject());
                    pnl.applyValues(newConfig);
                    RunUtils.executeMaven("Maven", newConfig);
                }
            } else {
                RunConfig newConfig = config;
                RunUtils.executeMaven("Maven", newConfig);
            }
            //TODO the waiting on tasks won't work..
        }
    }
    
    static class StopAction extends AbstractAction {
        private AbstractMavenExecutor exec;
        StopAction() {
            putValue(Action.SMALL_ICON, new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/netbeans/execute/stop.gif")));
            putValue(Action.NAME, "Stop execution");
            putValue(Action.SHORT_DESCRIPTION, "Stop the currently executing build");
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
