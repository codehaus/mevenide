package org.mevenide.ui.eclipse.editors.mavenxml;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.grammar.TagLib;
import org.mevenide.grammar.impl.EmptyTagLib;
import org.mevenide.grammar.impl.StaticTagLibImpl;

public class Namespace {

    private static final Log log = LogFactory.getLog(Namespace.class);
    
    public static final String TOPLEVEL = "-toplevel-";
    public static final String DEFAULTNAMESPACE = "defaultns";
    
    private String prefix;
    private String uri;

    private TagLib taglib;

	private Map attributes;
	private Map includes;

    public Namespace(String prefix, String uri) {
        this.prefix = prefix;
        this.uri = uri;
        attributes = new TreeMap();
        includes = new TreeMap();
        loadTagLib();
        if ( taglib == null ) {
            taglib = new EmptyTagLib();
        }
        
        debugNameSpaceInitialization();
        
    }

    private void debugNameSpaceInitialization() {
        if ( log.isDebugEnabled() ) {
            log.debug("TagLib: " + taglib.getName());
            
            log.debug("  SubTags = { ");
            for ( Iterator it =  includes.keySet().iterator(); it.hasNext(); ) {
                String key = (String) it.next();
                log.debug("\t> "  + key + " = " + includes.get(key) + ", ");
            }
            log.debug("  };");
            
            log.debug("  TagAttributes = { ");
            for ( Iterator it =  attributes.keySet().iterator(); it.hasNext(); ) {
                String key = (String) it.next();
                log.debug("\t> "  + key + " = " + attributes.get(key) + ", ");
            }
            log.debug("  };");
         }
    }

    public Map getAttributes() {
        return attributes;
    }

    public Map getIncludes() {
        return includes;
    }
    
    private void loadTagLib() {
        if ( uri.indexOf(":") != -1 ) {
            try {
                taglib = new StaticTagLibImpl(uri);
                Collection tags = taglib.getRootTags();
                for (Iterator it = tags.iterator(); it.hasNext();) {
                	String tag = (String) it.next();
                	includes.put(tag, taglib.getSubTags(tag));
                    attributes.put(tag, taglib.getTagAttrs(tag));
                }
                
            }
            catch (Exception e) {
                String message = "Cannot load taglib " + uri; 
                log.error(message, e);
            }
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
}
