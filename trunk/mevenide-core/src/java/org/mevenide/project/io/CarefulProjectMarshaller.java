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
package org.mevenide.project.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;

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
import org.apache.maven.project.UnitTest;
import org.apache.maven.project.Version;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.Filter;
import org.jdom.input.DefaultJDOMFactory;
import org.jdom.input.JDOMFactory;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * 
 * 
 * 
 */
public class CarefulProjectMarshaller implements IProjectMarshaller {
	
	private static final Log log = LogFactory.getLog(CarefulProjectMarshaller.class);
	//private static final String ENCODING = null;
	//private static final Boolean STANDALONE = null;

    private XMLOutputter outputter;
    private JDOMFactory factory;
    private SAXBuilder builder;
	
	public CarefulProjectMarshaller() throws Exception {
        builder = new SAXBuilder();
        factory = new DefaultJDOMFactory();
        outputter = new XMLOutputter();
        outputter.setIndentSize(4);
        outputter.setIndent(true);
        outputter.setNewlines(true);
//in beta10 only.        outputter.setFormat(Format.getPrettyFormat());
	}

    public void marshall(Writer pom, Project project) throws Exception
    {
        log.debug("do Marshall()");
        Document originalDoc = factory.document(factory.element("project"));
        log.debug("  updating document");
        doUpdateDocument(originalDoc, project);
        log.debug("  saving document");
        saveDocument(pom, originalDoc);
    }
    
    public void marshall(Writer pom, Project project, InputStream source) throws Exception
    {
        log.debug("do Marshall2()");
        Document originalDoc = builder.build(source);
        log.debug("  updating document");
        doUpdateDocument(originalDoc, project);
        log.debug("  saving document");
        saveDocument(pom, originalDoc);
    }
    
    
    
    private void saveDocument(Writer pom, Document doc) throws Exception
    {
        outputter.output(doc, pom);
    }
    
    private void doUpdateDocument(Document document, Project project) throws Exception
    {
        Element root = document.getRootElement();
        if (!"project".equals(root.getName()))
        {
            throw new IOException("not a maven project xml");
        }
        Counter counter = new Counter();
        findAndReplaceSimpleElement(counter, root, "extend", project.getExtend());
        findAndReplaceSimpleElement(counter, root, "pomVersion", project.getPomVersion());
        //REQUIRED
        findAndReplaceSimpleElement(counter, root, "name", project.getName());
        findAndReplaceSimpleElement(counter, root, "artifactId", project.getArtifactId());
        findAndReplaceSimpleElement(counter, root, "groupId", project.getGroupId());
        //REQUIRED
        findAndReplaceSimpleElement(counter, root, "currentVersion", project.getCurrentVersion());
        doUpdateOrganization(counter, root, project.getOrganization());
        findAndReplaceSimpleElement(counter, root, "inceptionYear", project.getInceptionYear());
        findAndReplaceSimpleElement(counter, root, "package", project.getPackage());
        findAndReplaceSimpleElement(counter, root, "logo", project.getLogo());
        findAndReplaceSimpleElement(counter, root, "gumpRepositoryId", project.getGumpRepositoryId());
        findAndReplaceSimpleElement(counter, root, "description", project.getDescription());
        //REQUIRED
        findAndReplaceSimpleElement(counter, root, "shortDescription", project.getShortDescription());
        findAndReplaceSimpleElement(counter, root, "url", project.getUrl());
        findAndReplaceSimpleElement(counter, root, "issueTrackingUrl", project.getIssueTrackingUrl());
        findAndReplaceSimpleElement(counter, root, "siteAddress", project.getSiteAddress());
        findAndReplaceSimpleElement(counter, root, "siteDirectory", project.getSiteDirectory());
        findAndReplaceSimpleElement(counter, root, "distributionSite", project.getDistributionSite());
        findAndReplaceSimpleElement(counter, root, "distributionDirectory", project.getDistributionDirectory());
        doUpdateRepository(counter, root, project.getRepository());
        doUpdateVersions(counter, root, project.getVersions());
        doUpdateBranches(counter, root, project.getBranches());
        doUpdateMailingLists(counter, root, project.getMailingLists());
        doUpdateDevelopers(counter, root, project.getDevelopers());
        doUpdateContributors(counter, root, project.getContributors());
        doUpdateLicenses(counter, root, project.getLicenses());
        doUpdateDependencies(counter, root, project.getDependencies());
        doUpdateBuild(counter, root, project.getBuild());
        doUpdateReports(counter, root, project.getReports());
    }
    
	private void doUpdateOrganization(Counter counter, Element root, Organization org) 
            throws Exception 
    {
        boolean shouldExist = org != null;
        Element orgElem = updateElement(counter, root, "organization", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
    		findAndReplaceSimpleElement(innerCount, orgElem, "name", org.getName());
    		findAndReplaceSimpleElement(innerCount, orgElem, "url", org.getUrl());
    		findAndReplaceSimpleElement(innerCount, orgElem, "logo", org.getLogo());
        }
    }
    
	private void doUpdateRepository(Counter counter, Element root, Repository repos) 
            throws Exception 
    {
        boolean shouldExist = repos != null;
        Element orgElem = updateElement(counter, root, "repository", shouldExist);
        if (shouldExist) {
            Counter innerCounter = new Counter();
    		findAndReplaceSimpleElement(innerCounter, orgElem, "connection", repos.getConnection());
    		findAndReplaceSimpleElement(innerCounter, orgElem, "developerConnection", repos.getDeveloperConnection());
    		findAndReplaceSimpleElement(innerCounter, orgElem, "url", repos.getUrl());
        }
    }    

    
	private void doUpdateVersions(Counter counter, Element root, List versions) 
            throws Exception 
    {
        boolean shouldExist = versions != null && versions.size() > 0;
        Element versionsElem = updateElement(counter, root, "versions", shouldExist);
        if (shouldExist) {
//            List versElemList = versionsElem.getChildren("version");
//            int versElemSize = versElemList == null ? 0 : versElemList.size();
            // usedElems stores a list of Version elements that are either new or overwritten.
            // is used later to get rid of the non-existing ones.
            List usedElems = new ArrayList();
            Iterator it = versions.iterator();
            while (it.hasNext())
            {
                Version version = (Version)it.next();
                String id = version.getId();
                List list = versionsElem.getContent(new SpecificElementFilter("version", "id", id));
                if (list != null && list.size() > 0)
                {
                    if (list.size() > 1)
                    {
                        log.info("filter returned multiple instances, the primary key is not unique - key=" + id);
                        // what to do, we found multiple ones instead of one..
                    } else {
                        Element vElem = (Element)list.get(0);
                        doUpdateSingleVersion(vElem, version);
                        usedElems.add(vElem);
                        log.debug("updating element " + vElem.getChildText("id"));
                    }
                } else {
                    //create a new version element
                    Element vElem = factory.element("version");
                    doUpdateSingleVersion(vElem, version);
                    usedElems.add(vElem);
                    versionsElem.addContent(vElem);
                    log.debug("creating new element " + vElem.getChildText("id"));
                }
            } // end iterator
            removeNonUsedSubelements(versionsElem, "version", usedElems);
        }
    }    
    
    private void doUpdateSingleVersion(Element versionElement, Version version)
    {
        Counter count = new Counter();
        findAndReplaceSimpleElement(count, versionElement, "id", version.getId());
        findAndReplaceSimpleElement(count, versionElement, "name", version.getName());
        findAndReplaceSimpleElement(count, versionElement, "tag", version.getTag());
    }

    
	private void doUpdateBranches(Counter counter, Element root, List branches) 
            throws Exception 
    {
        boolean shouldExist = branches != null && branches.size() > 0;
        Element branchesElem = updateElement(counter, root, "branches", shouldExist);
        if (shouldExist) {
//            List elemList = branchesElem.getChildren("branch");
//            int elemSize = elemList == null ? 0 : elemList.size();
            // usedElems stores a list of Version elements that are either new or overwritten.
            // is used later to get rid of the non-existing ones.
            List usedElems = new ArrayList();
            Iterator it = branches.iterator();
            while (it.hasNext())
            {
                Branch branch = (Branch)it.next();
                String id = branch.getTag();
                List list = branchesElem.getContent(new SpecificElementFilter("branch", "tag", id));
                if (list != null && list.size() > 0)
                {
                    if (list.size() > 1)
                    {
                        log.info("filter returned multiple instances, the primary key is not unique - key=" + id);
                        // what to do, we found multiple ones instead of one..
                    } else {
                        Element vElem = (Element)list.get(0);
                        doUpdateSingleBranch(vElem, branch);
                        usedElems.add(vElem);
                        log.debug("updating element " + vElem.getChildText("tag"));
                    }
                } else {
                    //create a new version element
                    Element vElem = factory.element("branch");
                    doUpdateSingleBranch(vElem, branch);
                    usedElems.add(vElem);
                    branchesElem.addContent(vElem);
                    log.debug("creating new element " + vElem.getChildText("tag"));
                }
            } // end iterator
            removeNonUsedSubelements(branchesElem, "branch", usedElems);
        }
    }    
    
    private void doUpdateSingleBranch(Element branchElement, Branch branch)
    {
        findAndReplaceSimpleElement(new Counter(), branchElement, "tag", branch.getTag());
    }

	private void doUpdateMailingLists(Counter counter, Element root, List mails) 
            throws Exception 
    {
        boolean shouldExist = mails != null && mails.size() > 0;
        Element mailsElem = updateElement(counter, root, "mailingLists", shouldExist);
        if (shouldExist) {
//            List elemList = mailsElem.getChildren("mailingList");
//            int elemSize = elemList == null ? 0 : elemList.size();
            // usedElems stores a list of Version elements that are either new or overwritten.
            // is used later to get rid of the non-existing ones.
            List usedElems = new ArrayList();
            Iterator it = mails.iterator();
            while (it.hasNext())
            {
                MailingList mail = (MailingList)it.next();
                String id = mail.getName();
                List list = mailsElem.getContent(new SpecificElementFilter("mailingList", "name", id));
                if (list != null && list.size() > 0)
                {
                    if (list.size() > 1)
                    {
                        log.info("filter returned multiple instances, the primary key is not unique - key=" + id);
                        // what to do, we found multiple ones instead of one..
                    } else {
                        Element vElem = (Element)list.get(0);
                        doUpdateSingleMailingList(vElem, mail);
                        usedElems.add(vElem);
                        log.debug("updating element " + vElem.getChildText("name"));
                    }
                } else {
                    //create a new version element
                    Element vElem = factory.element("mailingList");
                    doUpdateSingleMailingList(vElem, mail);
                    usedElems.add(vElem);
                    mailsElem.addContent(vElem);
                    log.debug("creating new element " + vElem.getChildText("name"));
                }
            } // end iterator
            removeNonUsedSubelements(mailsElem, "mailingList", usedElems);
        }
    }    
    
    private void doUpdateSingleMailingList(Element mailElement, MailingList mail)
    {
        Counter count = new Counter();
        findAndReplaceSimpleElement(count, mailElement, "name", mail.getName());
        findAndReplaceSimpleElement(count, mailElement, "subscribe", mail.getSubscribe());
        findAndReplaceSimpleElement(count, mailElement, "unsubscribe", mail.getUnsubscribe());
        findAndReplaceSimpleElement(count, mailElement, "archive", mail.getArchive());
    }

	private void doUpdateDevelopers(Counter counter, Element root, List developers) 
            throws Exception 
    {
        boolean shouldExist = developers != null && developers.size() > 0;
        Element developersElem = updateElement(counter, root, "developers", shouldExist);
        if (shouldExist) {
            // usedElems stores a list of Developer elements that are either new or overwritten.
            // is used later to get rid of the non-existing ones.
            List usedElems = new ArrayList();
            Iterator it = developers.iterator();
            while (it.hasNext())
            {
                Developer dev = (Developer)it.next();
                String id = dev.getName();
                List list = developersElem.getContent(new SpecificElementFilter("developer", "name", id));
                if (list != null && list.size() > 0)
                {
                    if (list.size() > 1)
                    {
                        log.info("filter returned multiple instances, the primary key is not unique - key=" + id);
                        // what to do, we found multiple ones instead of one..
                    } else {
                        Element vElem = (Element)list.get(0);
                        doUpdateSingleDeveloper(vElem, dev);
                        usedElems.add(vElem);
                        log.debug("updating element " + vElem.getChildText("name"));
                    }
                } else {
                    //create a new version element
                    Element vElem = factory.element("developer");
                    doUpdateSingleDeveloper(vElem, dev);
                    usedElems.add(vElem);
                    developersElem.addContent(vElem);
                    log.debug("creating new element " + vElem.getChildText("name"));
                }
            } // end iterator
            removeNonUsedSubelements(developersElem, "developer", usedElems);
        }
    }    
    
    private void doUpdateSingleDeveloper(Element devElement, Developer developer)
        throws Exception
    {
        Counter count = new Counter();
        findAndReplaceSimpleElement(count, devElement, "name", developer.getName());
        findAndReplaceSimpleElement(count, devElement, "id", developer.getId());
        findAndReplaceSimpleElement(count, devElement, "email", developer.getEmail());
        findAndReplaceSimpleElement(count, devElement, "organization", developer.getOrganization());
        // roles
        doUpdateRoles(count, devElement, developer.getRoles());
        findAndReplaceSimpleElement(count, devElement, "url", developer.getUrl());
        findAndReplaceSimpleElement(count, devElement, "timezone", developer.getTimezone());
    }

	private void doUpdateContributors(Counter counter, Element root, List contributors) 
            throws Exception 
    {
        boolean shouldExist = contributors != null && contributors.size() > 0;
        Element contributorsElem = updateElement(counter, root, "contributors", shouldExist);
        if (shouldExist) {
            // usedElems stores a list of Developer elements that are either new or overwritten.
            // is used later to get rid of the non-existing ones.
            List usedElems = new ArrayList();
            Iterator it = contributors.iterator();
            while (it.hasNext())
            {
                Contributor cont = (Contributor)it.next();
                String id = cont.getName();
                List list = contributorsElem.getContent(new SpecificElementFilter("contributor", "name", id));
                if (list != null && list.size() > 0)
                {
                    if (list.size() > 1)
                    {
                        log.info("filter returned multiple instances, the primary key is not unique - key=" + id);
                        // what to do, we found multiple ones instead of one..
                    } else {
                        Element vElem = (Element)list.get(0);
                        doUpdateSingleContributor(vElem, cont);
                        usedElems.add(vElem);
                        log.debug("updating element " + vElem.getChildText("name"));
                    }
                } else {
                    //create a new version element
                    Element vElem = factory.element("contributor");
                    doUpdateSingleContributor(vElem, cont);
                    usedElems.add(vElem);
                    contributorsElem.addContent(vElem);
                    log.debug("creating new element " + vElem.getChildText("name"));
                }
            } // end iterator
            removeNonUsedSubelements(contributorsElem, "contributor", usedElems);
        }
    }    
    
    private void doUpdateSingleContributor(Element conElement, Contributor contributor)
        throws Exception
    {
        Counter count = new Counter();
        findAndReplaceSimpleElement(count, conElement, "name", contributor.getName());
        findAndReplaceSimpleElement(count, conElement, "email", contributor.getEmail());
        findAndReplaceSimpleElement(count, conElement, "organization", contributor.getOrganization());
        // roles
        doUpdateRoles(count, conElement, contributor.getRoles());
        findAndReplaceSimpleElement(count, conElement, "url", contributor.getUrl());
        findAndReplaceSimpleElement(count, conElement, "timezone", contributor.getTimezone());
    }

    private void doUpdateRoles(Counter counter, Element root, SortedSet roles) 
            throws Exception 
    {
        boolean shouldExist = roles != null && roles.size() > 0;
        Element rolesElem = updateElement(counter, root, "roles", shouldExist);
        if (shouldExist) {
            // don't do funky stuff here now now..
            // is sortedset, just drop everything in..
            rolesElem.removeChildren();
            Iterator it = roles.iterator();
            while (it.hasNext())
            {
                String roleStr = (String)it.next();
                Element role = factory.element("role");
                role.setText(roleStr);
                rolesElem.addContent(role);
            } // end iterator
        }
    }    
    
	private void doUpdateLicenses(Counter counter, Element root, List licenses) 
            throws Exception 
    {
        boolean shouldExist = licenses != null && licenses.size() > 0;
        Element licensesElem = updateElement(counter, root, "licenses", shouldExist);
        if (shouldExist) {
            // usedElems stores a list of Licence elements that are either new or overwritten.
            // is used later to get rid of the non-existing ones.
            List usedElems = new ArrayList();
            Iterator it = licenses.iterator();
            while (it.hasNext())
            {
                License license = (License)it.next();
                String id = license.getName();
                List list = licensesElem.getContent(new SpecificElementFilter("license", "name", id));
                if (list != null && list.size() > 0)
                {
                    if (list.size() > 1)
                    {
                        log.info("filter returned multiple instances, the primary key is not unique - key=" + id);
                        // what to do, we found multiple ones instead of one..
                    } else {
                        Element vElem = (Element)list.get(0);
                        doUpdateSingleLicense(vElem, license);
                        usedElems.add(vElem);
                        log.debug("updating element " + vElem.getChildText("name"));
                    }
                } else {
                    //create a new version element
                    Element vElem = factory.element("license");
                    doUpdateSingleLicense(vElem, license);
                    usedElems.add(vElem);
                    licensesElem.addContent(vElem);
                    log.debug("creating new element " + vElem.getChildText("name"));
                }
            } // end iterator
            removeNonUsedSubelements(licensesElem, "license", usedElems);
        }
    }    
    
    private void doUpdateSingleLicense(Element licElement, License license)
    {
        Counter count = new Counter();
        findAndReplaceSimpleElement(count, licElement, "name", license.getName());
        findAndReplaceSimpleElement(count, licElement, "url", license.getUrl());
        findAndReplaceSimpleElement(count, licElement, "distribution", license.getDistribution());
        findAndReplaceSimpleElement(count, licElement, "comments", license.getComments());
    }
    
	private void doUpdateDependencies(Counter counter, Element root, List dependencies) 
            throws Exception 
    {
        boolean shouldExist = dependencies != null && dependencies.size() > 0;
        Element dependenciesElem = updateElement(counter, root, "dependencies", shouldExist);
        if (shouldExist) {
            // usedElems stores a list of Dependency elements that are either new or overwritten.
            // is used later to get rid of the non-existing ones.
            List usedElems = new ArrayList();
            Iterator it = dependencies.iterator();
            while (it.hasNext())
            {
                Dependency dep = (Dependency)it.next();
                List list = dependenciesElem.getContent(
                        new DependencyElementFilter(dep.getId(), dep.getGroupId(), dep.getArtifactId()));
                if (list != null && list.size() > 0)
                {
                    if (list.size() > 1)
                    {
                        log.info("filter returned multiple instances, the primary key is not unique - key=" + dep.getId());
                        // what to do, we found multiple ones instead of one..
                    } else {
                        Element vElem = (Element)list.get(0);
                        doUpdateSingleDependency(vElem, dep);
                        usedElems.add(vElem);
                        log.debug("updating element " + vElem.getChildText("artifactId"));
                    }
                } else {
                    //create a new version element
                    Element vElem = factory.element("dependency");
                    doUpdateSingleDependency(vElem, dep);
                    usedElems.add(vElem);
                    dependenciesElem.addContent(vElem);
                    log.debug("creating new element " + vElem.getChildText("artifactId"));
                }
            } // end iterator
            removeNonUsedSubelements(dependenciesElem, "dependency", usedElems);
        }
    }    
    
    private void doUpdateSingleDependency(Element depElement, Dependency dependency)
        throws Exception
    {
//        findAndReplaceSimpleElement(0, depElement, "id", dependency.getId());
        Counter count = new Counter();
        findAndReplaceSimpleElement(count, depElement, "artifactId", dependency.getArtifactId());
        findAndReplaceSimpleElement(count, depElement, "groupId", dependency.getGroupId());
        findAndReplaceSimpleElement(count, depElement, "version", dependency.getVersion());
        findAndReplaceSimpleElement(count, depElement, "jar", dependency.getJar());
        findAndReplaceSimpleElement(count, depElement, "type", dependency.getType());
        findAndReplaceSimpleElement(count, depElement, "url", dependency.getUrl());
        doUpdateProperties(count, depElement, myResolveProperties(dependency.getProperties()));
    }    
    
    private Map myResolveProperties(List propList)
    {
        Map toReturn = new TreeMap();
        if (propList != null) 
        {
            Iterator it = propList.iterator();
            while (it.hasNext())
            {
                String prop = (String)it.next();
                int index = prop.indexOf(':');
                if (index > 0)
                {
                    toReturn.put(prop.substring(0, index - 1), prop.substring(index + 1));
                } else {
                    toReturn.put(prop, null);
                }
            }
        }
        return toReturn;
    }
    
    private void doUpdateProperties(Counter counter, Element parent, Map props)
    {
        boolean shouldExist = props != null && props.size() > 0;
        Element propsElem = updateElement(counter, parent, "properties", shouldExist);
        if (shouldExist) {
            // usedElems stores a list of Properties elements that are either new or overwritten.
            // is used later to get rid of the non-existing ones.
            List usedElems = new ArrayList();
            Iterator it = props.keySet().iterator();
            System.out.println("--------------------- properties.. --------------------");
            while (it.hasNext())
            {
                String key = (String)it.next();
                System.out.println("key=" + key);
                Element propEl = propsElem.getChild(key);
                if (propEl != null)
                {
                    propEl.setText((String)props.get(key));
                    usedElems.add(propEl);
                    System.out.println("updating property element " + key);
                } else {
                    //create a new version element
                    propEl = factory.element(key);
                    propEl.setText((String)props.get(key));
                    usedElems.add(propEl);
                    propsElem.addContent(propEl);
                    System.out.println("creating new property element " + key);
                }
            } // end iterator
            it = propsElem.getChildren().iterator();
            while (it.hasNext())
            {
                Element el = (Element)it.next();
                if (!usedElems.contains(el)) {
                    System.out.println("removing element=" + el);
                    it.remove();
                }
            }
        }
    }
    
	private void doUpdateBuild(Counter counter, Element root, Build build) 
            throws Exception 
    {
        boolean shouldExist = build != null;
        Element buildElem = updateElement(counter, root, "build", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
    		findAndReplaceSimpleElement(innerCount, buildElem, "nagEmailAddress", build.getNagEmailAddress());
    		findAndReplaceSimpleElement(innerCount, buildElem, "sourceDirectory", build.getSourceDirectory());
            //doUpdateSourceModifications(innerCount, buildElem, build.getSourceModifications());
    		findAndReplaceSimpleElement(innerCount, buildElem, "unitTestSourceDirectory", build.getUnitTestSourceDirectory());
    		findAndReplaceSimpleElement(innerCount, buildElem, "integrationUnitTestSourceDirectory", build.getIntegrationUnitTestSourceDirectory());
    		findAndReplaceSimpleElement(innerCount, buildElem, "aspectSourceDirectory", build.getAspectSourceDirectory());
            doUpdateUnitTest(innerCount, buildElem, build.getUnitTest());
            doUpdateResources(innerCount, buildElem, build.getResources());
        }
    }
    
	private void doUpdateUnitTest(Counter counter, Element parent, UnitTest test) 
            throws Exception 
    {
        boolean shouldExist = test != null;
        Element testElem = updateElement(counter, parent, "unitTest", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            doUpdateExIncludes(innerCount, testElem, test.getIncludes(), "includes", "include");
            doUpdateExIncludes(innerCount, testElem, test.getExcludes(), "excludes", "exclude");
            doUpdateResources(innerCount, testElem, test.getResources());
        }
    }

    /**
     * updates resources, however the replacement algorithm here is not ideal,
     * no real primary id of simgle elements available..
     */
	private void doUpdateResources(Counter counter, Element root, List resources) 
            throws Exception 
    {
        boolean shouldExist = resources != null && resources.size() > 0;
        Element resourcesElem = updateElement(counter, root, "resources", shouldExist);
        if (shouldExist) {
            List newElems = new ArrayList(resources);
            Iterator it = resourcesElem.getChildren("resource").iterator();
            Iterator it2 = newElems.iterator();
            while (it.hasNext() && it2.hasNext())
            {
                Element el = (Element)it.next();
                Resource res = (Resource)it2.next();
                doUpdateSingleResource(el, res);
                log.debug("updating element " + el.getChildText("directory"));
            } // end iterator
            while (it.hasNext())
            {
                // when some resources are obsolete, remove them..
                it.next();
                it.remove();
            }
            while (it2.hasNext())
            {
                Resource res = (Resource)it2.next();
                Element newEl = factory.element("resource");
                doUpdateSingleResource(newEl, res);
                resourcesElem.addContent(newEl);
            }
        }
    }    

    private void doUpdateSingleResource(Element resElem, Resource resource)
            throws Exception
    {
        Counter innerCount = new Counter();
  		findAndReplaceSimpleElement(innerCount, resElem, "directory", resource.getDirectory());
  		findAndReplaceSimpleElement(innerCount, resElem, "targetPath", resource.getTargetPath());
        doUpdateExIncludes(innerCount, resElem, resource.getIncludes(), "includes", "include");
        doUpdateExIncludes(innerCount, resElem, resource.getExcludes(), "excludes", "exclude");
  		findAndReplaceSimpleElement(innerCount, resElem, "filtering", "" + resource.getFiltering());
    }
    
    
    private void doUpdateReports(Counter counter, Element parent, List reports)
    {
        boolean shouldExist = reports != null && reports.size() > 0;
        Element reportsElem = updateElement(counter, parent, "reports", shouldExist);
        if (shouldExist) {
            List newList = new ArrayList(reports);
            List liveList = parent.getChildren("report");
            Iterator it = liveList.iterator();
            // first remove the old ones that are not in the new list..
            while (it.hasNext())
            {
                Element el = (Element)it.next();
                String report = el.getText();
                int index = newList.indexOf(report);
                if (index < 0)
                {
                    it.remove();
                } else {
                    newList.remove(index);
                }
            }
            // now add everything that is not included.
            if (newList.size() > 0) 
            {
                Iterator it2 = newList.iterator();
                while (it2.hasNext())
                {
                    String rep = (String)it2.next();
                    Element newReport = factory.element("report");
                    newReport.setText(rep);
                    parent.addContent(newReport);
                }
            }
        }
    }

	private void doUpdateExIncludes(Counter counter, Element parent, List cludes, 
                                    String motherElementName, String childElementName) 
            throws Exception 
    {
        boolean shouldExist = cludes != null && cludes.size() > 0;
        Element cludeElem = updateElement(counter, parent, motherElementName, shouldExist);
        if (shouldExist) {
            // usedElems stores a list of Properties elements that are either new or overwritten.
            // is used later to get rid of the non-existing ones.
            List usedElems = new ArrayList();
            List newOnes = new ArrayList(cludes);
            Iterator it = cludeElem.getChildren(childElementName).iterator();
            while (it.hasNext())
            {
                Element el = (Element)it.next();
                int index = newOnes.indexOf(el.getText());
                if (index < 0)
                {
                    // not there anymore
                    it.remove();
                } else {
                    newOnes.remove(index);
                }
            } // end iterator
            if (newOnes.size() > 0)
            {
                // now add the new ones to the end..
                it = newOnes.iterator();
                while (it.hasNext())
                {
                    String ns = (String)it.next();
                    Element newEl = factory.element(childElementName);
                    newEl.setText(ns);
                    cludeElem.addContent(newEl);
                }
            }
        }
    }
    
//-----------------------------------------------------------------------------------
// utility methods
//-----------------------------------------------------------------------------------
    
    /**
     * will attempt to find the element with "name: in the model, if it exists, will update it, 
     * otherwise will create a new element and place it as prefferedLocation
     */
    private Element findAndReplaceSimpleElement(Counter counter, Element parent, String name, String text)
    {
        boolean shouldExist = text != null && text.trim().length() > 0;
        Element element = updateElement(counter, parent, name, shouldExist);
        if (shouldExist)
        {
            element.setText(text);
        }
        return element;
    }

    /**
     * will try to find a specified element, if it exists and it should not, will remove it.
     *if it doens't exists and it should, will create it.
     */
    private Element updateElement(Counter counter, Element parent, String name, boolean shouldExist)
    {
        Element element =  parent.getChild(name);
        if (element != null && shouldExist)
        {
            if (parent.getChildren().indexOf(element) <= counter.getCurrentIndex())
            {
                counter.increaseCount();
            }
            // don't increase count of preffered locations when the current location of the item is bigger then the preffered one..
            // dunnot if that is good algorithm
        }
        if (element == null && shouldExist)
        {
            element = factory.element(name);
//            log.debug("creating non-existing element " + name);
            insertAtPrefferedLocation(parent, element, counter);
            counter.increaseCount();
        } 
        if (!shouldExist && element != null)
        {
            //remove existing element that is no longer defined.
            boolean removed = parent.removeChild(name);
//            log.debug("removed element " + name + " " + removed);
        } 
        return element;
    }

    
    private void insertAtPrefferedLocation(Element parent, Element child, Counter counter)
    {
        List children = parent.getChildren();
        int count = children.size();
        if (counter.getCurrentIndex() >= count)
        {
//            log.debug("appending element");
            parent.addContent(child);
        } else
        {
            // find the element at the preffered location for this one, and add my element right after it.
            Element currElement = (Element)children.get(counter.getCurrentIndex());
//            List content = parent.getContent();
//            int index = content.indexOf(currElement);
            log.debug("inserting " + child.getName() + " with pref loc=" + counter.getCurrentIndex());
            //                content.add(index, element);
            //                parent.setContent(content);
//            parent.addContent(child);
            children.add(counter.getCurrentIndex(), child);
        }
    }

    /**
     * will remove any subelements of given type that are not included in the preserveList
     */
    private void removeNonUsedSubelements(Element parent, String subElemName, List preserveList)
    {
        // now we have to remove the version elements that are not used anymore..
        List currList;
        if (subElemName != null)
        {
            currList = parent.getChildren(subElemName);
        } else {
            currList = parent.getChildren();
        }
        // just a sanity operation, don't want the list to change..
        currList = new ArrayList(currList);
        Iterator it = currList.iterator();
        while (it.hasNext())
        {
            Element el = (Element)it.next();
            if (!preserveList.contains(el))
            {
                parent.removeContent(el);
                log.debug("removing not used element " + el);
            }
        }
    }

    /**
     * 
     */
    private static class SpecificElementFilter implements Filter
    {
        private String elemName;
        private String keyElem;
        private String keyVal;
        
        public SpecificElementFilter(String elementName, String keyElement, String keyValue)
        {
            elemName = elementName;
            keyElem = keyElement;
            keyVal = keyValue;
        }
        
        /**
         * not raally implemented.. is removed in beta9 anyway
         */
        public boolean canRemove(Object obj)
        {
            return true;
        }
        /**
         * not raally implemented.. is removed in beta9 anyway
         */
        public boolean canAdd(Object obj)
        {
            return true;
        }
        
        public boolean matches(Object obj)
        {
            if (obj instanceof Element)
            {
                Element elem = (Element)obj;
                if (elem.getName().equals(elemName)) 
                {
                    String key = elem.getChildText(keyElem);
                    if (key != null && key.equals(keyVal)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    /**
     * 
     */
    private static class DependencyElementFilter implements Filter
    {
        private String id;
        private String artifactId;
        private String groupId;
        
        public DependencyElementFilter(String id, String artifactId, String groupId)
        {
            this.id = id;
            this.artifactId = artifactId;
            this.groupId = groupId;
        }
        
        /**
         * not raally implemented.. is removed in beta9 anyway
         */
        public boolean canRemove(Object obj)
        {
            return true;
        }
        /**
         * not raally implemented.. is removed in beta9 anyway
         */
        public boolean canAdd(Object obj)
        {
            return true;
        }
        
        public boolean matches(Object obj)
        {
            if (obj instanceof Element)
            {
                Element elem = (Element)obj;
                if (elem.getName().equals("dependency")) 
                {
                    String elId = elem.getChildText("id");
                    String elGroupId = elem.getChildText("groupId");
                    String elArtifactId = elem.getChildText("artifactId");
                    
                    //HACK not sure if these conditions produce unique identification
                    if (elId != null && elId.equals(id)) {
                        return true;
                    }
                    if (elGroupId != null && elArtifactId != null &&
                        elGroupId.equals(groupId) && elArtifactId.equals(artifactId))
                    {
                        return true;
                    }
                }
            }
            return false;
        }
    }    
    
    private static class Counter 
    {
        int currentIndex = 0;
        public Counter()
        {
        }
        
        public void increaseCount()
        {
            currentIndex = currentIndex + 1;
        }
        
        public int getCurrentIndex()
        {
            return currentIndex;
        }
    }
}
