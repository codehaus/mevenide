/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.codehaus.mevenide.m2.embedder;

import java.io.File;
import java.util.List;
import org.apache.maven.GoalNotFoundException;
import org.apache.maven.Maven;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.mevenide.m2.logging.Logger;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.embed.Embedder;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class M2Embedder {
    
    private static Logger logger = Logger.getLogger(M2Embedder.class);
    
    private String file;
    private List goals;
    private List options;

    private Maven maven;
    
    public M2Embedder() {
    }
    
    public void run() throws EmbedderException {
        Embedder embedder = new Embedder();
        startEmbedder(embedder);
        
        maven = lookupMaven(embedder);
        initialize();
        
        execute();
     
    }

    private void execute() throws EmbedderException {
        try {
            maven.execute(new File(file), goals);
        }
        catch (ProjectBuildingException e) {
            String message = "Unable to build Project instance";
            handleError(message, e);
        }
        catch (GoalNotFoundException e) {
            String message = "Unable to run goals";
            handleError(message, e);
        }
    }


    private void initialize() throws EmbedderException {
        
        maven.setMavenHome( "E:/m2" );
        maven.setLocalRepository( System.getProperty("user.home") + "/.maven/repository" );
        
        try {
            maven.booty();
        }
        catch (Exception e) {
            String message = "Unable to initialize Maven component";
            handleError(message, e);
        }
    }

    private Maven lookupMaven(Embedder embedder) throws EmbedderException {
        try {
            return (Maven) embedder.lookup(Maven.ROLE);
        }
        catch (ComponentLookupException e) {
            String message = "Unable to lookup Maven component";
            logger.error(message, e);
            throw new EmbedderException(message, e);
        }
    }

    private void startEmbedder(Embedder embedder) throws EmbedderException {
        try {
	        ClassWorld classWorld = new ClassWorld("core", this.getClass().getClassLoader());
            embedder.start(classWorld);
        }
        catch (Exception e) {
            String message = "Unable to start embedder";
            handleError(message, e);
        }
    }

    private void handleError(String message, Throwable e) throws EmbedderException {
        logger.error(message, e);
        throw new EmbedderException(message, e);
    }
    
    public void setFile(String file) {
        this.file = file;
    }
    
    public void setGoals(List goals) {
        this.goals = goals;
    }
    
    public void setOptions(List options) {
        this.options = options;
    }
}
