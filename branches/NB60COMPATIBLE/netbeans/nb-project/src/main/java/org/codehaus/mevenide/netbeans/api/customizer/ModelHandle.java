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

import java.util.Iterator;
import java.util.List;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Model;
import org.apache.maven.profiles.ProfilesRoot;
import org.apache.maven.project.MavenProject;
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
    public static final String PANEL_MAPPING = "MAPPING"; //NOI18N
    public static final String PANEL_LIBRARIES = "LIBRARIES"; //NOI18N
    public static final String PANEL_SOURCES = "SOURCES"; //NOI18N
    
    public static final String PROFILE_PUBLIC = "netbeans-public"; //NOI18N
    public static final String PROFILE_PRIVATE = "netbeans-private"; //NOI18N
    public static final String PROPERTY_PROFILE = "netbeans.execution"; //NOI18N

    private Model model;
    private MavenProject project;
    private ProfilesRoot profiles; 
    private ActionToGoalMapping mapping;
    private org.apache.maven.model.Profile publicProfile;
    private org.apache.maven.profiles.Profile privateProfile;
    private boolean modMapping = false;
    private boolean modProfiles = false;
    private boolean modModel = false;
    
    static {
        AccessorImpl impl = new AccessorImpl();
        impl.assign();
    }
    
    
    static class AccessorImpl extends CustomizerProviderImpl.ModelAccessor {
        
         public ModelHandle createHandle(Model model, ProfilesRoot prof,
                                        MavenProject proj, ActionToGoalMapping mapp) {
            return new ModelHandle(model, prof, proj, mapp);
        }
        
         public void assign() {
             if (CustomizerProviderImpl.ACCESSOR == null) {
                 CustomizerProviderImpl.ACCESSOR = this;
             }
         }
    
    }
    
    /** Creates a new instance of ModelHandle */
    private ModelHandle(Model mdl, ProfilesRoot profile, MavenProject proj, ActionToGoalMapping mapping) {
        model = mdl;
        project = proj;
        this.mapping = mapping;
        this.profiles = profile;
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
        return mapping;
    }
    
    public boolean isModified(Object obj) {
        if (obj == mapping) {
            return modMapping; 
        } else if (obj == profiles) {
            return modProfiles;
        } else if (obj == model) {
            return modModel;
        }
        return true;
    }
    
    /**
     * always after modifying the models, makr them as modified.
     * without the marking, the particular file will not be saved.
     * @param obj either getPOMModel(), getActionMappings() or getProfileModel()
     */ 
    public void markAsModified(Object obj) {
        if (obj == mapping) {
            modMapping = true;
        } else if (obj == profiles) {
            modProfiles = true;
        } else if (obj == model) {
            modModel = true;
        }
    }
}
