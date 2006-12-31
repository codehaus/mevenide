/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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
package org.mevenide.tags.jbuilder.opentool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.apache.maven.repository.Artifact;
import org.jdom.JDOMException;
import org.mevenide.tags.AbstractMevenideTag;

/**
 * Generate a JBuiler configuration file for an OpenTool
 *
 * @author <a href="mailto:shuber2@jahia.com">Serge Huber</a>
 *
 */
public class GenerateConfig extends AbstractMevenideTag {

    private static final Log log = LogFactory.getLog(GenerateConfig.class);

    //context variable
    private static final String MAVEN_REPOSITORYLOCAL_PROPERTY = "maven.repo.local";
    private static final String DESCRIPTOR_DIR_PROPERTY = "maven.opentool.config.dir";
    private static final String CONFIG_FILENAME_PROPERTY = "maven.opentool.config.filename";

    /** the project descriptor of the JBuilder OpenTool under construction **/
    private Project pom;

    private FileOutputStream configFile;

    private boolean useLocalRepository = false;

    private String dependencyLocation = "";

    private boolean withThisArtifact = true;

    /*
     * (non-Javadoc)
     * @see org.apache.commons.jelly.Tag#doTag(org.apache.commons.jelly.XMLOutput)
     */
    public void doTag(XMLOutput arg0) throws MissingAttributeException, JellyTagException {
        try {
            setUpDescriptor();

            List artifacts = pom.getArtifacts();

            PrintWriter out = new PrintWriter(configFile);
            if ( artifacts != null ) {
                for (int i = 0; i < artifacts.size(); i++) {
                    Artifact artifact = (Artifact) artifacts.get(i);
                    if ("true".equals(artifact.getDependency().getProperty("jbuilder.opentool.bundle"))) {
                        if (useLocalRepository == true) {
                            out.println("addpath " + artifact.getPath());
                        } else {
                            out.println("addpath " + dependencyLocation +
                                        artifact.getName());
                        }
                    }
                 }
            }
            if (isWithThisArtifact()) {
                if (useLocalRepository) {
                    String localRepo = (String) context.getVariable(
                        MAVEN_REPOSITORYLOCAL_PROPERTY);
                    out.println("addpath " + localRepo + "/" +
                                pom.getGroupId() + "/jars/" +
                                pom.getArtifactId() + "-" +
                                pom.getCurrentVersion() + ".jar");
                } else {
                    out.println("addpath " + dependencyLocation +
                                pom.getArtifactId() + "-" +
                                pom.getCurrentVersion() + ".jar");
                }

            }
            out.flush();
            out.close();
            configFile.close();

        } catch (Exception exc) {
            throw new JellyTagException(exc);
        }

    }

    /**
     * setup descriptor from the plugin descriptor template, so dependencies will be refreshed at each build
     */
     void setUpDescriptor() throws JDOMException, IOException {
        String descriptorDirectory = (String) context.getVariable(DESCRIPTOR_DIR_PROPERTY);
        File descriptorDirFile = new File(descriptorDirectory);
        descriptorDirFile.mkdirs();
        String configFileName = (String) context.getVariable(CONFIG_FILENAME_PROPERTY);
        File descriptorPath = new File(descriptorDirectory, configFileName).getCanonicalFile();

        log.debug(descriptorPath);

        configFile = new FileOutputStream(descriptorPath);

    }

    public Project getPom() {
        return pom;
    }

    /** the project descriptor of the Eclipse plugin under construction **/
    public void setPom(Project pom) throws MissingAttributeException {
        checkAttribute(pom, "pom");
        this.pom = pom;
    }

    public void setUseLocalRepository(boolean useLocalRepository) {
        this.useLocalRepository = useLocalRepository;
    }

    public boolean isUseLocalRepository() {
        return useLocalRepository;
    }

    public void setDependencyLocation(String dependencyLocation) {
        this.dependencyLocation = dependencyLocation;
    }

    public String getDependencyLocation() {
        return dependencyLocation;
    }

    public void setWithThisArtifact(boolean withThisArtifact) {
        this.withThisArtifact = withThisArtifact;
    }

    public boolean isWithThisArtifact() {
        return withThisArtifact;
    }

}
