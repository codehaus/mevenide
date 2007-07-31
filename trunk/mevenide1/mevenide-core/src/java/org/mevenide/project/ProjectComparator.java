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
package org.mevenide.project;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.mevenide.util.MevenideUtils;

/**
 * 
 * @author Jeffrey Bonevich 
 * @version $Id$
 * 
 */
public class ProjectComparator {

    private static final Logger LOGGER = Logger.getLogger(ProjectComparator.class.getName());

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
    private volatile boolean projectChanged = false;

    class ShortCircuitException extends Exception {
    }

    protected ProjectComparator(Project project) {
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

    private void fireProjectChangeEvent(Project newProject, String subModel) {
    	if (LOGGER.isLoggable(Level.FINE)) {
    		LOGGER.fine("Firing ProjectChangeEvent with change in " + subModel);
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
    
    private void detectPropertiesChange(Properties newValue, Properties oldValue) throws ShortCircuitException {
        if (oldValue.size() != newValue.size()) {
            throw new ShortCircuitException();
        }
    }    

    public void compare(Project newProject) {
    	if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Computing project difference...");
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

    private void updateProject(Project newProject)
	{
    	if (projectChanged) {
            if (LOGGER.isLoggable(Level.FINE)) {
    			LOGGER.fine("Projects differ.  Updating comparator with new project.");
    		}
    		ProjectComparatorFactory.updateComparator(originalProject, newProject);
			originalProject = newProject;
			projectChanged = false;
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
            fireProjectChangeEvent(newProject, PROJECT);
        }
    }

    private void compareOrganization(Project newProject) {
        Organization newOrg = newProject.getOrganization();
        Organization originalOrg = originalProject.getOrganization();
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

    private void compareRepository(Project newProject) {
        Repository newRepo = newProject.getRepository();
        Repository originalRepo = originalProject.getRepository();
        if (comparable(newRepo, originalRepo)) {
            try {
				detectObjectChange(newRepo, originalRepo);
                detectAttributeChange(newRepo.getConnection(), originalRepo.getConnection());
                detectAttributeChange(newRepo.getDeveloperConnection(), originalRepo.getDeveloperConnection());
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
            if ( comparable(newBranches, origBranches) ) {
            	detectObjectChange(newBranches, origBranches);
                detectCollectionChange(newBranches, origBranches);
	            // just assume order is significant
	            for (int i = 0; i < origBranches.size(); i++) {
	                Branch newBranch = (Branch) newBranches.get(i);
	                Branch origBranch = (Branch) origBranches.get(i);
	                detectAttributeChange(newBranch.getTag(), origBranch.getTag());
	            }
            }
        }
        catch (ShortCircuitException e) {
            fireProjectChangeEvent(newProject, BRANCHES);
        }
    }

    private void compareVersions(Project newProject) {
        List newVersions = newProject.getVersions();
        List origVersions = originalProject.getVersions();
        if (comparable(newVersions, origVersions)) {
	        try {
	            detectObjectChange(newVersions, origVersions);
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

    private void compareMailingLists(Project newProject) {
        List newMailingLists = newProject.getMailingLists();
        List origMailingLists = originalProject.getMailingLists();
        if ( comparable(newMailingLists, origMailingLists) ) {
	        try {
	            detectObjectChange(newMailingLists, origMailingLists);
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

    private void compareContributors(Project newProject) {
        List newContributors = newProject.getContributors();
        List origContributors = originalProject.getContributors();
        if ( comparable(newContributors, origContributors) ) {
	        try {
	            detectObjectChange(newContributors, origContributors);
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
            detectObjectChange(newRoles, origRoles);
            detectCollectionChange(newRoles, origRoles);
            Iterator itrOrig = origRoles.iterator();
            Iterator itrNew = newRoles.iterator();
            while (itrOrig.hasNext()) {
                detectAttributeChange(itrNew.next().toString(), itrOrig.next().toString());
            }
    }

    private void compareDevelopers(Project newProject) {
        List newDevelopers = newProject.getDevelopers();
        List origDevelopers = originalProject.getDevelopers();
        if ( comparable(newDevelopers, origDevelopers ) ) {
	        try {
	            detectObjectChange(newDevelopers, origDevelopers);
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

    private void compareLicenses(Project newProject) {
        List origLicenses = originalProject.getLicenses();
        List newLicenses = newProject.getLicenses();
        if ( comparable(newLicenses, origLicenses) ) {
	        try {
	            detectObjectChange(newLicenses, origLicenses);
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

    private void compareDependencies(Project newProject) {
        List newDependencies = newProject.getDependencies();
        List origDependencies = originalProject.getDependencies();
        if ( comparable(newDependencies, origDependencies) ) {
	        try {
	            detectObjectChange(newDependencies, origDependencies);
	            detectCollectionChange(newDependencies, origDependencies);
	            // just assume order is significant
	            for (int i = 0; i < origDependencies.size(); i++) {
	                Dependency newDependency = (Dependency) newDependencies.get(i);
	                Dependency origDependency = (Dependency) origDependencies.get(i);
	                detectAttributeChange(newDependency.getId(), origDependency.getId());
	                detectAttributeChange(newDependency.getExtension(), origDependency.getExtension());
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
	            compareSourceModifications(newBuild.getSourceModifications(), originalBuild.getSourceModifications());
	            detectAttributeChange(newBuild.getNagEmailAddress(), originalBuild.getNagEmailAddress());
	            compareUnitTest(newBuild, originalBuild, newProject);
	            compareResources(newBuild.getResources(), originalBuild.getResources());
	        }
	        catch (ShortCircuitException e) {
	            fireProjectChangeEvent(newProject, BUILD);
	        }
        }
    }

    private void compareSourceModifications(List newSourceModifications, List origSourceModifications) throws ShortCircuitException {
		if ( comparable(newSourceModifications, origSourceModifications) ) {
	    	detectObjectChange(newSourceModifications, origSourceModifications);
	    	detectCollectionChange(newSourceModifications, origSourceModifications);
	        // just assume order is significant
	        for (int i = 0; i < origSourceModifications.size(); i++) {
	            SourceModification origSourceModification = (SourceModification) origSourceModifications.get(i);
	            SourceModification newSourceModification = (SourceModification) newSourceModifications.get(i);
	            compareSourceModification(origSourceModification, newSourceModification);
	        }
    	}
    } 
    
    private void compareSourceModification(SourceModification origSourceModification, SourceModification newSourceModification) throws ShortCircuitException {
    	if ( comparable(newSourceModification, origSourceModification) ) {
	    	detectObjectChange(newSourceModification, origSourceModification);
	        detectAttributeChange(newSourceModification.getClassName(), origSourceModification.getClassName());
	        compareResource(origSourceModification, newSourceModification);
    	}
    }
    
    private void compareUnitTest(Build newBuild, Build origBuild, Project newProject) {
		try {
	        UnitTest origUnitTest = origBuild.getUnitTest();
	        UnitTest newUnitTest = newBuild.getUnitTest();
	        if ( comparable(newUnitTest, origUnitTest) ) {
		        detectObjectChange(newUnitTest, origUnitTest);
		        detectObjectChange(newUnitTest.getIncludes(), origUnitTest.getIncludes());
		        detectCollectionChange(newUnitTest.getIncludes(), origUnitTest.getIncludes());
		        for (int j = 0; j < newUnitTest.getIncludes().size(); j++) {
		            detectAttributeChange(origUnitTest.getIncludes().get(j), newUnitTest.getIncludes().get(j));
		        }
		        detectObjectChange(newUnitTest.getExcludes(), origUnitTest.getExcludes());
		        detectCollectionChange(newUnitTest.getExcludes(), origUnitTest.getExcludes());
		        for (int j = 0; j < origUnitTest.getExcludes().size(); j++) {
		            detectAttributeChange(origUnitTest.getExcludes().get(j), newUnitTest.getExcludes().get(j));
		        }
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
	        detectObjectChange(newResource.getIncludes(), origResource.getIncludes());
	        detectCollectionChange(newResource.getIncludes(), origResource.getIncludes());
	        for (int j = 0; j < origResource.getIncludes().size(); j++) {
	            detectAttributeChange(origResource.getIncludes().get(j), newResource.getIncludes().get(j));
	        }
	        detectObjectChange(newResource.getExcludes(), origResource.getExcludes());
	        detectCollectionChange(newResource.getExcludes(), origResource.getExcludes());
	        for (int j = 0; j < origResource.getExcludes().size(); j++) {
	            detectAttributeChange(origResource.getExcludes().get(j), newResource.getExcludes().get(j));
	        }
    	}
    }
    
    private void compareResource(SourceModification origSourceModification, SourceModification newSourceModification) throws ShortCircuitException {
    	if ( comparable(newSourceModification, origSourceModification) ) {
	    	detectObjectChange(newSourceModification, origSourceModification);
	        detectAttributeChange(newSourceModification.getDirectory(), origSourceModification.getDirectory());
	        detectAttributeChange(newSourceModification.getClassName(), origSourceModification.getClassName());
                detectAttributeChange(newSourceModification.getProperty(), origSourceModification.getProperty());
	        detectObjectChange(newSourceModification.getIncludes(), origSourceModification.getIncludes());
	        detectCollectionChange(newSourceModification.getIncludes(), origSourceModification.getIncludes());
	        for (int j = 0; j < origSourceModification.getIncludes().size(); j++) {
	            detectAttributeChange(origSourceModification.getIncludes().get(j), newSourceModification.getIncludes().get(j));
	        }
	        detectObjectChange(newSourceModification.getExcludes(), origSourceModification.getExcludes());
	        detectCollectionChange(newSourceModification.getExcludes(), origSourceModification.getExcludes());
	        for (int j = 0; j < origSourceModification.getExcludes().size(); j++) {
	            detectAttributeChange(origSourceModification.getExcludes().get(j), newSourceModification.getExcludes().get(j));
	        }
    	}
    }    

    private void compareReports(Project newProject) {
        List newReports = newProject.getReports();
        List origReports = originalProject.getReports();
        if ( comparable(newReports, origReports) ) {
	        try {
	            detectObjectChange(newReports, origReports);
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

    private void compareProperties(Properties newProps, Properties origProps) throws ShortCircuitException {
    	if ( comparable(newProps, origProps) ) {
                detectObjectChange(newProps, origProps);
	        detectPropertiesChange(newProps, origProps);
	        // just assume order is significant
	        // FIXME: ?? properties in BaseObject are stored as 'name:value',
	        // so we could parse the retrieved props apart and compare property
	        // for property.  Kinda silly that Maven does not provide a
	        // getPropertyNames() method
                Enumeration propertyNames = newProps.propertyNames();
                while (propertyNames.hasMoreElements()) {
                    String name = (String) propertyNames.nextElement();
                    detectAttributeChange(newProps.getProperty(name), origProps.getProperty(name));
                }
    	}
    }
}
