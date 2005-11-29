/*
 * MyResolutionListener.java
 *
 * Created on November 29, 2005, 7:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
