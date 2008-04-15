/* ==========================================================================
 * Copyright 2005-2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.api.customizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Model;
import org.apache.maven.profiles.ProfilesRoot;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.configurations.M2ConfigProvider;
import org.codehaus.mevenide.netbeans.configurations.M2Configuration;
import org.codehaus.mevenide.netbeans.customizer.CustomizerProviderImpl;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;

/**
 * ModelHandle instance is passed down to customizer panel providers in the context lookup.
 * 
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public final class ModelHandle {
    public static final String PANEL_RUN = "RUN"; //NOI18N
    public static final String PANEL_BASIC = "BASIC"; //NOI18N
    public static final String PANEL_CONFIGURATION = "CONFIGURATION"; //NOI18N
    public static final String PANEL_MAPPING = "MAPPING"; //NOI18N
    public static final String PANEL_LIBRARIES = "LIBRARIES"; //NOI18N
    public static final String PANEL_SOURCES = "SOURCES"; //NOI18N
    
    public static final String PROFILE_PUBLIC = "netbeans-public"; //NOI18N
    public static final String PROFILE_PRIVATE = "netbeans-private"; //NOI18N
    public static final String PROPERTY_PROFILE = "netbeans.execution"; //NOI18N

    private Model model;
    private MavenProject project;
    private ProfilesRoot profiles; 
    private Map<String, ActionToGoalMapping> mappings;
    private Map<ActionToGoalMapping, Boolean> modMappings;
    private org.apache.maven.model.Profile publicProfile;
    private org.apache.maven.profiles.Profile privateProfile;
    private List<Configuration> configurations;
    private boolean modProfiles = false;
    private boolean modModel = false;
    private boolean modConfig = false;
    private Configuration active;
    private boolean enabled = false;
    
    static {
        AccessorImpl impl = new AccessorImpl();
        impl.assign();
    }
    
    
    static class AccessorImpl extends CustomizerProviderImpl.ModelAccessor {
        
         public ModelHandle createHandle(Model model, ProfilesRoot prof,
                                        MavenProject proj, 
                                        Map<String, ActionToGoalMapping> mapp, 
                                        List<ModelHandle.Configuration> configs,
                                        ModelHandle.Configuration active) {
            return new ModelHandle(model, prof, proj, mapp, configs, active);
        }
        
         public void assign() {
             if (CustomizerProviderImpl.ACCESSOR == null) {
                 CustomizerProviderImpl.ACCESSOR = this;
             }
         }
    
    }
    
    /** Creates a new instance of ModelHandle */
    private ModelHandle(Model mdl, ProfilesRoot profile, MavenProject proj, 
                        Map<String, ActionToGoalMapping> mappings,
                        List<Configuration> configs, Configuration active) {
        model = mdl;
        project = proj;
        this.profiles = profile;
        this.mappings = mappings;
        this.modMappings = new HashMap<ActionToGoalMapping, Boolean>();
        for (ActionToGoalMapping map : mappings.values()) {
            modMappings.put(map, Boolean.FALSE);
        }
        configurations = configs;
        this.active = active;
    }
    
    /**
     * pom.xml model
     */ 
    public Model getPOMModel() {
        return model;
    }
    
    /**
     * profiles.xml model
     */ 
    public ProfilesRoot getProfileModel() {
        return profiles;
    }
    
    /**
     * warning: can update the model, for non-updating one for use in value getters
     * use getNetbeansPublicProfile(false)
     */ 
    public org.apache.maven.model.Profile getNetbeansPublicProfile() {
        return getNetbeansPublicProfile(true);
    }
    
    public org.apache.maven.model.Profile getNetbeansPublicProfile(boolean addIfNotPresent) {
        if (publicProfile == null) {
            List lst = model.getProfiles();
            if (lst != null) {
                Iterator it = lst.iterator();
                while (it.hasNext()) {
                    org.apache.maven.model.Profile profile = (org.apache.maven.model.Profile) it.next();
                    if (PROFILE_PUBLIC.equals(profile.getId())) {
                        publicProfile = profile;
                        break;
                    }
                }
            }
            if (publicProfile == null && addIfNotPresent) {
                publicProfile = new org.apache.maven.model.Profile();
                publicProfile.setId(PROFILE_PUBLIC);
                Activation act = new Activation();
                ActivationProperty prop = new ActivationProperty();
                prop.setName(PROPERTY_PROFILE);
                prop.setValue("true"); //NOI18N
                act.setProperty(prop);
                publicProfile.setActivation(act);
                publicProfile.setBuild(new BuildBase());
                model.addProfile(publicProfile);
                markAsModified(model);
            }
        }
        if (publicProfile == null && !addIfNotPresent) {
            return new org.apache.maven.model.Profile();
        }
        return publicProfile;
    }
    /**
     * warning: can update the model, for non-updating one for use in value getters
     * use getNetbeansPrivateProfile(false)
     */ 
    public org.apache.maven.profiles.Profile getNetbeansPrivateProfile() {
        return getNetbeansPrivateProfile(true);
    }
    
    public org.apache.maven.profiles.Profile getNetbeansPrivateProfile(boolean addIfNotPresent) {
        if (privateProfile == null) {
            List lst = profiles.getProfiles();
            if (lst != null) {
                Iterator it = lst.iterator();
                while (it.hasNext()) {
                    org.apache.maven.profiles.Profile profile = (org.apache.maven.profiles.Profile) it.next();
                    if (PROFILE_PRIVATE.equals(profile.getId())) {
                        privateProfile = profile;
                        break;
                    }
                }
            }
            if (privateProfile == null && addIfNotPresent) {
                privateProfile = new org.apache.maven.profiles.Profile();
                privateProfile.setId(PROFILE_PRIVATE);
                org.apache.maven.profiles.Activation act = new org.apache.maven.profiles.Activation();
                org.apache.maven.profiles.ActivationProperty prop = new org.apache.maven.profiles.ActivationProperty();
                prop.setName(PROPERTY_PROFILE);
                prop.setValue("true"); //NOI18N
                act.setProperty(prop);
                privateProfile.setActivation(act);
                profiles.addProfile(privateProfile);
                markAsModified(profiles);
            }
        }
        if (privateProfile == null && !addIfNotPresent) {
            // just return something to prevent npes.. won't be live though..
            return new org.apache.maven.profiles.Profile();
        }
        return privateProfile;
    }
    
    /**
     * the non changed (not-to-be-changed) instance of the complete project. 
     * NOT TO BE CHANGED.
     */ 
    public MavenProject getProject() {
        return project;
    }
    
    /**
     * action mapping model
     */ 
    public ActionToGoalMapping getActionMappings() {
        return mappings.get(M2Configuration.DEFAULT);
    }
    
    /**
     * action mapping model
     */ 
    public ActionToGoalMapping getActionMappings(Configuration config) {
        ActionToGoalMapping mapp = mappings.get(config.getId());
        if (mapp == null) {
            mapp = new ActionToGoalMapping();
            mappings.put(config.getId(), mapp);
            modMappings.put(mapp, Boolean.FALSE);
        }
        return mapp;
    }

    
    public void setConfigurationsEnabled(boolean bool) {
        enabled = bool;
    }
    
    public boolean isConfigurationsEnabled() {
        return enabled;
    }
    
    public List<Configuration> getConfigurations() {
        return configurations;
    }
    
    public void addConfiguration(Configuration config) {
        configurations.add(config);
        modConfig = true;
    }
    
    public void removeConfiguration(Configuration config) {
        configurations.remove(config);
        if (active == config) {
            active = configurations.size() > 0 ? configurations.get(0) : null;
        }
        modConfig = true;
    }
    
    public Configuration getActiveConfiguration() {
        return active;
    }
    public void setActiveConfiguration(Configuration conf) {
        active = conf;
    }
    
    public boolean isModified(Object obj) {
        if (modMappings.containsKey(obj)) {
            return modMappings.get(obj); 
        } else if (obj == profiles) {
            return modProfiles;
        } else if (obj == model) {
            return modModel;
        } else if (obj == configurations || configurations.contains(obj)) {
            return modConfig;
        }
        return true;
    }
    
    /**
     * always after modifying the models, mark them as modified.
     * without the marking, the particular file will not be saved.
     * @param obj either getPOMModel(), getActionMappings() or getProfileModel()
     */ 
    public void markAsModified(Object obj) {
        if (modMappings.containsKey(obj)) {
            modMappings.put((ActionToGoalMapping)obj, Boolean.TRUE);
        } else if (obj == profiles) {
            modProfiles = true;
        } else if (obj == model) {
            modModel = true;
        } else if (obj == configurations || configurations.contains(obj)) {
            modConfig = true;
        }
    }

    
    public static Configuration createProfileConfiguration(String id) {
        Configuration conf = new Configuration();
        conf.setId(id);
        conf.setDisplayName(id);
        conf.setProfileBased(true);
        return conf;
    }
    
    public static Configuration createDefaultConfiguration() {
        Configuration conf = new Configuration();
        conf.setId(M2Configuration.DEFAULT);
        conf.setDisplayName("<Default Configuration>");
        conf.setDefault(true);
        return conf;
    }
    
    public static Configuration createCustomConfiguration(String id) {
        Configuration conf = new Configuration();
        conf.setId(id);
        conf.setDisplayName(id);
        return conf;
    }
    
    /**
     * a javabean wrapper for configurations within the project customizer
     * 
     */
    public static class Configuration {
        private String id;
        private boolean profileBased = false;
        private boolean defaul = false;

        private String displayName;
        private List<String> activatedProfiles;
        private boolean shared = false;
        
        Configuration() {}

        public String getFileNameExt() {
            return M2Configuration.getFileNameExt(id);
        }

        public boolean isDefault() {
            return defaul;
        }

        public void setDefault(boolean def) {
            this.defaul = def;
        }
        
        public List<String> getActivatedProfiles() {
            return activatedProfiles;
        }

        public void setActivatedProfiles(List<String> activatedProfiles) {
            this.activatedProfiles = activatedProfiles;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            if (isProfileBased()) {
                return;
            }
            this.displayName = displayName;
        }

        public String getId() {
            return id;
        }

        public void setShared(boolean shared) {
            this.shared = shared;
        }
        
        public boolean isShared() {
            return shared;
        }

        void setId(String id) {
            this.id = id;
        }

        public boolean isProfileBased() {
            return profileBased;
        }

        void setProfileBased(boolean profileBased) {
            this.profileBased = profileBased;
        }

        @Override
        public String toString() {
            return getDisplayName();
        }
        
        
    }
}
