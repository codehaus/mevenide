/*
 *  Copyright 2008 mkleint.
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

package org.codehaus.mevenide.indexer.api;

/**
 * 
 * @author mkleint
 */
public final class FieldQuery {

    public static final String FIELD_GROUPID = "groupId";
    public static final String FIELD_ARTIFACTID = "artifactId";
    public static final String FIELD_VERSION = "version";
    public static final String FIELD_CLASSES = "classes";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";
    
    public static final int MATCH_EXACT = 0;
    public static final int MATCH_ANY = 1;
    
    public static final int OCCUR_MUST = 0;
    public static final int OCCUR_SHOULD = 1;
    
    private int match = MATCH_ANY;
    private String field = FIELD_ANY;
    private int occur = OCCUR_SHOULD;

    public int getOccur() {
        return occur;
    }

    public void setOccur(int occur) {
        this.occur = occur;
    }
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getMatch() {
        return match;
    }

    public void setMatch(int match) {
        this.match = match;
    }
    
    
}
