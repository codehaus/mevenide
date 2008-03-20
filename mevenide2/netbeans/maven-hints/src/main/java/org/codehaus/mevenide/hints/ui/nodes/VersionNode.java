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
package org.codehaus.mevenide.hints.ui.nodes;

import javax.swing.Action;

import org.codehaus.mevenide.indexer.api.NBVersionInfo;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Utilities;

/**
 *
 * 
 * @author Anuradha
 */
public class VersionNode extends AbstractNode {

    private NBVersionInfo nbvi;
    private boolean hasJavadoc;
    private boolean hasSources;



    /** Creates a new instance of VersionNode */
    public VersionNode(NBVersionInfo versionInfo, boolean javadoc, boolean source) {
        super(Children.LEAF);
 
        hasJavadoc = javadoc;
        hasSources = source;
        this.nbvi = versionInfo;
        
            setName(versionInfo.getVersion());
            setDisplayName(versionInfo.getVersion() + " [ " + versionInfo.getType() 
                    + (versionInfo.getClassifier() != null ? ("," + versionInfo.getClassifier()) : "") + " ] -"
                    + "[ "+versionInfo.getRepoId()+" ]"
                    
                    );
        
        setIconBaseWithExtension("org/codehaus/mevenide/hints/DependencyJar.gif"); //NOI18N
    }

    @Override
    public Action[] getActions(boolean context) {
      
        return new Action[0];
    }

    @Override
    public java.awt.Image getIcon(int param) {
        java.awt.Image retValue = super.getIcon(param);
        if (hasJavadoc) {
            retValue = Utilities.mergeImages(retValue,
                    Utilities.loadImage("org/codehaus/mevenide/hints/DependencyJavadocIncluded.png"),//NOI18N
                    12, 12);
        }
        if (hasSources) {
            retValue = Utilities.mergeImages(retValue,
                    Utilities.loadImage("org/codehaus/mevenide/hints/DependencySrcIncluded.png"),//NOI18N
                    12, 8);
        }
        return retValue;

    }

    public NBVersionInfo getNBVersionInfo() {
        return nbvi;
    }

    @Override
    public String getShortDescription() {

        return nbvi.toString();
    }
}
