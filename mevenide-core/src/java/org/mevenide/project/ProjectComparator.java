/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Jeffrey Bonevich.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.project;

import java.util.Collection;
import java.util.List;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Vector;

import org.apache.maven.project.Branch;
import org.apache.maven.project.Build;
import org.apache.maven.project.Contributor;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.License;
import org.apache.maven.project.MailingList;
import org.apache.maven.project.Organization;
import org.apache.maven.project.Project;
import org.apache.maven.project.Repository;
import org.apache.maven.project.Resource;
import org.apache.maven.project.UnitTest;
import org.apache.maven.project.Version;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.util.MevenideUtils;

/**
 * 
 * @author Jeffrey Bonevich 
 * @version $Id$
 * 
 */
public class ProjectComparator {

    private static final Log log = LogFactory.getLog(ProjectComparator.class);

    public static final String PROJECT = "PROJECT";
    public static final String ORGANIZATION = "ORGANIZATION";
    public static final String REPOSITORY = "REPOSITORY";
    public static final String BRANCHES = "BRANCHES";
    public static final String VERSIONS = "VERSIONS";
    public static final String MAILINGLISTS = "MAILINGLISTS";
    public static final String CONTRIBUTORS = "CONTRIBUTORS";
    public static final String DEVELOPERS = "DEVELOPERS";
    public static final String LICENSES = "LICENSES";
    public static final String DEPENDENCIES = "DEPENDENCIES";
    public static final String BUILD = "BUILD";
    public static final String UNIT_TESTS = "UNIT_TESTS";
    public static final String RESOURCES = "RESOURCES";
    public static final String REPORTS = "REPORTS";
    
	public static final Project NULL_PROJECT = new Project();

    private Project originalProject;
    private Hashtable subModelListenerTable = new Hashtable();
    private Vector generalListeners = new Vector(5);

    class ShortCircuitException extends Exception {
    }

    public ProjectComparator(Project project) {
        this.originalProject = project;
    }

    public Project getOriginalProject() {
        return originalProject;
    }

    public void setOriginalProject(Project originalProject) {
        this.originalProject = originalProject;
    }

    public void addProjectChangeListener(IProjectChangeListener listener) {
        this.generalListeners.add(listener);
    }

    public void addProjectChangeListener(String subModel, IProjectChangeListener listener) {
        Vector subModelListeners = (Vector) subModelListenerTable.get(subModel);
        if (subModelListeners == null) {
            subModelListenerTable.put(subModel, subModelListeners = new Vector(5));
        }
        subModelListeners.add(listener);
    }

    public void removeProjectChangeListener(IProjectChangeListener listener) {
        this.generalListeners.remove(listener);
    }

    public void removeProjectChangeListener(String subModel, IProjectChangeListener listener) {
        Vector subModelListeners = (Vector) subModelListenerTable.get(subModel);
        if (subModelListeners != null) {
            subModelListeners.remove(listener);
        }
    }

    private void fireProjectChangeEvent(Project newProject, String subModel) {
        ProjectChangeEvent e = null;
        if (newProject != null) {
        	e = new ProjectChangeEvent(newProject, subModel);
        } else {
			e = new ProjectChangeEvent(NULL_PROJECT, subModel);
        }

        // send event to generic listeners
        Iterator itr = generalListeners.iterator();
        while (itr.hasNext()) {
            IProjectChangeListener listener = (IProjectChangeListener) itr.next();
            listener.projectChanged(e);
        }

        // send event to subModel listeners
        Vector subModelListeners = (Vector) subModelListenerTable.get(subModel);
        if (subModelListeners != null) {
            itr = subModelListeners.iterator();
            while (itr.hasNext()) {
                IProjectChangeListener listener = (IProjectChangeListener) itr.next();
                listener.projectChanged(e);
            }
        }
    }

    private boolean comparable(Object newValue, Object oldValue) {
        return (newValue != null || oldValue != null);
    }

    private void detectObjectChange(Object newObject, Object oldObject) throws ShortCircuitException {
        if ((newObject == null || oldObject == null) &&
        	! (newObject == null && oldObject == null)) {
            throw new ShortCircuitException();
        }
    }

    private void detectAttributeChange(Object newValue, Object oldValue) throws ShortCircuitException {
        if (MevenideUtils.notEquivalent(newValue, oldValue)) {
            throw new ShortCircuitException();
        }
    }

    private void detectAttributeChange(boolean newValue, boolean oldValue) throws ShortCircuitException {
        if (newValue != oldValue) {
            throw new ShortCircuitException();
        }
    }

    private void detectCollectionChange(Collection newValue, Collection oldValue) throws ShortCircuitException {
        if (oldValue.size() != newValue.size()) {
            throw new ShortCircuitException();
        }
    }

    public void compare(Project newProject) {
        if (log.isDebugEnabled()) {
            log.debug("Computing project difference...");
        }

        if (newProject == originalProject) {
            // same reference or both null? treat as identical projects
            return;
        }

        if (newProject == null || originalProject == null) {
            fireProjectChangeEvent(newProject, PROJECT);
            return;
        }

        compareProject(newProject);
        compareOrganization(newProject);
        compareRepository(newProject);
        compareBranches(newProject);
        compareVersions(newProject);
        compareMailingLists(newProject);
        compareContributors(newProject);
        compareDevelopers(newProject);
        compareLicenses(newProject);
        compareDependencies(newProject);
        compareBuild(newProject);
        compareReports(newProject);
        try {
            compareProperties(newProject.getProperties(), originalProject.getProperties());
        }
        catch (ShortCircuitException e) {
            fireProjectChangeEvent(newProject, PROJECT);
        }
    }

    private void compareProject(Project newProject) {
        try {
            detectAttributeChange(newProject.getName(), originalProject.getName());
            detectAttributeChange(newProject.getArtifactId(), originalProject.getArtifactId());
            detectAttributeChange(newProject.getGroupId(), originalProject.getGroupId());
            detectAttributeChange(newProject.getGumpRepositoryId(), originalProject.getGumpRepositoryId());
            detectAttributeChange(newProject.getExtend(), originalProject.getExtend());
            detectAttributeChange(newProject.getPomVersion(), originalProject.getPomVersion());
            detectAttributeChange(newProject.getCurrentVersion(), originalProject.getCurrentVersion());
            detectAttributeChange(newProject.getLogo(), originalProject.getLogo());
            detectAttributeChange(newProject.getInceptionYear(), originalProject.getInceptionYear());
            detectAttributeChange(newProject.getUrl(), originalProject.getUrl());
            detectAttributeChange(newProject.getPackage(), originalProject.getPackage());
            detectAttributeChange(newProject.getShortDescription(), originalProject.getShortDescription());
            detectAttributeChange(newProject.getDescription(), originalProject.getDescription());
            detectAttributeChange(newProject.getDistributionDirectory(), originalProject.getDistributionDirectory());
            detectAttributeChange(newProject.getDistributionSite(), originalProject.getDistributionSite());
            detectAttributeChange(newProject.getIssueTrackingUrl(), originalProject.getIssueTrackingUrl());
            detectAttributeChange(newProject.getSiteAddress(), originalProject.getSiteAddress());
            detectAttributeChange(newProject.getSiteDirectory(), originalProject.getSiteDirectory());
        }
        catch (ShortCircuitException e) {
	        if (log.isDebugEnabled()) {
	            log.debug("Change in project detected");
	        }
            fireProjectChangeEvent(newProject, PROJECT);
        }
    }

    private void compareOrganization(Project newProject) {
        Organization newOrg = newProject.getOrganization();
        Organization originalOrg = originalProject.getOrganization();
        if (comparable(newOrg, originalOrg)) {
            try {
                detectObjectChange(newOrg, originalOrg);
                detectAttributeChange(newOrg.getId(), originalOrg.getId());
                detectAttributeChange(newOrg.getName(), originalOrg.getName());
                detectAttributeChange(newOrg.getLogo(), originalOrg.getLogo());
                detectAttributeChange(newOrg.getUrl(), originalOrg.getUrl());
            }
            catch (ShortCircuitException e) {
                fireProjectChangeEvent(newProject, ORGANIZATION);
            }
        }
    }

    private void compareRepository(Project newProject) {
        Repository newRepo = newProject.getRepository();
        Repository originalRepo = originalProject.getRepository();
        if (comparable(newRepo, originalRepo)) {
            try {
				detectObjectChange(newRepo, originalRepo);
                detectAttributeChange(newRepo.getId(), originalRepo.getId());
                detectAttributeChange(newRepo.getName(), originalRepo.getName());
                detectAttributeChange(newRepo.getConnection(), originalRepo.getConnection());
                detectAttributeChange(newRepo.getConnection(), originalRepo.getConnection());
                detectAttributeChange(newRepo.getUrl(), originalRepo.getUrl());
            }
            catch (ShortCircuitException e) {
                fireProjectChangeEvent(newProject, REPOSITORY);
            }
        }
    }

    private void compareBranches(Project newProject) {
        List newBranches = newProject.getBranches();
        List origBranches = originalProject.getBranches();
        try {
            detectCollectionChange(newBranches, origBranches);
            // just assume order is significant
            for (int i = 0; i < origBranches.size(); i++) {
                Branch newBranch = (Branch) newBranches.get(i);
                Branch origBranch = (Branch) origBranches.get(i);
                detectAttributeChange(newBranch.getId(), origBranch.getId());
                detectAttributeChange(newBranch.getName(), origBranch.getName());
                detectAttributeChange(newBranch.getTag(), origBranch.getTag());
            }
        }
        catch (ShortCircuitException e) {
            fireProjectChangeEvent(newProject, BRANCHES);
        }
    }

    private void compareVersions(Project newProject) {
        List newVersions = newProject.getVersions();
        List origVersions = originalProject.getVersions();
        try {
            detectCollectionChange(newVersions, origVersions);
            // just assume order is significant
            for (int i = 0; i < origVersions.size(); i++) {
                Version newVersion = (Version) newVersions.get(i);
                Version origVersion = (Version) origVersions.get(i);
                detectAttributeChange(newVersion.getId(), origVersion.getId());
                detectAttributeChange(newVersion.getName(), origVersion.getName());
                detectAttributeChange(newVersion.getTag(), origVersion.getTag());
            }
        }
        catch (ShortCircuitException e) {
            fireProjectChangeEvent(newProject, VERSIONS);
        }
    }

    private void compareMailingLists(Project newProject) {
        List newMailingLists = newProject.getMailingLists();
        List origMailingLists = originalProject.getMailingLists();
        try {
            detectCollectionChange(newMailingLists, origMailingLists);
            // just assume order is significant
            for (int i = 0; i < origMailingLists.size(); i++) {
                MailingList newMailingList = (MailingList) newMailingLists.get(i);
                MailingList origMailingList = (MailingList) origMailingLists.get(i);
                detectAttributeChange(newMailingList.getId(), origMailingList.getId());
                detectAttributeChange(newMailingList.getName(), origMailingList.getName());
                detectAttributeChange(newMailingList.getArchive(), origMailingList.getArchive());
                detectAttributeChange(newMailingList.getSubscribe(), origMailingList.getSubscribe());
                detectAttributeChange(newMailingList.getUnsubscribe(), origMailingList.getUnsubscribe());
            }
        }
        catch (ShortCircuitException e) {
            fireProjectChangeEvent(newProject, MAILINGLISTS);
        }
    }

    private void compareContributors(Project newProject) {
        List newContributors = newProject.getContributors();
        List origContributors = originalProject.getContributors();
        try {
            compareContributors(newContributors, origContributors);
        }
        catch (ShortCircuitException e) {
            fireProjectChangeEvent(newProject, CONTRIBUTORS);
        }
    }

    private void compareContributors(List newContributors, List origContributors) throws ShortCircuitException {

        detectCollectionChange(newContributors, origContributors);
        // just assume order is significant
        for (int i = 0; i < origContributors.size(); i++) {
            Contributor newContributor = (Contributor) newContributors.get(i);
            Contributor origContributor = (Contributor) origContributors.get(i);
            detectAttributeChange(newContributor.getId(), origContributor.getId());
            detectAttributeChange(newContributor.getName(), origContributor.getName());
            detectAttributeChange(newContributor.getEmail(), origContributor.getEmail());
            detectAttributeChange(newContributor.getOrganization(), origContributor.getOrganization());
            detectAttributeChange(newContributor.getTimezone(), origContributor.getTimezone());
            detectAttributeChange(newContributor.getUrl(), origContributor.getUrl());

            SortedSet origRoles = origContributor.getRoles();
            SortedSet newRoles = newContributor.getRoles();
            detectCollectionChange(newRoles, origRoles);
            Iterator itrOrig = origRoles.iterator();
            Iterator itrNew = newRoles.iterator();
            while (itrOrig.hasNext()) {
                detectAttributeChange(itrNew.next().toString(), itrOrig.next().toString());
            }
        }
    }

    private void compareDevelopers(Project newProject) {
        List newDevelopers = newProject.getDevelopers();
        List origDevelopers = originalProject.getDevelopers();
        try {
            compareContributors(newDevelopers, origDevelopers);
        }
        catch (ShortCircuitException e) {
            fireProjectChangeEvent(newProject, DEVELOPERS);
        }
    }

    private void compareLicenses(Project newProject) {
        List origLicenses = originalProject.getLicenses();
        List newLicenses = newProject.getLicenses();
        try {
            detectCollectionChange(newLicenses, origLicenses);
            // just assume order is significant
            for (int i = 0; i < origLicenses.size(); i++) {
                License newLicense = (License) newLicenses.get(i);
                License origLicense = (License) origLicenses.get(i);
                detectAttributeChange(newLicense.getName(), origLicense.getName());
                detectAttributeChange(newLicense.getUrl(), origLicense.getUrl());
                detectAttributeChange(newLicense.getDistribution(), origLicense.getDistribution());
                detectAttributeChange(newLicense.getComments(), origLicense.getComments());
            }
        }
        catch (ShortCircuitException e) {
            fireProjectChangeEvent(newProject, LICENSES);
        }
    }

    private void compareDependencies(Project newProject) {
        List newDependencies = newProject.getDependencies();
        List origDependencies = originalProject.getDependencies();
        try {
            detectCollectionChange(newDependencies, origDependencies);
            // just assume order is significant
            for (int i = 0; i < origDependencies.size(); i++) {
                Dependency newDependency = (Dependency) newDependencies.get(i);
                Dependency origDependency = (Dependency) origDependencies.get(i);
                detectAttributeChange(newDependency.getId(), origDependency.getId());
                detectAttributeChange(newDependency.getName(), origDependency.getName());
                detectAttributeChange(newDependency.getArtifactId(), origDependency.getArtifactId());
                detectAttributeChange(newDependency.getGroupId(), origDependency.getGroupId());
                detectAttributeChange(newDependency.getJar(), origDependency.getJar());
                detectAttributeChange(newDependency.getType(), origDependency.getType());
                detectAttributeChange(newDependency.getUrl(), origDependency.getUrl());
                detectAttributeChange(newDependency.getVersion(), origDependency.getVersion());
            }
        }
        catch (ShortCircuitException e) {
            fireProjectChangeEvent(newProject, DEPENDENCIES);
        }
    }

    private void compareBuild(Project newProject) {
        Build newBuild = newProject.getBuild();
        Build originalBuild = originalProject.getBuild();
        if (comparable(newBuild, originalBuild)) {
	        try {
	        	detectObjectChange(newBuild, originalBuild);
	            detectAttributeChange(newBuild.getSourceDirectory(), originalBuild.getSourceDirectory());
	            detectAttributeChange(newBuild.getUnitTestSourceDirectory(), originalBuild.getUnitTestSourceDirectory());
	            detectAttributeChange(newBuild.getAspectSourceDirectory(), originalBuild.getAspectSourceDirectory());
	            detectAttributeChange(
	                newBuild.getIntegrationUnitTestSourceDirectory(),
	                originalBuild.getIntegrationUnitTestSourceDirectory());
	            detectAttributeChange(newBuild.getNagEmailAddress(), originalBuild.getNagEmailAddress());
	            compareUnitTest(newBuild, originalBuild, newProject);
	            compareResources(newBuild.getResources(), originalBuild.getResources());
	        }
	        catch (ShortCircuitException e) {
	            fireProjectChangeEvent(newProject, BUILD);
	        }
        }
    }

    private void compareUnitTest(Build newBuild, Build origBuild, Project newProject) {
		try {
	        UnitTest origUnitTest = origBuild.getUnitTest();
	        UnitTest newUnitTest = newBuild.getUnitTest();
	        compareResource(newUnitTest, origUnitTest);
	        compareResources(newUnitTest.getResources(), origUnitTest.getResources());
		}
		catch (ShortCircuitException e) {
			fireProjectChangeEvent(newProject, UNIT_TESTS);
		}
    }

    private void compareResources(List newResources, List origResources) throws ShortCircuitException {

        detectCollectionChange(newResources, origResources);
        // just assume order is significant
        for (int i = 0; i < origResources.size(); i++) {
            Resource origResource = (Resource) origResources.get(i);
            Resource newResource = (Resource) newResources.get(i);
            compareResource(origResource, newResource);
        }
    }

    private void compareResource(Resource origResource, Resource newResource) throws ShortCircuitException {
        detectAttributeChange(newResource.getDirectory(), origResource.getDirectory());
        detectAttributeChange(newResource.getTargetPath(), origResource.getTargetPath());
        detectAttributeChange(newResource.getFiltering(), origResource.getFiltering());

        detectCollectionChange(newResource.getIncludes(), origResource.getIncludes());
        for (int j = 0; j < origResource.getIncludes().size(); j++) {
            detectAttributeChange(origResource.getIncludes().get(j), newResource.getIncludes().get(j));
        }

        detectCollectionChange(newResource.getExcludes(), origResource.getExcludes());
        for (int j = 0; j < origResource.getExcludes().size(); j++) {
            detectAttributeChange(origResource.getExcludes().get(j), newResource.getExcludes().get(j));
        }
    }

    private void compareReports(Project newProject) {
        List newReports = newProject.getReports();
        List origReports = originalProject.getReports();
        try {
            detectCollectionChange(newReports, origReports);
            // just assume order is significant
            for (int i = 0; i < origReports.size(); i++) {
                String origReport = (String) origReports.get(i);
                String newReport = (String) newReports.get(i);
                detectAttributeChange(newReport, origReport);
            }
        }
        catch (ShortCircuitException e) {
            fireProjectChangeEvent(newProject, REPORTS);
        }
    }

    private void compareProperties(List newProps, List origProps) throws ShortCircuitException {

        detectCollectionChange(newProps, origProps);
        // just assume order is significant
        // FIXME: ?? properties in BaseObject are stored as 'name:value',
        // so we could parse the retrieved props apart and compare property
        // for property.  Kinda silly that Maven does not provide a
        // getPropertyNames() method
        for (int i = 0; i < origProps.size(); i++) {
            String origProp = (String) origProps.get(i);
            String newProp = (String) newProps.get(i);
            detectAttributeChange(newProp, origProp);
        }
    }

}
