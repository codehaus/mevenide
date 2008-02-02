/*
 *  Copyright 2005-2008 Mevenide Team.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.codehaus.mevenide.indexer;


import java.util.StringTokenizer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;


/**
 *
 * @author Anuradha
 */
public class FindQuery {

    private String any="";//default
    private String groupId="";//default
    private String artifactId="";//default
    private String name="";//default
    private String description="";//default
    private String packaging="";//default
    private String classes="";//default

    public FindQuery() {
    }

    public String getAny() {
        return any;
    }

    public void setAny(String any) {
        this.any = any;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }
//    LuceneQuery createLuceneQuery() throws ParseException {
//        StringBuffer buff = new StringBuffer();
//        appendField(buff, StandardIndexRecordFields.GROUPID_EXACT, groupId);
//        appendField(buff, StandardIndexRecordFields.ARTIFACTID_EXACT, artifactId);
//        appendField(buff, StandardIndexRecordFields.PROJECT_NAME, name);
//        appendField(buff, StandardIndexRecordFields.PROJECT_DESCRIPTION, description);
//        appendField(buff, StandardIndexRecordFields.PACKAGING, packaging);
//        appendField(buff, StandardIndexRecordFields.CLASSES, classes);
//        
//        if (any.trim().length() > 0) {
//            Query multi = LocalRepositoryIndexer.parseMultiFieldQuery(any);
//            buff.append(" ").append(multi.toString());
//        }
//        System.out.println("search query=" + buff.toString());
//        
//        return new LuceneQuery(LocalRepositoryIndexer.parseQuery(buff.toString()));
//    }
      private void appendField(StringBuffer buff, String field, String text) {
        StringTokenizer tokenizer = new StringTokenizer(text.trim());
        while (tokenizer.hasMoreTokens()) {
            buff.append(" +").append(field).append(":\"").append(tokenizer.nextToken()).append("\"");
        }
    }
}
