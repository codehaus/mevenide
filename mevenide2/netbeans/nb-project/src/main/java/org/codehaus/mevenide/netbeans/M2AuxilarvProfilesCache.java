/*
 *  Copyright 2008 Mevenide Team.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.w3c.dom.Element;
import static org.codehaus.mevenide.netbeans.api.ProfileUtils.PROFILES;
import static org.codehaus.mevenide.netbeans.api.ProfileUtils.NAMESPACE;
import static org.codehaus.mevenide.netbeans.api.ProfileUtils.ACTIVEPROFILES;
import static org.codehaus.mevenide.netbeans.api.ProfileUtils.SEPERATOR;
/**
 *
 * @author Anuradha G
 */
public class M2AuxilarvProfilesCache {

    private List<String> privateProfiles = new ArrayList<String>();
    private List<String> sharedProfiles = new ArrayList<String>();

    public M2AuxilarvProfilesCache(AuxiliaryConfiguration ac) {
        refresh(ac);
    }

    public synchronized void refresh(AuxiliaryConfiguration ac) {
        privateProfiles.clear();
        sharedProfiles.clear();
        privateProfiles.addAll(retrieveActiveProfiles(ac, false));
        sharedProfiles.addAll(retrieveActiveProfiles(ac, true));
        
    }

    private  List<String> retrieveActiveProfiles(AuxiliaryConfiguration ac, boolean shared) {

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
    public synchronized List<String> getActiveProfiles(boolean shared) {
        
        return new ArrayList<String>(shared?sharedProfiles:privateProfiles);
    }
    
    
}
