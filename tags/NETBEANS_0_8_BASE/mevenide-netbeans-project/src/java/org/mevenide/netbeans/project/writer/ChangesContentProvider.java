/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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

package org.mevenide.netbeans.project.writer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.project.customizer.DependencyPOMChange;
import org.mevenide.netbeans.project.customizer.MavenPOMSingleChange;
import org.mevenide.netbeans.project.customizer.MavenPOMTreeChange;
import org.mevenide.project.io.IContentProvider;

/**
 * provider of values for the CarefulProjectMarshaller, that proxies another content provider,
 * allowing to customize behaviour.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class ChangesContentProvider implements IContentProvider {
    
    private static final Log log = LogFactory.getLog(ChangesContentProvider.class);
    
    private IContentProvider provider;
    private List changes;
    private String path;
    private int location;
    /** Creates a new instance of ElementContentProvider */
    public ChangesContentProvider(IContentProvider origin, List chngs, String pth, int loc) {
        provider = origin;
        changes = chngs;
        path = pth;
        location = loc;
    }

    protected IContentProvider createChildContentProvider(IContentProvider origin, String pth) {
        return new ChangesContentProvider(origin, changes, pth, location);
    }
    
    public IContentProvider getSubContentProvider(String key) {
        MavenPOMTreeChange change = findSubTreeChange(path + "." + key);
        if (change != null) {
            if (change.getOldLocation() == location && change.getNewLocation() != location) {
                return null;
            } else {
                if (change.getNewLocation() == location) {
                    return change.getChangedContent();
                }
            }
        }
        IContentProvider child = provider.getSubContentProvider(key);
        return child != null ? createChildContentProvider(child, path + "." + key) : null;
    }

    public String getValue(String key) {
        MavenPOMSingleChange change = findChange(path + "." + key);
        if (change != null) {
            if (change.getOldLocation() == location && change.getNewLocation() != location) {
                return null;
            }
            if (change.getNewLocation() == location) {
                return change.getNewValue();
            }
        }
        return provider.getValue(key);
    }

    public List getSubContentProviderList(String parentKey, String childKey) {
        if ("dependencies".equals(parentKey) && "dependency".equals(childKey)) {
            return getDependenciesProviderList();
        } else {
            MavenPOMTreeChange change = findSubTreeChange(path + "." + parentKey);
            if (change != null) {
                if (change.getOldLocation() == location && change.getNewLocation() != location) {
                    return null;
                }
                if (change.getNewLocation() == location) {
                    return change.getChangedContent().getSubContentProviderList(parentKey, childKey);
                }
            }
            List orig = provider.getSubContentProviderList(parentKey, childKey);
            if (orig != null) {
                Iterator it = orig.iterator();
                List toReturn = new ArrayList();
                while (it.hasNext()) {
                    IContentProvider obj = (IContentProvider)it.next();
                    toReturn.add(createChildContentProvider(obj, path + "." + parentKey + "." + childKey));
                }
                return toReturn;
            }
            return null;
        }
    }
 
    public List getValueList(String parentKey, String childKey) {
        MavenPOMTreeChange change = findSubTreeChange(path + "." + parentKey);
        if (change != null) {
            if (change.getOldLocation() == location && change.getNewLocation() != location) {
                return null;
            }
            if (change.getNewLocation() == location) {
                return change.getChangedContent().getValueList(parentKey, childKey);
            }
        }
        return provider.getValueList(parentKey, childKey);
    }

    public List getProperties() {
        return provider.getProperties();
    }
    
    private MavenPOMSingleChange findChange(String pth) {
        Iterator it = changes.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof MavenPOMSingleChange) {
                MavenPOMSingleChange pom = (MavenPOMSingleChange)obj;
                if (pom.getPath().equals(pth)) {
                    return pom;
                }
            }
        }
        return null;
    }
    
    private MavenPOMTreeChange findSubTreeChange(String pth) {
        Iterator it = changes.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof MavenPOMTreeChange) {
                MavenPOMTreeChange pom = (MavenPOMTreeChange)obj;
                if (pom.getPath().equals(pth)) {
                    return pom;
                }
            }
        }
        return null;
    }
    
    private List getDependenciesProviderList() {
        List lst = new ArrayList();
        // iterate new changed ones..
        Iterator it = changes.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof DependencyPOMChange) {
                DependencyPOMChange change = (DependencyPOMChange)obj;
                if (change.getOldLocation() == location && change.getNewLocation() != location) {
                    // was there but moved or deleted, remove from old list
                    continue;
                }
                if (change.getNewLocation() == location) {
                    lst.add(change.getChangedContent());
                }
            }
        }
        return lst;
    }
    
}
