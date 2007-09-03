/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.grammar;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.codehaus.mevenide.netbeans.api.GoalsProvider;
import org.codehaus.mevenide.netbeans.embedder.MavenSettingsSingleton;
import org.codehaus.plexus.util.IOUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.openide.modules.InstalledFileLocator;

/**
 *
 * @author mkleint
 */
public class GoalsProviderImpl implements GoalsProvider {
    
    /** Creates a new instance of GoalsProviderImpl */
    public GoalsProviderImpl() {
    }
    
    private WeakReference<Set<String>> goalsRef = null;
    
    public Set<String> getAvailableGoals() {
        Set<String> cached = goalsRef != null ? goalsRef.get() :null;
        if (cached == null) {
            File expandedPath = InstalledFileLocator.getDefault().locate("maven2/maven-plugins-xml", null, false); //NOI18N
            assert expandedPath != null : "Shall have path expanded.."; //NOI18N
            List<String> groups = MavenSettingsSingleton.getInstance().getSettings().getPluginGroups();
            cached = new TreeSet<String>();
            for (String group : groups) {
                File folder = new File(expandedPath, group.replace('.', File.separatorChar));
                checkFolder(folder, cached, false);
            }
            goalsRef = new WeakReference<Set<String>>(cached);
        }
        return cached;
        
    }

    private void checkFolder(File parent, Set<String> list, boolean recurs) {
        File[] files = parent.listFiles();
        if (files == null) {
            //fix for #100894, happens when a plugin group is defined but not in our list.
            return;
        }
        boolean hasFile = false;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory() && recurs) {
                checkFolder(file, list, recurs);
            }
            if (file.isFile()) {
                InputStream str = null;
                try {
                    str = new FileInputStream(file);
                    SAXBuilder builder = new SAXBuilder();
                    //TODO jdom document tree is probably not the most memory effective way of doing things..
                    Document doc = builder.build(str);
                    Iterator it = doc.getRootElement().getDescendants(new Filter() {
                        public boolean matches(Object object) {
                            if (object instanceof Element) {
                                Element el = (Element)object;
                                if ("goal".equals(el.getName()) &&  //NOI18N
                                        el.getParentElement() != null && 
                                        "mojo".equals(el.getParentElement().getName())) { //NOI18N
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                    String prefix = doc.getRootElement().getChildTextTrim("goalPrefix"); //NOI18N
                    assert prefix != null : "No prefix for " + file.getAbsolutePath(); //NOI18N
                    while (it.hasNext()) {
                        Element goal = (Element)it.next();
                        list.add(prefix + ":" + goal.getText());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    IOUtil.close(str);
                }
                
            }
        }
    }
    
    
}
