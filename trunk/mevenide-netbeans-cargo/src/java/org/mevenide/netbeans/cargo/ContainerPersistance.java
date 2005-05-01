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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerFactory;
import org.codehaus.cargo.container.configuration.ConfigurationFactory;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class ContainerPersistance {
    private static final String CARGO_ROOT = "Cargo/Containers"; //NOI18N
    
    private static final String CONTAINER_TYPE = "ContainerType"; //NOI18N
    private static final String HOME_DIR = "HomeDir"; //NOI18N
    private static final String CONFIG_HOME_DIR = "ConfigHomeDir"; //NOI18N
    private static final String PORT = ServletPropertySet.PORT;
    private static final String USERS = ServletPropertySet.USERS;
    private static final String HOSTNAME = GeneralPropertySet.HOSTNAME;
    private static final String JVMARGS = GeneralPropertySet.JVMARGS;
    private static final String LOGGING = GeneralPropertySet.LOGGING;
    
    /** Creates a new instance of ContainerPersistance */
    private ContainerPersistance() {
    }
    
    public static void saveContainers() {
        FileObject obj = Repository.getDefault().getDefaultFileSystem().findResource(CARGO_ROOT);
        try {
            if (obj == null) {
                obj = FileUtil.createFolder(Repository.getDefault().getDefaultFileSystem().getRoot(), CARGO_ROOT);
            }
            FileObject[] fos = obj.getChildren();
            for (int i = 0; i < fos.length; i++) {
                fos[i].delete();
            }
            Set containers = CargoServerRegistry.getInstance().getContainers();
            Iterator it = containers.iterator();
            while (it.hasNext()) {
                Container cont = (Container)it.next();
                String name = FileUtil.findFreeFileName(obj, cont.getId(), "properties");
                FileObject fo = obj.createData(name);
                Properties props = new Properties();
                putProperty(props, CONTAINER_TYPE, cont.getId());
                putProperty(props, HOME_DIR, cont.getHomeDir().getAbsolutePath());
                putProperty(props, CONFIG_HOME_DIR, cont.getConfiguration().getDir().getAbsolutePath());
                putProperty(props, PORT, cont.getConfiguration().getPropertyValue(ServletPropertySet.PORT));
                putProperty(props, USERS, cont.getConfiguration().getPropertyValue(ServletPropertySet.USERS));
                putProperty(props, HOSTNAME, cont.getConfiguration().getPropertyValue(GeneralPropertySet.HOSTNAME));
                putProperty(props, LOGGING, cont.getConfiguration().getPropertyValue(GeneralPropertySet.LOGGING));
                putProperty(props, JVMARGS, cont.getConfiguration().getPropertyValue(GeneralPropertySet.JVMARGS));
                OutputStream str = null;
                FileLock lock = null;
                try {
                    lock = fo.lock();
                    str = fo.getOutputStream(lock);
                    props.store(str, null);
                } finally {
                    if (str != null) {
                        try {
                            str.close();
                        } catch (IOException x) {
                            // ignore
                            x.printStackTrace();
                        }
                    }
                    if (lock != null) {
                        System.out.println("releasing lock");
                        lock.releaseLock();
                    }
                }
            }
            
        } catch (IOException exc) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exc);
        } finally {
        }
    }
    
    private static void putProperty(Properties props, String key, String value) {
        if (value != null) {
            props.setProperty(key, value);
        }
    }
    
    public static void loadContainers() {
        FileObject obj = Repository.getDefault().getDefaultFileSystem().findResource(CARGO_ROOT);
        if (obj != null) {
            FileObject[] childs = obj.getChildren();
            for (int i = 0; i < childs.length; i++) {
                Properties prop = new Properties();
                InputStream str = null;
                try {
                    str = childs[i].getInputStream();
                    prop.load(str);
                    String type = prop.getProperty(CONTAINER_TYPE);
                    CargoServerRegistry reg = CargoServerRegistry.getInstance();
                    Container cont = reg.getFactory().createContainer(type);
                    cont.setHomeDir(prop.getProperty(HOME_DIR));
                    cont.setOutput(File.createTempFile(cont.getId(), "log"));
//                    File configDir = new File(prop.getProperty(CONFIG_HOME_DIR));
                    cont.setConfiguration(reg.getConfigFactory().createConfiguration(cont, ConfigurationFactory.STANDALONE));
                    cont.getConfiguration().setProperty(ServletPropertySet.PORT, prop.getProperty(PORT));
                    cont.getConfiguration().setProperty(ServletPropertySet.USERS, prop.getProperty(USERS));
                    cont.getConfiguration().setProperty(GeneralPropertySet.HOSTNAME, prop.getProperty(HOSTNAME));
                    cont.getConfiguration().setProperty(GeneralPropertySet.LOGGING, prop.getProperty(LOGGING));
                    cont.getConfiguration().setProperty(GeneralPropertySet.JVMARGS, prop.getProperty(JVMARGS));
                    
                    reg.addContainer(cont);
                } catch (IOException exc) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exc);
                } finally {
                    if (str != null) {
                        try {
                            str.close();
                        } catch (IOException x) {
                            // ignore
                        }
                    }
                }
            }
        }
    }
    
}
