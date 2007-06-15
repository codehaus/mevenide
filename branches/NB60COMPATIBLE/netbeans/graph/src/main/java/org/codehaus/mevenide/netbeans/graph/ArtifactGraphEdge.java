/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.graph;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class ArtifactGraphEdge {
    private String edge;
    private int level = 0;
    
    /** Creates a new instance of ArtifactGraphEdge */
    public ArtifactGraphEdge(String edge) {
        this.edge = edge;
    }
    
    @Override
    public String toString() {
        return edge;
    }
    
    public void setLevel(int lvl) {
        level = lvl;
    }
    
    public int getLevel() {
        return level;
    }
    
}
