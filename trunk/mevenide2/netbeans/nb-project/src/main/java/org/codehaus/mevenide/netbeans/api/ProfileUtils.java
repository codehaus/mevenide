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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.M2ProfilesAuxilaryConfigImpl;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.embedder.MavenSettingsSingleton;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.w3c.dom.Element;

/**
 *
 * @author Anuradha G
 */
public class ProfileUtils {

    private static final String PROFILES = "profiles";//NOI18N
    private static final String ACTIVEPROFILES = "activeProfiles";//NOI18N
    private static final String INACTIVEPROFILES = "inactiveProfiles";//NOI18N
    private static final String SEPERATOR = " ";//NOI18N

    /**
     * 
     * 
     */
    private static AuxiliaryConfiguration getAuxiliaryConfiguration(FileObject pom) {
        Project owner = FileOwnerQuery.getOwner(getRootProjectPom(pom));
        if (owner != null) {
            return owner.getLookup().lookup(M2ProfilesAuxilaryConfigImpl.class);
        }
        return null;
    }

    public static List<String> retrieveMergedActiveProfiles(MavenProject mavenProject, boolean shared, String... includes) {
        AuxiliaryConfiguration ac = getAuxiliaryConfiguration(FileUtil.toFileObject(mavenProject.getFile()));
        if (ac == null) {
            return Collections.<String>emptyList();
        }
        Set<String> prifileides = new HashSet<String>();

        List<Profile> profiles = mavenProject.getActiveProfiles();
        for (Profile profile : profiles) {
            prifileides.add(profile.getId());
        }

        List<String> retrieveActiveProfiles = retrieveActiveProfiles(ac, shared);
        for (String profile : retrieveActiveProfiles) {
            prifileides.add(profile);
        }

        List<String> retrieveDisableProfiles = retrieveInactiveProfiles(ac, shared, new String[0]);
        for (String profile : retrieveDisableProfiles) {
            prifileides.remove(profile);
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

    public static List<String> retrieveInactiveProfiles(FileObject pom, boolean shared, String... excludes) {
        AuxiliaryConfiguration ac = getAuxiliaryConfiguration(pom);
        if (ac == null) {
            return Collections.<String>emptyList();
        }


        return retrieveInactiveProfiles(ac, shared, excludes);
    }

    public static void enableProfile(FileObject pom, String id, boolean shared) {
        AuxiliaryConfiguration ac = getAuxiliaryConfiguration(pom);
        if (ac == null) {
            return;
        }

        Element element = ac.getConfigurationFragment(PROFILES, null, shared);
        if (element != null) {
            String disableProfiles = element.getAttributeNS(null, INACTIVEPROFILES);

            if (disableProfiles != null && disableProfiles.length() > 0) {
                StringTokenizer tokenizer = new StringTokenizer(disableProfiles, SEPERATOR);
                Set<String> set = new HashSet<String>(tokenizer.countTokens());
                while (tokenizer.hasMoreTokens()) {
                    set.add(tokenizer.nextToken());
                }
                set.remove(id);
                StringBuffer buffer = new StringBuffer();
                for (String profle : set) {
                    buffer.append(profle).append(SEPERATOR);
                }
                element.setAttributeNS(null, INACTIVEPROFILES, buffer.toString().trim());
            }
            String activeProfiles = element.getAttributeNS(null, ACTIVEPROFILES);
            element.setAttributeNS(null, ACTIVEPROFILES, activeProfiles + SEPERATOR + id);
            ac.putConfigurationFragment(element, shared);
            Project[] openProjects = OpenProjects.getDefault().getOpenProjects();
            List<Project> projects = Arrays.asList(openProjects);
            reloadProjectGroup(FileUtil.toFile(getRootProjectPom(pom).getParent()), projects);
        }
    }

    public static void disableProfile(FileObject pom, String id, boolean shared) {
        AuxiliaryConfiguration ac = getAuxiliaryConfiguration(pom);
        if (ac == null) {
            return;
        }
        Element element = ac.getConfigurationFragment(PROFILES, null, shared);
        if (element != null) {
            String activeProfiles = element.getAttributeNS(null, ACTIVEPROFILES);

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
                element.setAttributeNS(null, ACTIVEPROFILES, buffer.toString().trim());
            }
            String disableProfiles = element.getAttributeNS(null, INACTIVEPROFILES);
            element.setAttributeNS(null, INACTIVEPROFILES, disableProfiles + SEPERATOR + id);
            ac.putConfigurationFragment(element, shared);
            Project[] openProjects = OpenProjects.getDefault().getOpenProjects();
            List<Project> projects = Arrays.asList(openProjects);
            reloadProjectGroup(FileUtil.toFile(getRootProjectPom(pom)).getParentFile(), projects);
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

    private static List<String> retrieveActiveProfiles(AuxiliaryConfiguration ac, boolean shared) {

        Set<String> prifileides = new HashSet<String>();
        Element element = ac.getConfigurationFragment(PROFILES, null, shared);
        if (element != null) {

            String activeProfiles = element.getAttributeNS(null, ACTIVEPROFILES);

            if (activeProfiles != null && activeProfiles.length() > 0) {
                StringTokenizer tokenizer = new StringTokenizer(activeProfiles, SEPERATOR);

                while (tokenizer.hasMoreTokens()) {
                    prifileides.add(tokenizer.nextToken());
                }
            }
        }
        return new ArrayList<String>(prifileides);
    }

    private static List<String> retrieveInactiveProfiles(AuxiliaryConfiguration ac, boolean shared, String... excludes) {

        Set<String> prifileides = new HashSet<String>();
        Element element = ac.getConfigurationFragment(PROFILES, null, shared);
        if (element != null) {


            String disableProfiles = element.getAttributeNS(null, INACTIVEPROFILES);

            if (disableProfiles != null && disableProfiles.length() > 0) {
                StringTokenizer tokenizer = new StringTokenizer(disableProfiles, SEPERATOR);

                while (tokenizer.hasMoreTokens()) {
                    prifileides.remove(tokenizer.nextToken());
                }


            }
        }
        for (String pid : excludes) {
            prifileides.remove(pid);
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

                Model readModel = EmbedderFactory.getProjectEmbedder().readModel(pom);
                exteactProfiles(profileIds, dir, readModel);
            } catch (XmlPullParserException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

    }

    private static void reloadProjectGroup(File basedir, List<Project> projects) {

        Project project;
        try {
            project = ProjectManager.getDefault().findProject(FileUtil.toFileObject(basedir));
            if (project != null) {
                NbMavenProject mavProj = project.getLookup().lookup(NbMavenProject.class);

                if (mavProj != null && projects.contains(project)) {
                    ProjectURLWatcher.fireMavenProjectReload(project);
                }
                Model model =   EmbedderFactory.getProjectEmbedder().readModel(mavProj.getPOMFile());
                List<String> modules = model.getModules();
                for (String name : modules) {
                    reloadProjectGroup(new File(basedir, name), projects);
                }
            }
        } catch (XmlPullParserException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    private static FileObject getRootProjectPom(FileObject pom) {
        try {

            Model readModel = EmbedderFactory.getProjectEmbedder().readModel(FileUtil.toFile(pom));
            Parent parent = readModel.getParent();
            if (parent != null) {
                FileObject grandFo = pom.getParent().getParent();
                if (grandFo != null) {
                    FileObject parentpom = grandFo.getFileObject("pom", "xml");//NOI18N

                    if (parentpom != null) {
                        return getRootProjectPom(parentpom);
                    }
                }
            }
        } catch (XmlPullParserException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return pom;
    }
}
