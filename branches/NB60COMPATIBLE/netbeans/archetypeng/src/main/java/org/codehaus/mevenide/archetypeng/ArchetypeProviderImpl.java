/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.archetypeng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.apache.maven.archetype.metadata.ArchetypeDescriptor;
import org.apache.maven.archetype.metadata.RequiredProperty;
import org.apache.maven.archetype.metadata.io.xpp3.ArchetypeDescriptorXpp3Reader;
import org.apache.maven.artifact.Artifact;
import org.codehaus.mevenide.netbeans.api.archetype.Archetype;
import org.codehaus.mevenide.netbeans.api.execute.RunUtils;
import org.codehaus.mevenide.netbeans.execute.BeanRunConfig;
import org.codehaus.mevenide.netbeans.spi.archetype.ArchetypeNGProjectCreator;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.openide.WizardDescriptor;
import org.openide.execution.ExecutorTask;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author mkleint
 */
public class ArchetypeProviderImpl implements ArchetypeNGProjectCreator {
    private static final String USER_DIR_PROP = "user.dir"; //NOI18N

    public void runArchetype(File directory, WizardDescriptor wiz) throws IOException {
        Properties propFile = new Properties();
        propFile.setProperty("artifactId", (String)wiz.getProperty("artifactId")); //NOI18N
        propFile.setProperty("version", (String)wiz.getProperty("version")); //NOI18N
        propFile.setProperty("groupId", (String)wiz.getProperty("groupId")); //NOI18N
        final String pack = (String)wiz.getProperty("package"); //NOI18N
        if (pack != null && pack.trim().length() > 0) {
            propFile.setProperty("package", pack); //NOI18N
        }
        final Archetype arch = (Archetype)wiz.getProperty("archetype"); //NOI18N
        propFile.setProperty("archetype.artifactId", arch.getArtifactId()); //NOI18N
        propFile.setProperty("archetype.groupId", arch.getGroupId()); //NOI18N
        propFile.setProperty("archetype.version", arch.getVersion()); //NOI18N
        
        @SuppressWarnings("unchecked")
        HashMap<String, String> additional = (HashMap<String, String>)wiz.getProperty("additionalProps");
        if (additional != null) {
            for (String key : additional.keySet()) {
                propFile.setProperty(key, additional.get(key));
            }
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(directory, "archetype.properties"));
            propFile.store(out, "Properties for creating new project from archetype");
        } finally {
            IOUtil.close(out);
        }
        BeanRunConfig config = new BeanRunConfig();
        config.setActivatedProfiles(Collections.EMPTY_LIST);
        config.setExecutionDirectory(directory);
        config.setExecutionName(NbBundle.getMessage(ArchetypeProviderImpl.class, "RUN_Project_Creation"));
        //TODO externalize somehow to allow advanced users to change the value..
        config.setGoals(Collections.singletonList("org.apache.maven.plugins:maven-archetypeng-plugin:1.0-SNAPSHOT:generate-project")); //NOI18N
        Properties props = new Properties();
        if (arch.getRepository() != null) {
            props.setProperty("remoteRepositories", arch.getRepository()); //NOI18N
        }
        config.setProperties(props);
        config.setTaskDisplayName(NbBundle.getMessage(ArchetypeProviderImpl.class, "RUN_Maven"));
        // setup executor now..
        //hack - we need to setup the user.dir sys property..
        String oldUserdir = System.getProperty(USER_DIR_PROP); //NOI18N
        System.setProperty(USER_DIR_PROP, directory.getAbsolutePath()); //NOI18N
        try {
            ExecutorTask task = RunUtils.executeMaven(config); //NOI18N
            task.result();
        } finally {
            if (oldUserdir == null) {
                System.getProperties().remove(USER_DIR_PROP); //NOI18N
            } else {
                System.setProperty(USER_DIR_PROP, oldUserdir); //NOI18N
            }
        }
    }

    public Map<String, String> getAdditionalProperties(Artifact art) {
        HashMap<String, String> map = new HashMap<String, String>();
        File fil = art.getFile();
        JarFile jf = null;
        try {
            jf = new JarFile(fil);
            ZipEntry entry = jf.getJarEntry("META-INF/maven/archetype-metadata.xml");
            if (entry != null) {
                InputStream in = jf.getInputStream(entry);
                Reader rd = new InputStreamReader(in);
                ArchetypeDescriptorXpp3Reader reader = new ArchetypeDescriptorXpp3Reader();
                ArchetypeDescriptor desc = reader.read(rd);
                List lst = desc.getRequiredProperties();
                if (lst != null && lst.size() > 0) {
                    Iterator it = lst.iterator();
                    while (it.hasNext()) {
                        RequiredProperty prop = (RequiredProperty) it.next();
                        map.put(prop.getKey(), prop.getDefaultValue());
                    }
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (XmlPullParserException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (jf != null) {
                try {
                    jf.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return map;
    }

}
