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
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.repository.indexing.RepositoryIndexSearchException;
import org.apache.maven.repository.indexing.lucene.LuceneQuery;
import org.apache.maven.repository.indexing.record.StandardArtifactIndexRecord;
import org.apache.maven.repository.indexing.record.StandardIndexRecordFields;
import org.codehaus.mevenide.indexer.LocalRepositoryIndexer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Utilities;

/**
 *
 * @author mkleint
 */
public class VersionNode extends AbstractNode {
    
    private String artifactId;
    private String groupId;
    private DefaultArtifactVersion version;
    private StandardArtifactIndexRecord record;
    private Artifact artifact;
    private boolean hasJavadoc;
    private boolean hasSources;
    
    /** Creates a new instance of VersionNode */
    public VersionNode(String group, String artifact, DefaultArtifactVersion version) {
        super(Children.LEAF);
        setName(version.toString());
        setDisplayName(version.toString());
        artifactId = artifact;
        groupId = group;
        this.version = version;
        
        String query = "+" + StandardIndexRecordFields.GROUPID_EXACT + ":\"" + groupId + "\" +" +
                StandardIndexRecordFields.ARTIFACTID_EXACT + ":\"" + artifactId + "\" +" +
                StandardIndexRecordFields.VERSION_EXACT + ":\"" + version.toString() + "\"";
        System.out.println("query=" + query);
        hasSources = false;
        hasJavadoc = false;
        LuceneQuery lq;
        try {
            lq = new LuceneQuery(LocalRepositoryIndexer.parseQuery(query));
            List docs = LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), lq);
            System.out.println("returned =" + docs.size());
            Iterator it = docs.iterator();
            while (it.hasNext()) {
                StandardArtifactIndexRecord elem = (StandardArtifactIndexRecord) it.next();
                System.out.println("elem=" + elem.getGroupId() + ":" + elem.getArtifactId());
                hasSources = hasSources || "sources".equals(elem.getClassifier());
                hasJavadoc = hasJavadoc || "javadoc".equals(elem.getClassifier());
                if (record == null || elem.getClassifier() == null) {
                    if (!"sources".equals(elem.getClassifier()) && !"javadoc".equals(elem.getClassifier())) {
                        record = elem;
                    }
                }
            }
        } catch (RepositoryIndexSearchException ex) {
            // needs proper handling..
            ex.printStackTrace();
        } catch (ParseException ex) {
            //very unprobable
            ex.printStackTrace();
        }
        setIconBaseWithExtension("org/codehaus/mevenide/repository/DependencyJar.gif"); //NOI18N
    }
    
    public Action[] getActions(boolean context) {
        Action[] retValue;
        
        retValue = new Action[] {
            new ShowRecordAction()
        };
        return retValue;
    }
    
    public java.awt.Image getIcon(int param) {
        java.awt.Image retValue = super.getIcon(param);
        if (hasJavadoc) {
            retValue = Utilities.mergeImages(retValue,
                    Utilities.loadImage("org/codehaus/mevenide/repository/DependencyJavadocIncluded.png"),
                    12, 12);
        }
        if (hasSources) {
            retValue = Utilities.mergeImages(retValue,
                    Utilities.loadImage("org/codehaus/mevenide/repository/DependencySrcIncluded.png"),
                    12, 8);
        }
        return retValue;
        
    }
    
    public String getShortDescription() {
        StringBuffer buffer = new StringBuffer();
        if (record != null) {
            buffer.append("<html>GroupId:<b>").append(record.getGroupId()).append("</b><p>");
            buffer.append("ArtifactId:<b>").append(record.getArtifactId()).append("</b><p>");
            buffer.append("Version:<b>").append(record.getVersion().toString()).append("</b><p>");
            buffer.append("Packaging:<b>").append(record.getPackaging()).append("</b><p>");
            buffer.append("Name:").append(record.getProjectName()).append("<p>");
            buffer.append("Has Javadoc:").append(hasJavadoc ? "true" : "false").append("<p>");
            buffer.append("Has Sources:").append(hasSources ? "true" : "false");
            buffer.append("</html>");
        }
        return buffer.toString();
    }
    
    
    private class ShowRecordAction extends AbstractAction {
        ShowRecordAction() {
            putValue(Action.NAME, "Show record");
        }
        
        public void actionPerformed(ActionEvent e) {
            if (record != null) {
                System.out.println("-----------------------------------------------------------------------");
                System.out.println("groupId:" + record.getGroupId());
                System.out.println("artifactId:" + record.getArtifactId());
                System.out.println("version:" + record.getVersion());
                System.out.println("packaging:" + record.getPackaging());
                System.out.println("type:" + record.getType());
                System.out.println("name:" + record.getProjectName());
                System.out.println("description:" + record.getProjectDescription());
            }
        }
    }
    
    
}
