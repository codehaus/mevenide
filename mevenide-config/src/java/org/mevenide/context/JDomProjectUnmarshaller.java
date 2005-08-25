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
package org.mevenide.context;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.maven.project.ModelBase;
import org.apache.maven.project.Branch;
import org.apache.maven.project.Build;
import org.apache.maven.project.Contributor;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Developer;
import org.apache.maven.project.License;
import org.apache.maven.project.MailingList;
import org.apache.maven.project.Organization;
import org.apache.maven.project.Model;
import org.apache.maven.project.Project;
import org.apache.maven.project.Repository;
import org.apache.maven.project.Resource;
import org.apache.maven.project.SourceModification;
import org.apache.maven.project.UnitTest;
import org.apache.maven.project.Version;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: JDomProjectUnmarshaller.java,v 1.1 2004/05/09 20:16:15
 */
public class JDomProjectUnmarshaller implements IProjectUnmarshaller {
    private static final Log logger = LogFactory.getLog(JDomProjectUnmarshaller.class);
    /**
     * @deprecated no way to create a reasonable entity resolver.
     */
    public Project parse(Reader reader) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        builder.setExpandEntities(true);
        logger.fatal("deprecated. no way to create a reasonable entity resolver.");
//        builder.setEntityResolver(new EntResolver(file.getParentFile()));
        Document document = builder.build(reader);
        return generateProject(document.getRootElement());
    }
    /**
     * get the Maven's project instance from the file. Please not that it will not include the
     * values from any parent files defined in <extend>
     */
    public Project parse(File file) throws Exception {
        return generateProject(parseRootElement(file));
    }
    
    /**
     * parse the doc of the file passed in. External enitities are expanded.
     */
    public Element parseRootElement(File file) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        builder.setExpandEntities(true);
// TODO mkleint - I wonder if the directory in resolver is based on
//        the current file being parsed or rather the initial project.xml file
//        (extend files inherit the directory from the origin..)
        
        builder.setEntityResolver(new EntResolver(file.getParentFile()));
        Document document = builder.build(file);
        return document.getRootElement();
    }
    
    /**
     * parse the doc from the reader. Please note that expanding entities is not possible this way.
     * Use only if you don't have a physical file. (eg. for project.xmls in jars..)
     */
    public Element parseRootElement(Reader reader) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        builder.setExpandEntities(false);
        Document document = builder.build(reader);
        return document.getRootElement();
    }
    
    public Project generateProject(Element projectElement) {
        Project mavenProject = new Project();
        
        //simple childs
        mavenProject.setExtend(projectElement.getChildText("extend"));
        mavenProject.setPomVersion(projectElement.getChildText("pomVersion"));
        mavenProject.setName(projectElement.getChildText("name"));
        String id = projectElement.getChildText("id");
        String groupId = projectElement.getChildText("groupId");
        String artifactId = projectElement.getChildText("artifactId");
        mavenProject.setGroupId(groupId);
        mavenProject.setArtifactId(artifactId);
        if (id != null) {
            mavenProject.setId(id);
        } else {
            //TODO valid ??
            mavenProject.setId(groupId + ":" + artifactId);
        }
        mavenProject.setCurrentVersion(projectElement.getChildText("currentVersion"));
        mavenProject.setInceptionYear(projectElement.getChildText("inceptionYear"));
        mavenProject.setPackage(projectElement.getChildText("package"));
        mavenProject.setLogo(projectElement.getChildText("logo"));
        mavenProject.setGumpRepositoryId(projectElement.getChildText("gumpRepositoryId"));
        mavenProject.setDescription(projectElement.getChildText("description"));
        mavenProject.setShortDescription(projectElement.getChildText("shortDescription"));
        mavenProject.setUrl(projectElement.getChildText("url"));
        mavenProject.setIssueTrackingUrl(projectElement.getChildText("issueTrackingUrl"));
        mavenProject.setSiteAddress(projectElement.getChildText("siteAddress"));
        mavenProject.setSiteDirectory(projectElement.getChildText("siteDirectory"));
        mavenProject.setDistributionSite(projectElement.getChildText("distributionSite"));
        mavenProject.setDistributionDirectory(projectElement.getChildText("distributionDirectory"));
        
        //complex child elements
        mavenProject.setOrganization(parseOrganization(projectElement));
        mavenProject.setRepository(parseRepository(projectElement));
        //TODO a bug is filed, should be fixed for 1.1
        //mavenProject.setVersions(parseVersions(projectElement));
        mavenProject.setBranches(parseBranches(projectElement));
        mavenProject.setMailingLists(parseMailingLists(projectElement));
        mavenProject.setDevelopers(parseDevelopers(projectElement));
        mavenProject.setContributors(parseContributors(projectElement));
        mavenProject.setLicenses(parseLicenses(projectElement));
        mavenProject.setDependencies(parseDependencies(projectElement));
        mavenProject.setBuild(parseBuild(projectElement));
        mavenProject.setReports(parseReports(projectElement));
        Properties properties = parseProperties(projectElement);
        if ( properties != null && properties.size() > 0 )  {
            mavenProject.setProperties(properties);
            //TODO populateResolvedProperties(mavenProject, properties);
        }
        
        return mavenProject;
    }
    
//    private void populateResolvedProperties(ModelBase obj, Properties properties) {
//        if ( properties != null && properties.size() > 0 ) {
//            for (int i = 0; i < properties.size(); i++) {
//                String[] prop = resolveProperty((String) properties.get(i));
//                obj.resolvedProperties().put(prop[0], prop[1]);
//            }
//        }
//    }
    
    private Organization parseOrganization(Element projectElement) {
        Organization organization = null;
        Element elem = projectElement.getChild("organization");
        if ( elem != null ) {
            organization = new Organization();
            organization.setName(elem.getChildText("name"));
            organization.setUrl(elem.getChildText("url"));
            organization.setLogo(elem.getChildText("logo"));
        }
        return organization;
    }
    
    private List parseVersions(Element projectElement) {
        List versions = null;
        Element versionsElement = projectElement.getChild("versions");
        if ( versionsElement != null ) {
            List versionElements = versionsElement.getChildren("version");
            if ( versionElements != null && versionElements.size() > 0 ) {
                versions = new ArrayList();
                for (int i = 0; i < versionElements.size(); i++) {
                    Element versionElement = (Element) versionElements.get(i);
                    Version version = new Version();
                    version.setId(versionElement.getChildText("id"));
                    version.setName(versionElement.getChildText("name"));
                    version.setTag(versionElement.getChildText("tag"));
                    versions.add(version);
                }
            }
        }
        return versions;
    }
    
    private List parseDevelopers(Element projectElement) {
        List developers = null;
        Element developersElement = projectElement.getChild("developers");
        if ( developersElement != null ) {
            List developerElements = developersElement.getChildren("developer");
            if ( developerElements != null && developerElements.size() > 0 ) {
                developers = new ArrayList();
                for (int i = 0; i < developerElements.size(); i++) {
                    Element developerElement = (Element) developerElements.get(i);
                    Developer developer = new Developer();
                    developer.setName(developerElement.getChildText("name"));
                    developer.setId(developerElement.getChildText("id"));
                    developer.setEmail(developerElement.getChildText("email"));
                    developer.setOrganization(developerElement.getChildText("organization"));
                    List roles = parseRoles(developerElement);
                    if ( roles != null && roles.size() > 0 ) {
                        for (int j = 0; j < roles.size(); j++) {
                            developer.addRole((String) roles.get(j));
                        }
                    }
                    developer.setUrl(developerElement.getChildText("url"));
                    developer.setTimezone(developerElement.getChildText("timezone"));
                    developers.add(developer);
                }
            }
        }
        return developers;
    }
    
    private List parseContributors(Element projectElement) {
        List contributors = null;
        Element contributorsElement = projectElement.getChild("contributors");
        if ( contributorsElement != null ) {
            List contributorElements = contributorsElement.getChildren("contributor");
            if ( contributorElements != null && contributorElements.size() > 0 ) {
                contributors = new ArrayList();
                for (int i = 0; i < contributorElements.size(); i++) {
                    Element contributorElement = (Element) contributorElements.get(i);
                    Contributor contributor = new Contributor();
                    contributor.setName(contributorElement.getChildText("name"));
                    contributor.setEmail(contributorElement.getChildText("email"));
                    contributor.setOrganization(contributorElement.getChildText("organization"));
                    //do contributors really have roles ?
                    List roles = parseRoles(contributorElement);
                    if ( roles != null && roles.size() > 0 ) {
                        for (int j = 0; j < roles.size(); j++) {
                            contributor.addRole((String) roles.get(j));
                        }
                    }
                    contributor.setUrl(contributorElement.getChildText("url"));
                    contributor.setTimezone(contributorElement.getChildText("timezone"));
                    contributors.add(contributor);
                }
            }
        }
        return contributors;
    }
    
    private List parseRoles(Element developerElement) {
        List roles = null;
        Element rolesElement = developerElement.getChild("roles");
        if ( rolesElement != null ) {
            List roleElements = rolesElement.getChildren("role");
            if ( roleElements != null && roleElements.size() > 0 ) {
                roles = new ArrayList();
                for (int i = 0; i < roleElements.size(); i++) {
                    Element roleElement = (Element) roleElements.get(i);
                    String role = roleElement.getText();
                    roles.add(role);
                }
            }
        }
        return roles;
    }
    
    private List parseDependencies(Element projectElement) {
        List dependencies = null;
        Element dependenciesElement = projectElement.getChild("dependencies");
        if ( dependenciesElement != null ) {
            List dependencyElements = dependenciesElement.getChildren("dependency");
            if ( dependencyElements != null && dependencyElements.size() > 0 ) {
                dependencies = new ArrayList();
                for (int i = 0; i < dependencyElements.size(); i++) {
                    Element dependencyElement = (Element) dependencyElements.get(i);
                    Dependency dependency = new Dependency();
                    String id = dependencyElement.getChildText("id");
                    if (id != null) {
                        dependency.setId(id);
                    }
                    id = dependencyElement.getChildText("groupId");
                    if (id != null) {
                        dependency.setGroupId(id);
                    }
                    id = dependencyElement.getChildText("artifactId");
                    if (id != null) {
                        dependency.setArtifactId(id);
                    }
                    resolveDependency(dependency);
                    dependency.setVersion(dependencyElement.getChildText("version"));
                    String jar = dependencyElement.getChildText("jar");
                    if ( jar != null ) {
                        dependency.setJar(dependencyElement.getChildText("jar"));
                    }
                    dependency.setType(dependencyElement.getChildText("type"));
                    dependency.setUrl(dependencyElement.getChildText("url"));
                    Properties properties = parseProperties(dependencyElement);
                    if ( properties != null && properties.size() > 0 ) {
                        dependency.setProperties(properties);
//TODO	                    populateResolvedProperties(dependency, properties);
                    }
                    dependencies.add(dependency);
                }
            }
        }
        return dependencies;
    }
    
    public static void resolveDependency(Dependency dependency) {
        if ( dependency.getId() != null ) {
            if ( dependency.getGroupId() == null ) {
                String id = dependency.getId();
                int j = id.indexOf( ":" );
                
                if ( j > 0 ) {
                    dependency.setGroupId( id.substring( 0, j ) );
                    dependency.setArtifactId( id.substring( j + 1 ) );
                } else {
                    dependency.setGroupId( id );
                    dependency.setArtifactId( id );
                }
            } else if ( dependency.getArtifactId() == null ) {
                dependency.setArtifactId( dependency.getId() );
            }
        } else if ( dependency.getGroupId() == null ) {
            dependency.setGroupId( dependency.getArtifactId() );
        }
        dependency.setId( dependency.getGroupId() + ":" + dependency.getArtifactId() );
    }
    
    private List parseLicenses(Element projectElement) {
        List licenses = null;
        Element licensesElement = projectElement.getChild("licenses");
        if ( licensesElement != null ) {
            List licenseElements = licensesElement.getChildren("license");
            if ( licenseElements != null && licenseElements.size() > 0 ) {
                licenses = new ArrayList();
                for (int i = 0; i < licenseElements.size(); i++) {
                    Element licenseElement = (Element) licenseElements.get(i);
                    License license = new License();
                    license.setName(licenseElement.getChildText("name"));
                    license.setUrl(licenseElement.getChildText("url"));
                    license.setDistribution(licenseElement.getChildText("distribution"));
                    licenses.add(license);
                }
            }
        }
        return licenses;
    }
    
    private List parseReports(Element projectElement) {
        List reports = null;
        Element reportsElement = projectElement.getChild("reports");
        if ( reportsElement != null ) {
            List reportElements = reportsElement.getChildren("report");
            if ( reportElements != null && reportElements.size() > 0 ) {
                reports = new ArrayList();
                for (int i = 0; i < reportElements.size(); i++) {
                    Element reportElement = (Element) reportElements.get(i);
                    String report = reportElement.getText();
                    reports.add(report);
                }
            }
        }
        return reports;
    }
    
    private List parseBranches(Element projectElement) {
        List branches = null;
        Element branchesElement = projectElement.getChild("branches");
        if ( branchesElement != null ) {
            List branchElements = branchesElement.getChildren("branch");
            if ( branchElements != null && branchElements.size() > 0 ) {
                branches = new ArrayList();
                for (int i = 0; i < branchElements.size(); i++) {
                    Element branchElement = (Element) branchElements.get(i);
                    Branch branch = new Branch();
                    branch.setTag(branchElement.getChildText("tag"));
                    branches.add(branch);
                }
            }
        }
        return branches;
    }
    
    private List parseMailingLists(Element projectElement) {
        List mailingLists = null;
        Element mailingListsElement = projectElement.getChild("mailingLists");
        if ( mailingListsElement != null ) {
            List mailingListElements = mailingListsElement.getChildren("mailingList");
            if ( mailingListElements != null && mailingListElements.size() > 0 ) {
                mailingLists = new ArrayList();
                for (int i = 0; i < mailingListElements.size(); i++) {
                    Element mailingListElement = (Element) mailingListElements.get(i);
                    MailingList mailingList = new MailingList();
                    mailingList.setName(mailingListElement.getChildText("name"));
                    mailingList.setSubscribe(mailingListElement.getChildText("subscribe"));
                    mailingList.setUnsubscribe(mailingListElement.getChildText("unsubscribe"));
                    mailingList.setArchive(mailingListElement.getChildText("archive"));
                    mailingLists.add(mailingList);
                }
            }
        }
        return mailingLists;
    }
    
    private Properties parseProperties(Element element) {
        Properties properties = null;
        Element propertiesElement = element.getChild("properties");
        if ( propertiesElement != null ) {
            List propertiesElements = propertiesElement.getChildren();
            if ( propertiesElements != null && propertiesElements.size() > 0 ) {
                properties = new Properties();
                for (int i = 0; i < propertiesElements.size(); i++) {
                    Element propertyElement = (Element) propertiesElements.get(i);
//                    String property = propertyElement.getName() + ":" +
//                            (propertyElement.getText() == null || propertyElement.getText().length() == 0
//                                  ? " " //HACK it seems empty string won't do..mkleint
//                                  : propertyElement.getText());
                    properties.setProperty(propertyElement.getName(), propertyElement.getText() == null || propertyElement.getText().length() == 0
                            ? " " //HACK it seems empty string won't do..mkleint
                            : propertyElement.getText());;
                }
            }
        }
        return properties;
    }
    
    private Repository parseRepository(Element projectElement) {
        Repository repository = null;
        Element elem = projectElement.getChild("repository");
        if ( elem != null ) {
            repository = new Repository();
            repository.setConnection(elem.getChildText("connection"));
            repository.setDeveloperConnection(elem.getChildText("developerConnection"));
            repository.setUrl(elem.getChildText("url"));
        }
        return repository;
    }
    
    private Build parseBuild(Element projectElement) {
        Build build = null;
        Element buildElement = projectElement.getChild("build");
        if ( buildElement != null ) {
            build = new Build();
            build.setNagEmailAddress(buildElement.getChildText("nagEmailAddress"));
            build.setSourceDirectory(buildElement.getChildText("sourceDirectory"));
            build.setSourceModifications(parseSourceModifications(buildElement));
            build.setUnitTestSourceDirectory(buildElement.getChildText("unitTestSourceDirectory"));
            build.setAspectSourceDirectory(buildElement.getChildText("aspectSourceDirectory"));
            build.setUnitTest(parseUnitTest(buildElement));
            build.setResources(parseResources(buildElement));
        }
        return build;
    }
    
    private UnitTest parseUnitTest(Element buildElement) {
        UnitTest unitTest = null;
        Element unitTestElement = buildElement.getChild("unitTest");
        if ( unitTestElement != null ) {
            unitTest = new UnitTest();
            List includes = parseIncludes(unitTestElement);
            if ( includes != null && includes.size() > 0 ) {
                for (int j = 0; j < includes.size(); j++) {
                    unitTest.addInclude((String) includes.get(j));
                }
            }
            List excludes = parseExcludes(unitTestElement);
            if ( excludes != null && excludes.size() > 0 ) {
                for (int j = 0; j < excludes.size(); j++) {
                    unitTest.addExclude((String) excludes.get(j));
                }
            }
            unitTest.setResources(parseResources(unitTestElement));
        }
        return unitTest;
    }
    
    private List parseResources(Element element) {
        List resources = null;
        Element resourcesElement = element.getChild("resources");
        if ( resourcesElement != null ) {
            List resourceElements = resourcesElement.getChildren("resource");
            if ( resourceElements != null && resourceElements.size() > 0 ) {
                resources = new ArrayList();
                for (int i = 0; i < resourceElements.size(); i++) {
                    Element resourceElement = (Element) resourceElements.get(i);
                    Resource resource = new Resource();
                    resource.setDirectory(resourceElement.getChildText("directory"));
                    resource.setTargetPath(resourceElement.getChildText("targetPath"));
                    String filtering = resourceElement.getChildText("filtering");
                    resource.setFiltering("true".equals(filtering) ? true : false);
                    List includes = parseIncludes(resourceElement);
                    if ( includes != null && includes.size() > 0 ) {
                        for (int j = 0; j < includes.size(); j++) {
                            resource.addInclude((String) includes.get(j));
                        }
                    }
                    List excludes = parseExcludes(resourceElement);
                    if ( excludes != null && excludes.size() > 0 ) {
                        for (int j = 0; j < excludes.size(); j++) {
                            resource.addExclude((String) excludes.get(j));
                        }
                    }
                    resources.add(resource);
                }
            }
        }
        return resources;
    }
    
    private List parseSourceModifications(Element buildElement) {
        List sourceModifications = null;
        Element sourceModificationsElement = buildElement.getChild("sourceModifications");
        if ( sourceModificationsElement != null ) {
            List sourceModificationElements = sourceModificationsElement.getChildren("sourceModification");
            if ( sourceModificationElements != null && sourceModificationElements.size() > 0 ) {
                sourceModifications = new ArrayList();
                for (int i = 0; i < sourceModificationElements.size(); i++) {
                    Element sourceModificationElement = (Element) sourceModificationElements.get(i);
                    SourceModification sourceModification = new SourceModification();
                    sourceModification.setClassName(sourceModificationElement.getChildText("className"));
                    List includes = parseIncludes(sourceModificationElement);
                    if ( includes != null && includes.size() > 0 ) {
                        for (int j = 0; j < includes.size(); j++) {
                            sourceModification.addInclude((String) includes.get(j));
                        }
                    }
                    List excludes = parseExcludes(sourceModificationElement);
                    if ( excludes != null && excludes.size() > 0 ) {
                        for (int j = 0; j < excludes.size(); j++) {
                            sourceModification.addExclude((String) excludes.get(j));
                        }
                    }
                    sourceModifications.add(sourceModification);
                }
            }
        }
        return sourceModifications;
    }
    
    private List parseIncludes(Element element) {
        List includes = null;
        Element includesElement = element.getChild("includes");
        if ( includesElement != null ) {
            List includeElements = includesElement.getChildren("include");
            if ( includeElements != null && includeElements.size() > 0 ) {
                includes = new ArrayList();
                for (int i = 0; i < includeElements.size(); i++) {
                    Element includeElement = (Element) includeElements.get(i);
                    String include = includeElement.getText();
                    includes.add(include);
                }
            }
        }
        return includes;
    }
    
    private List parseExcludes(Element element) {
        List excludes = null;
        Element excludesElement = element.getChild("excludes");
        if ( excludesElement != null ) {
            List excludeElements = excludesElement.getChildren("exclude");
            if ( excludeElements != null && excludeElements.size() > 0 ) {
                excludes = new ArrayList();
                for (int i = 0; i < excludeElements.size(); i++) {
                    Element excludeElement = (Element) excludeElements.get(i);
                    String exclude = excludeElement.getText();
                    excludes.add(exclude);
                }
            }
        }
        return excludes;
    }
    
    /**
     * simplistic entity resolver. Might need refinement later.
     */
    private class EntResolver implements EntityResolver {
        private File directory;
        EntResolver(File dir) {
            directory = dir;
        }
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            logger.debug("resolver" + systemId);
            if (systemId.startsWith("file:")) {
                String path = systemId.substring("file:".length()); //NOI18N
                File file = new File(directory, path);
                // kind of bug in jdom.. for file:dependency.ent one gets wrong systemId
                //MEVENIDE-110
                int index = path.indexOf("file:"); //NOI18N
                if (index > -1) {
                    path = path.substring(0, index) + path.substring(index + "file:".length()); //NOI18N
                    file = new File(path);
                }
                logger.debug("path=" + path);
                file = normalizeFile(file);
                if (file.exists()) {
                    return new InputSource(new FileReader(file));
                }
            }
            return new InputSource(systemId);
        }
        
    }
    
    
//-------------------------------------------------------------------------------
//-------------------------------------------------------------------------------
// copied from mevenideutils because I was lazy to move part of the class.
    /**
     * Resolves a string in the Maven kludgy name:value format to an array
     * of strings, guaranteed to be exactly two items in length: [name, value].
     * @param property
     * @return
     */
    private static final String PROPERTY_SEPARATOR = ":";
    private static final String EMPTY_STR = "";
    private static String[] resolveProperty(String property) {
        String[] parts = property.split(PROPERTY_SEPARATOR);
        String name = parts[0];
        String value;
        if (parts.length > 1) {
            value = parts[1];
        } else {
            value = EMPTY_STR;
        }
        return new String[] {name, value};
    }
    
    /**
     * change relative files to absolute, (especially important for the <path>/../..<path> style of file path descriptions.
     * On windows normalized case, on unix don't follow symlinks..
     */
    static File normalizeFile(File file) {
        String osName = System.getProperty("os.name");
        boolean isWindows = osName.startsWith("Wind");
        if (isWindows) {
            // On Windows, best to canonicalize.
            if (file.getParent() != null) {
                try {
                    file = file.getCanonicalFile();
                } catch (IOException e) {
                    logger.warn("getCanonicalFile() on file "+file+" failed. "+ e.toString()); // NOI18N
                    // OK, so at least try to absolutize the path
                    file = file.getAbsoluteFile();
                }
            } else {
                // this is for the drive File.
                file = file.getAbsoluteFile();
            }
        } else {
            // On Unix, do not want to traverse symlinks.
            // what about Mac?
            file = new File(file.toURI().normalize()).getAbsoluteFile();
        }
        return file;
    }
}
