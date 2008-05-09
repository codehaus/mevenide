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

package org.codehaus.mevenide.netbeans.configurations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.spi.project.ProjectConfiguration;
import org.codehaus.mevenide.netbeans.spi.actions.AbstractMavenActionsProvider;
import org.codehaus.mevenide.netbeans.execute.UserActionGoalProvider;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.openide.filesystems.FileObject;

/**
 *
 * @author mkleint
 */
public class M2Configuration extends AbstractMavenActionsProvider implements ProjectConfiguration  {

    public static String DEFAULT = "%%DEFAULT%%"; //NOI18N
    
    static M2Configuration createDefault(NbMavenProject prj) {
        return new M2Configuration(DEFAULT, prj);
    }
    
    private String id;
    private List<String> profiles;
    private NbMavenProject project;
    static final String FILENAME_PREFIX = "nbactions-"; //NOI18N
    static final String FILENAME_SUFFIX = ".xml"; //NOI18N
    private Date lastModified = new Date();
    private boolean lastTimeExists = true;
    
    public M2Configuration(String id, NbMavenProject proj) {
        this.id = id;
        this.project = proj;
        profiles = Collections.<String>emptyList();
    }

    public String getDisplayName() {
        if (DEFAULT.equals(id)) {
            return "<default config>";
        }
        return id;
    }
    
    public String getId() {
        return id;
    }
    
    public void setActivatedProfiles(List<String> profs) {
        profiles = profs;
    }
    
    public List<String> getActivatedProfiles() {
        return profiles;
    }
    
    
    public static String getFileNameExt(String id) {
        if (DEFAULT.equals(id)) {
            return UserActionGoalProvider.FILENAME;
        }
        return FILENAME_PREFIX + id + FILENAME_SUFFIX;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final M2Configuration other = (M2Configuration) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public InputStream getActionDefinitionStream() {
        if (DEFAULT.equals(id)) {
            return null;
        }
        FileObject fo = project.getProjectDirectory().getFileObject(FILENAME_PREFIX + id + FILENAME_SUFFIX);
        lastTimeExists = fo != null;
        if (fo != null) {
            try {
                lastModified = fo.lastModified();
                return fo.getInputStream();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        lastModified = new Date();
        return null;
    }
    
   /**
     * get custom action maven mapping configuration
     * No replacements happen.
     * The instances returned is always a new copy, can be modified or reused.
     * Same method in NbGlobalActionGolaProvider 
     */
    public NetbeansActionMapping[] getCustomMappings() {
        NetbeansActionMapping[] fallbackActions = new NetbeansActionMapping[0];
        
        try {
            List<NetbeansActionMapping> toRet = new ArrayList<NetbeansActionMapping>();
            // just a converter for the To-Object reader..
            Reader read = performDynamicSubstitutions(Collections.EMPTY_MAP, getRawMappingsAsString());
            // basically doing a copy here..
            ActionToGoalMapping mapping = reader.read(read);    
            List lst = mapping.getActions();
            if (lst != null) {
                Iterator it = lst.iterator();
                while(it.hasNext()) {
                    NetbeansActionMapping mapp = (NetbeansActionMapping) it.next();
                    if (mapp.getActionName().startsWith("CUSTOM-")) { //NOI18N
                        toRet.add(mapp);
                    }
                }
            }
            return toRet.toArray(new NetbeansActionMapping[toRet.size()]);
        } catch (XmlPullParserException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return fallbackActions;
    }
    
    @Override
    protected boolean reloadStream() {
        FileObject fo = project.getProjectDirectory().getFileObject(FILENAME_PREFIX + id + FILENAME_SUFFIX);
        boolean prevExists = lastTimeExists;
        lastTimeExists = fo != null;
        return ((fo == null && prevExists) || (fo != null && fo.lastModified().after(lastModified)));
    }


}
