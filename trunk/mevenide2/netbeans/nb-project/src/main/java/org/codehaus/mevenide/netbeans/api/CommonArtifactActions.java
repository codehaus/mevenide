/*
 *  Copyright 2008 Anuradha.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.codehaus.mevenide.netbeans.api;

import javax.swing.Action;
import org.apache.maven.artifact.Artifact;
import org.codehaus.mevenide.netbeans.actions.ViewBugTrackerAction;
import org.codehaus.mevenide.netbeans.actions.ViewJavadocAction;
import org.codehaus.mevenide.netbeans.actions.ViewProjectHomeAction;
import org.codehaus.mevenide.netbeans.actions.scm.SCMActions;

/**
 *
 * @author Anuradha G (anuradha@codehaus.org)
 */
public class CommonArtifactActions {
    
    public static Action createViewProjectHomeAction(Artifact artifact) {
       return new ViewProjectHomeAction(artifact);
    }
    
    public static Action createViewJavadocAction(Artifact artifact) {
       return new ViewJavadocAction(artifact);

    }
    
    public static Action createViewBugTrackerAction(Artifact artifact) {
        return new ViewBugTrackerAction(artifact);
        
       
    }
    
    public static Action createSCMActions(Artifact artifact) {
        
        return new SCMActions(artifact);
    }
    
}
