/*
 * MavenNbModuleImpl.java
 *
 * Created on February 18, 2007, 12:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.apisupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.jar.Manifest;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.project.Project;
import org.netbeans.modules.apisupport.project.NbModuleTypeProvider;
import org.netbeans.modules.apisupport.project.spi.NbModuleImplementation;
import org.netbeans.modules.apisupport.project.universe.NbPlatform;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.SpecificationVersion;

/**
 *
 * @author mkleint
 */
public class MavenNbModuleImpl implements NbModuleImplementation, NbModuleTypeProvider {
    private Project project;
    
    /** Creates a new instance of MavenNbModuleImpl */
    public MavenNbModuleImpl(Project project) {
        this.project = project;
    }
    
    private File getModuleXmlLocation() {
        String file = PluginPropertyUtils.getPluginProperty(project, 
                "org.codehaus.mojo", 
                "nbm-maven-plugin",
                "descriptor", null);
        return new File(FileUtil.toFile(project.getProjectDirectory()), file);
    }
    
    private Xpp3Dom getModuleDom() throws UnsupportedEncodingException, IOException, XmlPullParserException {
        //TODO convert to FileOBject and have the IO stream from there..
        FileInputStream is = new FileInputStream(getModuleXmlLocation());
        Reader reader = new InputStreamReader(is, "UTF-8");
        try {
            return Xpp3DomBuilder.build(reader);
        } finally {
            IOUtil.close(reader);
        }
    }
    
    public String getSpecVersion() {
        //TODO
        return "1.0";
    }

    public String getCodeNameBase() {
        try {
            Xpp3Dom dom = getModuleDom();
            Xpp3Dom cnb = dom.getChild("codeNameBase");
            if (cnb != null) {
                System.out.println("cnb=" + cnb.getValue());
                String val = cnb.getValue();
                if (val.indexOf( "/") > -1) {
                    val = val.substring(0, val.indexOf("/") - 1);
                }
                return val;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MavenProject prj = project.getLookup().lookup(ProjectURLWatcher.class).getMavenProject();
        System.out.println("a fallback codenamebase=" + prj.getGroupId() + "." + prj.getArtifact());
        return prj.getGroupId() + "." + prj.getArtifact();
    }

    public String getSourceDirectoryPath() {
        //TODO
        return "src/main/java";
    }

    public FileObject getSourceDirectory() {
        FileObject fo = project.getProjectDirectory().getFileObject(getSourceDirectoryPath());
        if (fo == null) {
            try         {
                fo = FileUtil.createFolder(project.getProjectDirectory(),
                                           getSourceDirectoryPath());
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return fo;
    }

    public FileObject getManifestFile() {
        try {
            Xpp3Dom dom = getModuleDom();
            Xpp3Dom cnb = dom.getChild("manifest");
            if (cnb != null) {
                return project.getProjectDirectory().getFileObject(cnb.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Manifest getManifest() {
        FileObject manifestFO = getManifestFile();
        if (manifestFO != null) {
            try {
                InputStream is = manifestFO.getInputStream();
                try {
                    return new Manifest(is);
                } finally {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getResourceDirectoryPath(boolean isTest) {
        if (isTest) {
            return "src/test/resources";
        }
        return "src/main/resources";
    }

    public FileObject getResourceDirectory(boolean isTest) {
        FileObject fo = project.getProjectDirectory().getFileObject(getResourceDirectoryPath(isTest));
        if (fo == null) {
            try {
                fo = FileUtil.createFolder(project.getProjectDirectory(),
                                           getResourceDirectoryPath(isTest));
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return fo;
    }

    public boolean addDependency(String codeNameBase, String releaseVersion,
                                 SpecificationVersion version,
                                 boolean useInCompiler) throws IOException {
        return true;
    }

    public SpecificationVersion getDepedencyVersion(String codenamebase) throws IOException {
        return new SpecificationVersion("1.0");
    }

    public NbModuleType getModuleType() {
        return NbModuleTypeProvider.STANDALONE;
    }

    public NbPlatform getPlatform(boolean arg0) {
        //TODO
        return NbPlatform.getDefaultPlatform();
    }

}
