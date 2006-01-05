/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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

package org.mevenide.ui.eclipse.editors.jelly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.grammar.TagLib;
import org.mevenide.grammar.impl.EmptyTagLibImpl;
import org.mevenide.grammar.impl.StaticTagLibImpl;

public class Namespace {

    private static final Log log = LogFactory.getLog(Namespace.class);

    public static final String TOPLEVEL = "-toplevel-";
    public static final String DEFAULTNAMESPACE = "default:maven";
	
	private static final List WERKZ_LIST = new ArrayList(); 
	    
    private static final List PROJECT_LIST = new ArrayList();
    {
    	PROJECT_LIST.add("project");
    }
    
    
    private String prefix;
    private String uri;
    
    /** indicates if the NameSpace should targeted to generic jelly editor */
    private boolean generic;
    
    private TagLib taglib;

	private Map attributes = new TreeMap();
	
	private List rootTags = new ArrayList();
	private Map subTags = new TreeMap();
	
    private static final String[] WERKZ_TAGS = new String[] {"goal", "preGoal", "postGoal",};

    static {
        for (int i = 0; i < WERKZ_TAGS.length; i++) {
        	WERKZ_LIST.add(WERKZ_TAGS[i]);
        }   
    }
    
    public Namespace(String prefix, String uri) {
    	this.prefix = prefix;
        this.uri = uri;
        
        loadTagLib();
        if ( taglib == null ) {
            taglib = new EmptyTagLibImpl(prefix + ":" + uri);
        }
        for (Iterator it = getTaglib().getRootTags().iterator(); it.hasNext();) {
            String tag = (String) it.next();
            rootTags.add(prefix + ":" + tag);
        }
        Collections.sort(rootTags);
    }


    public Map getAttributes() {
        return attributes;
    }

  
    private void loadTagLib() {
        try {
            if ( uri.indexOf(":") != -1 ) {
	            taglib = new StaticTagLibImpl(uri);
	        }
            Collection tags = taglib.getRootTags();
            for (Iterator it = tags.iterator(); it.hasNext();) {
            	String tag = (String) it.next();
            	String tagKey = tag;
            	if ( tagKey.indexOf(":") == -1 ) {
    	            tagKey = prefix + ":" + tagKey;
    	        }
            	attributes.put(tagKey, taglib.getTagAttrs(tag));
            }
            
            //@todo donot harcode me
            TagLib defaultTagLib = new StaticTagLibImpl("default-maven");
            for (Iterator it = defaultTagLib.getRootTags().iterator(); it.hasNext();) {
                String tag = (String) it.next();
                attributes.put(tag, defaultTagLib.getTagAttrs(tag));
            }  
        }
        catch (Exception e) {
            String message = "Cannot load taglib " + uri; 
            log.error(message, e);
        }
        
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public TagLib getTaglib() {
        return taglib;
    }
    
    public void setTaglib(TagLib taglib) {
        this.taglib = taglib;
    }


    public Collection getSubTags(String outerTag) {
        if ( !generic && TOPLEVEL.equals(outerTag) ) {
        	return PROJECT_LIST;
        }
        else if ( !generic && "project".equals(outerTag) ) {
            return WERKZ_LIST;
        } 
        else if ( !generic && WERKZ_LIST.contains(outerTag) ) {
            Collection coll = new TreeSet();
            coll.add("attainGoal");
            coll.addAll(rootTags);
            return coll;
        }
        else {
	        if ( outerTag.indexOf(":") != -1 ) {
	            outerTag = StringUtils.split(outerTag, ":")[1];
	        }
            return getCandidateTags(outerTag);
      }
    }

    private Collection getCandidateTags(String outerTag) {
        List collectedTags = new ArrayList();
        Collection candidates = (List) subTags.get(outerTag);
        
        if ( candidates == null ) {
        	candidates = getTaglib().getSubTags(outerTag);
        	candidates = candidates == null ? new ArrayList() : candidates;
        	
        	for (Iterator iter = candidates.iterator(); iter.hasNext();) {
                String tag = (String) iter.next();
                collectedTags.add(prefix + ":" + tag);
            }
        	
        	//Collections.sort((List) collectedTags);
        	candidates = collectedTags;
        	subTags.put(outerTag, collectedTags);
        	
        	if ( !candidates.contains("attainGoal") && !generic ) {
        	    candidates.add("attainGoal");
        	}
        }
        
        return candidates;
    }
    
    public List getRootTags() {
        return rootTags;
    }
    
    public boolean isGeneric() {
        return generic;
    }
    
    public void setGeneric(boolean generic) {
        this.generic = generic;
    }
    public static List getWerkzList() {
        return WERKZ_LIST;
    }
}
