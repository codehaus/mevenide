package org.mevenide.ui.eclipse.editors.mavenxml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.grammar.TagLib;
import org.mevenide.grammar.impl.EmptyTagLibImpl;
import org.mevenide.grammar.impl.StaticTagLibImpl;

public class Namespace {

    private static final Log log = LogFactory.getLog(Namespace.class);

    public static final String TOPLEVEL = "-toplevel-";
    public static final String DEFAULTNAMESPACE = "defaultns";
	
	private static final List WERKZ_LIST = new ArrayList();    
    
    private String prefix;
    private String uri;

    private TagLib taglib;

	private Map attributes = new TreeMap();
	
	private List rootTags = new ArrayList();
	private Map subTags = new TreeMap();
	
    private static final String[] WERKZ_TAGS = new String[] {"goal", "preGoal", "attainGoal", "postGoal",};


    public Namespace(String prefix, String uri) {
    	this.prefix = prefix;
        this.uri = uri;
        initializeWerkzList();
        loadTagLib();
        if ( taglib == null ) {
            taglib = new EmptyTagLibImpl(prefix + ":" + uri);
        }
        for (Iterator it = getTaglib().getRootTags().iterator(); it.hasNext();) {
            String tag = (String) it.next();
            rootTags.add(prefix + ":" + tag);
        }
    }

    private void initializeWerkzList() {
    	for (int i = 0; i < WERKZ_TAGS.length; i++) {
        	WERKZ_LIST.add(WERKZ_TAGS[i]);    
        }
    }

    public Map getAttributes() {
        return attributes;
    }

  
    private void loadTagLib() {
        try {
	        if ( uri.indexOf(":") != -1 ) {
	            taglib = new StaticTagLibImpl(uri);
	        }
	        else if ( WERKZ_LIST.contains(uri)) {
	            taglib = new StaticTagLibImpl("default-maven");
	        }
	        else {
	            return;
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
        if ( "project".equals(outerTag) ) {
        	return WERKZ_LIST; 
        } 
        else if ( WERKZ_LIST.contains(outerTag) ) {
        	return rootTags;
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
        	
        	collectedTags.addAll(rootTags);
        	candidates = collectedTags;
        	subTags.put(outerTag, candidates);
        }
        
        return candidates;
    }
}
