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

import javax.swing.Action;
import org.apache.maven.artifact.Artifact;

import org.codehaus.mevenide.indexer.api.NBVersionInfo;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences.RepositoryInfo;
import org.codehaus.mevenide.indexer.api.RepositoryUtil;
import org.codehaus.mevenide.netbeans.api.CommonArtifactActions;
import org.codehaus.mevenide.repository.dependency.AddAsDependencyAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author mkleint
 * @author Anuradha
 */
public class VersionNode extends AbstractNode {

    private NBVersionInfo record;
    private boolean hasJavadoc;
    private boolean hasSources;
    private RepositoryInfo info;
    public static Children createChildren(RepositoryInfo info,NBVersionInfo record) {
        if (!info.isRemote() && !"pom".equals(record.getType())) { //NOI18N
            try {
                Artifact art = RepositoryUtil.createArtifact(record);
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
    public VersionNode(RepositoryInfo info,NBVersionInfo versionInfo, boolean javadoc, boolean source, boolean dispNameShort) {
        super(createChildren(info,versionInfo));
        this.info = info;
        hasJavadoc = javadoc;
        hasSources = source;
        this.record = versionInfo;
        if (dispNameShort) {
            setName(versionInfo.getVersion());
            setDisplayName(versionInfo.getVersion() + " [ " + versionInfo.getType() 
                    + (versionInfo.getClassifier() != null ? ("," + versionInfo.getClassifier()) : "") + " ]");
        } else {
            setName(versionInfo.getGroupId() + ":" + versionInfo.getArtifactId() + ":" + versionInfo.getVersion()); //NOI18N
        }
        setIconBaseWithExtension("org/codehaus/mevenide/repository/DependencyJar.gif"); //NOI18N
    }

    @Override
    public Action[] getActions(boolean context) {
       Artifact artifact = RepositoryUtil.createArtifact(record);
        Action[] retValue;
        if(info.isRemote()){
             retValue = new Action[]{
            new AddAsDependencyAction(artifact),
            CommonArtifactActions.createFindUsages(artifact),
            null,
            CommonArtifactActions.createViewProjectHomeAction(artifact),
            CommonArtifactActions.createViewBugTrackerAction(artifact),
            CommonArtifactActions.createSCMActions(artifact)
        };
        
        }else{
        

        retValue = new Action[]{
            new AddAsDependencyAction(artifact),
            null,
            CommonArtifactActions.createFindUsages(artifact),
            null,
            CommonArtifactActions.createViewJavadocAction(artifact),
            CommonArtifactActions.createViewProjectHomeAction(artifact),
            CommonArtifactActions.createViewBugTrackerAction(artifact),
            CommonArtifactActions.createSCMActions(artifact)
        };
        }
        return retValue;
    }

    @Override
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

    @Override
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
}
