/* 
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 */
package org.mevenide.project.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.apache.maven.project.Build;
import org.apache.maven.project.Contributor;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Developer;
import org.apache.maven.project.License;
import org.apache.maven.project.MailingList;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.apache.maven.project.Version;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DefaultProjectMarshaller implements IProjectMarshaller {
	private static final String NAMESPACE = null;
	private static final String ENCODING = null;
	private static final Boolean STANDALONE = null;
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
		
		marshallRequiredString(project.getId(), "id");
		marshallRequiredString(project.getName(), "name");
		
		marshallString(project.getGroupId(), "groupId");
		marshallString(project.getArtifactId(), "artifactId");
		
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
		marshallString(project.getDistributionDirectory(), "distributionDirectory");
		
		marshallRepository(project);
		marshallVersions(project);
		
//		marshallBranches(serializer, project);
		
		marshallMailingLists(project);
		marshallDevelopers(project);
		marshallContributors(project);
		marshallLicenses(project);
		marshallDependencies(project);
		marshallBuild(project);
		marshallReports(project);
		
// 		properties element not managed by DefaultProjectUnmarshaller
//		should we do it here ?
//		marshallProperties(serializer, project);
		
		serializer.endTag(NAMESPACE, "project");
		serializer.endDocument();
	}
	
	private  void marshallOrganization(Project project) throws Exception {
		serializer.startTag(NAMESPACE, "organization");
		
		marshallRequiredString(project.getOrganization().getName(), "name");
		marshallString(project.getOrganization().getUrl(), "url");
		marshallString(project.getOrganization().getLogo(), "logo");
		
		serializer.endTag(NAMESPACE, "organization");
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
		serializer.startTag(NAMESPACE, "developers");
		Developer developer;
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

	private  void marshallContactDetails(Contributor contributor) throws IOException {
		serializer.startTag(NAMESPACE, "contactDetails");
		
		marshallRequiredString(contributor.getEmail(), "email");
		marshallString(contributor.getOrganization(), "organization");
		
		marshallRoles(contributor);
		
		marshallString(contributor.getUrl(), "url");
		
		//timezone skipped
		
		serializer.endTag(NAMESPACE, "contactDetails");
	}

	private  void marshallRoles(Contributor contributor) throws IOException {
		SortedSet roles = contributor.getRoles();
		if ( roles != null ) {
			serializer.startTag(NAMESPACE, "roles");
			
			Iterator iterator = roles.iterator();
			while (iterator.hasNext()) {
				marshallString((String) iterator.next(), "role");
			}
			
			serializer.endTag(NAMESPACE, "roles");
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
					//marshallRequiredString(contributor.getEmail(), "email");
					
					marshallContactDetails(contributor);
					
					serializer.endTag(NAMESPACE, "contributor");
				}
			}
			serializer.endTag(NAMESPACE, "contributors");
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
					marshallString(dependency.getId(), "id");
					marshallString(dependency.getGroupId(), "groupId");
					marshallString(dependency.getArtifactId(), "artifactId");
						
					marshallRequiredString(dependency.getVersion(), "version");
					
					marshallString(dependency.getJar(), "jar");
					marshallString(dependency.getType(), "type");
					marshallString(dependency.getUrl(), "url");
					
					//dependency.properties ?
				}
			}
			serializer.endTag(NAMESPACE, "dependencies");
		}
	}
	
	private  void marshallBuild(Project project) throws Exception {
		Build build = project.getBuild();
		serializer.startTag(NAMESPACE, "build");
		
		marshallString(build.getNagEmailAddress(), "nagEmailAddress");
		marshallString(build.getSourceDirectory(), "sourceDirectory");
		marshallString(build.getUnitTestSourceDirectory(), "unitTestSourceDirectory");
		marshallString(build.getIntegrationUnitTestSourceDirectory(), "integrationUnitTestSourceDirectory");
		marshallString(build.getAspectSourceDirectory(), "aspectSourceDirectory");

		if ( build.getUnitTest() != null ) {
			serializer.startTag(NAMESPACE, "unitTest");
			marshallIncludes(build.getUnitTest().getIncludes());
			marshallExcludes(build.getUnitTest().getExcludes());
			marshallResources(build.getUnitTest().getResources());
			serializer.endTag(NAMESPACE, "unitTest");
		}
		
		//project.build.integrationUnitTest ? 
		marshallResources(build.getResources());
		
		serializer.endTag(NAMESPACE, "build");
	}

	private  void marshallResources(List resources)
		throws IOException {
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
			System.out.println("WARNING : " + tag + " should not be null");
		}
		marshallString(line, tag);
	}
	
	
	private  boolean isNull(String property) {
		return property == null || property.trim().equals("");
	}
}
