/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.project;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Branch;
import org.apache.maven.project.Build;
import org.apache.maven.project.Contributor;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Developer;
import org.apache.maven.project.License;
import org.apache.maven.project.MailingList;
import org.apache.maven.project.Organization;
import org.apache.maven.project.Project;
import org.apache.maven.project.Repository;
import org.apache.maven.project.Resource;
import org.apache.maven.project.SourceModification;
import org.apache.maven.project.UnitTest;
import org.apache.maven.project.Version;
import org.mevenide.context.IProjectContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.properties.IPropertyResolver;
import org.mevenide.project.io.JDomProjectUnmarshaller;
import org.mevenide.util.MevenideUtils;


/**
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 *
 */
public class DefaultProjectContext implements IProjectContext {
    private static final Log logger = LogFactory.getLog(DefaultProjectContext.class);
    
    private Project mergedProject;
    private List projects;
    private List projectFiles;
    private List projectTimestamps;
    
    private Object LOCK = new Object();
    private JDomProjectUnmarshaller unmarshaller;
    private IPropertyResolver propResolver;
    private IQueryContext queryContext;
    
    public DefaultProjectContext(IQueryContext context, IPropertyResolver resolver) {
        unmarshaller = new JDomProjectUnmarshaller();
        propResolver = resolver;
        queryContext = context;
        projectTimestamps = new ArrayList();
        projectFiles = new ArrayList();
        projects = new ArrayList();
        if (context.getProjectDirectory() == null) {
            throw new IllegalStateException("Cannot initialize ProjectContext without a project-based Querycontext");
        }
        synchronized (LOCK) {
            reloadData();
        }
    }
    
    // shall be run in synchronized lock.
    private void reloadData() {
        projectFiles.clear();
        projectTimestamps.clear();
        projects.clear();
        mergedProject = new Project();
        readProject(new File(queryContext.getProjectDirectory(), "project.xml"));
        Iterator it = projects.iterator();
        while (it.hasNext()) {
            mergedProject = mergeProjects(mergedProject, (Project)it.next());
        }
    }
    
    public Project getFinalProject() {
        synchronized (LOCK) {
            checkTimeStamps();
            return mergedProject;
        }
    }
    
    public File[] getProjectFiles() {
        File[] fls;
        synchronized (LOCK) {
            checkTimeStamps();
            fls = new File[projectFiles.size()];
            fls = (File[])projectFiles.toArray(fls);
        }
        return fls;
    }
    
    public Project[] getProjectLayers() {
        Project[] prjs;
        synchronized (LOCK) {
            checkTimeStamps();
            prjs = new Project[projects.size()];
            prjs = (Project[])projects.toArray(prjs);
        }
        return prjs;
    }
    
    // shall be run in synchronized lock.
    private void checkTimeStamps() {
        for (int i = 0; i < projectFiles.size(); i++) {
            File file = (File)projectFiles.get(i);
            Long stamp = (Long)projectTimestamps.get(i);
            if (file.lastModified() > stamp.longValue()) {
                //TODO don't delete all, but only current and succeeding..
                reloadData();
            }
        }
    }
    
    // shall be run in synchronized lock.
    private void readProject(File file) {
        logger.debug("readproject=" + file);
        if (file.exists()) {
            Project proj;
            try {
                proj = unmarshaller.parse(new FileReader(file));
            } catch (Exception exc) {
                logger.error("cannot parse file=" + file, exc);
                return;
            }
            if (proj != null) {
                projects.add(proj);
                projectFiles.add(file);
                projectTimestamps.add(new Long(file.lastModified()));
                String extend = proj.getExtend();
                if (extend != null) {
                    extend = propResolver.resolveString(extend);
                    File absolute = new File(extend);
                    absolute = MevenideUtils.normalizeFile(absolute);
                    if (absolute.exists() && (!absolute.equals(file))) {
                        readProject(absolute);
                    } else {
                        File relative = new File(queryContext.getProjectDirectory(), extend);
                        relative = MevenideUtils.normalizeFile(relative);
                        if (relative.exists() && (!relative.equals(file))) {
                            readProject(relative);
                        } else {
                            // TODO - for debugging purposes
                            // later just semisilently ignore??
                            throw new IllegalStateException("Cannot read parent.(" + extend + ")" );
                        }
                    }
                }
            }
        }
    }
    
    //##########################################################################
    // project merge related
    //##########################################################################
    
    private Project mergeProjects(Project primary, Project secondary) {
        Project toReturn = new Project();
        if (primary != null && secondary != null) {
            toReturn.setExtend(primary.getExtend() != null ? primary.getExtend() : secondary.getExtend());
            toReturn.setPomVersion(primary.getPomVersion() != null ? primary.getPomVersion() : secondary.getPomVersion());
            toReturn.setId(primary.getId() != null ? primary.getId() : secondary.getId());
            toReturn.setName(primary.getName() != null ? primary.getName() : secondary.getName());
            toReturn.setGroupId(primary.getGroupId() != null ? primary.getGroupId() : secondary.getGroupId());
            toReturn.setArtifactId(primary.getArtifactId() != null ? primary.getArtifactId() : secondary.getArtifactId());
            toReturn.setCurrentVersion(primary.getCurrentVersion() != null ? primary.getCurrentVersion() : secondary.getCurrentVersion());
            toReturn.setOrganization(mergeOrganization(primary.getOrganization(), secondary.getOrganization()));
            toReturn.setInceptionYear(primary.getInceptionYear() != null ? primary.getInceptionYear() : secondary.getInceptionYear());
            toReturn.setPackage(primary.getPackage() != null ? primary.getPackage() : secondary.getPackage());
            toReturn.setLogo(primary.getLogo() != null ? primary.getLogo() : secondary.getLogo());
            toReturn.setGumpRepositoryId(primary.getGumpRepositoryId() != null ? primary.getGumpRepositoryId() : secondary.getGumpRepositoryId());
            toReturn.setDescription(primary.getDescription() != null ? primary.getDescription() : secondary.getDescription());
            toReturn.setShortDescription(primary.getShortDescription() != null ? primary.getShortDescription() : secondary.getShortDescription());
            toReturn.setUrl(primary.getUrl() != null ? primary.getUrl() : secondary.getUrl());
            toReturn.setIssueTrackingUrl(primary.getIssueTrackingUrl() != null ? primary.getIssueTrackingUrl() : secondary.getIssueTrackingUrl());
            toReturn.setSiteAddress(primary.getSiteAddress() != null ? primary.getSiteAddress() : secondary.getSiteAddress());
            toReturn.setSiteDirectory(primary.getSiteDirectory() != null ? primary.getSiteDirectory() : secondary.getSiteDirectory());
            toReturn.setDistributionSite(primary.getDistributionSite() != null ? primary.getDistributionSite() : secondary.getDistributionSite());
            toReturn.setDistributionDirectory(primary.getDistributionDirectory() != null ? primary.getDistributionDirectory() : secondary.getDistributionDirectory());
            toReturn.setRepository(mergeRepository(primary.getRepository(), secondary.getRepository()));
            toReturn.setVersions(mergeVersions(primary.getVersions(), secondary.getVersions()));
            toReturn.setBranches(mergeBranches(primary.getBranches(), secondary.getBranches()));
            toReturn.setMailingLists(mergeMailingLists(primary.getMailingLists(), secondary.getMailingLists()));
            toReturn.setDevelopers(mergeDevelopers(primary.getDevelopers(), secondary.getDevelopers()));
            toReturn.setContributors(mergeContributors(primary.getContributors(), secondary.getContributors()));
            toReturn.setLicenses(mergeLicenses(primary.getLicenses(), secondary.getLicenses()));
            toReturn.setDependencies(mergeDependencies(primary.getDependencies(), secondary.getDependencies()));
            toReturn.setBuild(mergeBuild(primary.getBuild(), secondary.getBuild()));
            toReturn.setReports(mergeReports(primary.getReports(), secondary.getReports()));
            toReturn.setProperties(mergeProperties(primary.getProperties(), secondary.getProperties()));
        } else {
            throw new IllegalStateException("Shall not call method with null params.");
        }
        return toReturn;
    }
    
    private Organization mergeOrganization(Organization primary, Organization secondary) {
        Organization source = (primary != null ? primary : secondary);
        if (source != null) {
            Organization toReturn = new Organization();
            toReturn.setName(source.getName());
            toReturn.setUrl(source.getUrl());
            toReturn.setLogo(source.getLogo());
            return toReturn;
        }
        return null;
    }
    
    private Repository mergeRepository(Repository primary, Repository secondary) {
        Repository source = (primary != null ? primary : secondary);
        if (source != null) {
            Repository toReturn = new Repository();
            toReturn.setConnection(source.getConnection());
            toReturn.setUrl(source.getUrl());
            toReturn.setDeveloperConnection(source.getDeveloperConnection());
            return toReturn;
        }
        return null;
    }
    
    private List mergeVersions(List primary, List secondary) {
        List source = (primary != null ? primary : secondary);
        if (source != null) {
            List toReturn = new ArrayList();
            Iterator it = source.iterator();
            while (it.hasNext()) {
                Version ver = (Version)it.next();
                Version target = new Version();
                target.setId(ver.getId());
                target.setName(ver.getName());
                target.setTag(ver.getTag());
                toReturn.add(target);
            }
            return toReturn;
        }
        return null;
    }
    
    private List mergeBranches(List primary, List secondary) {
        List source = (primary != null ? primary : secondary);
        if (source != null) {
            List toReturn = new ArrayList();
            Iterator it = source.iterator();
            while (it.hasNext()) {
                Branch ver = (Branch)it.next();
                Branch target = new Branch();
                target.setTag(ver.getTag());
                toReturn.add(target);
            }
            return toReturn;
        }
        return null;
    }
    
    private List mergeMailingLists(List primary, List secondary) {
        List source = (primary != null ? primary : secondary);
        if (source != null) {
            List toReturn = new ArrayList();
            Iterator it = source.iterator();
            while (it.hasNext()) {
                MailingList orig = (MailingList)it.next();
                MailingList target = new MailingList();
                target.setName(orig.getName());
                target.setArchive(orig.getArchive());
                target.setSubscribe(orig.getSubscribe());
                target.setUnsubscribe(orig.getUnsubscribe());
                toReturn.add(target);
            }
            return toReturn;
        }
        return null;
    }
    
    private List mergeDevelopers(List primary, List secondary) {
        List source = (primary != null ? primary : secondary);
        if (source != null) {
            List toReturn = new ArrayList();
            Iterator it = source.iterator();
            while (it.hasNext()) {
                Developer orig = (Developer)it.next();
                Developer target = new Developer();
                target.setName(orig.getName());
                target.setId(orig.getId());
                target.setEmail(orig.getEmail());
                target.setOrganization(orig.getOrganization());
                target.setUrl(orig.getUrl());
                target.setTimezone(orig.getTimezone());
                SortedSet roles = orig.getRoles();
                if (roles != null) {
                    Iterator it2 = roles.iterator();
                    while (it2.hasNext()) {
                        target.addRole((String)it2.next());
                    }
                }
                toReturn.add(target);
            }
            return toReturn;
        }
        return null;
    }
    
    
    private List mergeContributors(List primary, List secondary) {
        List source = (primary != null ? primary : secondary);
        if (source != null) {
            List toReturn = new ArrayList();
            Iterator it = source.iterator();
            while (it.hasNext()) {
                Contributor orig = (Contributor)it.next();
                Contributor target = new Contributor();
                target.setName(orig.getName());
                target.setEmail(orig.getEmail());
                target.setOrganization(orig.getOrganization());
                target.setUrl(orig.getUrl());
                target.setTimezone(orig.getTimezone());
                SortedSet roles = orig.getRoles();
                if (roles != null) {
                    Iterator it2 = roles.iterator();
                    while (it2.hasNext()) {
                        target.addRole((String)it2.next());
                    }
                }
                toReturn.add(target);
            }
            return toReturn;
        }
        return null;
    }
    
    private List mergeLicenses(List primary, List secondary) {
        List source = (primary != null ? primary : secondary);
        if (source != null) {
            List toReturn = new ArrayList();
            Iterator it = source.iterator();
            while (it.hasNext()) {
                License orig = (License)it.next();
                License target = new License();
                target.setName(orig.getName());
                target.setUrl(orig.getUrl());
                target.setDistribution(orig.getDistribution());
                toReturn.add(target);
            }
            return toReturn;
        }
        return null;
    }
    /**
     * truly merging dependencies.
     */
    private List mergeDependencies(List primary, List secondary) {
        List source = new ArrayList();
        if (primary != null) {
            source.addAll(primary);
        }
        if (secondary != null) {
            source.addAll(secondary);
        }
        
        List toReturn = new ArrayList();
        Iterator it = source.iterator();
        while (it.hasNext()) {
            Dependency orig = (Dependency)it.next();
            Dependency target = new Dependency();
            target.setUrl(orig.getUrl());
            target.setGroupId(orig.getGroupId());
            target.setArtifactId(orig.getArtifactId());
            target.setId(orig.getId());
            target.setVersion(orig.getVersion());
            //TODO - throws NPE on me now..
            //            target.setJar(orig.getJar());
            target.setType(orig.getType());
            List props = orig.getProperties();
            if (props != null) {
                Iterator it2 = props.iterator();
                while (it2.hasNext()) {
                    target.addProperty((String)it2.next());
                }
            }
            toReturn.add(target);
        }
        return toReturn;
        
    }
    
    private Build mergeBuild(Build primary, Build secondary) {
        Build toReturn = new Build();
        if (primary == null) {
            primary = new Build();
        }
        if (secondary == null) {
            secondary = new Build();
        }
        toReturn.setNagEmailAddress(primary.getNagEmailAddress() != null ? primary.getNagEmailAddress() : secondary.getNagEmailAddress());
        toReturn.setSourceDirectory(primary.getSourceDirectory() != null ? primary.getSourceDirectory() : secondary.getSourceDirectory());
        toReturn.setAspectSourceDirectory(primary.getAspectSourceDirectory() != null ? primary.getAspectSourceDirectory() : secondary.getAspectSourceDirectory());
        toReturn.setUnitTestSourceDirectory(primary.getUnitTestSourceDirectory() != null ? primary.getUnitTestSourceDirectory() : secondary.getUnitTestSourceDirectory());
        toReturn.setIntegrationUnitTestSourceDirectory(primary.getIntegrationUnitTestSourceDirectory() != null ? primary.getIntegrationUnitTestSourceDirectory() : secondary.getIntegrationUnitTestSourceDirectory());
        toReturn.setSourceModification(mergeSourceModifications(primary.getSourceModifications(), secondary.getSourceModifications()));
        toReturn.setUnitTest(mergeUnitTest(primary.getUnitTest(), secondary.getUnitTest()));
        toReturn.setResources(mergeResources(primary.getResources(), secondary.getResources()));
        return toReturn;
    }
    
    /**
     * truly merging sourcemodifications.
     */
    private List mergeSourceModifications(List primary, List secondary) {
        List source = new ArrayList();
        if (primary != null) {
            source.addAll(primary);
        }
        if (secondary != null) {
            source.addAll(secondary);
        }
        
        List toReturn = new ArrayList();
        Iterator it = source.iterator();
        while (it.hasNext()) {
            SourceModification orig = (SourceModification)it.next();
            SourceModification target = new SourceModification();
            target.setClassName(orig.getClassName());
            List includes = orig.getIncludes();
            if (includes != null) {
                Iterator it2 = includes.iterator();
                while (it2.hasNext()) {
                    target.addInclude((String)it2.next());
                }
            }
            List excludes = orig.getExcludes();
            if (excludes != null) {
                Iterator it2 = excludes.iterator();
                while (it2.hasNext()) {
                    target.addExclude((String)it2.next());
                }
            }
            toReturn.add(target);
        }
        return toReturn;
        
    }
    
    // TODO no idea here.. merge or select one?
    private UnitTest mergeUnitTest(UnitTest primary, UnitTest secondary) {
        UnitTest source = (primary != null ? primary : secondary);
        if (source != null) {
            UnitTest toReturn = new UnitTest();
            List includes = source.getIncludes();
            if (includes != null) {
                Iterator it2 = includes.iterator();
                while (it2.hasNext()) {
                    toReturn.addInclude((String)it2.next());
                }
            }
            List excludes = source.getExcludes();
            if (excludes != null) {
                Iterator it2 = excludes.iterator();
                while (it2.hasNext()) {
                    toReturn.addExclude((String)it2.next());
                }
            }
            // passing null se secondary becasue we want just resources from the
            // current source instance.
            toReturn.setResources(mergeResources(source.getResources(), null));
            return toReturn;
        }
        return null;
    }
    
    // TODO no idea here.. merge or select one?
    private List mergeResources(List primary, List secondary) {
        List source = ((primary != null && primary.size() > 0) ? primary : secondary);
        //        List source = new ArrayList();
        //        if (primary != null) {
        //            source.addAll(primary);
        //        }
        //        if (secondary != null) {
        //            source.addAll(secondary);
        //        }
        List toReturn = new ArrayList();
        if (source != null) {
            Iterator it = source.iterator();
            while (it.hasNext()) {
                Resource orig = (Resource)it.next();
                Resource target = new Resource();
                target.setDirectory(orig.getDirectory());
                target.setFiltering(orig.getFiltering());
                target.setTargetPath(orig.getTargetPath());
                List includes = orig.getIncludes();
                if (includes != null) {
                    Iterator it2 = includes.iterator();
                    while (it2.hasNext()) {
                        target.addInclude((String)it2.next());
                    }
                }
                List excludes = orig.getExcludes();
                if (excludes != null) {
                    Iterator it2 = excludes.iterator();
                    while (it2.hasNext()) {
                        target.addExclude((String)it2.next());
                    }
                }
                toReturn.add(target);
            }
        }
        return toReturn;
    }
    
    /**
     * one wins, or shall a real merge happen?
     */
    private List mergeReports(List primary, List secondary) {
        List source = (primary != null ? primary : secondary);
        if (source != null) {
            List toReturn = new ArrayList();
            Iterator it = source.iterator();
            while (it.hasNext()) {
                toReturn.add(it.next());
            }
            return toReturn;
        }
        return null;
    }
    /**
     * truly merging sourcemodifications.
     */
    private List mergeProperties(List primary, List secondary) {
        List toReturn = new ArrayList();
        if (primary != null) {
            toReturn.addAll(primary);
        }
        if (secondary != null) {
            toReturn.addAll(secondary);
        }
        return toReturn;
    }
}