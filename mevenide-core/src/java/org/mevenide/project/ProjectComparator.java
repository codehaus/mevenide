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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.model.Branch;
import org.apache.maven.model.Build;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Developer;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Resource;
import org.apache.maven.model.UnitTest;
import org.apache.maven.model.Version;
import org.apache.maven.project.MavenProject;
import org.mevenide.util.MevenideUtils;

/**
 * 
 * @author Jeffrey Bonevich 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
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
    
	public static final MavenProject NULL_PROJECT = new MavenProject();

    private MavenProject originalProject;
    private Hashtable subModelListenerTable = new Hashtable();
    private Vector generalListeners = new Vector(5);
    private volatile boolean projectChanged = false;

    class ShortCircuitException extends Exception {
    }

    protected ProjectComparator(MavenProject project) {
        this.originalProject = project;
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

    private void fireProjectChangeEvent(MavenProject newProject, String subModel) {
    	if (log.isDebugEnabled()) {
    		log.debug("Firing ProjectChangeEvent with change in " + subModel);
    	}
    	ProjectChangeEvent e = null;
        if (newProject != null) {
        	e = new ProjectChangeEvent(newProject, subModel);
        } else {
			e = new ProjectChangeEvent(NULL_PROJECT, subModel);
        }
        projectChanged = true;

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

    private void detectMapChange(Map newValue, Map oldValue) throws ShortCircuitException {
    	if ( oldValue.size() != newValue.size() ) {
    		throw new ShortCircuitException();
    	}
    }
    
    public void compare(MavenProject newProject) {
        if (log.isDebugEnabled()) {
            log.debug("Computing project difference...");
        }

        // same reference or both null? treat as identical projects
        if (newProject != originalProject) {

	        if (newProject == null || originalProject == null) {
	            fireProjectChangeEvent(newProject, PROJECT);
	        } else {
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
	        updateProject(newProject);
        }
    }

    private void updateProject(MavenProject newProject)
	{
    	if (projectChanged) {
    		if (log.isDebugEnabled()) {
    			log.debug("Projects differ.  Updating comparator with new project.");
    		}
    		ProjectComparatorFactory.updateComparator(originalProject, newProject);
			originalProject = newProject;
			projectChanged = false;
    	}
	}

	private void compareProject(MavenProject newProject) {
        try {
            detectAttributeChange(newProject.getModel().getName(), originalProject.getModel().getName());
            detectAttributeChange(newProject.getModel().getArtifactId(), originalProject.getModel().getArtifactId());
            detectAttributeChange(newProject.getModel().getGroupId(), originalProject.getModel().getGroupId());
            detectAttributeChange(newProject.getModel().getGumpRepositoryId(), originalProject.getModel().getGumpRepositoryId());
            detectAttributeChange(newProject.getModel().getExtend(), originalProject.getModel().getExtend());
            detectAttributeChange(newProject.getModel().getPomVersion(), originalProject.getModel().getPomVersion());
            detectAttributeChange(newProject.getModel().getCurrentVersion(), originalProject.getModel().getCurrentVersion());
            detectAttributeChange(newProject.getModel().getCurrentVersion(), originalProject.getModel().getCurrentVersion());
            detectAttributeChange(newProject.getModel().getLogo(), originalProject.getModel().getLogo());
            detectAttributeChange(newProject.getModel().getInceptionYear(), originalProject.getModel().getInceptionYear());
            detectAttributeChange(newProject.getModel().getUrl(), originalProject.getModel().getUrl());
            detectAttributeChange(newProject.getModel().getPackage(), originalProject.getModel().getPackage());
            detectAttributeChange(newProject.getModel().getShortDescription(), originalProject.getModel().getShortDescription());
            detectAttributeChange(newProject.getModel().getDescription(), originalProject.getModel().getDescription());
            detectAttributeChange(newProject.getModel().getDistributionDirectory(), originalProject.getModel().getDistributionDirectory());
            detectAttributeChange(newProject.getModel().getDistributionSite(), originalProject.getModel().getDistributionSite());
            detectAttributeChange(newProject.getModel().getIssueTrackingUrl(), originalProject.getModel().getIssueTrackingUrl());
            detectAttributeChange(newProject.getModel().getSiteAddress(), originalProject.getModel().getSiteAddress());
            detectAttributeChange(newProject.getModel().getSiteDirectory(), originalProject.getModel().getSiteDirectory());
        }
        catch (ShortCircuitException e) {
            fireProjectChangeEvent(newProject, PROJECT);
        }
    }

    private void compareOrganization(MavenProject newProject) {
        Organization newOrg = newProject.getModel().getOrganization();
        Organization originalOrg = originalProject.getModel().getOrganization();
        if (comparable(newOrg, originalOrg)) {
            try {
                detectObjectChange(newOrg, originalOrg);
                detectAttributeChange(newOrg.getName(), originalOrg.getName());
                detectAttributeChange(newOrg.getLogo(), originalOrg.getLogo());
                detectAttributeChange(newOrg.getUrl(), originalOrg.getUrl());
            }
            catch (ShortCircuitException e) {
                fireProjectChangeEvent(newProject, ORGANIZATION);
            }
        }
    }

    private void compareRepository(MavenProject newProject) {
        Scm newRepo = newProject.getModel().getScm();
        Scm originalRepo = originalProject.getModel().getScm();
        if (comparable(newRepo, originalRepo)) {
            try {
				detectObjectChange(newRepo, originalRepo);
                detectAttributeChange(newRepo.getConnection(), originalRepo.getConnection());
                detectAttributeChange(newRepo.getConnection(), originalRepo.getConnection());
                detectAttributeChange(newRepo.getUrl(), originalRepo.getUrl());
            }
            catch (ShortCircuitException e) {
                fireProjectChangeEvent(newProject, REPOSITORY);
            }
        }
    }

    private void compareBranches(MavenProject newProject) {
        List newBranches = newProject.getModel().getBranches();
        List origBranches = originalProject.getModel().getBranches();
        try {
            detectCollectionChange(newBranches, origBranches);
            // just assume order is significant
            for (int i = 0; i < origBranches.size(); i++) {
                Branch newBranch = (Branch) newBranches.get(i);
                Branch origBranch = (Branch) origBranches.get(i);
                detectAttributeChange(newBranch.getTag(), origBranch.getTag());
            }
        }
        catch (ShortCircuitException e) {
            fireProjectChangeEvent(newProject, BRANCHES);
        }
    }

    private void compareVersions(MavenProject newProject) {
        List newVersions = newProject.getModel().getVersions();
        List origVersions = originalProject.getModel().getVersions();
        if (comparable(newVersions, origVersions)) {
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
    }

    private void compareMailingLists(MavenProject newProject) {
        List newMailingLists = newProject.getModel().getMailingLists();
        List origMailingLists = originalProject.getModel().getMailingLists();
        if ( comparable(newMailingLists, origMailingLists) ) {
	        try {
	            detectCollectionChange(newMailingLists, origMailingLists);
	            // just assume order is significant
	            for (int i = 0; i < origMailingLists.size(); i++) {
	                MailingList newMailingList = (MailingList) newMailingLists.get(i);
	                MailingList origMailingList = (MailingList) origMailingLists.get(i);
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
    }

    private void compareContributors(MavenProject newProject) {
        List newContributors = newProject.getModel().getContributors();
        List origContributors = originalProject.getModel().getContributors();
        if ( comparable(newContributors, origContributors) ) {
	        try {
	        	detectCollectionChange(newContributors, origContributors);
	        	// just assume order is significant
	        	for (int i = 0; i < origContributors.size(); i++) {
	        		compareContributor((Contributor) newContributors.get(i), (Contributor) origContributors.get(i));
	        	}
	        }
	        catch (ShortCircuitException e) {
	            fireProjectChangeEvent(newProject, CONTRIBUTORS);
	        }
        }
    }

    private void compareContributor(Contributor newContributor, Contributor origContributor)
		throws ShortCircuitException {

            detectAttributeChange(newContributor.getName(), origContributor.getName());
            detectAttributeChange(newContributor.getEmail(), origContributor.getEmail());
            detectAttributeChange(newContributor.getOrganization(), origContributor.getOrganization());
            detectAttributeChange(newContributor.getTimezone(), origContributor.getTimezone());
            detectAttributeChange(newContributor.getUrl(), origContributor.getUrl());

            List origRoles = origContributor.getRoles();
            List newRoles = newContributor.getRoles();
            detectCollectionChange(newRoles, origRoles);
            Iterator itrOrig = origRoles.iterator();
            Iterator itrNew = newRoles.iterator();
            while (itrOrig.hasNext()) {
                detectAttributeChange(itrNew.next().toString(), itrOrig.next().toString());
            }
    }

    private void compareDevelopers(MavenProject newProject) {
        List newDevelopers = newProject.getModel().getDevelopers();
        List origDevelopers = originalProject.getModel().getDevelopers();
        if ( comparable(newDevelopers, origDevelopers ) ) {
	        try {
	            detectCollectionChange(newDevelopers, origDevelopers);
	            // just assume order is significant
	            for (int i = 0; i < origDevelopers.size(); i++) {
	            	Developer newDeveloper = (Developer) newDevelopers.get(i);
	            	Developer origDeveloper = (Developer) origDevelopers.get(i);
	            	detectAttributeChange(newDeveloper.getId(), origDeveloper.getId());
	            	compareContributor(newDeveloper, origDeveloper);
	            }
	        }
	        catch (ShortCircuitException e) {
	            fireProjectChangeEvent(newProject, DEVELOPERS);
	        }
        }
    }

    private void compareLicenses(MavenProject newProject) {
        List origLicenses = originalProject.getModel().getLicenses();
        List newLicenses = newProject.getModel().getLicenses();
        if ( comparable(newLicenses, origLicenses) ) {
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
    }

    private void compareDependencies(MavenProject newProject) {
        List newDependencies = newProject.getModel().getDependencies();
        List origDependencies = originalProject.getModel().getDependencies();
        if ( comparable(newDependencies, origDependencies) ) {
	        try {
	            detectCollectionChange(newDependencies, origDependencies);
	            // just assume order is significant
	            for (int i = 0; i < origDependencies.size(); i++) {
	                Dependency newDependency = (Dependency) newDependencies.get(i);
	                Dependency origDependency = (Dependency) origDependencies.get(i);
	                detectAttributeChange(newDependency.getId(), origDependency.getId());
	                detectAttributeChange(newDependency.getArtifactId(), origDependency.getArtifactId());
	                detectAttributeChange(newDependency.getGroupId(), origDependency.getGroupId());
	                detectAttributeChange(newDependency.getArtifact(), origDependency.getArtifact());
	                detectAttributeChange(newDependency.getType(), origDependency.getType());
	                detectAttributeChange(newDependency.getUrl(), origDependency.getUrl());
	                detectAttributeChange(newDependency.getVersion(), origDependency.getVersion());
	            }
	        }
	        catch (ShortCircuitException e) {
	            fireProjectChangeEvent(newProject, DEPENDENCIES);
	        }
        }
    }

    private void compareBuild(MavenProject newProject) {
        Build newBuild = newProject.getModel().getBuild();
        Build originalBuild = originalProject.getModel().getBuild();
        if (comparable(newBuild, originalBuild)) {
	        try {
	        	detectObjectChange(newBuild, originalBuild);
	            detectAttributeChange(newBuild.getSourceDirectory(), originalBuild.getSourceDirectory());
	            detectAttributeChange(newBuild.getUnitTestSourceDirectory(), originalBuild.getUnitTestSourceDirectory());
	            detectAttributeChange(newBuild.getAspectSourceDirectory(), originalBuild.getAspectSourceDirectory());
	            detectAttributeChange(newBuild.getNagEmailAddress(), originalBuild.getNagEmailAddress());
	            compareUnitTest(newBuild, originalBuild, newProject);
	            compareResources(newBuild.getResources(), originalBuild.getResources());
	        }
	        catch (ShortCircuitException e) {
	            fireProjectChangeEvent(newProject, BUILD);
	        }
        }
    }

    private void compareUnitTest(Build newBuild, Build origBuild, MavenProject newProject) {
		try {
	        UnitTest origUnitTest = origBuild.getUnitTest();
	        UnitTest newUnitTest = newBuild.getUnitTest();
	        if ( comparable(newUnitTest, origUnitTest) ) {
		        detectObjectChange(newUnitTest, origUnitTest);
  	            compareResources(newUnitTest.getResources(), origUnitTest.getResources());
	        }
		}
		catch (ShortCircuitException e) {
			fireProjectChangeEvent(newProject, UNIT_TESTS);
		}
    }

    private void compareResources(List newResources, List origResources) throws ShortCircuitException {
    	if ( comparable(newResources, origResources) ) {
	    	detectObjectChange(newResources, origResources);
	    	detectCollectionChange(newResources, origResources);
	        // just assume order is significant
	        for (int i = 0; i < origResources.size(); i++) {
	            Resource origResource = (Resource) origResources.get(i);
	            Resource newResource = (Resource) newResources.get(i);
	            compareResource(origResource, newResource);
	        }
    	}
    }

    private void compareResource(Resource origResource, Resource newResource) throws ShortCircuitException {
    	if ( comparable(newResource, origResource) ) {
	    	detectObjectChange(newResource, origResource);
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
    }

    private void compareReports(MavenProject newProject) {
        List newReports = newProject.getModel().getReports();
        List origReports = originalProject.getModel().getReports();
        if ( comparable(newReports, origReports) ) {
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
    }

    private void compareProperties(Map newProps, Map origProps) throws ShortCircuitException {
    	if ( comparable(newProps, origProps) ) {
	        detectMapChange(newProps, origProps);
	        Set origKeySet = origProps.keySet();
	        Iterator origkeyIterator = origKeySet.iterator();
	        while ( origkeyIterator.hasNext() ) {
	        	Object nextOrigKey = origkeyIterator.next();
	        	if ( newProps.containsKey(nextOrigKey) ) {
	        		detectObjectChange(origProps.get(nextOrigKey), newProps.get(nextOrigKey));
	        	}
	        	else {
	        	    throw new ShortCircuitException();
	        	}
	        }
	        
    	}
    }

}
