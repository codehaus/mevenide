/* ==========================================================================
 * Copyright 2006 Mevenide Team
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
package org.netbeans.modules.maven.debug;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.ListeningConnector;
import com.sun.jdi.connect.Transport;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerAdapter;
import org.netbeans.api.debugger.jpda.DebuggerStartException;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.debugger.jpda.MethodBreakpoint;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.Project;
import org.openide.util.RequestProcessor;
import org.openide.windows.InputOutput;


/**
 * Start the JPDA debugger
 * @author <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 */
public class JPDAStart implements Runnable {
    
    
    /**
     * @parameter expression="${jpda.transport}"
     */
    private String transport = "dt_socket"; //NOI18N
    
    /**
     * @parameter expression="${project.artifactId}"
     */
    private String name;
    
    /**
     * @parameter expression="${jpda.stopclass}"
     */
    private String stopClassName;
    
    private final Object[] lock = new Object[2];
    
    private Project project;
    private InputOutput io;

    JPDAStart(InputOutput inputOutput) {
        io = inputOutput;
    }
    
    /**
     * returns the port/address that the debugger listens to..
     */
    public String execute(Project project) throws Throwable {
        this.project = project;
        io.getOut().println("NetBeans: JPDA Listening Start..."); //NOI18N
//            getLog().debug("Entering synch lock"); //NOI18N
        synchronized (lock) {
//                getLog().debug("Entered synch lock"); //NOI18N
            RequestProcessor.getDefault().post(this);
//                    getLog().debug("Entering wait"); //NOI18N
            lock.wait();
//                    getLog().debug("Wait finished"); //NOI18N
            if (lock[1] != null) {
                throw ((Throwable) lock[1]); //NOI18N
            }
        }
        return (String)lock[0];
    }
    
    public void run() {
        synchronized (lock) {
            
            try {
                
                ListeningConnector lc = null;
                Iterator i = Bootstrap.virtualMachineManager().
                        listeningConnectors().iterator();
                for (; i.hasNext();) {
                    lc = (ListeningConnector) i.next();
                    Transport t = lc.transport();
                    if (t != null && t.name().equals(getTransport())) {
                        break;
                    }
                }
                if (lc == null) {
                    throw new RuntimeException
                            ("No trasports named " + getTransport() + " found!"); //NOI18N
                }
                // TODO: revisit later when http://developer.java.sun.com/developer/bugParade/bugs/4932074.html gets integrated into JDK
                // This code parses the address string "HOST:PORT" to extract PORT and then point debugee to localhost:PORT
                // This is NOT a clean solution to the problem but it SHOULD work in 99% cases
                final Map args = lc.defaultArguments();
                String address = lc.startListening(args);
                int port = -1;
                try {
                    port = Integer.parseInt(address.substring(address.indexOf(':') + 1));
//                    getProject ().setNewProperty (getAddressProperty (), "localhost:" + port);
                    Connector.IntegerArgument portArg = (Connector.IntegerArgument) args.get("port"); //NOI18N
                    portArg.setValue(port);
                    lock[0] = Integer.toString(port);
                } catch (Exception e) {
                    // this address format is not known, use default
//                    getProject ().setNewProperty (getAddressProperty (), address);
                    lock[0] = address;
                }
                io.getOut().println("JPDA Address: " + address); //NOI18N
                io.getOut().println("Port:" + lock[0]); //NOI18N
                
                ClassPath sourcePath = Utils.createSourcePath(project);
                ClassPath jdkSourcePath = Utils.createJDKSourcePath(project);
                
                if (getStopClassName() != null && getStopClassName().length() > 0) {
                    MethodBreakpoint b = Utils.createBreakpoint(getStopClassName());
                    DebuggerManager.getDebuggerManager().addDebuggerListener(
                            DebuggerManager.PROP_DEBUGGER_ENGINES,
                            new Listener(b));
                }
                
                
                final Map properties = new HashMap();
                properties.put("sourcepath", sourcePath); //NOI18N
                properties.put("name", getName()); //NOI18N
                properties.put("jdksources", jdkSourcePath); //NOI18N
                
                final ListeningConnector flc = lc;
                RequestProcessor.getDefault().post(new Runnable() {

                    public void run() {
                        try {
                            JPDADebugger.startListening(flc, args,
                                                        new Object[]{properties});
                        }
                        catch (DebuggerStartException ex) {
                            io.getErr().println("Debugger Start Error."); //NOI18N
                            Logger.getLogger(JPDAStart.class.getName()).log(Level.INFO, "Debugger Start Error.", ex);
                        }
                    }
                });
            } catch (java.io.IOException ioex) {
                io.getErr().println("IO Error:"); //NOI18N
//                org.openide.ErrorManager.getDefault().notify(ioex);
                lock[1] = ioex;
            } catch (com.sun.jdi.connect.IllegalConnectorArgumentsException icaex) {
                io.getErr().println("Illegal Connector"); //NOI18N
                lock[1] = icaex;
            } finally {
                lock.notify();
            }
        }
        
    }
    
    
    // support methods .........................................................
    
    
    
    
    // innerclasses ............................................................
    
    private static class Listener extends DebuggerManagerAdapter {
        
        private MethodBreakpoint    breakpoint;
        private Set                 debuggers = new HashSet();
        
        
        Listener(MethodBreakpoint breakpoint) {
            this.breakpoint = breakpoint;
        }
        
        public void propertyChange(PropertyChangeEvent e) {
            if (JPDADebugger.PROP_STATE.equals(e.getPropertyName())) {
                int state = ((Integer) e.getNewValue()).intValue();
                if ( (state == JPDADebugger.STATE_DISCONNECTED) ||
                        (state == JPDADebugger.STATE_STOPPED)
                        ) {
//                    RequestProcessor.getDefault ().post (new Runnable () {
//                        public void run () {
                    if (breakpoint != null) {
                        DebuggerManager.getDebuggerManager().
                                removeBreakpoint(breakpoint);
                        breakpoint = null;
                    }
//                        }
//                    });
                    dispose();
                }
            }
            return;
        }
        
        private void dispose() {
            DebuggerManager.getDebuggerManager().removeDebuggerListener(
                    DebuggerManager.PROP_DEBUGGER_ENGINES,
                    this
                    );
            Iterator it = debuggers.iterator();
            while (it.hasNext()) {
                JPDADebugger d = (JPDADebugger) it.next();
                d.removePropertyChangeListener(
                        JPDADebugger.PROP_STATE,
                        this
                        );
            }
        }
        
        public void engineAdded(DebuggerEngine engine) {
            JPDADebugger debugger = (JPDADebugger) engine.lookupFirst
                    (null, JPDADebugger.class);
            if (debugger == null) {
                return;
            }
            debugger.addPropertyChangeListener(
                    JPDADebugger.PROP_STATE,
                    this
                    );
            debuggers.add(debugger);
        }
        
        @Override
        public void engineRemoved(DebuggerEngine engine) {
            JPDADebugger debugger = (JPDADebugger) engine.lookupFirst
                    (null, JPDADebugger.class);
            if (debugger == null) {
                return;
            }
            debugger.removePropertyChangeListener(
                    JPDADebugger.PROP_STATE,
                    this
                    );
            debuggers.remove(debugger);
        }
    }
    
    public String getTransport() {
        return transport;
    }
    
    public void setTransport(String transport) {
        this.transport = transport;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getStopClassName() {
        return stopClassName;
    }
    
    public void setStopClassName(String stopClassName) {
        this.stopClassName = stopClassName;
    }
    
    
    
    
}