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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.FastCharStream;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.FilteredTermEnum;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.repository.indexing.RepositoryIndexSearchException;
import org.apache.maven.repository.indexing.lucene.LuceneQuery;
import org.apache.maven.repository.indexing.query.Query;
import org.apache.maven.repository.indexing.record.StandardArtifactIndexRecord;
import org.apache.maven.repository.indexing.record.StandardIndexRecordFields;
import org.codehaus.mevenide.indexer.LocalRepositoryIndexer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author mkleint
 */
public class VersionNode extends AbstractNode {

    private String artifactId;
    private String groupId;
    private DefaultArtifactVersion version;
    
    /** Creates a new instance of VersionNode */
    public VersionNode(String group, String artifact, DefaultArtifactVersion version) {
        super(Children.LEAF);
        setName(version.toString());
        setName(version.toString());
        artifactId = artifact;
        groupId = group;
        this.version = version;
    }

    public Action[] getActions(boolean context) {
        Action[] retValue;
        
        retValue = new Action[] {
            new ShowRecordAction()
        };
        return retValue;
    }

    public Image getIcon(int type) {
        Image retValue;
        
        retValue = super.getIcon(type);
        return retValue;
    }
    
    
    private class ShowRecordAction extends AbstractAction {
        ShowRecordAction() {
            putValue(Action.NAME, "Show record");
        }

        public void actionPerformed(ActionEvent e) {
            QueryParser parser = new QueryParser("content", new SimpleAnalyzer());
            String query = StandardIndexRecordFields.GROUPID + ":" + groupId + " " + 
                                   StandardIndexRecordFields.ARTIFACTID + ":" + artifactId + " " + 
                                   StandardIndexRecordFields.VERSION + ":" + version.toString();
            System.out.println("query =" + query);
            LuceneQuery lq;
            try {
                lq = new LuceneQuery(parser.parse(query));
                List docs = LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), lq);
                StandardArtifactIndexRecord record = (StandardArtifactIndexRecord) docs.iterator().next();
                System.out.println("-----------------------------------------------------------------------");
                System.out.println("groupId:" + record.getGroupId());
                System.out.println("artifactId:" + record.getArtifactId());
                System.out.println("version:" + record.getVersion());
                System.out.println("packaging:" + record.getPackaging());
                System.out.println("type:" + record.getType());
                System.out.println("name:" + record.getProjectName());
                System.out.println("description:" + record.getProjectDescription());
            } catch (RepositoryIndexSearchException ex) {
                ex.printStackTrace();
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
}
