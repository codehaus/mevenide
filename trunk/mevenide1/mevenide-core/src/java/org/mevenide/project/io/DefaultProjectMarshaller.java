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
package org.mevenide.project.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.logging.Logger;

import org.apache.maven.project.Branch;
import org.apache.maven.project.Build;
import org.apache.maven.project.Contributor;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Developer;
import org.apache.maven.project.License;
import org.apache.maven.project.MailingList;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.apache.maven.project.SourceModification;
import org.apache.maven.project.Version;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

/**
 * 
 * @choice we do not respect the structure defined in maven-project.xsd
 * 
 * instead we follow the structure used  by DefaultProjectUnmarshaller
 * incase we want to respect the xsd, just replace with a previous 
 * version (should be 1.2)   
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DefaultProjectMarshaller implements IProjectMarshaller {
	
	private static final Logger LOGGER = Logger.getLogger(DefaultProjectMarshaller.class.getName());
	private static final String NAMESPACE = null;
	//private static final String ENCODING = null;
	//private static final Boolean STANDALONE = null;
	
	private XmlSerializer serializer ;
	
	public DefaultProjectMarshaller() throws Exception {
		XmlPullParserFactory factory 
			= XmlPullParserFactory.newInstance(
						System.getProperty(XmlPullParserFactory.PROPERTY_NAME),
						null);
		
		serializer = factory.newSerializer();	
	}

	private void initialize(Writer pom) throws IOException {
		serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "    ");
		serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\r\n");
		serializer.setOutput(pom);
		//serializer.startDocument(ENCODING, STANDALONE);	
	}

	public  void marshall(Writer pom, Project project) throws Exception {
		initialize(pom);
		
		serializer.startTag(NAMESPACE, "project");
		
		marshallString(project.getExtend(), "extend");
		marshallString(project.getPomVersion(), "pomVersion");
		
		//marshallString(project.getId(), "id");
		marshallString(project.getArtifactId(), "artifactId");
		marshallRequiredString(project.getName(), "name");
		
		marshallString(project.getGroupId(), "groupId");
		
		marshallRequiredString(project.getCurrentVersion(), "currentVersion");
		
		marshallOrganization(project);
		
		marshallString(project.getInceptionYear(), "inceptionYear");
		marshallString(project.getPackage(), "package");
		marshallString(project.getLogo(), "logo");
		marshallString(project.getGumpRepositoryId(), "gumpRepositoryId");
		
		marshallString(project.getDescription(), "description");
		
		marshallRequiredString(project.getShortDescription(), "shortDescription");
		
		marshallString(project.getUrl(), "url");
		marshallString(project.getIssueTrackingUrl(), "issueTrackingUrl");
		marshallString(project.getSiteAddress(), "siteAddress");
		marshallString(project.getSiteDirectory(), "siteDirectory");
		marshallString(project.getDistributionSite(), "distributionSite");
		marshallString(project.getDistributionDirectory(), "distributionDirectory");
		
		marshallRepository(project);
		marshallVersions(project);
		marshallBranches(project);
		
		marshallMailingLists(project);
		marshallDevelopers(project);
		marshallContributors(project);
		marshallLicenses(project);
		marshallDependencies(project);
		marshallBuild(project);
		marshallReports(project);
		
// 		properties element not managed by DefaultProjectUnmarshaller
//		should we do it here ?
		marshallProperties(project.getProperties());
		
		serializer.endTag(NAMESPACE, "project");
		serializer.endDocument();
	}
	
	private  void marshallOrganization(Project project) throws Exception {
		
		if ( project.getOrganization() != null ) {
		    serializer.startTag(NAMESPACE, "organization");
			marshallRequiredString(project.getOrganization().getName(), "name");
			marshallString(project.getOrganization().getUrl(), "url");
			marshallString(project.getOrganization().getLogo(), "logo");
			serializer.endTag(NAMESPACE, "organization");
		}
		
	}
	
	private  void marshallRepository(Project project) throws Exception {
		if ( project.getRepository() != null ) {
			serializer.startTag(NAMESPACE, "repository");
			
			marshallRequiredString(project.getRepository().getConnection(), "connection");
			marshallString(project.getRepository().getDeveloperConnection(), "developerConnection");
			marshallString(project.getRepository().getUrl(), "url");
			
			serializer.endTag(NAMESPACE, "repository");
		}
	}
	
	private  void marshallVersions(Project project) throws Exception {
		List versions = project.getVersions();
		if ( versions != null ) {
			serializer.startTag(NAMESPACE, "versions");
			Version version;
			for (int i = 0; i < versions.size(); i++) {
				version = (Version) versions.get(i);
				if ( version != null) {
					serializer.startTag(NAMESPACE, "version");
					
					marshallString(version.getId(), "id");
					marshallString(version.getName(), "name");
					marshallString(version.getTag(), "tag");
							  
					serializer.endTag(NAMESPACE, "version");
				}
			}
			serializer.endTag(NAMESPACE, "versions");
		}
	}
	
	private  void marshallBranches(Project project) throws Exception {
		List branches = project.getBranches();
		if ( branches != null ) {
			serializer.startTag(NAMESPACE, "branches");
			Branch branch;
			for (int i = 0; i < branches.size(); i++) {
				branch = (Branch) branches.get(i);
				if ( branch != null) {
					serializer.startTag(NAMESPACE, "branch");
					
					marshallString(branch.getTag(), "tag");
							  
					serializer.endTag(NAMESPACE, "branch");
				}
			}
			serializer.endTag(NAMESPACE, "branches");
		}
	}
	
	private  void marshallMailingLists(Project project) throws Exception {
		List mailingLists = project.getMailingLists();
		if ( mailingLists != null ) {
			serializer.startTag(NAMESPACE, "mailingLists");
			MailingList mailingList;
			for (int i = 0; i < mailingLists.size(); i++) {
				mailingList = (MailingList) mailingLists.get(i);
				if ( mailingList != null ) {
					serializer.startTag(NAMESPACE, "mailingList");
					
					marshallRequiredString(mailingList.getName(), "name");
					marshallRequiredString(mailingList.getSubscribe(), "subscribe");
					marshallRequiredString(mailingList.getUnsubscribe(), "unsubscribe");
					marshallString(mailingList.getArchive(), "archive");
					
					serializer.endTag(NAMESPACE, "mailingList");
				}
			}
			serializer.endTag(NAMESPACE, "mailingLists");
		}
	}
	
	private  void marshallDevelopers(Project project) throws Exception {
		List developers = project.getDevelopers();
		Developer developer;
		if ( developers != null ) {
			serializer.startTag(NAMESPACE, "developers");
			for (int i = 0; i < developers.size(); i++) {
				developer = (Developer) developers.get(i);
				if ( developer != null ) {
					serializer.startTag(NAMESPACE, "developer");
					
					marshallRequiredString(developer.getName(), "name");
					
					marshallRequiredString(developer.getId(), "id");
					
					marshallContactDetails(developer);
					
					serializer.endTag(NAMESPACE, "developer");
				}
			}
			serializer.endTag(NAMESPACE, "developers");
		}
	}

	private  void marshallContributors(Project project) throws Exception {
		List contributors = project.getContributors();
		if ( contributors != null && contributors.size() > 0 ) {
			serializer.startTag(NAMESPACE, "contributors");
			Contributor contributor;
			for (int i = 0; i < contributors.size(); i++) {
				contributor = (Contributor) contributors.get(i);
				if ( contributor != null ) {
					serializer.startTag(NAMESPACE, "contributor");
					marshallRequiredString(contributor.getName(), "name");
					
					marshallContactDetails(contributor);
					
					serializer.endTag(NAMESPACE, "contributor");
				}
			}
			serializer.endTag(NAMESPACE, "contributors");
		}
	}
	
	private void marshallContactDetails(Contributor contributor) throws Exception  {
		marshallRequiredString(contributor.getEmail(), "email");
		marshallString(contributor.getOrganization(), "organization");
		marshallRoles(contributor);
		marshallString(contributor.getUrl(), "url");
		marshallString(contributor.getTimezone(), "timezone");
	}
	
	private  void marshallRoles(Contributor contributor) throws IOException {
		List roles = contributor.getRoles();
		if ( roles != null ) {
			serializer.startTag(NAMESPACE, "roles");
			
			Iterator iterator = roles.iterator();
			while (iterator.hasNext()) {
				marshallString((String) iterator.next(), "role");
			}
			
			serializer.endTag(NAMESPACE, "roles");
		}
	}
	
	private  void marshallLicenses(Project project) throws Exception {
		List licenses = project.getLicenses();
		if ( licenses != null && licenses.size() > 0 ) {
			serializer.startTag(NAMESPACE, "licenses");
			License license;
			for (int i = 0; i < licenses.size(); i++) {
				license = (License) licenses.get(i);
				if ( license != null ) {
					serializer.startTag(NAMESPACE, "license");
					
					marshallString(license.getName(), "name");
					marshallString(license.getUrl(), "url");
					marshallString(license.getDistribution(), "distribution");
					marshallString(license.getComments(), "comments");
					
					serializer.endTag(NAMESPACE, "license");
				}
			}
			serializer.endTag(NAMESPACE, "licenses");
		}
	}
	
	private  void marshallDependencies(Project project) throws Exception {
		List dependencies = project.getDependencies();
		if ( dependencies != null && dependencies.size() > 0 ) {
			serializer.startTag(NAMESPACE, "dependencies");
			Dependency dependency;
			for (int i = 0; i < dependencies.size(); i++) {
				dependency = (Dependency) dependencies.get(i);
				if ( dependency != null ) {
					
					serializer.startTag(NAMESPACE, "dependency");
					
					//marshallString(dependency.getId(), "id");
					marshallString(dependency.getGroupId(), "groupId");
					marshallString(dependency.getArtifactId(), "artifactId");
						
					marshallRequiredString(dependency.getVersion(), "version");
					
					//marshallString(dependency.getArtifact(), "artifact");
					marshallString(dependency.getJar(), "jar");
					marshallString(dependency.getType(), "type");
					marshallString(dependency.getUrl(), "url");
					
					//marshallProperties(dependency.getProperties());
					marshallProperties(dependency);
					
					serializer.endTag(NAMESPACE, "dependency");
					//dependency.properties ?
				}
			}
			serializer.endTag(NAMESPACE, "dependencies");
		}
	}
	
	
	private void marshallProperties(Dependency dependency) throws Exception {
		marshallProperties(dependency.getProperties());
	}
	
	private void marshallProperties(Map properties) throws Exception {
		//commented lines will work under maven-new
		if ( properties != null ) {
			Iterator it = properties.keySet().iterator();
			serializer.startTag(NAMESPACE, "properties");
				
			while ( it.hasNext() ) {
				
				String key = (String) it.next();
				String value = (String) properties.get(key);  
				
				marshallString(value, key);
				
				//serializer.text("\r\n                " + key + "=" + value);
				
			}
			//serializer.text("\r\n            ");
			serializer.endTag(NAMESPACE, "properties");	
		}
	}
	
	private  void marshallBuild(Project project) throws Exception {
		Build build = project.getBuild();
		if ( build != null ) {
		
			serializer.startTag(NAMESPACE, "build");
			
			marshallString(build.getNagEmailAddress(), "nagEmailAddress");
			marshallString(build.getSourceDirectory(), "sourceDirectory");
			marshallSourceModifications(build.getSourceModifications());
			marshallString(build.getUnitTestSourceDirectory(), "unitTestSourceDirectory");
			marshallString(build.getAspectSourceDirectory(), "aspectSourceDirectory");
			
			if ( build.getUnitTest() != null ) {
				serializer.startTag(NAMESPACE, "unitTest");
				marshallIncludes(build.getUnitTest().getIncludes());
				marshallExcludes(build.getUnitTest().getExcludes());
				marshallResources(build.getUnitTest().getResources());
				serializer.endTag(NAMESPACE, "unitTest");
			}
			
			//marshallString(build.getIntegrationUnitTestSourceDirectory(), "integrationUnitTestSourceDirectory");
			 
			marshallResources(build.getResources());
			
			serializer.endTag(NAMESPACE, "build");
		}
	}

	private void marshallSourceModifications(List sourceModifications) throws IOException {
	    if ( sourceModifications != null && sourceModifications.size() > 0 ) {
			 serializer.startTag(NAMESPACE, "sourceModifications");
			
			 SourceModification sourceModification;
			 for (int i = 0; i < sourceModifications.size(); i++) {
				sourceModification = (SourceModification) sourceModifications.get(i);
				if ( sourceModification != null ) {
					serializer.startTag(NAMESPACE, "sourceModification");

					marshallString(sourceModification.getClassName(), "className");
					
					marshallIncludes(sourceModification.getIncludes());
					marshallExcludes(sourceModification.getExcludes());

					serializer.endTag(NAMESPACE, "sourceModification");
				}
			}
			
			serializer.endTag(NAMESPACE, "sourceModifications");
		}
	}
	
	private  void marshallResources(List resources) throws IOException {
		if ( resources != null && resources.size() > 0 ) {
			 serializer.startTag(NAMESPACE, "resources");
			
			 Resource resource;
			 for (int i = 0; i < resources.size(); i++) {
				resource = (Resource) resources.get(i);
				if ( resource != null ) {
					serializer.startTag(NAMESPACE, "resource");

					marshallString(resource.getDirectory(), "directory");
					marshallString(resource.getTargetPath(), "targetPath");

					marshallIncludes(resource.getIncludes());
					marshallExcludes(resource.getExcludes());

					serializer.endTag(NAMESPACE, "resource");
				}
			}
			
			serializer.endTag(NAMESPACE, "resources");
		}
	}

	private  void marshallExcludes(List excludes) throws IOException {
		if ( excludes != null && excludes.size() > 0 ) {
			serializer.startTag(NAMESPACE, "excludes");
			for (int i = 0; i < excludes.size(); i++) {
				marshallRequiredString((String) excludes.get(i), "exclude");
			}
			serializer.endTag(NAMESPACE, "excludes");
		}
	}
	
	//see if we can merge marshallIncludes and marshallExcludes in a smart way
	private  void marshallIncludes(List includes) throws IOException {
		if ( includes != null && includes.size() > 0 ) {
			serializer.startTag(NAMESPACE, "includes");
			for (int i = 0; i < includes.size(); i++) {
				marshallRequiredString((String) includes.get(i), "include");	
			}
			serializer.endTag(NAMESPACE, "includes");
		}
	}
	
	private  void marshallReports(Project project) throws Exception {
		List reports = project.getReports();
		if ( reports != null &&  reports.size() > 0 ) {
			serializer.startTag(NAMESPACE, "reports");
			for (int i = 0; i < reports.size(); i++) {
				marshallString((String) reports.get(i), "report");
			}
			serializer.endTag(NAMESPACE, "reports");
		}
	}
	
	private  void marshallString(String line, String tag) throws IOException {
		if ( !isNull(line) ) {
			serializer.startTag(NAMESPACE, tag).text(line).endTag(NAMESPACE, tag);
		}
	}
	
	private  void marshallRequiredString(String line, String tag) throws IOException {
		if ( isNull(line) ) {
			LOGGER.warning(tag + " should not be null");
		}
		marshallString(line, tag);
	}
	
	
	private  boolean isNull(String property) {
		return property == null || property.trim().equals("");
	}
}
