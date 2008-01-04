/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.repository;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.repository.dependency.AddAsDependencyAction;
import org.codehaus.mevenide.repository.scm.SCMActions;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author mkleint
 */
public class VersionNode extends AbstractNode {
    
    private StandardArtifactIndexRecord record;
    private boolean hasJavadoc;
    private boolean hasSources;
    
    public static Children createChildren(StandardArtifactIndexRecord record) {
        if (!"pom".equals(record.getType())) { //NOI18N
            try {
                Artifact art = RepositoryUtils.createArtifact(record,
                        EmbedderFactory.getProjectEmbedder().getLocalRepository());
                FileObject fo = FileUtil.toFileObject(art.getFile());
            
                if (fo != null) {
                    DataObject dobj = DataObject.find(fo);
                    return new FilterNode.Children(dobj.getNodeDelegate().cloneNode());
                }
            } catch (DataObjectNotFoundException e) {
            }
        }
        return Children.LEAF;
    }
    
    /** Creates a new instance of VersionNode */
    public VersionNode(StandardArtifactIndexRecord record, boolean javadoc, boolean source, boolean dispNameShort) {
        super(createChildren(record));
        hasJavadoc = javadoc;
        hasSources = source;
        this.record = record;
        if (dispNameShort) {
            setName(record.getVersion());
            setDisplayName(record.getVersion());
        } else {
            setName(record.getGroupId() + ":" + record.getArtifactId() + ":" + record.getVersion()); //NOI18N
        }
        setIconBaseWithExtension("org/codehaus/mevenide/repository/DependencyJar.gif"); //NOI18N
    }
    
    @Override
    public Action[] getActions(boolean context) {
        Action[] retValue;
        
        retValue = new Action[] {
            new ViewProjectHomeAction(),
            new ViewJavadocAction(),
            new ViewBugTrackerAction(),
            null,
            new AddAsDependencyAction(record),
            null,
            new SCMActions(record)
            
        };
        return retValue;
    }
    
    public java.awt.Image getIcon(int param) {
        java.awt.Image retValue = super.getIcon(param);
        if (hasJavadoc) {
            retValue = Utilities.mergeImages(retValue,
                    Utilities.loadImage("org/codehaus/mevenide/repository/DependencyJavadocIncluded.png"),//NOI18N
                    12, 12);
        }
        if (hasSources) {
            retValue = Utilities.mergeImages(retValue,
                    Utilities.loadImage("org/codehaus/mevenide/repository/DependencySrcIncluded.png"),//NOI18N
                    12, 8);
        }
        return retValue;
        
    }
    
    public String getShortDescription() {
        StringBuffer buffer = new StringBuffer();
        if (record != null) {
            buffer.append("<html>").append(NbBundle.getMessage(VersionNode.class, "TXT_GroupId")).append("<b>").append(record.getGroupId()).append("</b><p>");
            buffer.append(NbBundle.getMessage(VersionNode.class, "TXT_ArtifactId")).append("<b>").append(record.getArtifactId()).append("</b><p>");
            buffer.append(NbBundle.getMessage(VersionNode.class, "TXT_Version")).append("<b>").append(record.getVersion().toString()).append("</b><p>");
            buffer.append(NbBundle.getMessage(VersionNode.class, "TXT_Packaging")).append("<b>").append(record.getPackaging()).append("</b><p>");
            buffer.append(NbBundle.getMessage(VersionNode.class, "TXT_Name")).append(record.getProjectName()).append("<p>");
            buffer.append(NbBundle.getMessage(VersionNode.class, "TXT_HasJavadoc")).append(hasJavadoc ? NbBundle.getMessage(VersionNode.class, "TXT_true") : NbBundle.getMessage(VersionNode.class, "TXT_false")).append("<p>");
            buffer.append(NbBundle.getMessage(VersionNode.class, "TXT_HasSources")).append(hasSources ? NbBundle.getMessage(VersionNode.class, "TXT_true") : NbBundle.getMessage(VersionNode.class, "TXT_false"));
            buffer.append("</html>");
        }
        return buffer.toString();
    }
    
    private FileObject getJavadocFile() {
        Artifact art = RepositoryUtils.createJavadocArtifact(record,
                EmbedderFactory.getProjectEmbedder().getLocalRepository());
        return FileUtil.toFileObject(art.getFile());
    }
    
    
    private class ViewJavadocAction extends AbstractAction {
        public ViewJavadocAction() {
            putValue(Action.NAME, NbBundle.getMessage(VersionNode.class, "LBL_View_Javadoc"));
            setEnabled(hasJavadoc);
        }
        public void actionPerformed(ActionEvent event) {
            FileObject fo = getJavadocFile();
            if (fo != null) {
                FileObject jarfo = FileUtil.getArchiveRoot(fo);
                if (jarfo != null) {
                    FileObject index = jarfo.getFileObject("apidocs/index.html"); //NOI18N
                    if (index == null) {
                        index = jarfo.getFileObject("index.html"); //NOI18N
                    }
                    if (index == null) {
                        index = jarfo;
                    }
                    URL link = URLMapper.findURL(index, URLMapper.EXTERNAL);
                    HtmlBrowser.URLDisplayer.getDefault().showURL(link);
                }
            }
        }
        
    }
    
     private class ViewBugTrackerAction extends AbstractAction {
        public ViewBugTrackerAction() {
            putValue(Action.NAME, NbBundle.getMessage(VersionNode.class, "LBL_View_BugTracker"));
           MavenProject mp = RepositoryUtils.readMavenProject(record);
           //enable only if url persent
            setEnabled(mp!=null && mp.getIssueManagement()
                    != null&& mp.getIssueManagement().getUrl()!=null);
        }
        public void actionPerformed(ActionEvent event) {
          IssueManagement im= RepositoryUtils.readMavenProject(record).getIssueManagement();
          try {

            URLDisplayer.getDefault().showURL(new URL(im.getUrl()));
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
        }

       
        
    }
      private class ViewProjectHomeAction extends AbstractAction {
        public ViewProjectHomeAction() {
            putValue(Action.NAME, NbBundle.getMessage(VersionNode.class, "LBL_View_ProjectHome"));
           MavenProject mp = RepositoryUtils.readMavenProject(record);
           //enable only if url persent
            setEnabled(mp!=null && mp.getUrl()!=null);
        }
        public void actionPerformed(ActionEvent event) {
           MavenProject mp = RepositoryUtils.readMavenProject(record);
          try {

            URLDisplayer.getDefault().showURL(new URL(mp.getUrl()));
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
        }

       
        
    }
    
}
