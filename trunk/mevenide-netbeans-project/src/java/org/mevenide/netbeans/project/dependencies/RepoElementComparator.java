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

import java.util.Comparator;
import org.mevenide.repository.RepoPathElement;


/**
 * comparator of RepoElements.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class RepoElementComparator implements Comparator {
    
    private static Comparator instance = new RepoElementComparator();
    /**
     * return instance of comparator
     */
    public static Comparator getInstance() {
        return instance;
    }
    
    public int compare(Object o1, Object o2) {
        if (o1 instanceof RepoPathElement && ! (o2 instanceof RepoPathElement)) {
            return -1;
        }
        if (o2 instanceof RepoPathElement && ! (o1 instanceof RepoPathElement)) {
            return 1;
        }
        if (! (o2 instanceof RepoPathElement) && ! (o1 instanceof RepoPathElement)) {
            return 0;
        }
        
        RepoPathElement repo1 = (RepoPathElement)o1;
        RepoPathElement repo2 = (RepoPathElement)o2;
        int grp = compareStrings(repo1.getGroupId(), repo2.getGroupId());
        if (grp != 0) {
            return grp;
        }
        int type = compareStrings(repo1.getType(), repo2.getType());
        if (type != 0) {
            return type;
        }
        int art = compareStrings(repo1.getArtifactId(), repo2.getArtifactId());
        if (art != 0) {
            return art;
        }
        return compareStrings(repo1.getVersion(), repo2.getVersion());
    }
    
    private int compareStrings(String s1, String s2) {
        if (s1 == null && s2 != null) {
            return 1;
        }
        if (s2 == null && s1 != null) {
            return -1;
        }
        if (s1 == null && s2 == null) {
            return 0;
        }
        return s1.compareTo(s2);
    }
    
    
}
