/*
 * MavenLibraryImpl.java
 *
 * Created on April 7, 2004, 6:18 PM
 */

package org.mevenide.netbeans.project.libraries;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.spi.project.libraries.LibraryImplementation;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenLibraryImpl implements LibraryImplementation {
    
    private String description;
    private String name;
    private String locBundle;
    private PropertyChangeSupport support;
    private List content = Collections.EMPTY_LIST;
    /** Creates a new instance of MavenLibraryImpl */
    public MavenLibraryImpl() {
        support = new PropertyChangeSupport(this);
    }
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        support.addPropertyChangeListener(propertyChangeListener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        support.removePropertyChangeListener(propertyChangeListener);
    }
    
    public List getContent(String str) throws java.lang.IllegalArgumentException {
        if ("classpath".equals(str)) {
            return content;
        }
        return Collections.EMPTY_LIST;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getLocalizingBundle() {
        return locBundle;
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return MavenRepoLibraryProvider.TYPE;
    }
    
    
    public void setContent(String str, List list) throws java.lang.IllegalArgumentException {
        if ("classpath".equals(str)) {
            content = list;
        } else {
            throw new IllegalArgumentException("what to do with type=" + str);
        }
    }
    
    public void setDescription(String str) {
        description = str;
    }
    public void setLocalizingBundle(String str) {
        locBundle = str;
    }
    
    public void setName(String str) {
        name = str;
    }
    
}
