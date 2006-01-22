/*
 * ModelRunConfig.java
 *
 * Created on January 16, 2006, 6:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.execute;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.maven.model.Plugin;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.mevenide.netbeans.execute.model.SimplePluginConfig;
import org.codehaus.mevenide.netbeans.execute.model.io.xpp3.NetbeansBuildActionXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.openide.filesystems.FileUtil;

/**
 * run configuration backed up by model
 * @author mkleint
 */
public final class ModelRunConfig implements RunConfig {

    private ClassLoader classloader;

    private NetbeansActionMapping model;

    private NbMavenProject project;
    
    /** Creates a new instance of ModelRunConfig */
    public ModelRunConfig(NbMavenProject proj, NetbeansActionMapping mod, ClassLoader loader) {
        project = proj;
        model = mod;
        classloader = loader;
    }
    
    public ModelRunConfig(NbMavenProject proj, Reader modelXml, ClassLoader loader) 
                             throws IOException, XmlPullParserException {
        project = proj;
        classloader = loader;
        model = new NetbeansBuildActionXpp3Reader().read(modelXml);
    }

    public File getExecutionDirectory() {
        return FileUtil.toFile(project.getProjectDirectory());
    }

    public NbMavenProject getProject() {
        return project;
    }

    public List getGoals() {
        Iterator it = model.getGoals().iterator();
        while (it.hasNext()) {
            String elem = (String)it.next();
            System.out.println("model goal=" + elem);
                    }
        return model.getGoals();
    }

    public String getExecutionName() {
        return project.getName();
    }

    public Properties getProperties() {
        return model.getProperties();
    }

    public ClassLoader getClassLoader() {
        return classloader;
    }

    public List getAdditionalPluginConfigurations() {
        List toReturn = new ArrayList();
        Iterator it = model.getPlugins().iterator();
        while (it.hasNext()) {
            SimplePluginConfig elem = (SimplePluginConfig)it.next();
            Plugin plug = new Plugin();
            plug.setGroupId(elem.getGroupId());
            plug.setArtifactId(elem.getArtifactId());
            plug.setVersion(elem.getVersion());
            plug.setConfiguration(elem.getConfiguration());
            toReturn.add(plug);
        }
        return toReturn;
    }
    
}
