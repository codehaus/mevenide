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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.LoadProperties;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.Move;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.FileUtils;
import org.netbeans.nbbuild.CreateModuleXML;
import org.netbeans.nbbuild.MakeListOfNBM;
import org.netbeans.nbbuild.MakeNBM;
import org.netbeans.nbbuild.MakeNBM.Blurb;
import org.netbeans.nbbuild.MakeNBM.Signature;

/**
 *
 * @author <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 *   execute XXXlifecycle="nbm"
 * @goal nbm
 * @phase package
 * @description build a nbm file
 * @execute phase="package" 
 * *
 */
public class CreateNbmMojo
    extends AbstractNbmMojo
{
    /**
     * @parameter expression="${project.build.directory}/nbm"
     * @required
     */
    protected String nbmBuildDir;

    /**
     * @todo Change type to File
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    private String buildDir;

    /**
     * 
     * @parameter alias="jarName" expression="${project.build.finalName}"
     * @required
     */
    private String finalName;

    /**
     * a module name, used for module name in jar and the actual jar name in nbm
     * @parameter alias="moduleName" expression="${project.groupId}"
     * @required
     */
    private String moduleName;
    
    /**
     * the module is autoload - cannot be both eager and autoload
     * @parameter defaultvalue="false"
     */
    private boolean autoload;
    
    /**
     * the module is eager - cannot be both eager and autoload
     * @parameter defaultvalue="false"
     */
    private boolean eager;
    
    /**
     * the cluster the module belongs to
     * @parameter expression="${project.groupId}"
     */
    
    private String cluster;
    
    /**
     * true if module requires restart of Netbeans when installing.
     * @parameter defaultvalue="false"
     */
    private boolean needsRestart;
    
    /**
     * author of the netbeans module
     * @parameter expression="${project.organization.name}"
     */
    private String moduleAuthor;
    
    /**
     * homepage of the netbeans module
     * @parameter expression="${project.url}
     */
    private String homepageUrl;
    
    /**
     * distribution url for nbm file. That's where the autoupdate file will link to and where
     * users download the module from.
     * @parameter expression="${project.url}
     */
    private String distributionUrl;
    
    /**
     * name for the license.
     * @parameter
     */
    private String licenseName;
    
    /**
     * a file with content of the license
     * //TODO have some basic value??
     * @parameter 
     */
    private String licenseFile;
    
    /**
     * keystore location for signing the nbm file
     * @parameter
     */
    private String keystore;
    
    /**
     * keystore password
     * @parameter
     */
    private String keystorepassword;
    
    /**
     * keystore alias
     * @parameter
     */
    private String keystorealias;
    
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;


    public void execute()
        throws MojoExecutionException
    {
        getLog().info("CreateNbmMojo");
        Project antProject = registerNbmAntTasks();
        
        // 1. initialization
        if (autoload && eager) {
            getLog().error("Module cannot be both eager and autoload");
            throw new MojoExecutionException("Module cannot be both eager and autoload");
        }
        
        String moduleJarName = moduleName.replace('.', '-');

        // it can happen the moduleName is in format org.milos/1
            int index = moduleJarName.indexOf('/');
            if (index > -1) {
                moduleJarName = moduleJarName.substring(0, index).trim();
            } 
        
        File jarFile = new File( nbmBuildDir, finalName + ".jar");
        File nbmFile = new File( nbmBuildDir, finalName + ".nbm");
        File clusterDir = new File(nbmBuildDir, "netbeans" + File.separator + cluster);
        String moduleLocation = "modules";
        if (eager) {
            moduleLocation = moduleLocation + File.separator + "eager";
        }
        if (autoload) {
            moduleLocation = moduleLocation + File.separator + "autoload";
        }
        File moduleJarLocation = new File(clusterDir, moduleLocation);
        moduleJarLocation.mkdirs();
        
        //2. create nbm resources
        File moduleFile = new File(moduleJarLocation, moduleJarName + ".jar");
        try {
            FileUtils.newFileUtils().copyFile(jarFile, moduleFile);
        } catch (IOException ex) {
            getLog().error("Cannot copy module jar");
            throw new MojoExecutionException("Cannot copy module jar", ex);
        }

        File configDir = new File(clusterDir, "config" + File.separator + "Modules");
        configDir.mkdirs();
        CreateModuleXML moduleXmlTask = (CreateModuleXML)antProject.createTask("createmodulexml");
        moduleXmlTask.setXmldir(configDir);
        FileSet fs = new FileSet();
        fs.setDir(moduleJarLocation);
        fs.setIncludes(moduleJarName + ".jar");
        if (autoload) {
            moduleXmlTask.addAutoload(fs);
        } 
        else if (eager) {
            moduleXmlTask.addEager(fs);
        } else {
            moduleXmlTask.addEnabled(fs);
        }
        try {
            moduleXmlTask.execute();
        } catch (BuildException e)
        {
            getLog().error( "Cannot generate config file." );
            throw new MojoExecutionException( e.getMessage(), e );
        }
        LoadProperties loadTask = (LoadProperties)antProject.createTask("loadproperties");
        loadTask.setResource("directories.properties");
        try {
            loadTask.execute();
        } catch (BuildException e)
        {
            getLog().error( "Cannot load properties." );
            throw new MojoExecutionException( e.getMessage(), e );
        }
        MakeListOfNBM makeTask = (MakeListOfNBM)antProject.createTask("genlist");
        antProject.setNewProperty("module.name", moduleJarName);
        antProject.setProperty("cluster.dir", "netbeans" + File.separator + cluster);
        FileSet set = makeTask.createFileSet();
        set.setDir(clusterDir);
        PatternSet pattern = set.createPatternSet();
        pattern.setIncludes("**");
        makeTask.setModule(moduleLocation + File.separator + moduleJarName + ".jar");
        makeTask.setOutputfiledir(clusterDir);
        try {
            makeTask.execute();
        } catch (BuildException e)
        {
            getLog().error( "Cannot Generate nbm list" );
            throw new MojoExecutionException( e.getMessage(), e );
        }
        
        // 3. generate nbm
        MakeNBM nbmTask = (MakeNBM)antProject.createTask("makenbm");
        nbmTask.setFile(nbmFile);
        nbmTask.setProductDir(clusterDir);
        nbmTask.setModule(moduleLocation + File.separator + moduleJarName + ".jar");
        nbmTask.setNeedsrestart(Boolean.toString(needsRestart));
        nbmTask.setModuleauthor(moduleAuthor);
        if (keystore != null && keystorealias != null && keystorepassword != null) {
            File ks = new File(keystore);
            if (!ks.exists()) {
                getLog().warn("Cannot find keystore file at " + ks.getAbsolutePath());
            } else {
                Signature sig = nbmTask.createSignature();
                sig.setKeystore(ks);
                sig.setAlias(keystorealias);
                sig.setStorepass(keystorepassword);
            }
        } else if (keystore != null || keystorepassword != null || keystorealias != null) {
            getLog().warn("If you want to sign the nbm file, you need to define all three keystore related parameters.");
        }
        if (licenseName != null && licenseFile != null) {
            File lf = new File(licenseFile);
            if (!lf.exists()) {
                getLog().warn("Cannot find license file at " + lf.getAbsolutePath());
            } else {
                Blurb lb = nbmTask.createLicense();
                lb.setFile(lf);
                lb.setName(licenseName);
            }
        } else if (licenseName != null || licenseFile != null) {
            getLog().warn("To add a license to the nbm, you need to specify both licenseName and licenseFile parameters");
        } else {
            Blurb lb = nbmTask.createLicense();
            lb.addText("<Here comes the license>");
            lb.setName("Unknown license agreement");
        }
        if (homepageUrl != null) {
            nbmTask.setHomepage(homepageUrl);
        }
        if (distributionUrl != null) {
            nbmTask.setDistribution(distributionUrl);
        } 
        try {
            nbmTask.execute();
        } catch (BuildException e)
        {
            getLog().error( "Cannot Generate nbm file" );
            throw new MojoExecutionException( e.getMessage(), e );
        }
        
        
        
    }
}
