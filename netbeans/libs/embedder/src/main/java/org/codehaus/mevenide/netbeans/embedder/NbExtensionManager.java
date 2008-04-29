/*
 *  Copyright 2008 mkleint.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.codehaus.mevenide.netbeans.embedder;

import java.lang.reflect.Field;
import java.util.List;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.extension.DefaultExtensionManager;
import org.apache.maven.extension.ExtensionManagerException;
import org.apache.maven.model.Extension;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

/**
 * Makes sure the extensions are downloaded even in project loading embedder.
 * That's necessary for proper loading of project in case the extension holds
 * artifacthandler definitions..
 * 
 * @author mkleint
 */
public class NbExtensionManager extends DefaultExtensionManager {

    protected Field wagonMan;

    /** Creates a new instance of NbExtensionManager */
    public NbExtensionManager() {
        super();
        try {
            wagonMan = DefaultExtensionManager.class.getDeclaredField("wagonManager");
            wagonMan.setAccessible(true);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void addExtension(Extension extension, Model originatingModel, List remoteRepositories, MavenExecutionRequest request) throws ExtensionManagerException {
        openSesame();
        try {
            super.addExtension(extension, originatingModel, remoteRepositories, request);
        } finally {
            closeSesame();
        }
    }

    @Override
    public void addExtension(Extension extension, MavenProject project, MavenExecutionRequest request) throws ExtensionManagerException {
        openSesame();
        try {
            super.addExtension(extension, project, request);
        } finally {
            closeSesame();
        }
    }

    @Override
    public void addPluginAsExtension(Plugin plugin, Model originatingModel, List remoteRepositories, MavenExecutionRequest request) throws ExtensionManagerException {
        openSesame();
        try {
            super.addPluginAsExtension(plugin, originatingModel, remoteRepositories, request);
        } finally {
            closeSesame();
        }
    }

    @Override
    public void registerWagons() {
        openSesame();
        try {
            super.registerWagons();
        } finally {
            closeSesame();
        }
    }

    private void closeSesame() {
        if (wagonMan != null) {
            try {
                Object manObj = wagonMan.get(this);
                if (manObj instanceof NbWagonManager) {
                    NbWagonManager manager = (NbWagonManager)manObj;
                    manager.closeSesame();
                }
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void openSesame() {
        if (wagonMan != null) {
            try {
                Object manObj = wagonMan.get(this);
                if (manObj instanceof NbWagonManager) {
                    NbWagonManager manager = (NbWagonManager)manObj;
                    manager.openSesame();
                }
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
