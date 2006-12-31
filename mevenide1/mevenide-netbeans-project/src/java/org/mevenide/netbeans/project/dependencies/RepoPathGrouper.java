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

package org.mevenide.netbeans.project.dependencies;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.mevenide.repository.RepoPathElement;

/**
 *
 * @author cenda
 */
public class RepoPathGrouper {
    private RepoPathElement[] elements;
    /** Creates a new instance of RepoPathGrouper */
    public RepoPathGrouper(RepoPathElement[] els) {
        elements = els;
        checkEqualPath(els);
    }
    
    public RepoPathGrouper[] getChildren() throws Exception {
        HashSet set = new HashSet();
        for (int i = 0; i < elements.length; i++) {
            RepoPathElement[] chil = elements[i].getChildren();
            for (int k = 0; k < chil.length; k++) {
                Iterator it = set.iterator();
                boolean added = false;
                while (it.hasNext()) {
                    RepoPathGrouper gr = (RepoPathGrouper)it.next();
                    if (gr.addElement(chil[k])) {
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    set.add(new RepoPathGrouper(new RepoPathElement[] { chil[k] }));
                }
            }
        }
        RepoPathGrouper[] toRet = new RepoPathGrouper[set.size()];
        return (RepoPathGrouper[])set.toArray(toRet);
    }
    
    private boolean addElement(RepoPathElement newEl) {
        if (checkEquals(newEl)) {
            Collection col = new ArrayList();
            col.addAll(Arrays.asList(elements));
            col.add(newEl);
            RepoPathElement[] newones = new RepoPathElement[elements.length + 1];
            elements = (RepoPathElement[])col.toArray(newones);
            return true;
        }
        return false;
    } 
    
    private boolean checkEquals(RepoPathElement newEl) {
        if (elements.length > 0) {
            return 0 == RepoElementComparator.getInstance().compare(elements[0], newEl);
        }
        return false;
    }
    
    private static boolean checkEqualPath(RepoPathElement[] els) {
        if (els.length < 2) {
            return true;
        }
        for (int i = 1 ; i < els.length;i++) {
            if ( 0 != RepoElementComparator.getInstance().compare(els[0], els[i])) {
                return false;
            }
        }
        return true;
    }
    
    public int getLevel() {
        return elements[0].getLevel();
    }
    
    public String getGroupId() {
        return elements[0].getGroupId();
    }
    
    public String getArtifactId() {
        return elements[0].getArtifactId();
    }
    
    public String getVersion() {
        return elements[0].getVersion();
    }
    
    public String getType() {
        return elements[0].getType();
    }
    
    public boolean isLocal() {
        RepoPathElement[] els = elements;
        for (int i = 0; i < els.length; i++) {
            if (!els[i].isRemote()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isRemote() {
        RepoPathElement[] els = elements;
        for (int i = 0; i < els.length; i++) {
            if (els[i].isRemote()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isLeaf() {
        return elements[0].isLeaf();
    }
    
    public RepoPathElement[] getElements() {
        return (RepoPathElement[])Arrays.asList(elements).toArray(new RepoPathElement[elements.length]);
    }
}
