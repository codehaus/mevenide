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

package org.codehaus.mevenide.netbeans.execute;

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
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 * user defined definitions, to be found in the project directory in the nbactions.xml file.
 * @author mkleint
 */
public class UserActionGoalProvider extends AbstractActionGoalProvider {
    
    public static final String FILENAME = "nbactions.xml"; //NOI18N
    
    private NbMavenProject project;
    private Date lastModified = new Date();
    private boolean lastTimeExists = true;
    /** Creates a new instance of UserActionGoalProvider */
    public UserActionGoalProvider(NbMavenProject project) {
        this.project = project;
    }
    
    public InputStream getActionDefinitionStream() {
        FileObject fo = project.getProjectDirectory().getFileObject(FILENAME);
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
        FileObject fo = project.getProjectDirectory().getFileObject(FILENAME);
        boolean prevExists = lastTimeExists;
        lastTimeExists = fo != null;
        return ((fo == null && prevExists) || (fo != null && fo.lastModified().after(lastModified)));
    }
}
