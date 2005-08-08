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

package org.mevenide.netbeans.project.queries;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.project.dependency.DependencyResolverFactory;
import org.mevenide.project.dependency.IDependencyResolver;
import org.mevenide.properties.IPropertyResolver;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.FileOwnerQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;


/**
 * A global implementation of FileOwnerQueryImplementation, is required to link together the maven project
 * and it's artifact in the maven repository. any other files shall be handled by the 
 * default netbeans implementation.
 * 
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class MavenFileOwnerQueryImpl implements FileOwnerQueryImplementation {
     private static final Log logger = LogFactory.getLog(MavenFileOwnerQueryImpl.class);
    
     private Set set;
     private Object lock = new Object();
     private List listeners;
    /** Creates a new instance of MavenFileBuiltQueryImpl */
    public MavenFileOwnerQueryImpl() {
         logger.debug("MavenFileOwnerQueryImpl()");
         set = new HashSet();
         listeners = new ArrayList();
    }
    
    public static MavenFileOwnerQueryImpl getInstance() {
        Lookup.Result implementations = Lookup.getDefault().lookup(new Lookup.Template(FileOwnerQueryImplementation.class));
        Iterator it = implementations.allInstances().iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof MavenFileOwnerQueryImpl) {
                return (MavenFileOwnerQueryImpl)obj;
            }
            logger.debug("fileOwnwequeryImpl=" + obj.getClass());
        }
        return null;
    }
    
    public void addMavenProject(MavenProject project) {
        synchronized (lock) {
            set.add(project);
        }
        fireChange();
    }
    public void removeMavenProject(MavenProject project) {
        synchronized (lock) {
            set.remove(project);
        }
        fireChange();
    }
    
    public void addChangeListener(ChangeListener list) {
        synchronized (listeners) {
            listeners.add(list);
        }
    }
    
    public void removeChangeListener(ChangeListener list) {
        synchronized (listeners) {
            listeners.remove(list);
        }
    }
    
    private void fireChange() {
        List lst = new ArrayList();
        synchronized (listeners) {
            lst.addAll(listeners);
        }
        Iterator it = lst.iterator();
        ChangeEvent event = new ChangeEvent(this);
        while (it.hasNext()) {
            ChangeListener change = (ChangeListener)it.next();
            change.stateChanged(event);
        }
    }
    
    /**
     * get the list of currently opened maven projects.. kind of hack, but well..
     */
    public Set getOpenedProjects() {
        synchronized (lock) {
            return new HashSet(set);
        }
    }
    
    public Project getOwner(URI uri) {
        //logger.debug("getOwner of uri=" + uri);
        File file = new File(uri);
        return getOwner(file);
    }
    
    public Project getOwner(FileObject fileObject) {
        File file = FileUtil.toFile(fileObject);
        if (file != null) {
            //logger.fatal("getOwner of fileobject=" + fileObject.getNameExt());
            return getOwner(file);
        } 
        return null;
    }
    
    
    private Project getOwner(File file) {
        Set currentProjects;
        synchronized (lock) {
            currentProjects = new HashSet(set);
        }
        try {
            IDependencyResolver resolver = DependencyResolverFactory.getFactory().newInstance(file.getAbsolutePath());
            String version = resolver.guessVersion();
            String artifactid = resolver.guessArtifactId();
            String groupid = resolver.guessGroupId();
 //           logger.debug("version=" + version + "  artifact=" + artifactid + "  groupid=" + groupid);
            Iterator it = currentProjects.iterator();
            while (it.hasNext()) {
                MavenProject project = (MavenProject)it.next();
                org.apache.maven.project.Project proj = project.getOriginalMavenProject();
                IPropertyResolver res = project.getPropertyResolver();
				//#MEVENIDE-287 handle SNAPSHOT in a special way
                if   (version != null 
                  && ("SNAPSHOT".equals(version) || doCompare(version, res.resolveString(proj.getCurrentVersion()))) 
                  && artifactid != null 
                  && (  doCompare(artifactid, res.resolveString(proj.getArtifactId())) 
                     || doCompare(artifactid, res.resolveString(proj.getId()))) 
                  && groupid != null 
                  && (doCompare(groupid, res.resolveString(proj.getGroupId())) || groupid.equals(artifactid))) {
                        logger.debug("found project=" + project.getDisplayName());
                        return project;
                }
            }
        } catch (Exception exc) {
            logger.error("Something wrong with resolver.", exc);
        }
        return null;
        
    }
    
    private boolean doCompare(String one, String two) {
        if (one == null || two == null) {
            return false;
        }
        return one.trim().equals(two.trim());
    }    
    
}
