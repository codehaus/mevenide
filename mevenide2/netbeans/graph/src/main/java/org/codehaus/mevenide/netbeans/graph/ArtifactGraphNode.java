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
import org.apache.maven.artifact.resolver.ResolutionNode;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class ArtifactGraphNode {
    private ResolutionNode artifact;
    //for the layout
    double locX;
    double locY;
    double dispX;
    double dispY;
    private boolean fixed;
    private Widget widget;
    
    private boolean root;
    /** Creates a new instance of ArtifactGraphNode */
    public ArtifactGraphNode(ResolutionNode art) {
        artifact = art;
    }
    
    
    ResolutionNode getArtifact() {
        return artifact;
    }
    
    void setArtifact(ResolutionNode ar) {
        artifact = ar;
    }
    
    public void setRoot(boolean r) {
        root = r;
    }
    
    public boolean isRoot() {
        return root;
    }
    
    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }
    
    public boolean isFixed() {
        return fixed;
    }
    
    public boolean isVisible() {
        return widget != null ? widget.isVisible() : true;
    }
    
    void setWidget(Widget wid) {
        widget = wid;
    }
}
