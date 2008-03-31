/*
 *  Copyright 2008 mkleint.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.codehaus.mevenide.netbeans.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.embedder.MavenSettingsSingleton;
import org.codehaus.mevenide.netbeans.embedder.writer.WriterUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 * Various maven model related utilities.
 * @author mkleint
 * @author Anuradha G
 */
public final class ModelUtils {

    /**
     * 
     * @param pom       FolderObject that represent POM 
     * @param group     
     * @param artifact
     * @param version
     * @param type
     * @param scope
     * @param classifier
     * @param acceptNull accept null values to scope,type and classifier.
     *                   If true null values will remove corresponding tag.
     */
    public static void addDependency(FileObject pom,
            String group,
            String artifact,
            String version,
            String type,
            String scope,
            String classifier, boolean acceptNull) {

        Model model = WriterUtils.loadModel(pom);
        if (model != null) {
            Dependency dep = PluginPropertyUtils.checkModelDependency(model, group, artifact, true);
            dep.setVersion(version);
            if (acceptNull || scope != null) {
                dep.setScope(scope);
            }
            if (acceptNull || type != null) {
                dep.setType(type);
            }
            if (acceptNull || classifier != null) {
                dep.setClassifier(classifier);
            }
            try {
                WriterUtils.writePomModel(pom, model);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Get all possible profiles defined .
     * 
     * @param mavenProject 
     * @return
     */
    public static List<String> retrieveAllProfiles(MavenProject mavenProject) {
        Set<String> profileIds = new HashSet<String>();
        //Add settings file Properties
        profileIds.addAll(MavenSettingsSingleton.getInstance().createUserSettingsModel().
                getProfilesAsMap().keySet());
        MavenProject root = getRootMavenProject(mavenProject);


        exteactProfiles(profileIds, root.getBasedir(), root.getModel());



        return new ArrayList<String>(profileIds);
    }

    private static MavenProject getRootMavenProject(MavenProject mavenProject) {
        if (mavenProject.getParent() != null) {

            return getRootMavenProject(mavenProject.getParent());
        }

        return mavenProject;
    }

    private static void exteactProfiles(Set<String> profileIds, File file, Model model) {
        
        File basedir = FileUtil.normalizeFile(file);
        FileObject fileObject = FileUtil.toFileObject(basedir);
        //read from profiles.xml
        Iterator it2 = MavenSettingsSingleton.createProfilesModel(fileObject).getProfiles().iterator();
        while (it2.hasNext()) {
            Profile prof = (Profile) it2.next();
            profileIds.add(prof.getId());
        }
        //read from modle
        List<Profile> profiles = model.getProfiles();
        for (Profile profile : profiles) {
            profileIds.add(profile.getId());
        }

        List<String> modules = model.getModules();
        for (String name : modules) {
            File dir = FileUtil.normalizeFile(new File(file, name));
            File pom = FileUtil.normalizeFile(new File(dir, "pom.xml"));//NOI18N

            try {

                Model readModel = EmbedderFactory.getProjectEmbedder().readModel(pom);
                exteactProfiles(profileIds, dir, readModel);
            } catch (XmlPullParserException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

    }
}
