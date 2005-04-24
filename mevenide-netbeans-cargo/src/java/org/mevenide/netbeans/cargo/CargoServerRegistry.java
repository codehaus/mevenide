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


package org.mevenide.netbeans.cargo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.ContainerFactory;
import org.codehaus.cargo.container.State;
import org.codehaus.cargo.container.configuration.ConfigurationFactory;
import org.codehaus.cargo.container.configuration.DefaultConfigurationFactory;
import org.codehaus.cargo.container.deployable.DeployableFactory;
import org.codehaus.cargo.container.deployer.DefaultDeployerFactory;
import org.codehaus.cargo.container.deployer.DeployerFactory;
import org.codehaus.cargo.container.jetty.Jetty4xEmbeddedContainer;
import org.codehaus.cargo.container.jo.Jo1xContainer;
import org.codehaus.cargo.container.orion.Oc4j9xContainer;
import org.codehaus.cargo.container.orion.Orion1xContainer;
import org.codehaus.cargo.container.orion.Orion2xContainer;
import org.codehaus.cargo.container.resin.Resin2xContainer;
import org.codehaus.cargo.container.resin.Resin3xContainer;
import org.codehaus.cargo.container.spi.DefaultDeployableFactory;
import org.codehaus.cargo.container.tomcat.Tomcat3xContainer;
import org.codehaus.cargo.container.tomcat.Tomcat4xContainer;
import org.codehaus.cargo.container.tomcat.Tomcat5xContainer;
import org.codehaus.cargo.container.weblogic.WebLogic8xContainer;
import org.codehaus.cargo.util.monitor.FileMonitor;
import org.codehaus.cargo.util.monitor.Monitor;
import org.codehaus.cargo.util.monitor.NullMonitor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.util.RequestProcessor;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class CargoServerRegistry {
    //TODO have smething more customizable/pluggable for this
    public static String[] CONTAINER_TYPES = new String[] {
        Resin2xContainer.ID,
        Resin3xContainer.ID,
        Orion1xContainer.ID,
        Orion2xContainer.ID,
        Oc4j9xContainer.ID,
// skip jetty because it's embedded.
//      Jetty4xEmbeddedContainer.ID, 
        Tomcat3xContainer.ID, 
        Tomcat4xContainer.ID, 
        Tomcat5xContainer.ID, 
        WebLogic8xContainer.ID, 
        Jo1xContainer.ID
    };
    
    private HashMap installUrls;
    private HashSet running;
    private ArrayList listeners;
    private Monitor monitor;
    private File monitorFile;
    private RequestProcessor processor;
    private ContainerFactory factory;
    private ConfigurationFactory configFactory;
    private DeployableFactory deployableFactory;
    private DeployerFactory deployerFactory;
    
    private static CargoServerRegistry instance;
    /** Creates a new instance of CargoServerRegistry */
    private CargoServerRegistry() {
        installUrls = new HashMap();
        installUrls.put(Resin2xContainer.ID, "http://www.caucho.com/download/resin-2.1.14.zip");
        installUrls.put(Resin3xContainer.ID, "http://www.caucho.com/download/resin-3.0.9.zip");
        installUrls.put(Orion1xContainer.ID, "http://www.orionserver.com/distributions/orion1.6.0b.zip");
        installUrls.put(Orion2xContainer.ID, "http://www.orionserver.com/distributions/orion2.0.5.zip");
        installUrls.put(Tomcat3xContainer.ID, "http://www.apache.org/dist/jakarta/tomcat-3/v3.3.2/bin/jakarta-tomcat-3.3.2.zip");
        installUrls.put(Tomcat4xContainer.ID, "http://www.apache.org/dist/jakarta/tomcat-4/v4.1.31/bin/jakarta-tomcat-4.1.31.zip");
        installUrls.put(Tomcat5xContainer.ID, "http://www.apache.org/dist/jakarta/tomcat-5/v5.0.30/bin/jakarta-tomcat-5.0.30.zip");
        processor = new RequestProcessor("Cargo Containers", 10);
        factory = new ContainerFactory();
        configFactory = new DefaultConfigurationFactory();
        deployableFactory = new DefaultDeployableFactory();
        deployerFactory = new DefaultDeployerFactory();
        try {
            monitorFile = File.createTempFile("cargo", "log");
            monitor = new FileMonitor(monitorFile, false);
        } catch (IOException exc) {
            //TODO.. message?
            monitor = new NullMonitor();
        }
        running = new HashSet();
        listeners = new ArrayList();
    }
    
    public static synchronized CargoServerRegistry getInstance() {
        if (instance == null) {
            instance = new CargoServerRegistry();
        }
        return instance;
    }
    
    public ContainerFactory getFactory() {
        return factory;
    }
    
    public ConfigurationFactory getConfigFactory() {
        return configFactory;
    }
    
    public DeployableFactory getDeployableFactory() {
        return deployableFactory;
    }
    
    public DeployerFactory getDeployerFactory() {
        return deployerFactory;
    }
    
    public File getMonitorFile() {
        return monitorFile;
    }
    
    public synchronized void addContainer(Container cont) {
        running.add(cont);
        cont.setMonitor(monitor);
        fireAdded(cont);
        
    }
    
    public synchronized void startContainer(final Container cont) {
        if (!running.contains(cont)) {
            throw new IllegalStateException();
        }
        // replace processor with engine?
        processor.post(new Runnable() {
            public void run() {
                fireChange(cont, new RegistryEvent(cont, State.STARTING));
                try {
                    cont.start();
                } catch (ContainerException exc) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exc);
                    NotifyDescriptor nd = new NotifyDescriptor.Message(exc.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                } finally {
                    fireChange(cont);
                }
            }
        });
    }
    
    public synchronized void stopContainer(final Container cont) {
        processor.post(new Runnable() {
            public void run() {
                fireChange(cont, new RegistryEvent(cont, State.STOPPING));
                try {
                    cont.stop();
                } catch (ContainerException exc) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exc);
                    NotifyDescriptor nd = new NotifyDescriptor.Message(exc.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                } finally {
                    fireChange(cont);
                }
            }
        });
    }
    
    public synchronized void removeContainer(Container cont) {
        if (!running.contains(cont)) {
            throw new IllegalStateException();
        }
        if (cont.getState() != State.STOPPED) {
            stopContainer(cont);
        }
        running.remove(cont);
        fireRemoved(cont);
    }
    
    public synchronized Set getContainers() {
        return Collections.unmodifiableSet(running);
    }
    
    public void addRegistryListener(RegistryListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }
    
    public void removeRegistryListener(RegistryListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
    
    private void fireAdded(Container cont) {
        ArrayList lst = null;
        synchronized (listeners) {
            lst = new ArrayList(listeners);
        }
        RegistryEvent evnt = new RegistryEvent(cont);
        Iterator it = lst.iterator();
        while (it.hasNext()) {
            RegistryListener listener = (RegistryListener)it.next();
            listener.containerAdded(evnt);
        }
    }
    
    private void fireRemoved(Container cont) {
        ArrayList lst = null;
        synchronized (listeners) {
            lst = new ArrayList(listeners);
        }
        RegistryEvent evnt = new RegistryEvent(cont);
        Iterator it = lst.iterator();
        while (it.hasNext()) {
            RegistryListener listener = (RegistryListener)it.next();
            listener.containerRemoved(evnt);
        }
    }
    
    private void fireChange(Container cont, RegistryEvent evnt) {
        ArrayList lst = null;
        synchronized (listeners) {
            lst = new ArrayList(listeners);
        }
        Iterator it = lst.iterator();
        while (it.hasNext()) {
            RegistryListener listener = (RegistryListener)it.next();
            listener.stateChanged(evnt);
        }
    }
    
    private void fireChange(Container cont) {
        RegistryEvent evnt = new RegistryEvent(cont);
        fireChange(cont, evnt);
    }
    
}
