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

package org.codehaus.mevenide.netbeans.customizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Model;
import org.apache.maven.profiles.ProfilesRoot;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public final class ModelHandle {
    public static final String PANEL_RUN = "RUN";
    static final String PANEL_BASIC = "BASIC";
    static final String PANEL_MAPPING = "MAPPING";

    private Model model;
    private MavenProject project;
    private ProfilesRoot profiles; 
    private ActionToGoalMapping mapping;
    private List listeners;
    private org.apache.maven.model.Profile publicProfile;
    private org.apache.maven.profiles.Profile privateProfile;
    
    /** Creates a new instance of ModelHandle */
    ModelHandle(Model mdl, ProfilesRoot profile, MavenProject proj, ActionToGoalMapping mapping) {
        model = mdl;
        project = proj;
        this.mapping = mapping;
        this.profiles = profile;
        listeners = new ArrayList();
    }
    
    /**
     * action listeners are notified when the dialog is closed and values are to be applied 
     * before the data is actually written
     */
    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }
    /**
     * action listeners are notified when the dialog is closed and values are to be applied 
     * before the data is actually written
     */
    public void removeActionListener(ActionListener listener) {
        listeners.remove(listener);
    }
    
    public Model getPOMModel() {
        return model;
    }
    
    public ProfilesRoot getProfileModel() {
        return profiles;
    }
    
    public org.apache.maven.model.Profile getNetbeansPublicProfile() {
        if (publicProfile == null) {
            List lst = model.getProfiles();
            if (lst != null) {
                Iterator it = lst.iterator();
                while (it.hasNext()) {
                    org.apache.maven.model.Profile profile = (org.apache.maven.model.Profile) it.next();
                    if ("netbeans-public".equals(profile.getId())) {
                        publicProfile = profile;
                        break;
                    }
                }
            }
            if (publicProfile == null) {
                publicProfile = new org.apache.maven.model.Profile();
                publicProfile.setId("netbeans-public");
                Activation act = new Activation();
                ActivationProperty prop = new ActivationProperty();
                prop.setName("netbeans.execution");
                prop.setValue("true");
                act.setProperty(prop);
                publicProfile.setActivation(act);
                publicProfile.setBuild(new BuildBase());
                model.addProfile(publicProfile);
            }
        }
        return publicProfile;
    }
    
    public org.apache.maven.profiles.Profile getNetbeansPrivateProfile() {
        if (privateProfile == null) {
            List lst = profiles.getProfiles();
            if (lst != null) {
                Iterator it = lst.iterator();
                while (it.hasNext()) {
                    org.apache.maven.profiles.Profile profile = (org.apache.maven.profiles.Profile) it.next();
                    if ("netbeans-private".equals(profile.getId())) {
                        privateProfile = profile;
                        break;
                    }
                }
            }
            if (privateProfile == null) {
                privateProfile = new org.apache.maven.profiles.Profile();
                privateProfile.setId("netbeans-private");
                org.apache.maven.profiles.Activation act = new org.apache.maven.profiles.Activation();
                org.apache.maven.profiles.ActivationProperty prop = new org.apache.maven.profiles.ActivationProperty();
                prop.setName("netbeans.execution");
                prop.setValue("true");
                act.setProperty(prop);
                privateProfile.setActivation(act);
                profiles.addProfile(privateProfile);
            }
            
        }
        return privateProfile;
    }
    
    public MavenProject getProject() {
        return project;
    }
    
    public ActionToGoalMapping getActionMappings() {
        return mapping;
    }

    void fireActionPerformed() {
        Iterator it = listeners.iterator();
        ActionEvent evnt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "X");
        while (it.hasNext()) {
            ActionListener elem = (ActionListener) it.next();
            elem.actionPerformed(evnt);
        }
    }
    
}
