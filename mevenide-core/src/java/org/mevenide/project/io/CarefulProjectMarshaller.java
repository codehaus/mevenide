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
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMFactory;
import org.jdom.filter.ElementFilter;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mevenide.util.ProjectUtils;

/**
 * a pom marshaller that attempts to preserve formatting and add items to the correct positions.
 * 
 * @author  Milos Kleint (ca206216@tiscali.cz)
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 */
public class CarefulProjectMarshaller implements IProjectMarshaller {
    
    private static final Log log = LogFactory.getLog(CarefulProjectMarshaller.class);

    private XMLOutputter outputter;
    private JDOMFactory factory;
    private SAXBuilder builder;
	
    public CarefulProjectMarshaller() /*throws Exception */{
        this(Format.getPrettyFormat()
                .setIndent("    ")
                .setLineSeparator(System.getProperty("line.separator")));
    }
    
    public CarefulProjectMarshaller(Format format) {
        builder = new SAXBuilder();
        factory = new DefaultJDOMFactory();
        outputter = new XMLOutputter();
        outputter.setFormat(format);
    }

    public void marshall(Writer pom, Project project) throws Exception {
        log.debug("do Marshall()");
        Document originalDoc = factory.document(factory.element("project"));
        log.debug("  updating document");
        doUpdateDocument(originalDoc, new BeanContentProvider(project));
        log.debug("  saving document");
        saveDocument(pom, originalDoc);
    }
    
    public void marshall(Writer pom, Project project, InputStream source) throws Exception {
        log.debug("do Marshall2()");
        Document originalDoc = builder.build(source);
        log.debug("  updating document");
        doUpdateDocument(originalDoc, new BeanContentProvider(project));
        log.debug("  saving document");
        saveDocument(pom, originalDoc);
    }

    public void marshall(Writer pom, IContentProvider provider) throws Exception {
        log.debug("do Marshall()");
        Document originalDoc = factory.document(factory.element("project"));
        log.debug("  updating document");
        doUpdateDocument(originalDoc, provider);
        log.debug("  saving document");
        saveDocument(pom, originalDoc);
    }
    
    public void marshall(Writer pom, IContentProvider provider, Document originalDoc) throws Exception {
        log.debug("do Marshall2()");
        log.debug("  updating document");
        doUpdateDocument(originalDoc, provider);
        log.debug("  saving document");
        saveDocument(pom, originalDoc);
    }    
    
    
    private void saveDocument(Writer pom, Document doc) throws Exception {
        outputter.output(doc, pom);
    }
    
    private void doUpdateDocument(Document document, IContentProvider project) throws Exception {
        Element root = document.getRootElement();
        if (!"project".equals(root.getName()))
        {
            throw new IOException("not a maven project xml");
        }
        Counter counter = new Counter();
        findAndReplaceSimpleElement(counter, root, "extend", project.getValue("extend"));
        findAndReplaceSimpleElement(counter, root, "pomVersion", project.getValue("pomVersion"));
        //REQUIRED
        findAndReplaceSimpleElement(counter, root, "artifactId", project.getValue("artifactId"));
        findAndReplaceSimpleElement(counter, root, "name", project.getValue("name"));
        
        if ( project instanceof BeanContentProvider && ((BeanContentProvider) project).getBean() instanceof Project ) {
            Project mavenProject = (Project) ((BeanContentProvider) project).getBean(); 
            findAndReplaceSimpleElement(counter, root, "groupId", ProjectUtils.getGroupId(mavenProject));
        }
        else {
            findAndReplaceSimpleElement(counter, root, "groupId", project.getValue("groupId"));
        }
        //REQUIRED
        findAndReplaceSimpleElement(counter, root, "currentVersion", project.getValue("currentVersion"));
        doUpdateOrganization(counter, root, project.getSubContentProvider("organization"));
        findAndReplaceSimpleElement(counter, root, "inceptionYear", project.getValue("inceptionYear"));
        findAndReplaceSimpleElement(counter, root, "package", project.getValue("package"));
        findAndReplaceSimpleElement(counter, root, "logo", project.getValue("logo"));
        findAndReplaceSimpleElement(counter, root, "gumpRepositoryId", project.getValue("gumpRepositoryId"));
        findAndReplaceSimpleElement(counter, root, "description", project.getValue("description"));
        //REQUIRED
        findAndReplaceSimpleElement(counter, root, "shortDescription", project.getValue("shortDescription"));
        findAndReplaceSimpleElement(counter, root, "url", project.getValue("url"));
        findAndReplaceSimpleElement(counter, root, "issueTrackingUrl", project.getValue("issueTrackingUrl"));
        findAndReplaceSimpleElement(counter, root, "siteAddress", project.getValue("siteAddress"));
        findAndReplaceSimpleElement(counter, root, "siteDirectory", project.getValue("siteDirectory"));
        findAndReplaceSimpleElement(counter, root, "distributionSite", project.getValue("distributionSite"));
        findAndReplaceSimpleElement(counter, root, "distributionDirectory", project.getValue("distributionDirectory"));
        doUpdateRepository(counter, root, project.getSubContentProvider("repository"));
        doUpdateChildren(counter, root, project.getSubContentProviderList("versions", "version"), "versions", "version", "id");
        doUpdateChildren(counter, root, project.getSubContentProviderList("branches", "branch"), "branches", "branch", "tag");
        doUpdateChildren(counter, root, project.getSubContentProviderList("mailingLists", "mailingList"), "mailingLists", "mailingList", "name");
        doUpdateChildren(counter, root, project.getSubContentProviderList("developers", "developer"), "developers", "developer", "name");
        doUpdateChildren(counter, root, project.getSubContentProviderList("contributors", "contributor"), "contributors", "contributor", "name");
        doUpdateChildren(counter, root, project.getSubContentProviderList("licenses", "license"), "licenses", "license", "name");
        doUpdateDependencies(counter, root, project.getSubContentProviderList("dependencies", "dependency"));
        doUpdateBuild(counter, root, project.getSubContentProvider("build"));
        doUpdateSimpleChildren(counter, root, project.getValueList("reports", "report"), "reports", "report");
    }
    
    private void doUpdateOrganization(Counter counter, Element root, IContentProvider org) 
            throws Exception {
        boolean shouldExist = org != null;
        Element orgElem = updateElement(counter, root, "organization", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
    	    findAndReplaceSimpleElement(innerCount, orgElem, "name", org.getValue("name"));
    	    findAndReplaceSimpleElement(innerCount, orgElem, "url", org.getValue("url"));
            findAndReplaceSimpleElement(innerCount, orgElem, "logo", org.getValue("logo"));
        }
    }
    
    private void doUpdateRepository(Counter counter, Element root, IContentProvider repos) 
            throws Exception {
        boolean shouldExist = repos != null;
        Element orgElem = updateElement(counter, root, "repository", shouldExist);
        if (shouldExist) {
            Counter innerCounter = new Counter();
            findAndReplaceSimpleElement(innerCounter, orgElem, "connection", repos.getValue("connection"));
            findAndReplaceSimpleElement(innerCounter, orgElem, "developerConnection", repos.getValue("developerConnection"));
            findAndReplaceSimpleElement(innerCounter, orgElem, "url", repos.getValue("url"));
        }
    }    

    private void doUpdateChildren(Counter counter, Element root, List newValues, String parentName, String childName, String childKey) throws Exception {
        boolean shouldExist = newValues != null && !newValues.isEmpty();
        Element parentElement = updateElement(counter, root, parentName, shouldExist);
        if (shouldExist) {
            // currentChildren provides a filtered view into the actual data.
            // JDOM automatically adjusts the insertion point to account for
            // white space, comments, and elements that do not match the filter.
            List currentChildren = parentElement.getContent(new ElementFilter(childName));

            // usedElems stores a list of Version elements that are either new or overwritten.
            // is used later to get rid of the non-existing ones.
            List usedElems = new ArrayList();

            for (int i = 0; i < newValues.size(); ++i) {
                IContentProvider newValue = (IContentProvider)newValues.get(i);

                String id = newValue.getValue(childKey);
                List list = parentElement.getContent(new SpecificElementFilter(childName, childKey, id));
                if (list != null && !list.isEmpty()) {
                    if (list.size() > 1) {
                        log.info("filter returned multiple instances, the primary key is not unique - key=" + id);
                        // what to do, we found multiple ones instead of one..
                    } else {
                        Element child = (Element)list.get(0);

                        // move to new position
                        int pos = currentChildren.indexOf(child);
                        if (pos != i) {
                           currentChildren.remove(pos);
                           currentChildren.add(i, child);
                        }

                        doUpdateChild(child, newValue);
                        usedElems.add(child);
                        log.debug("updating element " + child.getChildText(childKey));
                    }
                } else {
                    //create a new version element
                    Element child = factory.element(childName);
                    doUpdateChild(child, newValue);
                    usedElems.add(child);
                    currentChildren.add(i, child);
                    log.debug("creating new element " + child.getChildText(childKey));
                }
            } // end iterator
            removeNonUsedSubelements(parentElement, childName, usedElems);
        }
    }

    private void doUpdateChild(Element child, IContentProvider newValue) throws Exception {
        if ("version".equals(child.getName())) { doUpdateSingleVersion(child, newValue); }
        else if ("branch".equals(child.getName())) { doUpdateSingleBranch(child, newValue); }
        else if ("mailingList".equals(child.getName())) { doUpdateSingleMailingList(child, newValue); }
        else if ("developer".equals(child.getName())) { doUpdateSingleDeveloper(child, newValue); }
        else if ("contributor".equals(child.getName())) { doUpdateSingleContributor(child, newValue); }
        else if ("license".equals(child.getName())) { doUpdateSingleLicense(child, newValue); }
    }    

    private void doUpdateSingleVersion(Element versionElement, IContentProvider version) {
        Counter count = new Counter();
        findAndReplaceSimpleElement(count, versionElement, "id", version.getValue("id"));
        findAndReplaceSimpleElement(count, versionElement, "name", version.getValue("name"));
        findAndReplaceSimpleElement(count, versionElement, "tag", version.getValue("tag"));
    }

    
    private void doUpdateSingleBranch(Element branchElement, IContentProvider branch) {
        findAndReplaceSimpleElement(new Counter(), branchElement, "tag", branch.getValue("tag"));
    }

    private void doUpdateSingleMailingList(Element mailElement, IContentProvider mail) {
        Counter count = new Counter();
        findAndReplaceSimpleElement(count, mailElement, "name", mail.getValue("name"));
        findAndReplaceSimpleElement(count, mailElement, "subscribe", mail.getValue("subscribe"));
        findAndReplaceSimpleElement(count, mailElement, "unsubscribe", mail.getValue("unsubscribe"));
        findAndReplaceSimpleElement(count, mailElement, "archive", mail.getValue("archive"));
    }

    private void doUpdateSingleDeveloper(Element devElement, IContentProvider developer)
        throws Exception {
        Counter count = new Counter();
        findAndReplaceSimpleElement(count, devElement, "name", developer.getValue("name"));
        findAndReplaceSimpleElement(count, devElement, "id", developer.getValue("id"));
        findAndReplaceSimpleElement(count, devElement, "email", developer.getValue("email"));
        findAndReplaceSimpleElement(count, devElement, "organization", developer.getValue("organization"));
        // roles
        // FIXME: cannot call this until IContentProvider supports Set
//        doUpdateRoles(count, devElement, developer.getValueList("roles", "role"));
        findAndReplaceSimpleElement(count, devElement, "url", developer.getValue("url"));
        findAndReplaceSimpleElement(count, devElement, "timezone", developer.getValue("timezone"));
    }

    private void doUpdateSingleContributor(Element conElement, IContentProvider contributor)
        throws Exception {
        Counter count = new Counter();
        findAndReplaceSimpleElement(count, conElement, "name", contributor.getValue("name"));
        findAndReplaceSimpleElement(count, conElement, "email", contributor.getValue("email"));
        findAndReplaceSimpleElement(count, conElement, "organization", contributor.getValue("organization"));
        // roles
        doUpdateRoles(count, conElement, contributor.getValueList("roles", "role"));
        findAndReplaceSimpleElement(count, conElement, "url", contributor.getValue("url"));
        findAndReplaceSimpleElement(count, conElement, "timezone", contributor.getValue("timezone"));
    }

    private void doUpdateRoles(Counter counter, Element root, Collection roles) throws Exception {
        boolean shouldExist = roles != null && !roles.isEmpty();
        Element rolesElem = updateElement(counter, root, "roles", shouldExist);
        if (shouldExist) {
            // don't do funky stuff here now now..
            // is sortedset, just drop everything in..
            rolesElem.removeContent();
            Iterator it = roles.iterator();
            while (it.hasNext()) {
                String roleStr = (String)it.next();
                Element role = factory.element("role");
                role.setText(roleStr);
                rolesElem.addContent(role);
            } // end iterator
        } 
    }    
    
    private void doUpdateSingleLicense(Element licElement, IContentProvider license) {
        Counter count = new Counter();
        findAndReplaceSimpleElement(count, licElement, "name", license.getValue("name"));
        findAndReplaceSimpleElement(count, licElement, "url", license.getValue("url"));
        findAndReplaceSimpleElement(count, licElement, "distribution", license.getValue("distribution"));
        findAndReplaceSimpleElement(count, licElement, "comments", license.getValue("comments"));
    }
    
    private void doUpdateDependencies(Counter counter, Element root, List dependencies) 
            throws Exception {
        boolean shouldExist = dependencies != null && !dependencies.isEmpty();
        Element dependenciesElem = updateElement(counter, root, "dependencies", shouldExist);
        if (shouldExist) {
           // currentChildren provides a filtered view into the actual data.
           // JDOM automatically adjusts the insertion point to account for
           // white space, comments, and elements that do not match the filter.
           List currentChildren = dependenciesElem.getContent(new ElementFilter("dependency"));

            // usedElems stores a list of Dependency elements that are either new or overwritten.
            // is used later to get rid of the non-existing ones.
            List usedElems = new ArrayList();

            for (int i = 0; i < dependencies.size(); ++i) {
                IContentProvider dep = (IContentProvider)dependencies.get(i);

                List list = dependenciesElem.getContent(
                        new DependencyElementFilter(dep.getValue("id"), dep.getValue("artifactId"), dep.getValue("groupId"), dep.getValue("type")));
                if (list != null && !list.isEmpty()) {
                    if (list.size() > 1) {
                        log.info("filter returned multiple instances, the primary key is not unique - key=" + dep.getValue("id"));
                        // what to do, we found multiple ones instead of one..
                    } else {
                        Element vElem = (Element)list.get(0);

                        // move to new position
                        int pos = currentChildren.indexOf(vElem);
                        if (pos != i) {
                           currentChildren.remove(pos);
                           currentChildren.add(i, vElem);
                        }

                        doUpdateSingleDependency(vElem, dep);
                        usedElems.add(vElem);
                        log.debug("updating element " + vElem.getChildText("artifactId"));
                    }
                } else {
                    //create a new version element
                    Element vElem = factory.element("dependency");
                    doUpdateSingleDependency(vElem, dep);
                    usedElems.add(vElem);
                    currentChildren.add(i, vElem);
                    log.debug("creating new element " + vElem.getChildText("artifactId"));
                }
            } // end iterator
            removeNonUsedSubelements(dependenciesElem, "dependency", usedElems);
        }
    }    
    
    private void doUpdateSingleDependency(Element depElement, IContentProvider dependency)
        throws Exception {
//        findAndReplaceSimpleElement(0, depElement, "id", dependency.getId());
        Counter count = new Counter();
        findAndReplaceSimpleElement(count, depElement, "groupId", dependency.getValue("groupId"));
        findAndReplaceSimpleElement(count, depElement, "artifactId", dependency.getValue("artifactId"));
        findAndReplaceSimpleElement(count, depElement, "version", dependency.getValue("version"));
        findAndReplaceSimpleElement(count, depElement, "jar", dependency.getValue("jar"));
        findAndReplaceSimpleElement(count, depElement, "type", dependency.getValue("type"));
        findAndReplaceSimpleElement(count, depElement, "url", dependency.getValue("url"));
        doUpdateProperties(count, depElement, myResolveProperties(dependency.getProperties()));
    }    
    
    private Map myResolveProperties(List propList) {
        Map toReturn = new TreeMap();
        if (propList != null) {
            Iterator it = propList.iterator();
            while (it.hasNext()) {
                String prop = (String)it.next();
                int index = prop.indexOf(':');
                if (index > 0) {
                    toReturn.put(prop.substring(0, index), prop.substring(index + 1));
                } else {
                    toReturn.put(prop, null);
                }
            }
        }
        return toReturn;
    }
    
    private void doUpdateProperties(Counter counter, Element parent, Map props) {
        boolean shouldExist = props != null && !props.isEmpty();
        Element propsElem = updateElement(counter, parent, "properties", shouldExist);
        if (shouldExist) {
            // usedElems stores a list of Properties elements that are either new or overwritten.
            // is used later to get rid of the non-existing ones.
            List usedElems = new ArrayList();

            Iterator it = props.keySet().iterator();
            while (it.hasNext()) {
                String key = (String)it.next();

                Element propEl = propsElem.getChild(key);
                if (propEl != null) {
                    propEl.setText((String)props.get(key));
                    usedElems.add(propEl);
                } else {
                    //create a new version element
                    propEl = factory.element(key);
                    propEl.setText((String)props.get(key));
                    usedElems.add(propEl);
                    propsElem.addContent(propEl);
                }
            } // end iterator
            it = propsElem.getChildren().iterator();
            while (it.hasNext()) {
                Element el = (Element)it.next();
                if (!usedElems.contains(el)) {
                    it.remove();
                }
            }
        }
    }
    
    private void doUpdateBuild(Counter counter, Element root, IContentProvider build) 
            throws Exception {
        boolean shouldExist = build != null;
        Element buildElem = updateElement(counter, root, "build", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, buildElem, "nagEmailAddress", build.getValue("nagEmailAddress"));
            findAndReplaceSimpleElement(innerCount, buildElem, "sourceDirectory", build.getValue("sourceDirectory"));
            //doUpdateSourceModifications(innerCount, buildElem, build.getSourceModifications());
            findAndReplaceSimpleElement(innerCount, buildElem, "unitTestSourceDirectory", build.getValue("unitTestSourceDirectory"));
            findAndReplaceSimpleElement(innerCount, buildElem, "integrationUnitTestSourceDirectory", build.getValue("integrationUnitTestSourceDirectory"));
            findAndReplaceSimpleElement(innerCount, buildElem, "aspectSourceDirectory", build.getValue("aspectSourceDirectory"));
            doUpdateUnitTest(innerCount, buildElem, build.getSubContentProvider("unitTest"));
            doUpdateResources(innerCount, buildElem, build.getSubContentProviderList("resources", "resource"));
            doUpdateSourceModifications(innerCount, buildElem, build.getSubContentProviderList("sourceModifications", "sourceModification"));
        }
    }
    
    private void doUpdateUnitTest(Counter counter, Element parent, IContentProvider test) 
            throws Exception {
        boolean shouldExist = test != null;
        Element testElem = updateElement(counter, parent, "unitTest", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            doUpdateSimpleChildren(innerCount, testElem, test.getValueList("includes", "include"), "includes", "include");
            doUpdateSimpleChildren(innerCount, testElem, test.getValueList("excludes", "exclude"), "excludes", "exclude");
            doUpdateResources(innerCount, testElem, test.getSubContentProviderList("resources", "resource"));
        }
    }

    /**
     * updates resources, however the replacement algorithm here is not ideal,
     * no real primary id of single elements available..
     */
    private void doUpdateResources(Counter counter, Element root, List resources) throws Exception {
        boolean shouldExist = resources != null && !resources.isEmpty();
        Element resourcesElem = updateElement(counter, root, "resources", shouldExist);
        if (shouldExist) {
            // children provides a filtered view into the actual data.
            // JDOM automatically adjusts the insertion point to account for
            // white space, comments, and elements that do not match the filter.
            List children = resourcesElem.getContent(new ElementFilter("resource"));

            Iterator tgtIterator = children.iterator();
            Iterator srcIterator = resources.iterator();

            while (tgtIterator.hasNext() && srcIterator.hasNext()) {
                Element el = (Element)tgtIterator.next();
                IContentProvider res = (IContentProvider)srcIterator.next();
                doUpdateSingleResource(el, res);
                log.debug("updating element " + el.getChildText("directory"));
            } // end iterator

            while (tgtIterator.hasNext()) {
                // when some items are obsolete, remove them..
                tgtIterator.next();
                tgtIterator.remove();
            }

            while (srcIterator.hasNext()) {
                IContentProvider res = (IContentProvider)srcIterator.next();
                Element newEl = factory.element("resource");
                doUpdateSingleResource(newEl, res);
                children.add(newEl);
            }
        }
    }    

    /**
     * updates resources, however the replacement algorithm here is not ideal,
     * no real primary id of single elements available..
     */
    private void doUpdateSourceModifications(Counter counter, Element root, List sourceModifications) throws Exception {
        boolean shouldExist = sourceModifications != null && !sourceModifications.isEmpty();
        Element sourcesModificationsElem = updateElement(counter, root, "sourceModifications", shouldExist);
        if (shouldExist) {
            // children provides a filtered view into the actual data.
            // JDOM automatically adjusts the insertion point to account for
            // white space, comments, and elements that do not match the filter.
            List children = sourcesModificationsElem.getContent(new ElementFilter("sourceModification"));

            Iterator tgtIterator = children.iterator();
            Iterator srcIterator = sourceModifications.iterator();

            while (tgtIterator.hasNext() && srcIterator.hasNext()) {
                Element el = (Element)tgtIterator.next();
                IContentProvider res = (IContentProvider)srcIterator.next();
                doUpdateSingleSourceModification(el, res);
            }

            while (tgtIterator.hasNext()) {
                // when some items are obsolete, remove them..
                tgtIterator.next();
                tgtIterator.remove();
            }

            while (srcIterator.hasNext()) {
                IContentProvider res = (IContentProvider)srcIterator.next();
                Element newEl = factory.element("sourceModification");
                doUpdateSingleSourceModification(newEl, res);
                children.add(newEl);
            }
        }
    }
    
    private void doUpdateSingleResource(Element resElem, IContentProvider resource) throws Exception {
        Counter innerCount = new Counter();
        findAndReplaceSimpleElement(innerCount, resElem, "directory", resource.getValue("directory"));
        findAndReplaceSimpleElement(innerCount, resElem, "targetPath", resource.getValue("targetPath"));
        doUpdateSimpleChildren(innerCount, resElem, resource.getValueList("includes", "include"), "includes", "include");
        doUpdateSimpleChildren(innerCount, resElem, resource.getValueList("excludes", "exclude"), "excludes", "exclude");
        findAndReplaceSimpleElement(innerCount, resElem, "filtering", resource.getValue("filtering"));
    }
    
    private void doUpdateSingleSourceModification(Element resElem, IContentProvider resource) throws Exception {
        Counter innerCount = new Counter();
        findAndReplaceSimpleElement(innerCount, resElem, "className", resource.getValue("className"));
        doUpdateSimpleChildren(innerCount, resElem, resource.getValueList("includes", "include"), "includes", "include");
        doUpdateSimpleChildren(innerCount, resElem, resource.getValueList("excludes", "exclude"), "excludes", "exclude");
    }
    
    private void doUpdateSimpleChildren(Counter counter, Element root, List newValues, String parentName, String childName) {
        boolean shouldExist = newValues != null && !newValues.isEmpty();
        Element cludeElem = updateElement(counter, root, parentName, shouldExist);
        if (shouldExist) {
            // children provides a filtered view into the actual data.
            // JDOM automatically adjusts the insertion point to account for
            // white space, comments, and elements that do not match the filter.
            List children = cludeElem.getContent(new ElementFilter(childName));

            Iterator tgtIterator = children.iterator();
            Iterator srcIterator = newValues.iterator();

            while (tgtIterator.hasNext() && srcIterator.hasNext()) {
                Element child = (Element)tgtIterator.next();
                child.setText((String)srcIterator.next());
            }

            while (tgtIterator.hasNext()) {
                // when some items are obsolete, remove them..
                tgtIterator.next();
                tgtIterator.remove();
            }

            while (srcIterator.hasNext()) {
                Element child = factory.element(childName);
                child.setText((String)srcIterator.next());
                children.add(child);
            }
        }
    }
    
//-----------------------------------------------------------------------------------
// utility methods
//-----------------------------------------------------------------------------------
    
    /**
     * will attempt to find the element with "name" in the model, if it exists, will update it, 
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
            insertAtPrefferedLocation(parent, element, counter);
            counter.increaseCount();
        } 
        if (!shouldExist && element != null)
        {
            //remove existing element that is no longer defined.
            parent.removeChild(name);
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
//            Element currElement = (Element)children.get(counter.getCurrentIndex());
//            List content = parent.getContent();
//            int index = content.indexOf(currElement);
            log.debug("inserting " + child.getName() + " with pref loc=" + counter.getCurrentIndex());
//            content.add(index, element);
//            parent.setContent(content);
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
    private static class SpecificElementFilter implements Filter {
        private String elemName;
        private String keyElem;
        private String keyVal;
        
        public SpecificElementFilter(String elementName, String keyElement, String keyValue) {
            elemName = elementName;
            keyElem = keyElement;
            keyVal = keyValue;
        }
        
        /**
         * not raally implemented.. is removed in beta9 anyway
         */
        public boolean canRemove(Object obj) {
            return true;
        }
        /**
         * not raally implemented.. is removed in beta9 anyway
         */
        public boolean canAdd(Object obj) {
            return true;
        }
        
        public boolean matches(Object obj) {
            if (obj instanceof Element) {
                Element elem = (Element)obj;
                if (elem.getName().equals(elemName)) {
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
    private static class DependencyElementFilter implements Filter {
        private String id;
        private String artifactId;
        private String groupId;
        private String type;
        
        public DependencyElementFilter(String id, String artifactId, String groupId) {
            this(id, artifactId, groupId, null);
        }
        
        public DependencyElementFilter(String id, String artifactId, String groupId, String type) {
            this.id = id;
            this.artifactId = artifactId;
            this.groupId = groupId;
            this.type = type == null ? "jar" : type;
        }
        
        /**
         * not raally implemented.. is removed in beta9 anyway
         */
        public boolean canRemove(Object obj) {
            return true;
        }
        /**
         * not raally implemented.. is removed in beta9 anyway
         */
        public boolean canAdd(Object obj) {
            return true;
        }
        
        public boolean matches(Object obj) {
            if (obj instanceof Element) {
                Element elem = (Element)obj;
                if (elem.getName().equals("dependency")) {
                    String elId = elem.getChildText("id");
                    String elGroupId = elem.getChildText("groupId");
                    String elArtifactId = elem.getChildText("artifactId");
                    String elType = elem.getChildText("type");
                    if (elType == null) {
                        elType = "jar";
                    }
                    if (elId != null && elId.equals(id)) {
                        return elType != null && elType.equals(type);
                    }
                    if (elGroupId != null && elArtifactId != null &&
                        elGroupId.equals(groupId) && elArtifactId.equals(artifactId)) {
                        return elType != null && elType.equals(type);
                    }
                }
            }
            return false;
        }
    }    
    
    private static class Counter {
        int currentIndex = 0;
        public Counter() {
        }
        
        public void increaseCount() {
            currentIndex = currentIndex + 1;
        }
        
        public int getCurrentIndex() {
            return currentIndex;
        }
    }
}
