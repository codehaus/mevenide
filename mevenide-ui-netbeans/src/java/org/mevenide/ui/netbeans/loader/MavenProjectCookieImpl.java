/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.ArtifactListBuilder;
import org.apache.maven.MavenUtils;
import org.apache.maven.project.Project;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.ui.netbeans.ArtifactCookie;
import org.mevenide.ui.netbeans.MavenProjectCookie;
import org.openide.ErrorManager;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport.Reflection;
import org.openide.util.RequestProcessor;
import org.openide.xml.XMLUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenProjectCookieImpl implements MavenProjectCookie, ArtifactCookie
{
    private static Log log = LogFactory.getLog(MavenProjectCookieImpl.class);
    
    private volatile boolean loaded;
    private volatile boolean loadFailed;
    private File projectFile;
    private Project project;
    private LocationFinderAggregator locationResolver;
    private File projectPropFile;
    private File projectBuildPropFile;
    private File userBuildPropFile;
    private File mavenCust;
    private List artefactLst;
    private String name;
    private Object lock = new Object();
    private PropertyChangeSupport support;
    
    /** Creates a new instance of MavenProjectCookieImpl */
    public MavenProjectCookieImpl(DataObject dobj)
    {
        support = new PropertyChangeSupport(this);
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
        dobj.getPrimaryFile().addFileChangeListener(new FileChangeAdapter()
        {
                public void fileChanged (FileEvent fe) {
                    try {
                        log.debug("Project File changed" + fe.getFile());
                        reloadProject();
                    } catch (Exception exc)
                    {
                        log.warn("Exception thrown while reloading project", exc);
                        //TODO ignore for now.. shall it throw something anyway?
                        // load() consumes the exceptions..
                    }
                }
        });
    }
    
    private void load()
    {
        log.debug("Loading ProjectCookie: " + projectFile);
        if (projectFile != null && !loaded)
        {
            Project oldProject = project;
            synchronized (lock)
            {
                if (loaded) return;
                try
                {
                    log.debug("timestamp - reading project");
                    project = MavenUtils.getProject(projectFile);
                    log.debug("timestamp - reading project finished.");
                    log.debug("timestamp - reading artifacts");
                    artefactLst = ArtifactListBuilder.build(project);
                    log.debug("timestamp - reading artifacts finished.");
                    loaded = true;
                    loadFailed = false;
                } catch (Exception io)
                {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, io);
                    log.warn("Failed loading project", io);
                    loaded = true;
                    loadFailed = true;
                }
            }
            firePropertyChange(PROP_PROJECT, oldProject, project);
        }
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
        if (loaded && !loadFailed && project != null)
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
    
    public Property[] getProperties()
    {
        log.debug("Loading properties of " + projectFile);
        if (!loaded) { 
            RequestProcessor.getDefault().post(new Runnable()
            {
                public void run()
                {
                     load();
                }
            });
            // before loading, we will not display any props, wait for them to load.
            return new Property[0];
        }
        Property[] toReturn;
        if (project == null)
        {
            toReturn = new Property[0];
        } else
        {
            toReturn = new Property[13];
            try
            {
                toReturn[0] = new Reflection(project, String.class, "getDescription", null);
                toReturn[0].setName("description");
                toReturn[0].setDisplayName("Description");
                toReturn[1] = new Reflection(project, String.class, "getShortDescription", null);
                toReturn[1].setName("shortDescription");
                toReturn[1].setDisplayName("Short Description");
                toReturn[2] = new Reflection(project, String.class, "getId", null);
                toReturn[2].setName("ID");
                toReturn[2].setDisplayName("ID of the project");
                toReturn[3] = new Reflection(project, String.class, "getInceptionYear", null);
                toReturn[3].setName("InceptionYear");
                toReturn[3].setDisplayName("Inception Year");
                toReturn[4] = new Reflection(project, String.class, "getIssueTrackingUrl", null);
                toReturn[4].setName("IssuetrackingURL");
                toReturn[4].setDisplayName("Issue tracking URL");
                toReturn[5] = new Reflection(project, String.class, "getCurrentVersion", null);
                toReturn[5].setName("Version");
                toReturn[5].setDisplayName("Project version");
                toReturn[6] = new Reflection(project, String.class, "getUrl", null);
                toReturn[6].setName("HomepageURL");
                toReturn[6].setDisplayName("Homepage URL");
                toReturn[7] = new Reflection(project, String.class, "getName", null);
                toReturn[7].setName("Project name ");
                toReturn[7].setDisplayName("Project Name");
                toReturn[8] = new Reflection(project, String.class, "getDistributionSite", null);
                toReturn[8].setName("DistributionSite");
                toReturn[8].setDisplayName("Distribution Site");
                toReturn[9] = new Reflection(project, String.class, "getDistributionDirectory", null);
                toReturn[9].setName("DistributionDir");
                toReturn[9].setDisplayName("Distribution Directory");
                toReturn[10] = new Reflection(project, String.class, "getPackage", null);
                toReturn[10].setName("Package");
                toReturn[10].setDisplayName("Package");
//                toReturn[11] = new Reflection(project, String.class, "getType", null);
//                toReturn[11].setName("Type");
//                toReturn[11].setDisplayName("Type");
                toReturn[11] = new Reflection(project, String.class, "getLogo", null);
                toReturn[11].setName("Logo");
                toReturn[11].setDisplayName("Logo");
                toReturn[12] = new Reflection(project, String.class, "getGroupId", null);
                toReturn[12].setName("GroupId");
                toReturn[12].setDisplayName("Group ID");
                
                //TODO add other properties and better organization of props..
            } catch (NoSuchMethodException exc)
            {
                toReturn = new Property[0];
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
    
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        log.debug("addpropertychangelistener" + listener.getClass());
        support.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        support.removePropertyChangeListener(listener);
    }
    
    protected final void firePropertyChange(String propName, Object oldVal, Object newVal)
    {
        log.debug("firePropertyChange");
//        String oldValStr = (oldVal == null ? "null" : "" + oldVal.hashCode());
//        String newValStr = (newVal == null ? "null" : "" + newVal.hashCode());
//        log.debug(" old=" + oldValStr);
//        log.debug(" new=" + newValStr);
        if (oldVal != null && newVal != null && oldVal.hashCode() == newVal.hashCode())
        {
            //HACK kind of hack, if the oldval and NewVal are the same instance, won't trigger property chnage.
            // however the Project changed for example..
           oldVal = null; 
        }
        support.firePropertyChange(propName, oldVal, newVal);
    }

    /**
     * reloads the maven project..
     */
    public void reloadProject() throws Exception
    {
        synchronized (lock) {
            loaded = false;
            loadFailed = false;
        } 
        load();
    }
    
    /**
     * simple content handler to get the artifactID and version of the project only.
     */
    
    private static class SimpleContentHandler extends DefaultHandler
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
        
        
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException
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
