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

package org.codehaus.mevenide.repository.search;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.repository.indexing.record.StandardArtifactIndexRecord;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.repository.RepositoryUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

/**
 *
 * @author mkleint
 */
public class SearchResultChildren extends Children.Keys {
    
    private List keys;
    
    private ArrayList mainkeys;
    
    private ArrayList attachedkeys;
    
    /**
     * Creates a new instance of SearchResultChildren
     */
    public SearchResultChildren(List results) {
        keys = results;
    }
    
    protected Node[] createNodes(Object key) {
        StandardArtifactIndexRecord record = (StandardArtifactIndexRecord)key;
        Iterator it = attachedkeys.iterator();
        boolean hasSources = false;
        boolean hasJavadoc = false;
        while (it.hasNext() && (!hasJavadoc || !hasSources)) {
            StandardArtifactIndexRecord elem = (StandardArtifactIndexRecord) it.next();
            if (elem.getGroupId().equals(record.getGroupId()) &&
                    elem.getArtifactId().equals(record.getArtifactId()) &&
                    elem.getVersion().equals(record.getVersion())) {
                hasSources = hasSources || "sources".equals(elem.getClassifier());
                hasJavadoc = hasJavadoc || "javadoc".equals(elem.getClassifier());
            }
        }
        return new Node[] { new ResultNode(record, hasJavadoc, hasSources) };
    }
    
    
    protected void addNotify() {
        super.addNotify();
        Iterator it = keys.iterator();
        mainkeys = new ArrayList(keys.size());
        attachedkeys = new ArrayList(keys.size());
        while (it.hasNext()) {
            StandardArtifactIndexRecord record = (StandardArtifactIndexRecord) it.next();
            if (record.getClassifier() != null && (record.getClassifier().equals("javadoc")
                    || record.getClassifier().equals("sources"))) {
                attachedkeys.add(record);
            } else {
                mainkeys.add(record);
            }
        }
        setKeys(mainkeys);
    }
    
    protected void removeNotify() {
        super.removeNotify();
        setKeys(Collections.EMPTY_LIST);
    }
    
    private class ResultNode extends AbstractNode {
        private StandardArtifactIndexRecord record;
        private boolean sources;
        private boolean javadoc;
        
        public ResultNode(StandardArtifactIndexRecord rec, boolean hasJavadoc, boolean hasSources) {
            super(Children.LEAF);
            record = rec;
            javadoc = hasJavadoc;
            sources = hasSources;
            setName(record.getGroupId() + ":" + record.getArtifactId() + ":" + record.getVersion());
            setIconBaseWithExtension("org/codehaus/mevenide/repository/DependencyJar.gif"); //NOI18N
        }
        
        public Action[] getActions(boolean context) {
            Action[] retValue;
            
            retValue = new Action[] {
                new ShowRecordAction(record)
            };
            return retValue;
        }
        
        public java.awt.Image getIcon(int param) {
            java.awt.Image retValue = super.getIcon(param);
            if (javadoc) {
                retValue = Utilities.mergeImages(retValue,
                        Utilities.loadImage("org/codehaus/mevenide/repository/DependencyJavadocIncluded.png"),
                        12, 12);
            }
            if (sources) {
                retValue = Utilities.mergeImages(retValue,
                        Utilities.loadImage("org/codehaus/mevenide/repository/DependencySrcIncluded.png"),
                        12, 8);
            }
            return retValue;
            
        }
    }
    
    
    private static class ShowRecordAction extends AbstractAction {
        
        private StandardArtifactIndexRecord record;
        ShowRecordAction(StandardArtifactIndexRecord record) {
            putValue(Action.NAME, "Show record");
            this.record = record;
        }
        
        public void actionPerformed(ActionEvent e) {
            System.out.println("-----------------------------------------------------------------------");
            System.out.println("groupId:" + record.getGroupId());
            System.out.println("artifactId:" + record.getArtifactId());
            System.out.println("version:" + record.getVersion());
            System.out.println("packaging:" + record.getPackaging() + "-");
            System.out.println("type:" + record.getType() + "-");
            System.out.println("name:" + record.getProjectName());
            System.out.println("description:" + record.getProjectDescription());
            System.out.println("filename:" + record.getFilename());
            System.out.println("repository:" + record.getRepository());
            try {
                Artifact art = RepositoryUtils.createArtifact(record, EmbedderFactory.getProjectEmbedder().getLocalRepository());
            } catch (MavenEmbedderException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
}
