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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.project.dependency.DependencyResolverFactory;
import org.mevenide.project.dependency.IDependencyResolver;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.FileOwnerQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;


/**
 * A global implementation of FileOwnerQueryImplementation, is required to link together the maven project
 * and it's artifact in the maven repository. any other files shall be handled by the 
 * default netbeans implementation.
 * 
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenFileOwnerQueryImpl implements FileOwnerQueryImplementation {
     private static final Log logger = LogFactory.getLog(MavenFileOwnerQueryImpl.class);
    
     private Set set;
     private Object LOCK = new Object();
    /** Creates a new instance of MavenFileBuiltQueryImpl */
    public MavenFileOwnerQueryImpl() {
         logger.debug("MavenFileOwnerQueryImpl()");
         set = new HashSet();
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
        synchronized (LOCK) {
            set.add(project);
        }
    }
    public void removeMavenProject(MavenProject project) {
        synchronized (LOCK) {
            set.remove(project);
        }
    }
    
    /**
     * get the list of currently opened maven projects.. kind of hack, but well..
     */
    public Set getOpenedProjects() {
        synchronized (LOCK) {
            return new HashSet(set);
        }
    }
    
    public Project getOwner(URI uri) {
        logger.debug("getOwner of uri=" + uri);
        Set currentProjects;
        synchronized (LOCK) {
            currentProjects = new HashSet(set);
        }
        File file = new File(uri);
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
//                logger.debug("project=" + project.getDisplayName() + "  artif=" + proj.getArtifactId() + "  group=" + proj.getGroupId() + "  version=" + proj.getCurrentVersion() +  "  id=" + proj.getId());
                if (version != null && version.equals(proj.getCurrentVersion()) &&
                    artifactid != null && (artifactid.equals(proj.getArtifactId()) || artifactid.equals(proj.getId())) &&
                    groupid != null && (groupid.equals(proj.getGroupId()) || groupid.equals(artifactid))) {
                        logger.debug("found project=" + project.getDisplayName());
                        return project;
                }
            }
        } catch (Exception exc) {
            logger.error("Something wrong with resolver.", exc);
        }
        return null;
    }
    
    public Project getOwner(FileObject fileObject) {
        logger.debug("getOwner of fileobject=" + fileObject.getNameExt());
        return null;
    }
    
}
