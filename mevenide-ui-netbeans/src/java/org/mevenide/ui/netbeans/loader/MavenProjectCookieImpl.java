/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Milos Kleint (ca206216@tiscali.cz).  All rights
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
package org.mevenide.ui.netbeans.loader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.ArtifactListBuilder;
import org.apache.maven.MavenUtils;
import org.mevenide.ui.netbeans.ArtifactCookie;
import org.mevenide.ui.netbeans.MavenProjectCookie;
import org.apache.maven.project.Project;
import org.mevenide.environment.LocationFinderAggregator;
import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.xml.XMLUtil;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author  cenda
 */
public class MavenProjectCookieImpl implements MavenProjectCookie, ArtifactCookie
{
    private static Log log = LogFactory.getLog(MavenProjectCookieImpl.class);
    
    private boolean loaded;
    private boolean loadFailed;
    private File projectFile;
    private Project project;
    private LocationFinderAggregator locationResolver;
    private File projectPropFile;
    private File projectBuildPropFile;
    private File userBuildPropFile;
    private File mavenCust;
    private List artefactLst;
    private String name;
    //    private static final String[] simpleElements = {
    //        "pomVersion",
    //        "id",
    //        "name",
    //        "groupId",
    //        "currentVersion",
    //        "inceptionYear",
    //        "package",
    //        "logo",
    //        "gumpRepositoryId",
    //        "description",
    //        "shortDescription",
    //        "url",
    //        "issueTrackingUrl",
    //        "siteAddress",
    //        "siteDirectory",
    //        "distributionSite",
    //        "distributionDirectory",
    //        "extends"
    //    };
    
    /** Creates a new instance of MavenProjectCookieImpl */
    public MavenProjectCookieImpl(DataObject dobj)
    {
        projectFile = FileUtil.toFile(dobj.getPrimaryFile());
        loaded = false;
        loadFailed = false;
            locationResolver = new LocationFinderAggregator();
            File parentDir = projectFile.getParentFile();
            locationResolver.setEffectiveWorkingDirectory(parentDir.getAbsolutePath());
            projectPropFile = new File(parentDir, FILENAME_PROJECT);
            projectBuildPropFile = new File(parentDir, FILENAME_BUILD);
            userBuildPropFile = new File(System.getProperty("user.home"), FILENAME_BUILD);
            mavenCust = new File(parentDir, FILENAME_MAVEN);
    }
    
    private void load()
    {
        log.debug("Loading ProjectCookie: " + projectFile);
        if (projectFile != null)
        {
            try
            {
                project = MavenUtils.getProject(projectFile);
            } catch (Exception io)
            {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, io);
                log.warn("Failed loading project", io);
                loaded = true;
                loadFailed = true;
                return;
            }
            artefactLst = ArtifactListBuilder.build(project);
        }
        loaded = true;
        loadFailed = false;
    }
    
    public File getMavenCustomFile()
    {
        return mavenCust;
    }
    
    public File getProjectBuildPropFile()
    {
        return projectBuildPropFile;
    }
    
    public File getProjectFile()
    {
        return projectFile;
    }
    
    public File getProjectPropFile()
    {
        return projectPropFile;
    }
    
    public File getUserBuildPropFile()
    {
        return userBuildPropFile;
    }

    public String getProjectName()
    {
        if (loaded && !loadFailed)
        {
            return project.getArtifactId() + ":" + project.getCurrentVersion();
        }
        if (name == null)
        {
            try
            {
                XMLReader reader = XMLUtil.createXMLReader();
                SimpleContentHandler handler = new SimpleContentHandler();
                reader.setContentHandler(handler);
                log.debug("URI=" + projectFile.toURI().toASCIIString());
                reader.parse(projectFile.toURI().toASCIIString());
                name = "" + handler.getName() + ":" + handler.getVersion();
            } catch (SAXException exc)
            {
                log.warn("Cannot parse file:" + projectFile, exc);
                name = "Unparsable";
            } catch (FileNotFoundException fnf)
            {
                log.error("Project File not found", fnf);
            } catch (IOException io)
            {
                log.error("Project File reading error", io);
            }
        }
        return name;
    }
    
//    public String getVersion()
//    {
//        if (!loaded) { 
//            load();
//        }
//        if (project != null)
//        {
//            return project.getCurrentVersion();
//        }
//        return ""; 
//    }
    
    public Node.Property[] getProperties()
    {
        log.debug("Loading properties of " + projectFile);
        if (!loaded) { 
            load();
        }
        Node.Property[] toReturn;
        if (project == null)
        {
            toReturn = new Node.Property[0];
        } else
        {
            toReturn = new Node.Property[8];
            try
            {
                toReturn[0] = new PropertySupport.Reflection(project, String.class, "getDescription", null);
                toReturn[0].setName("description");
                toReturn[0].setDisplayName("Description");
                toReturn[1] = new PropertySupport.Reflection(project, String.class, "getShortDescription", null);
                toReturn[1].setName("shortDescription");
                toReturn[1].setDisplayName("Short Description");
                toReturn[2] = new PropertySupport.Reflection(project, String.class, "getId", null);
                toReturn[2].setName("ID");
                toReturn[2].setDisplayName("ID of the project");
                toReturn[3] = new PropertySupport.Reflection(project, String.class, "getInceptionYear", null);
                toReturn[3].setName("Inception year");
                toReturn[3].setDisplayName("Year when project started");
                toReturn[4] = new PropertySupport.Reflection(project, String.class, "getIssueTrackingUrl", null);
                toReturn[4].setName("Issue tracking URL");
                toReturn[4].setDisplayName("Issue tracking URL");
                toReturn[5] = new PropertySupport.Reflection(project, String.class, "getCurrentVersion", null);
                toReturn[5].setName("Version");
                toReturn[5].setDisplayName("Project version");
                toReturn[6] = new PropertySupport.Reflection(project, String.class, "getUrl", null);
                toReturn[6].setName("Org. homepage URL ");
                toReturn[6].setDisplayName("Org. homepage URL ");
                toReturn[7] = new PropertySupport.Reflection(project, String.class, "getName", null);
                toReturn[7].setName("Project name ");
                toReturn[7].setDisplayName("Project Name");
                
                //TODO add other properties and better organization of props..
            } catch (NoSuchMethodException exc)
            {
                toReturn = new Node.Property[0];
                ErrorManager.getDefault().notify(exc);
                log.error("Error loading properties", exc);
            }
        }
        return toReturn;
    }
    
    public Project getMavenProject()
    {
        log.debug("getMavenProject() of " + projectFile);
        if (!loaded) { 
            load();
        }
        return project;
    }
    
    public List getArtifacts()
    {
        log.debug("getArtifacts of " + projectFile);
        if (!loaded) { 
            load();
        }
        return artefactLst;
    }    
    
    /**
     * simple content handler to get the artifactID and version of the project only.
     */
    
    private class SimpleContentHandler extends DefaultHandler
    {
        private static final String ELEMENT_VERSION = "currentVersion"; //NOI18N
        private static final String ELEMENT_ID = "id"; //NOI18N
        private String version;
        private String artifactID;
        private boolean isVersion = false;
        private boolean isID = false;
        private StringBuffer buff;
        private int level = 0;
        
        public String getVersion()
        {
            return version;
        }
        public String getName()
        {
            return artifactID;
        }
        
        public void characters(char[] ch, int start, int length) throws SAXException
        {
            if (isVersion || isID)
            {
                buff.append(ch, start, length);
            }
        }
        
        
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException
        {
            level = level - 1;
            if (ELEMENT_ID.equals(qName) && level == 1)
            {
                isID = false;
                artifactID = buff.toString();
            }
            if (ELEMENT_VERSION.equals(qName) && level == 1)
            {
                isVersion = false;
                version = buff.toString();
            }
        }
        
        
        public void startElement(String namespaceURI, String localName, String qName, org.xml.sax.Attributes atts) throws SAXException
        {
            if (ELEMENT_ID.equals(qName) && level == 1)
            {
                isID = true;
                buff = new StringBuffer();
            }
            if (ELEMENT_VERSION.equals(qName)  && level == 1)
            {
                isVersion = true;
                buff = new StringBuffer();
            }
            level = level + 1;
        }
        
    }
    
}
