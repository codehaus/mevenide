/*
 * ArchetypeNGProjectCreator.java
 * 
 * Created on Jul 30, 2007, 8:28:28 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.spi.archetype;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.maven.artifact.Artifact;
import org.openide.WizardDescriptor;

/**
 * a custom project creation code based on ArchetypeNG. Expected to be 
 * exactly one implementation registered in META-INF/services lookup.
 * @author mkleint
 */
public interface ArchetypeNGProjectCreator {

    void runArchetype(File directory, WizardDescriptor wiz) throws IOException;
    
    Map<String, String> getAdditionalProperties(Artifact art);
}
