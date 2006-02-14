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
    
    private ResolutionListener listener;
    /** Creates a new instance of MyResolutionListener */
    public MyResolutionListener() {
//        System.out.println("my resolutionlistener created");
        EmbedderFactory.setProjectResolutionListener(this);
        
    }
    
    public void setDelegateResolutionListener(ResolutionListener listen) {
        listener = listen;
    }
    
    public void clearDelegateResolutionListener() {
        setDelegateResolutionListener(null);
    }
    
    public void  testArtifact(Artifact node) {
        if (listener != null) {
            listener.testArtifact(node);
        }
//        System.out.println("testArtifact" + node);
    }

    public void  startProcessChildren( Artifact artifact ) {
        if (listener != null) {
            listener.startProcessChildren(artifact);
        }
//        System.out.println("startProcessChildren" + artifact);
    }

    public void  endProcessChildren( Artifact artifact ){
        if (listener != null) {
            listener.endProcessChildren(artifact);
        }
//        System.out.println("endProcessChildren" + artifact);
    }

    public void  includeArtifact( Artifact artifact ){
        if (listener != null) {
            listener.includeArtifact(artifact);
        }
//        System.out.println("includeArtifact" + artifact);
    }

    public void  omitForNearer( Artifact omitted, Artifact kept ) {
        if (listener != null) {
            listener.omitForNearer(omitted, kept);
        }
//        System.out.println("omitted.. kept" + kept);
    }

    public void  updateScope( Artifact artifact, String scope ){
        if (listener != null) {
            listener.updateScope(artifact, scope);
        }
//        System.out.println("update scope");
    }

    public void  manageArtifact( Artifact artifact, Artifact replacement ){
        if (listener != null) {
            listener.manageArtifact(artifact, replacement);
        }
//        System.out.println("MANAGE Artifact=" + artifact + " replacement=" + replacement);
    }

    public void  omitForCycle( Artifact artifact ){
        if (listener != null) {
            listener.omitForCycle(artifact);
        }
//        System.out.println("omit cycle" + artifact);
    }

    public void  updateScopeCurrentPom( Artifact artifact, String scope ){
        if (listener != null) {
            listener.updateScopeCurrentPom(artifact, scope);
        }
//        System.out.println("update scope");
    }

    public void  selectVersionFromRange( Artifact artifact ){
        if (listener != null) {
            listener.selectVersionFromRange(artifact);
        }
//        System.out.println("select version");
    }

    public void  restrictRange( Artifact artifact, Artifact replacement, VersionRange newRange ){
        if (listener != null) {
            listener.restrictRange(artifact, replacement, newRange);
        }
//        System.out.println("restrict range");
    }
    
}
