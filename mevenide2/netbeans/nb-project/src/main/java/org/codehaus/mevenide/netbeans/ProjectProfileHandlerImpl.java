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
package org.codehaus.mevenide.netbeans;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.build.model.ModelLineage;
import org.codehaus.mevenide.netbeans.api.ProjectProfileHandler;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.embedder.MavenSettingsSingleton;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Anuradha G
 */
public class ProjectProfileHandlerImpl implements ProjectProfileHandler {

    private static final String PROFILES = "profiles";//NOI18N
    private static final String ACTIVEPROFILES = "activeProfiles";//NOI18N
    private static final String SEPERATOR = " ";//NOI18N
    private static final String NAMESPACE = null;//FIXME add propper namespase
    private List<String> privateProfiles = new ArrayList<String>();
    private List<String> sharedProfiles = new ArrayList<String>();
    private AuxiliaryConfiguration ac;
    private NbMavenProject nmp;

    ProjectProfileHandlerImpl(NbMavenProject nmp, AuxiliaryConfiguration ac) {
        this.nmp = nmp;
        this.ac = ac;
        privateProfiles.addAll(retrieveActiveProfiles(ac, false));
        sharedProfiles.addAll(retrieveActiveProfiles(ac, true));
    }

    public List<String> getAllProfiles() {
        Set<String> profileIds = new HashSet<String>();
        //Add settings file Properties
        profileIds.addAll(MavenSettingsSingleton.getInstance().createUserSettingsModel().
                getProfilesAsMap().keySet());

        extractProfiles(profileIds, nmp.getPOMFile());
        return new ArrayList<String>(profileIds);
    }

    public List<String> getActiveProfiles(boolean shared) {
       return new ArrayList<String>(shared ? sharedProfiles : privateProfiles);
    }
    public List<String> getMergedActiveProfiles(boolean shared) {
                Set<String> prifileides = new HashSet<String>();
        MavenProject mavenProject = nmp.getOriginalMavenProject();
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
        prifileides.addAll(getActiveProfiles(shared));
        return new ArrayList<String>(prifileides);
    }

    public void disableProfile(String id, boolean shared) {
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
        if(shared){
            sharedProfiles.remove(id);
        }else{
            privateProfiles.remove(id);
        }
    }

    public void enableProfile(String id, boolean shared) {
        Element element = ac.getConfigurationFragment(PROFILES, NAMESPACE, shared);
        if (element == null) {

            String root = "project-private"; // NOI18N"

            Document doc = XMLUtil.createDocument(root, NAMESPACE, null, null);
            element = doc.createElementNS(NAMESPACE, PROFILES);
        }


        String activeProfiles = element.getAttributeNS(NAMESPACE, ACTIVEPROFILES);
        element.setAttributeNS(NAMESPACE, ACTIVEPROFILES, activeProfiles + SEPERATOR + id);
        ac.putConfigurationFragment(element, shared);
        if(shared){
            if(!sharedProfiles.contains(id))
             sharedProfiles.add(id);
        }else{
            if(!privateProfiles.contains(id))
             privateProfiles.add(id);
        }
    }

    private static void extractProfiles(Set<String> profileIds, File file) {
        ModelLineage lineage = EmbedderFactory.createModelLineage(file, EmbedderFactory.createOnlineEmbedder(), true);
        Iterator it = lineage.modelIterator();
        while (it.hasNext()) {
            Model mdl = (Model) it.next();
            List mdlProfiles = mdl.getProfiles();
            if (mdlProfiles != null) {
                Iterator it2 = mdlProfiles.iterator();
                while (it2.hasNext()) {
                    Profile prf = (Profile)it2.next();
                    profileIds.add(prf.getId());
                }
            }
        }
        File basedir = FileUtil.normalizeFile(file.getParentFile());
        FileObject fileObject = FileUtil.toFileObject(basedir);
        //read from profiles.xml
        Iterator it2 = MavenSettingsSingleton.createProfilesModel(fileObject).getProfiles().iterator();
        while (it2.hasNext()) {
            org.apache.maven.profiles.Profile prof = (org.apache.maven.profiles.Profile) it2.next();
            profileIds.add(prof.getId());
        }
    }

    private List<String> retrieveActiveProfiles(AuxiliaryConfiguration ac, boolean shared) {

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



 
}
