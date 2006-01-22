/* ==========================================================================
 * Copyright 2005 Mevenide Team
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

package org.codehaus.mevenide.netbeans.embedder;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ResolutionListener;
import org.apache.maven.artifact.versioning.VersionRange;

/**
 *
 * @author mkleint
 */
public class MyResolutionListener implements ResolutionListener {
    
    /** Creates a new instance of MyResolutionListener */
    public MyResolutionListener() {
        System.out.println("my resolutionlistener created");
        throw new RuntimeException();
    }
    
    public void  testArtifact( Artifact node ){
        System.out.println("testArtifact" + node);
    }

    public void  startProcessChildren( Artifact artifact ) {
        System.out.println("startProcessChildren" + artifact);
    }

    public void  endProcessChildren( Artifact artifact ){
        System.out.println("endProcessChildren" + artifact);
    }

    public void  includeArtifact( Artifact artifact ){
        System.out.println("includeArtifact" + artifact);
    }

    public void  omitForNearer( Artifact omitted, Artifact kept ) {
    }

    public void  updateScope( Artifact artifact, String scope ){
    }

    public void  manageArtifact( Artifact artifact, Artifact replacement ){
        System.out.println("manageArtifact" + artifact);
    }

    public void  omitForCycle( Artifact artifact ){
    }

    public void  updateScopeCurrentPom( Artifact artifact, String scope ){
    }

    public void  selectVersionFromRange( Artifact artifact ){
    }

    public void  restrictRange( Artifact artifact, Artifact replacement, VersionRange newRange ){
    }
    
}
