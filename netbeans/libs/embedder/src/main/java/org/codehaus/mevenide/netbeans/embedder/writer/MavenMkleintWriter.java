/*
 * $Id$
 */

package org.codehaus.mevenide.netbeans.embedder.writer;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Extension;
import org.apache.maven.model.FileSet;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Parent;
import org.apache.maven.model.PatternSet;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginConfiguration;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Relocation;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryBase;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Site;
import org.jdom.Content;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.filter.Filter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Class MavenMkleintWriter.
 * 
 * @version $Revision$ $Date$
 */
public class MavenMkleintWriter {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field factory
     */
    private DefaultJDOMFactory factory;
    private String lineSeparator;

      //----------------/
     //- Constructors -/
    //----------------/

    public MavenMkleintWriter() {
        factory = new DefaultJDOMFactory();
        lineSeparator = System.getProperty("line.separator");
    } //-- org.apache.maven.model.io.mkleint.MavenMkleintWriter()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method findAndReplaceProperties
     * 
     * @param counter
     * @param props
     * @param name
     * @param parent
     */
    public Element findAndReplaceProperties(Counter counter, Element parent, String name, Map props)
    {
        boolean shouldExist = props != null && ! props.isEmpty();
        Element element = updateElement(counter, parent, name, shouldExist);
        if (shouldExist) {
            Iterator it = props.keySet().iterator();
            Counter innerCounter = new Counter();
            while (it.hasNext()) {
                String key = (String) it.next();
                findAndReplaceSimpleElement(innerCounter, element, key, (String)props.get(key));
                }
            ArrayList lst = new ArrayList(props.keySet());
            it = element.getChildren().iterator();
            while (it.hasNext()) {
                Element elem = (Element) it.next();
                String key = elem.getName();
                if (!lst.contains(key)) {
                    element.removeChild(key, element.getNamespace());
                }
            }
        }
        return element;
    } //-- Element findAndReplaceProperties(Counter, Element, String, Map) 

    /**
     * Method findAndReplaceSimpleElement
     * 
     * @param counter
     * @param text
     * @param name
     * @param parent
     */
    public Element findAndReplaceSimpleElement(Counter counter, Element parent, String name, String text)
    {
        boolean shouldExist = text != null && text.trim().length() > 0;
        Element element = updateElement(counter, parent, name, shouldExist);
        if (shouldExist) {
            element.setText(text);
        }
        return element;
    } //-- Element findAndReplaceSimpleElement(Counter, Element, String, String) 

    /**
     * Method insertAtPreferredLocation
     * 
     * @param parent
     * @param counter
     * @param child
     */
    public void insertAtPreferredLocation(Element parent, Element child, Counter counter)
    {
        int contentIndex = 0;
        int elementCounter = 0;
        Iterator it = parent.getContent().iterator();
        Text lastText = null;
        while (it.hasNext() && elementCounter < counter.getCurrentIndex()) {
            Object next = it.next();
            if (next instanceof Element) {
                elementCounter = elementCounter + 1;
            }
            if (next instanceof Text) {
                lastText = (Text)next;
            }
            contentIndex = contentIndex + 1;
        }
        if (lastText == null) {
            int index = parent.getParentElement().indexOf(parent);
            if (index > 0) {
                Content cont = parent.getParentElement().getContent( index  - 1);
                if (cont instanceof Text) {
                    lastText = (Text)cont;
                }
            }
        }
        if (lastText != null && lastText.getTextTrim().length() == 0) {
            lastText = (Text)lastText.clone();
        } else {
            String starter = lineSeparator;
            Element curPar = parent;
            while (curPar != null) {
                curPar = curPar.getParentElement();
                starter = starter + "    "; //TODO make settable?
            }
            lastText = factory.text(starter);
        }
        if (counter.getCurrentIndex() >= elementCounter) {
            parent.addContent(lastText);
            parent.addContent(child);
        } else {
            parent.addContent(contentIndex, child);
            parent.addContent(contentIndex, lastText);
        }
    } //-- void insertAtPreferredLocation(Element, Element, Counter) 

    /**
     * Method updateActivation
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateActivation(Activation value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "activation", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "activeByDefault", value.isActiveByDefault() == false ? null : String.valueOf( value.isActiveByDefault() ));
            findAndReplaceSimpleElement(innerCount, root,  "jdk", value.getJdk());
            updateActivationOS( value.getOs(), innerCount, element);
            updateActivationProperty( value.getProperty(), innerCount, element);
            updateActivationFile( value.getFile(), innerCount, element);
        }
    } //-- void updateActivation(Activation, Counter, Element) 

    /**
     * Method updateActivationFile
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateActivationFile(ActivationFile value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "activationFile", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "missing", value.getMissing());
            findAndReplaceSimpleElement(innerCount, root,  "exists", value.getExists());
        }
    } //-- void updateActivationFile(ActivationFile, Counter, Element) 

    /**
     * Method updateActivationOS
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateActivationOS(ActivationOS value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "activationOS", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "name", value.getName());
            findAndReplaceSimpleElement(innerCount, root,  "family", value.getFamily());
            findAndReplaceSimpleElement(innerCount, root,  "arch", value.getArch());
            findAndReplaceSimpleElement(innerCount, root,  "version", value.getVersion());
        }
    } //-- void updateActivationOS(ActivationOS, Counter, Element) 

    /**
     * Method updateActivationProperty
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateActivationProperty(ActivationProperty value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "activationProperty", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "name", value.getName());
            findAndReplaceSimpleElement(innerCount, root,  "value", value.getValue());
        }
    } //-- void updateActivationProperty(ActivationProperty, Counter, Element) 

    /**
     * Method updateBuild
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateBuild(Build value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "build", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "sourceDirectory", value.getSourceDirectory());
            findAndReplaceSimpleElement(innerCount, root,  "scriptSourceDirectory", value.getScriptSourceDirectory());
            findAndReplaceSimpleElement(innerCount, root,  "testSourceDirectory", value.getTestSourceDirectory());
            findAndReplaceSimpleElement(innerCount, root,  "outputDirectory", value.getOutputDirectory());
            findAndReplaceSimpleElement(innerCount, root,  "testOutputDirectory", value.getTestOutputDirectory());
            findAndReplaceSimpleElement(innerCount, root,  "defaultGoal", value.getDefaultGoal());
            findAndReplaceSimpleElement(innerCount, root,  "directory", value.getDirectory());
            findAndReplaceSimpleElement(innerCount, root,  "finalName", value.getFinalName());
            updatePluginManagement( value.getPluginManagement(), innerCount, element);
        }
    } //-- void updateBuild(Build, Counter, Element) 

    /**
     * Method updateBuildBase
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateBuildBase(BuildBase value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "buildBase", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "defaultGoal", value.getDefaultGoal());
            findAndReplaceSimpleElement(innerCount, root,  "directory", value.getDirectory());
            findAndReplaceSimpleElement(innerCount, root,  "finalName", value.getFinalName());
            updatePluginManagement( value.getPluginManagement(), innerCount, element);
        }
    } //-- void updateBuildBase(BuildBase, Counter, Element) 

    /**
     * Method updateCiManagement
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateCiManagement(CiManagement value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "ciManagement", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "system", value.getSystem());
            findAndReplaceSimpleElement(innerCount, root,  "url", value.getUrl());
        }
    } //-- void updateCiManagement(CiManagement, Counter, Element) 

    /**
     * Method updateConfigurationContainer
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateConfigurationContainer(ConfigurationContainer value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "configurationContainer", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "inherited", value.getInherited());
        }
    } //-- void updateConfigurationContainer(ConfigurationContainer, Counter, Element) 

    /**
     * Method updateContributor
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateContributor(Contributor value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "contributor", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "name", value.getName());
            findAndReplaceSimpleElement(innerCount, root,  "email", value.getEmail());
            findAndReplaceSimpleElement(innerCount, root,  "url", value.getUrl());
            findAndReplaceSimpleElement(innerCount, root,  "organization", value.getOrganization());
            findAndReplaceSimpleElement(innerCount, root,  "organizationUrl", value.getOrganizationUrl());
            findAndReplaceSimpleElement(innerCount, root,  "timezone", value.getTimezone());
            findAndReplaceProperties(innerCount, root,  "properties", value.getProperties());
        }
    } //-- void updateContributor(Contributor, Counter, Element) 

    /**
     * Method updateDependency
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateDependency(Dependency value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "dependency", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "groupId", value.getGroupId());
            findAndReplaceSimpleElement(innerCount, root,  "artifactId", value.getArtifactId());
            findAndReplaceSimpleElement(innerCount, root,  "version", value.getVersion());
            findAndReplaceSimpleElement(innerCount, root,  "type", value.getType() == null || value.getType().equals( "jar" ) ? null : value.getType());
            findAndReplaceSimpleElement(innerCount, root,  "classifier", value.getClassifier());
            findAndReplaceSimpleElement(innerCount, root,  "scope", value.getScope());
            findAndReplaceSimpleElement(innerCount, root,  "systemPath", value.getSystemPath());
            findAndReplaceSimpleElement(innerCount, root,  "optional", value.isOptional() == false ? null : String.valueOf( value.isOptional() ));
        }
    } //-- void updateDependency(Dependency, Counter, Element) 

    /**
     * Method updateDependencyManagement
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateDependencyManagement(DependencyManagement value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "dependencyManagement", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
        }
    } //-- void updateDependencyManagement(DependencyManagement, Counter, Element) 

    /**
     * Method updateDeploymentRepository
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateDeploymentRepository(DeploymentRepository value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "deploymentRepository", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "uniqueVersion", value.isUniqueVersion() == true ? null : String.valueOf( value.isUniqueVersion() ));
            findAndReplaceSimpleElement(innerCount, root,  "id", value.getId());
            findAndReplaceSimpleElement(innerCount, root,  "name", value.getName());
            findAndReplaceSimpleElement(innerCount, root,  "url", value.getUrl());
            findAndReplaceSimpleElement(innerCount, root,  "layout", value.getLayout() == null || value.getLayout().equals( "default" ) ? null : value.getLayout());
        }
    } //-- void updateDeploymentRepository(DeploymentRepository, Counter, Element) 

    /**
     * Method updateDeveloper
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateDeveloper(Developer value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "developer", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "id", value.getId());
            findAndReplaceSimpleElement(innerCount, root,  "name", value.getName());
            findAndReplaceSimpleElement(innerCount, root,  "email", value.getEmail());
            findAndReplaceSimpleElement(innerCount, root,  "url", value.getUrl());
            findAndReplaceSimpleElement(innerCount, root,  "organization", value.getOrganization());
            findAndReplaceSimpleElement(innerCount, root,  "organizationUrl", value.getOrganizationUrl());
            findAndReplaceSimpleElement(innerCount, root,  "timezone", value.getTimezone());
            findAndReplaceProperties(innerCount, root,  "properties", value.getProperties());
        }
    } //-- void updateDeveloper(Developer, Counter, Element) 

    /**
     * Method updateDistributionManagement
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateDistributionManagement(DistributionManagement value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "distributionManagement", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            updateDeploymentRepository( value.getRepository(), innerCount, element);
            updateDeploymentRepository( value.getSnapshotRepository(), innerCount, element);
            updateSite( value.getSite(), innerCount, element);
            findAndReplaceSimpleElement(innerCount, root,  "downloadUrl", value.getDownloadUrl());
            updateRelocation( value.getRelocation(), innerCount, element);
            findAndReplaceSimpleElement(innerCount, root,  "status", value.getStatus());
        }
    } //-- void updateDistributionManagement(DistributionManagement, Counter, Element) 

    /**
     * Method updateElement
     * 
     * @param counter
     * @param shouldExist
     * @param name
     * @param parent
     */
    public Element updateElement(Counter counter, Element parent, String name, boolean shouldExist)
    {
        Element element =  parent.getChild(name, parent.getNamespace());
        if (element != null && shouldExist) {
            if (parent.getChildren().indexOf(element) <= counter.getCurrentIndex()) {
                counter.increaseCount();
            }
        }
        if (element == null && shouldExist) {
            element = factory.element(name, parent.getNamespace());
            insertAtPreferredLocation(parent, element, counter);
            counter.increaseCount();
        }
        if (!shouldExist && element != null) {
            parent.removeChild(name, parent.getNamespace());
        }
        return element;
    } //-- Element updateElement(Counter, Element, String, boolean) 

    /**
     * Method updateExclusion
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateExclusion(Exclusion value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "exclusion", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "artifactId", value.getArtifactId());
            findAndReplaceSimpleElement(innerCount, root,  "groupId", value.getGroupId());
        }
    } //-- void updateExclusion(Exclusion, Counter, Element) 

    /**
     * Method updateExtension
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateExtension(Extension value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "extension", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "groupId", value.getGroupId());
            findAndReplaceSimpleElement(innerCount, root,  "artifactId", value.getArtifactId());
            findAndReplaceSimpleElement(innerCount, root,  "version", value.getVersion());
        }
    } //-- void updateExtension(Extension, Counter, Element) 

    /**
     * Method updateFileSet
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateFileSet(FileSet value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "fileSet", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "directory", value.getDirectory());
        }
    } //-- void updateFileSet(FileSet, Counter, Element) 

    /**
     * Method updateIssueManagement
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateIssueManagement(IssueManagement value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "issueManagement", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "system", value.getSystem());
            findAndReplaceSimpleElement(innerCount, root,  "url", value.getUrl());
        }
    } //-- void updateIssueManagement(IssueManagement, Counter, Element) 

    /**
     * Method updateLicense
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateLicense(License value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "license", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "name", value.getName());
            findAndReplaceSimpleElement(innerCount, root,  "url", value.getUrl());
            findAndReplaceSimpleElement(innerCount, root,  "distribution", value.getDistribution());
            findAndReplaceSimpleElement(innerCount, root,  "comments", value.getComments());
        }
    } //-- void updateLicense(License, Counter, Element) 

    /**
     * Method updateMailingList
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateMailingList(MailingList value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "mailingList", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "name", value.getName());
            findAndReplaceSimpleElement(innerCount, root,  "subscribe", value.getSubscribe());
            findAndReplaceSimpleElement(innerCount, root,  "unsubscribe", value.getUnsubscribe());
            findAndReplaceSimpleElement(innerCount, root,  "post", value.getPost());
            findAndReplaceSimpleElement(innerCount, root,  "archive", value.getArchive());
        }
    } //-- void updateMailingList(MailingList, Counter, Element) 

    /**
     * Method updateModel
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateModel(Model value, Counter counter, Element element)
    {
        Element root = element;
        Counter innerCount = new Counter();
        updateParent( value.getParent(), innerCount, element);
        findAndReplaceSimpleElement(innerCount, root,  "modelVersion", value.getModelVersion());
        findAndReplaceSimpleElement(innerCount, root,  "groupId", value.getGroupId());
        findAndReplaceSimpleElement(innerCount, root,  "artifactId", value.getArtifactId());
        findAndReplaceSimpleElement(innerCount, root,  "packaging", value.getPackaging() == null || value.getPackaging().equals( "jar" ) ? null : value.getPackaging());
        findAndReplaceSimpleElement(innerCount, root,  "name", value.getName());
        findAndReplaceSimpleElement(innerCount, root,  "version", value.getVersion());
        findAndReplaceSimpleElement(innerCount, root,  "description", value.getDescription());
        findAndReplaceSimpleElement(innerCount, root,  "url", value.getUrl());
        updatePrerequisites( value.getPrerequisites(), innerCount, element);
        updateIssueManagement( value.getIssueManagement(), innerCount, element);
        updateCiManagement( value.getCiManagement(), innerCount, element);
        findAndReplaceSimpleElement(innerCount, root,  "inceptionYear", value.getInceptionYear());
        updateScm( value.getScm(), innerCount, element);
        updateOrganization( value.getOrganization(), innerCount, element);
        updateBuild( value.getBuild(), innerCount, element);
        updateReporting( value.getReporting(), innerCount, element);
        updateDependencyManagement( value.getDependencyManagement(), innerCount, element);
        updateDistributionManagement( value.getDistributionManagement(), innerCount, element);
        findAndReplaceProperties(innerCount, root,  "properties", value.getProperties());
    } //-- void updateModel(Model, Counter, Element) 

    /**
     * Method updateModelBase
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateModelBase(ModelBase value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "modelBase", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            updateReporting( value.getReporting(), innerCount, element);
            updateDependencyManagement( value.getDependencyManagement(), innerCount, element);
            updateDistributionManagement( value.getDistributionManagement(), innerCount, element);
            findAndReplaceProperties(innerCount, root,  "properties", value.getProperties());
        }
    } //-- void updateModelBase(ModelBase, Counter, Element) 

    /**
     * Method updateNotifier
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateNotifier(Notifier value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "notifier", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "type", value.getType() == null || value.getType().equals( "mail" ) ? null : value.getType());
            findAndReplaceSimpleElement(innerCount, root,  "sendOnError", value.isSendOnError() == true ? null : String.valueOf( value.isSendOnError() ));
            findAndReplaceSimpleElement(innerCount, root,  "sendOnFailure", value.isSendOnFailure() == true ? null : String.valueOf( value.isSendOnFailure() ));
            findAndReplaceSimpleElement(innerCount, root,  "sendOnSuccess", value.isSendOnSuccess() == true ? null : String.valueOf( value.isSendOnSuccess() ));
            findAndReplaceSimpleElement(innerCount, root,  "sendOnWarning", value.isSendOnWarning() == true ? null : String.valueOf( value.isSendOnWarning() ));
            findAndReplaceSimpleElement(innerCount, root,  "address", value.getAddress());
            findAndReplaceProperties(innerCount, root,  "configuration", value.getConfiguration());
        }
    } //-- void updateNotifier(Notifier, Counter, Element) 

    /**
     * Method updateOrganization
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateOrganization(Organization value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "organization", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "name", value.getName());
            findAndReplaceSimpleElement(innerCount, root,  "url", value.getUrl());
        }
    } //-- void updateOrganization(Organization, Counter, Element) 

    /**
     * Method updateParent
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateParent(Parent value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "parent", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "artifactId", value.getArtifactId());
            findAndReplaceSimpleElement(innerCount, root,  "groupId", value.getGroupId());
            findAndReplaceSimpleElement(innerCount, root,  "version", value.getVersion());
            findAndReplaceSimpleElement(innerCount, root,  "relativePath", value.getRelativePath() == null || value.getRelativePath().equals( "../pom.xml" ) ? null : value.getRelativePath());
        }
    } //-- void updateParent(Parent, Counter, Element) 

    /**
     * Method updatePatternSet
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updatePatternSet(PatternSet value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "patternSet", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
        }
    } //-- void updatePatternSet(PatternSet, Counter, Element) 

    /**
     * Method updatePlugin
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updatePlugin(Plugin value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "plugin", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "groupId", value.getGroupId() == null || value.getGroupId().equals( "org.apache.maven.plugins" ) ? null : value.getGroupId());
            findAndReplaceSimpleElement(innerCount, root,  "artifactId", value.getArtifactId());
            findAndReplaceSimpleElement(innerCount, root,  "version", value.getVersion());
            findAndReplaceSimpleElement(innerCount, root,  "extensions", value.isExtensions() == false ? null : String.valueOf( value.isExtensions() ));
            findAndReplaceSimpleElement(innerCount, root,  "inherited", value.getInherited());
        }
    } //-- void updatePlugin(Plugin, Counter, Element) 

    /**
     * Method updatePluginConfiguration
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updatePluginConfiguration(PluginConfiguration value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "pluginConfiguration", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            updatePluginManagement( value.getPluginManagement(), innerCount, element);
        }
    } //-- void updatePluginConfiguration(PluginConfiguration, Counter, Element) 

    /**
     * Method updatePluginContainer
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updatePluginContainer(PluginContainer value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "pluginContainer", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
        }
    } //-- void updatePluginContainer(PluginContainer, Counter, Element) 

    /**
     * Method updatePluginExecution
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updatePluginExecution(PluginExecution value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "pluginExecution", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "id", value.getId() == null || value.getId().equals( "default" ) ? null : value.getId());
            findAndReplaceSimpleElement(innerCount, root,  "phase", value.getPhase());
            findAndReplaceSimpleElement(innerCount, root,  "inherited", value.getInherited());
        }
    } //-- void updatePluginExecution(PluginExecution, Counter, Element) 

    /**
     * Method updatePluginManagement
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updatePluginManagement(PluginManagement value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "pluginManagement", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
        }
    } //-- void updatePluginManagement(PluginManagement, Counter, Element) 

    /**
     * Method updatePrerequisites
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updatePrerequisites(Prerequisites value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "prerequisites", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "maven", value.getMaven() == null || value.getMaven().equals( "2.0" ) ? null : value.getMaven());
        }
    } //-- void updatePrerequisites(Prerequisites, Counter, Element) 

    /**
     * Method updateProfile
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateProfile(Profile value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "profile", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "id", value.getId());
            updateActivation( value.getActivation(), innerCount, element);
            updateBuildBase( value.getBuild(), innerCount, element);
            updateReporting( value.getReporting(), innerCount, element);
            updateDependencyManagement( value.getDependencyManagement(), innerCount, element);
            updateDistributionManagement( value.getDistributionManagement(), innerCount, element);
            findAndReplaceProperties(innerCount, root,  "properties", value.getProperties());
        }
    } //-- void updateProfile(Profile, Counter, Element) 

    /**
     * Method updateRelocation
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateRelocation(Relocation value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "relocation", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "groupId", value.getGroupId());
            findAndReplaceSimpleElement(innerCount, root,  "artifactId", value.getArtifactId());
            findAndReplaceSimpleElement(innerCount, root,  "version", value.getVersion());
            findAndReplaceSimpleElement(innerCount, root,  "message", value.getMessage());
        }
    } //-- void updateRelocation(Relocation, Counter, Element) 

    /**
     * Method updateReportPlugin
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateReportPlugin(ReportPlugin value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "reportPlugin", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "groupId", value.getGroupId() == null || value.getGroupId().equals( "org.apache.maven.plugins" ) ? null : value.getGroupId());
            findAndReplaceSimpleElement(innerCount, root,  "artifactId", value.getArtifactId());
            findAndReplaceSimpleElement(innerCount, root,  "version", value.getVersion());
            findAndReplaceSimpleElement(innerCount, root,  "inherited", value.getInherited());
        }
    } //-- void updateReportPlugin(ReportPlugin, Counter, Element) 

    /**
     * Method updateReportSet
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateReportSet(ReportSet value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "reportSet", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "id", value.getId() == null || value.getId().equals( "default" ) ? null : value.getId());
            findAndReplaceSimpleElement(innerCount, root,  "inherited", value.getInherited());
        }
    } //-- void updateReportSet(ReportSet, Counter, Element) 

    /**
     * Method updateReporting
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateReporting(Reporting value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "reporting", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "excludeDefaults", value.isExcludeDefaults() == false ? null : String.valueOf( value.isExcludeDefaults() ));
            findAndReplaceSimpleElement(innerCount, root,  "outputDirectory", value.getOutputDirectory());
        }
    } //-- void updateReporting(Reporting, Counter, Element) 

    /**
     * Method updateRepository
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateRepository(Repository value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "repository", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            updateRepositoryPolicy( value.getReleases(), innerCount, element);
            updateRepositoryPolicy( value.getSnapshots(), innerCount, element);
            findAndReplaceSimpleElement(innerCount, root,  "id", value.getId());
            findAndReplaceSimpleElement(innerCount, root,  "name", value.getName());
            findAndReplaceSimpleElement(innerCount, root,  "url", value.getUrl());
            findAndReplaceSimpleElement(innerCount, root,  "layout", value.getLayout() == null || value.getLayout().equals( "default" ) ? null : value.getLayout());
        }
    } //-- void updateRepository(Repository, Counter, Element) 

    /**
     * Method updateRepositoryBase
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateRepositoryBase(RepositoryBase value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "repositoryBase", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "id", value.getId());
            findAndReplaceSimpleElement(innerCount, root,  "name", value.getName());
            findAndReplaceSimpleElement(innerCount, root,  "url", value.getUrl());
            findAndReplaceSimpleElement(innerCount, root,  "layout", value.getLayout() == null || value.getLayout().equals( "default" ) ? null : value.getLayout());
        }
    } //-- void updateRepositoryBase(RepositoryBase, Counter, Element) 

    /**
     * Method updateRepositoryPolicy
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateRepositoryPolicy(RepositoryPolicy value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "repositoryPolicy", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "enabled", value.isEnabled() == true ? null : String.valueOf( value.isEnabled() ));
            findAndReplaceSimpleElement(innerCount, root,  "updatePolicy", value.getUpdatePolicy());
            findAndReplaceSimpleElement(innerCount, root,  "checksumPolicy", value.getChecksumPolicy());
        }
    } //-- void updateRepositoryPolicy(RepositoryPolicy, Counter, Element) 

    /**
     * Method updateResource
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateResource(Resource value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "resource", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "targetPath", value.getTargetPath());
            findAndReplaceSimpleElement(innerCount, root,  "filtering", value.isFiltering() == false ? null : String.valueOf( value.isFiltering() ));
            findAndReplaceSimpleElement(innerCount, root,  "directory", value.getDirectory());
        }
    } //-- void updateResource(Resource, Counter, Element) 

    /**
     * Method updateScm
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateScm(Scm value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "scm", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "connection", value.getConnection());
            findAndReplaceSimpleElement(innerCount, root,  "developerConnection", value.getDeveloperConnection());
            findAndReplaceSimpleElement(innerCount, root,  "tag", value.getTag() == null || value.getTag().equals( "HEAD" ) ? null : value.getTag());
            findAndReplaceSimpleElement(innerCount, root,  "url", value.getUrl());
        }
    } //-- void updateScm(Scm, Counter, Element) 

    /**
     * Method updateSite
     * 
     * @param value
     * @param element
     * @param counter
     */
    public void updateSite(Site value, Counter counter, Element element)
    {
        boolean shouldExist = value != null;
        Element root = updateElement(counter, element, "site", shouldExist);
        if (shouldExist) {
            Counter innerCount = new Counter();
            findAndReplaceSimpleElement(innerCount, root,  "id", value.getId());
            findAndReplaceSimpleElement(innerCount, root,  "name", value.getName());
            findAndReplaceSimpleElement(innerCount, root,  "url", value.getUrl());
        }
    } //-- void updateSite(Site, Counter, Element) 

    /**
     * Method write
     * 
     * @param project
     * @param stream
     * @param document
     */
    public void write(Model project, Document document, OutputStream stream)
        throws java.io.IOException
    {
        updateModel(project, new Counter(), document.getRootElement());
        XMLOutputter outputter = new XMLOutputter();
////        outputter.setFormat(Format.getPrettyFormat()
////        .setIndent("    ")
////        .setLineSeparator(System.getProperty("line.separator")));
        outputter.output(document, stream);
    } //-- void write(Model, Document, OutputStream) 


      //-----------------/
     //- Inner Classes -/
    //-----------------/

    /**
     * Class Counter.
     * 
     * @version $Revision$ $Date$
     */
    public class Counter {


          //--------------------------/
         //- Class/Member Variables -/
        //--------------------------/

        /**
         * Field currentIndex
         */
        private int currentIndex = 0;


          //-----------/
         //- Methods -/
        //-----------/

        /**
         * Method getCurrentIndex
         */
        public int getCurrentIndex()
        {
            return currentIndex;
        } //-- int getCurrentIndex() 

        /**
         * Method increaseCount
         */
        public void increaseCount()
        {
            currentIndex = currentIndex + 1;
        } //-- void increaseCount() 

    }

}
