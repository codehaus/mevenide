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
package org.codehaus.mevenide.pde.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.ArtifactListBuilder;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.jelly.MavenJellyContext;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Resource;
import org.apache.maven.model.SourceModification;
import org.apache.maven.model.UnitTest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.Project;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.properties.IPropertyResolver;


/**  
 * 
 * @todo trash this converter when real converter discovered (if exists)
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class MavenProjectConverter {

    private Project m1Project;
    private IPropertyResolver propertyResolver;
    private MavenJellyContext context;
    
    public MavenProjectConverter(MavenJellyContext context) {
        propertyResolver = getPropertyResolver();
        this.context = context;
    }
    
    public MavenProjectConverter(Project project, MavenJellyContext context) {
        this.m1Project = project;
        propertyResolver = getPropertyResolver();
        this.context = context;
    }
    
    public MavenProject convert() throws ConverterException {
        if ( m1Project == null ) {
            throw new ConverterException("Converter.NotSet");
        }
        Model model = new Model();
        
        model.setArtifactId(m1Project.getArtifactId());
        model.setDescription(m1Project.getDescription());
        model.setGroupId(m1Project.getGroupId());
        model.setInceptionYear(m1Project.getInceptionYear());
        model.setLogo(m1Project.getLogo());
        model.setModelVersion("4.0.0");
        model.setName(m1Project.getName());
        model.setPackage(m1Project.getPackage());
        model.setShortDescription(m1Project.getShortDescription());
        model.setUrl(m1Project.getUrl());
        model.setVersion(m1Project.getCurrentVersion());

        model.setDependencies(getDependencies());
        model.setBuild(getBuild());
        model.setOrganization(getOrganization());
        //model.setPackageGroups(getPackageGroups());
        
        //@todo copy other complex elements to model as well 
        
        //model.setCiManagement(getCiManagement());
        //model.setContributors(getContributors());
        //model.setDevelopers(getDevelopers());
        //model.setDistributionManagement(getDistributionManagement());
        //model.setIssueManagement(getIssueManagement());
        //model.setLicenses(getLicenses());
        //model.setMailingLists(getMailingLists());
        //model.setReports(getReports());
        //model.setScm(getScm());
        
        MavenProject project = new MavenProject(model);
        project.setFile(m1Project.getFile());
        
        setArtifacts(project);
        
        return project;
    }
    

    //copied from mevenide-ui-eclipse
    private void setArtifacts(MavenProject project) {
        //change user.dir to allow to build artifacts correctly
	    String backupUserDir = System.getProperty("user.dir"); //$NON-NLS-1$
	    System.setProperty("user.dir", project.getFile().getParentFile().getAbsolutePath()); //$NON-NLS-1$
	    
	    //needed for rc3 to correctly setRelativePaths
	    System.setProperty("maven.home", propertyResolver.getResolvedValue("maven.home")); //$NON-NLS-1$
	    
	    //m1Project.setContext(MavenUtils.createContext(m1Project.getFile().getParentFile()));
	    m1Project.setContext(context);
	    
	    if ( project.getDependencies() == null ) {
	        project.setDependencies(new ArrayList());
	    }
		List artifacts = ArtifactListBuilder.build(m1Project);
		
		Set m2Artifacts = new HashSet();
		
		for (Iterator iter = artifacts.iterator(); iter.hasNext();) {
            org.apache.maven.repository.Artifact m1Artifact = (org.apache.maven.repository.Artifact) iter.next();
            org.apache.maven.project.Dependency m1Dependency = m1Artifact.getDependency();
            Artifact artifact = new DefaultArtifact(m1Dependency.getArtifactId(), m1Dependency.getGroupId(), m1Dependency.getVersion(), m1Dependency.getType());
            artifact.setPath(m1Artifact.getPath());
            m2Artifacts.add(artifact);
        }
		
		project.setArtifacts(m2Artifacts);
		
		System.setProperty("user.dir", backupUserDir);
    }

    private List getDependencies() {
        List dependencies = null;
        
        List m1Dependencies = m1Project.getDependencies();
        if ( m1Dependencies != null ) {
            
            dependencies = new ArrayList(m1Dependencies.size());
            for (Iterator it = m1Dependencies.iterator(); it.hasNext();) {
                Dependency dependency = new Dependency();
                org.apache.maven.project.Dependency m1Dependency = (org.apache.maven.project.Dependency) it.next();
                dependency.setArtifact(m1Dependency.getArtifact());
                dependency.setArtifactId(m1Dependency.getArtifactId());
                dependency.setGroupId(m1Dependency.getGroupId());
                dependency.setProperties(getProperties(m1Dependency));
                dependency.setType(m1Dependency.getType());
                dependency.setUrl(m1Dependency.getUrl());
                dependency.setVersion(m1Dependency.getVersion());
                dependencies.add(dependency);
                
            }
        }
        
        return dependencies;
    }

    private Properties getProperties(org.apache.maven.project.Dependency m1Dependency) {
        Properties properties = null;
        if ( m1Dependency.getProperties() != null ) {
            properties = new Properties(); 
            for (Iterator it = m1Dependency.getProperties().iterator(); it.hasNext();) {
                String prop = (String) it.next();
                String key = prop.substring(0, prop.indexOf(':') + 1);
                String value = prop.substring(prop.indexOf(':') + 1, prop.length() - 1);
                properties.put(key, value);
            }
        }
        return properties;
    }

    private Build getBuild() {
        Build build = null;
        org.apache.maven.project.Build m1Build = m1Project.getBuild();
        
        if ( m1Build != null ) {
	        build = new Build();
	        
	        build.setAspectSourceDirectory(m1Build.getAspectSourceDirectory());
	        build.setDirectory(propertyResolver.getResolvedValue("maven.build.dir"));
	        build.setFinalName(propertyResolver.getResolvedValue("maven.final.name"));
	        build.setIntegrationUnitTestSourceDirectory(m1Build.getIntegrationUnitTestSourceDirectory());
	        build.setOutput(propertyResolver.getResolvedValue("maven.build.dest"));
	        build.setResources(getResources(m1Build));
	        build.setSourceDirectory(m1Build.getSourceDirectory());
	        build.setSourceModifications(getSourceModification(m1Build));
	        build.setTestOutput(propertyResolver.getResolvedValue("maven.test.dest"));
	        build.setUnitTest(getUnitTest());
	        build.setUnitTestSourceDirectory(m1Build.getUnitTestSourceDirectory());
        }
        return build;
    }

    private IPropertyResolver getPropertyResolver() {
		java.io.File projectDir = m1Project.getFile().getParentFile();
		DefaultQueryContext queryContext = new DefaultQueryContext(projectDir);
        return queryContext.getResolver();
		
        /* IQueryContext queryContext = new DefaultQueryContext(m1Project.getFile());
        IPropertyResolver resolver = PropertyResolverFactory.getFactory().createContextBasedResolver(queryContext);
        IPropertyLocator locator = PropertyLocatorFactory.getFactory().createContextBasedLocator(queryContext);
        IProjectContext projectContext = new DefaultProjectContext(queryContext, resolver);
        ((DefaultQueryContext)queryContext).initializeProjectContext(projectContext);
        return resolver;*/
    }

    private UnitTest getUnitTest() {
        UnitTest unitTest = null;
        
        if ( m1Project.getBuild() != null && m1Project.getBuild().getUnitTest() != null ) {
            org.apache.maven.project.UnitTest m1UnitTest = m1Project.getBuild().getUnitTest();
            unitTest = new UnitTest();
            unitTest.setResources(getUnitTestResources(m1UnitTest));
            unitTest.setIncludes(m1UnitTest.getIncludes());
            unitTest.setExcludes(m1UnitTest.getExcludes());
               
        }
        
        return unitTest;
    }

    private List getUnitTestResources(org.apache.maven.project.UnitTest m1UnitTest) {
        List resources = null;
        if ( m1UnitTest != null ) {
            List m1Resources = m1UnitTest.getResources();
            resources = m1ToM2Resources(m1Resources);
        }
        return resources;
    }

    private List m1ToM2Resources(List m1Resources) {
        List resources = new ArrayList(m1Resources.size());
        for (Iterator it = m1Resources.iterator(); it.hasNext();) {
            Resource resource = new Resource(); 
            org.apache.maven.project.Resource m1Resource = (org.apache.maven.project.Resource) it.next();
            resource.setDirectory(m1Resource.getDirectory());
            resource.setFiltering(m1Resource.getFiltering());
            resource.setTargetPath(m1Resource.getTargetPath());
            resource.setIncludes(m1Resource.getIncludes());
            resource.setExcludes(m1Resource.getExcludes());
            resources.add(resource);
        }
        return resources;
    }

    private List getSourceModification(org.apache.maven.project.Build m1Build) {
        List sourceModifications = null;
        
        List m1SourceModifications = m1Build.getSourceModifications();
        
        if( m1SourceModifications != null ) {
            
            sourceModifications = new ArrayList(m1SourceModifications.size());
            
            for (Iterator it = m1SourceModifications.iterator(); it.hasNext();) {
                org.apache.maven.project.SourceModification m1SourceModification = (org.apache.maven.project.SourceModification) it.next();
                
                SourceModification sourceModification = new SourceModification();
                sourceModification.setClassName(m1SourceModification.getClassName());
                sourceModification.setDirectory(m1SourceModification.getDirectory());
                sourceModification.setFiltering(m1SourceModification.getFiltering());
                sourceModification.setExcludes(m1SourceModification.getExcludes());
                sourceModification.setIncludes(m1SourceModification.getIncludes());
                    
            }
            
        }
        
        return sourceModifications;
    }

    private List getResources(org.apache.maven.project.Build m1Build) {
        List resources = null;
        if ( m1Build != null ) {
            List m1Resources = m1Build.getResources();
            resources = m1ToM2Resources(m1Resources);
        }
        return resources;
    }

    private Organization getOrganization() {
        Organization organization = new Organization();
        organization.setLogo(m1Project.getOrganization().getLogo());
        organization.setName(m1Project.getOrganization().getName());
        organization.setUrl(m1Project.getUrl());
        return organization;
    }

    public Project getProject() { return m1Project; }
    public void setProject(Project project) { this.m1Project = project; }
}
