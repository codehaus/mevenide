/*
 *  Copyright 2008 Anuradha.
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.M2AuxilaryConfigImpl;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.embedder.MavenSettingsSingleton;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Anuradha G
 */
public class ProfileUtils {

    private static final String PROFILES = "profiles";//NOI18N
    private static final String ACTIVEPROFILES = "activeProfiles";//NOI18N
    private static final String SEPERATOR = " ";//NOI18N
    private static final String NAMESPACE = null;//FIXME add propper namespase

    /**
     * 
     * 
     */
    private static AuxiliaryConfiguration getAuxiliaryConfiguration(FileObject pom) {
        Project owner = FileOwnerQuery.getOwner(pom);
        if (owner != null) {
            return owner.getLookup().lookup(M2AuxilaryConfigImpl.class);
        }
        return null;
    }

    public static List<String> retrieveActiveProfiles(MavenProject mavenProject) {
        Set<String> prifileides = new HashSet<String>();
        List<Profile> profiles = mavenProject.getActiveProfiles();
        for (Profile profile : profiles) {
            prifileides.add(profile.getId());
        }
        //read from Settings.xml
        List<String> profileStrings = MavenSettingsSingleton.getInstance().createUserSettingsModel().getActiveProfiles();
        for (String profile : profileStrings) {
            prifileides.add(profile);
        }
        
        File basedir = FileUtil.normalizeFile(mavenProject.getBasedir());
        FileObject fileObject = FileUtil.toFileObject(basedir);
        //read from profiles.xml
        Iterator it2 = MavenSettingsSingleton.createProfilesModel(fileObject).getActiveProfiles().iterator();
        while (it2.hasNext()) {
          
            prifileides.add((String) it2.next());
        }
        return new ArrayList<String>(prifileides);
    }

    public static List<String> retrieveMergedActiveProfiles(MavenProject mavenProject, boolean shared, String... includes) {
        AuxiliaryConfiguration ac = getAuxiliaryConfiguration(FileUtil.toFileObject(mavenProject.getFile()));
        if (ac == null) {
            return Collections.<String>emptyList();
        }
        Set<String> prifileides = new HashSet<String>();

        prifileides.addAll(retrieveActiveProfiles(mavenProject));

        List<String> retrieveActiveProfiles = retrieveActiveProfiles(ac, shared);
        for (String profile : retrieveActiveProfiles) {
            prifileides.add(profile);
        }

        for (String profileIds : includes) {
            prifileides.add(profileIds);
        }
        return new ArrayList<String>(prifileides);
    }

    public static List<String> retrieveActiveProfiles(FileObject pom, boolean shared) {
        AuxiliaryConfiguration ac = getAuxiliaryConfiguration(pom);
        if (ac == null) {
            return Collections.<String>emptyList();
        }

        return retrieveActiveProfiles(ac, shared);
    }

    public static void enableProfile(FileObject pom, String id, boolean shared) {
        AuxiliaryConfiguration ac = getAuxiliaryConfiguration(pom);
        if (ac == null) {
            return;
        }

        Element element = ac.getConfigurationFragment(PROFILES, NAMESPACE, shared);
        if (element == null) {

            String root = "project-private"; // NOI18N"

            Document doc = XMLUtil.createDocument(root, NAMESPACE, null, null);
            element = doc.createElementNS(NAMESPACE, PROFILES);
        }


        String activeProfiles = element.getAttributeNS(NAMESPACE, ACTIVEPROFILES);
        element.setAttributeNS(NAMESPACE, ACTIVEPROFILES, activeProfiles + SEPERATOR + id);
        ac.putConfigurationFragment(element, shared);

    }

    public static void disableProfile(FileObject pom, String id, boolean shared) {
        AuxiliaryConfiguration ac = getAuxiliaryConfiguration(pom);
        if (ac == null) {
            return;
        }

        Element element = ac.getConfigurationFragment(PROFILES, NAMESPACE, shared);
        if (element == null) {

            String root = "project-private"; // NOI18N"

            Document doc = XMLUtil.createDocument(root, NAMESPACE, null, null);
            element = doc.createElementNS(NAMESPACE, PROFILES);
        }
        String activeProfiles = element.getAttributeNS(NAMESPACE, ACTIVEPROFILES);

        if (activeProfiles != null && activeProfiles.length() > 0) {
            StringTokenizer tokenizer = new StringTokenizer(activeProfiles, SEPERATOR);
            Set<String> set = new HashSet<String>(tokenizer.countTokens());
            while (tokenizer.hasMoreTokens()) {
                set.add(tokenizer.nextToken());
            }
            set.remove(id);
            StringBuffer buffer = new StringBuffer();
            for (String profle : set) {
                buffer.append(profle).append(SEPERATOR);
            }
            element.setAttributeNS(NAMESPACE, ACTIVEPROFILES, buffer.toString().trim());
        }

        ac.putConfigurationFragment(element, shared);

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

    private static List<String> retrieveActiveProfiles(AuxiliaryConfiguration ac, boolean shared) {

        Set<String> prifileides = new HashSet<String>();
        Element element = ac.getConfigurationFragment(PROFILES, NAMESPACE, shared);
        if (element != null) {

            String activeProfiles = element.getAttributeNS(NAMESPACE, ACTIVEPROFILES);

            if (activeProfiles != null && activeProfiles.length() > 0) {
                StringTokenizer tokenizer = new StringTokenizer(activeProfiles, SEPERATOR);

                while (tokenizer.hasMoreTokens()) {
                    prifileides.add(tokenizer.nextToken());
                }
            }
        }
        return new ArrayList<String>(prifileides);
    }

    private static void exteactProfiles(Set<String> profileIds, File file, Model model) {

        File basedir = FileUtil.normalizeFile(file);
        FileObject fileObject = FileUtil.toFileObject(basedir);
        //read from profiles.xml
        Iterator it2 = MavenSettingsSingleton.createProfilesModel(fileObject).getProfiles().iterator();
        while (it2.hasNext()) {
            org.apache.maven.settings.Profile prof = (org.apache.maven.settings.Profile) it2.next();
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
                if(pom.exists()){
                    Model readModel = EmbedderFactory.getProjectEmbedder().readModel(pom);
                    exteactProfiles(profileIds, dir, readModel);
                }
            } catch (XmlPullParserException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

    }
}
