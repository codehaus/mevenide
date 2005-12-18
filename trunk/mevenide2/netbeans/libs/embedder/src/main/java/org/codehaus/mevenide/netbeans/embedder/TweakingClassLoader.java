/*
 * TweakingClassLoader.java
 *
 * Created on November 29, 2005, 7:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.embedder;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;


/**
 *
 * @author mkleint
 */
public class TweakingClassLoader extends ClassLoader {
    /** Creates a new instance of TweakingClassLoader */
    public TweakingClassLoader(ClassLoader parent) {
        super(parent);
    }

    protected Enumeration findResources(String name) throws IOException {
//        System.out.println("findResources=" + name);

        Enumeration retValue;
        
        retValue = super.findResources(name);
        if ("META-INF/plexus/components.xml".equals(name)) {
            URL url = getParent().getResource("org/codehaus/mevenide/netbeans/embedder/components.xml");
//            System.out.println("url=" + url);
            List lst = new ArrayList(Collections.list(retValue));
            lst.add(url);
            return Collections.enumeration(lst);
        }
        return retValue;
    }
    
}
