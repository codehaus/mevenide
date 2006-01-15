/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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


package org.apache.maven.plugin.nbm;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Input;
import org.apache.tools.ant.taskdefs.PathConvert;
import org.apache.tools.ant.types.FileSet;


/**
 * a goal for identifying netbeans modules from the installation and populationg the local
 * repository with them.
 *
 * @author <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 * @goal populate-repository
 * @requiresProject false
 * @aggregator
 */
public class PopulateRepositoryMojo
        extends AbstractNbmMojo {
    /**
     * Local maven repository.
     *
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    protected ArtifactRepository localRepository;
    
    /**
     * Location of netbeans installation
     *
     * @parameter expression="${netbeansInstallDirectory}"
     */
    
    protected String netbeansInstallDirectory;
    
    /**
     * Optional parameter, when specified, will force all modules to have the designated version.
     * Good when depending on releases. Then you would for example specify RELEASE50 in this parameter and
     * all modules get this version in the repository.
     * @parameter expression="${forcedVersion}"
     */
    protected String forcedVersion;
    
    /**
     * Maven ArtifactFactory.
     *
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;
    
    /**
     * Maven ArtifactInstaller.
     *
     * @parameter expression="${component.org.apache.maven.artifact.installer.ArtifactInstaller}"
     * @required
     * @readonly
     */
    private ArtifactInstaller artifactInstaller;    
    
    
    public void execute() throws MojoExecutionException {
        getLog().info("Populate repository with netbeans modules");
        Project antProject = registerNbmAntTasks();
        
        if (netbeansInstallDirectory == null) {
            Input input = (Input)antProject.createTask("input");
            input.setMessage("Please enter netbeans installation directory:");
            input.setAddproperty("installDir");
            try {
                input.execute();
            } catch (BuildException e) {
                getLog().error( "Cannot run ant:input" );
                throw new MojoExecutionException( e.getMessage(), e );
            }
            String prop = antProject.getProperty("installDir");
            netbeansInstallDirectory = prop;
        }
        
        File rootDir = new File(netbeansInstallDirectory);
        if (!rootDir.exists()) {
            getLog().error("Netbeans installation doesn't exist.");
            throw new MojoExecutionException("Netbeans installation doesn't exist.");
        }
        getLog().info("Copying Netbeans artifacts from " + netbeansInstallDirectory);
        
        PathConvert convert = (PathConvert)antProject.createTask("pathconvert");
        convert.setPathSep(",");
        convert.setProperty("netbeansincludes");
        FileSet set = new FileSet();
        set.setDir(rootDir);
        set.createInclude().setName("**/modules/*.jar");
        set.createInclude().setName("**/modules/autoload/*.jar");
        set.createInclude().setName("**/modules/eager/*.jar");
        set.createInclude().setName("platform*/core/*.jar");
        set.createInclude().setName("platform*/lib/*.jar");
        
        convert.createPath().addFileset(set);
        try {
            convert.execute();
        } catch (BuildException e) {
            getLog().error( "Cannot run ant:pathconvert" );
            throw new MojoExecutionException( e.getMessage(), e );
        }
        
        String prop = antProject.getProperty("netbeansincludes");
        StringTokenizer tok = new StringTokenizer(prop, ",");
        ExamineManifest examinator = new ExamineManifest();
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken();
            File module = new File(token);
            examinator.resetExamination();
            examinator.setJarFile(module);
            examinator.checkFile();
            if (examinator.isNetbeansModule()) {
                //TODO get artifact id from the module's manifest?
                String artifact = module.getName().substring(0, module.getName().indexOf(".jar"));
                String version = forcedVersion == null ? examinator.getSpecVersion() : forcedVersion;
                String group = "org.netbeans." + (examinator.hasPublicPackages() ? "api" : "modules");
                
                Artifact art = createArtifact(artifact, version, group);
                File pom = createMavenProject(artifact, version, group);
                ArtifactMetadata metadata = new ProjectArtifactMetadata(art, pom);
                art.addMetadata( metadata );
                
                try {
                    artifactInstaller.install(module, art, localRepository );
                } catch ( ArtifactInstallationException e ) {
                    // TODO: install exception that does not give a trace
                    throw new BuildException( "Error installing artifact", e );
                }
                
            }
        }
    }
    
    private File createMavenProject(String artifact, String version, String group) {
        Model mavenModel = new Model();

        mavenModel.setGroupId(group);
        mavenModel.setArtifactId(artifact);
        mavenModel.setVersion(version);
        mavenModel.setPackaging("jar");
        mavenModel.setModelVersion("4.0.0");
        FileWriter writer = null;
        File fil = null;
            try {
                MavenXpp3Writer xpp = new MavenXpp3Writer();
                fil = File.createTempFile("maven", "pom");
                writer = new FileWriter(fil);
                xpp.write(writer, mavenModel);
                return fil;
            } catch (IOException ex) {
                ex.printStackTrace();
            
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        }
        return fil;
    }
    
    
    private Artifact createArtifact(String artifact, String version, String group) {
        return artifactFactory.createBuildArtifact(group, artifact, version, "jar");
    }
}
