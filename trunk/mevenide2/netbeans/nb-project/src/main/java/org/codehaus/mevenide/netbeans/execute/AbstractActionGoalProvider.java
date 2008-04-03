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

import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.mevenide.netbeans.AdditionalM2ActionsProvider;
import org.codehaus.mevenide.netbeans.MavenSourcesImpl;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.mevenide.netbeans.execute.model.io.xpp3.NetbeansBuildActionXpp3Reader;
import org.codehaus.mevenide.netbeans.execute.model.io.xpp3.NetbeansBuildActionXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ActionProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;

/**
 * a default implementation of AdditionalM2ActionsProvider, a fallback when nothing is
 * user configured or overriden by a more specialized provider.
 * @author mkleint
 */
public abstract class AbstractActionGoalProvider implements AdditionalM2ActionsProvider {

    private static final String WEB_PATH = "webpagePath";//NOI18N
    private static final String CLASSNAME = "className";//NOI18N
    private static final String CLASSNAME_EXT = "classNameWithExtension";//NOI18N
    private static final String PACK_CLASSNAME = "packageClassName";//NOI18N
    protected ActionToGoalMapping originalMappings;
    protected NetbeansBuildActionXpp3Reader reader = new NetbeansBuildActionXpp3Reader();
    private NetbeansBuildActionXpp3Writer writer = new NetbeansBuildActionXpp3Writer();

    /** Creates a new instance of DefaultActionProvider */
    public AbstractActionGoalProvider() {
    }

    /**
     * just gets the array of FOs from lookup.
     */
    protected static FileObject[] extractFileObjectsfromLookup(Lookup lookup) {
        List<FileObject> files = new ArrayList<FileObject>();
        Iterator<? extends DataObject> it = lookup.lookup(new Lookup.Template<DataObject>(DataObject.class)).allInstances().iterator();
        while (it.hasNext()) {
            DataObject d = it.next();
            FileObject f = d.getPrimaryFile();
            files.add(f);
        }
        return files.toArray(new FileObject[files.size()]);
    }

    public boolean isActionEnable(String action, NbMavenProject project, Lookup lookup) {
        ActionToGoalMapping rawMappings = getRawMappings();
        Iterator it = rawMappings.getActions().iterator();
        String prjPack = project.getProjectWatcher().getPackagingType();
        while (it.hasNext()) {
            NetbeansActionMapping elem = (NetbeansActionMapping) it.next();
            if (action.equals(elem.getActionName()) &&
                    (elem.getPackagings().contains(prjPack.trim()) ||
                    elem.getPackagings().contains("*"))) {//NOI18N
                return true;
            }
        }

        return false;
    }

    public final RunConfig createConfigForDefaultAction(String actionName, NbMavenProject project, Lookup lookup) {
        FileObject[] fos = extractFileObjectsfromLookup(lookup);
        String relPath = null;
        String group = null;
        HashMap<String, String> replaceMap = new HashMap<String, String>();
        if (fos.length > 0) {
            Sources srcs = project.getLookup().lookup(Sources.class);
            SourceGroup[] grp = srcs.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            for (int i = 0; i < grp.length; i++) {
                relPath = FileUtil.getRelativePath(grp[i].getRootFolder(), fos[0]);
                if (relPath != null) {
                    group = grp[i].getName();
                    replaceMap.put(CLASSNAME_EXT, fos[0].getNameExt());
                    replaceMap.put(CLASSNAME, fos[0].getName());
                    String pack = FileUtil.getRelativePath(grp[i].getRootFolder(), fos[0].getParent());
                    replaceMap.put(PACK_CLASSNAME, (pack + (pack.length() > 0 ? "." : "") + fos[0].getName()).replace('/', '.')); //NOI18N
                    break;
                }
            }
            if (relPath == null) {
                replaceMap.put(CLASSNAME_EXT, "");//NOI18N
                replaceMap.put(CLASSNAME, "");//NOI18N
                replaceMap.put(PACK_CLASSNAME, "");//NOI18N
            }
            grp = srcs.getSourceGroups("doc_root"); //NOI18N J2EE
            for (int i = 0; i < grp.length; i++) {
                relPath = FileUtil.getRelativePath(grp[i].getRootFolder(), fos[0]);
                if (relPath != null) {
                    replaceMap.put(WEB_PATH, relPath);
                    break;
                }
            }
            if (relPath == null) {
                replaceMap.put(WEB_PATH, "");//NOI18N
            }

        }
        if (group != null && group.equals(MavenSourcesImpl.NAME_TESTSOURCE) &&
                ActionProvider.COMMAND_RUN_SINGLE.equals(actionName)) {
            actionName = ActionProvider.COMMAND_TEST_SINGLE;
        }
        if (group != null && group.equals(MavenSourcesImpl.NAME_TESTSOURCE) &&
                ActionProvider.COMMAND_DEBUG_SINGLE.equals(actionName)) {
            actionName = ActionProvider.COMMAND_DEBUG_TEST_SINGLE;
        }
        if (group != null && group.equals(MavenSourcesImpl.NAME_SOURCE) &&
                (ActionProvider.COMMAND_TEST_SINGLE.equals(actionName) ||
                ActionProvider.COMMAND_DEBUG_TEST_SINGLE.equals(actionName))) {
            //TODO.. get the rel path for test not class..
        }
        return mapGoalsToAction(project, actionName, replaceMap);
    }

    public ActionToGoalMapping getRawMappings() {
        if (originalMappings == null || reloadStream()) {
            InputStream in = getActionDefinitionStream();
            if (in == null) {
                originalMappings = new ActionToGoalMapping();
            } else {
                Reader rdr = null;
                try {
                    rdr = new InputStreamReader(getActionDefinitionStream());
                    originalMappings = reader.read(rdr);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    originalMappings = new ActionToGoalMapping();
                } catch (XmlPullParserException ex) {
                    ex.printStackTrace();
                    originalMappings = new ActionToGoalMapping();
                } finally {
                    if (rdr != null) {
                        try {
                            rdr.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        return originalMappings;
    }

    public String getRawMappingsAsString() {
        StringWriter str = new StringWriter();
        try {
            writer.write(str, getRawMappings());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return str.toString();
    }

    /**
     * override in children that are listening on changes of model and need refreshing..
     */
    protected boolean reloadStream() {
        return false;
    }

    /**
     * get a action to maven mapping configuration for the given action.
     * No replacements happen.
     * The instance returned is always a new copy, can be modified or reused.
     */
    public NetbeansActionMapping getMappingForAction(String actionName, NbMavenProject project) {
        NetbeansActionMapping action = null;
        try {
            // just a converter for the To-Object reader..
            Reader read = performDynamicSubstitutions(Collections.EMPTY_MAP, getRawMappingsAsString());
            // basically doing a copy here..
            ActionToGoalMapping mapping = reader.read(read);
            Iterator it = mapping.getActions().iterator();
            String prjPack = project.getProjectWatcher().getPackagingType();
            while (it.hasNext()) {
                NetbeansActionMapping elem = (NetbeansActionMapping) it.next();
                if (actionName.equals(elem.getActionName()) &&
                        (elem.getPackagings().contains(prjPack.trim()) ||
                        elem.getPackagings().contains("*"))) {//NOI18N
                    action = elem;
                    break;
                }
            }
        } catch (XmlPullParserException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return action;

    }

    /**
     * content of the input stream shall be the xml with action definitions
     */
    protected abstract InputStream getActionDefinitionStream();

    private RunConfig mapGoalsToAction(NbMavenProject project, String actionName, HashMap replaceMap) {
        try {
            // TODO need some caching really badly here..
            Reader read = performDynamicSubstitutions(replaceMap, getRawMappingsAsString());
            ActionToGoalMapping mapping = reader.read(read);
            Iterator it = mapping.getActions().iterator();
            NetbeansActionMapping action = null;
            String prjPack = project.getProjectWatcher().getPackagingType();
            while (it.hasNext()) {
                NetbeansActionMapping elem = (NetbeansActionMapping) it.next();
                if (actionName.equals(elem.getActionName()) &&
                        (elem.getPackagings().contains(prjPack.trim()) ||
                        elem.getPackagings().contains("*") || elem.getPackagings().size() == 0)) {//NOI18N
                    action = elem;
                    break;
                }
            }
            if (action != null) {
                return new ModelRunConfig(project, action);
            }
        } catch (XmlPullParserException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * takes the input stream and a map, and for each occurence of ${<mapKey>}, replaces it with map entry value..
     */
    protected Reader performDynamicSubstitutions(final Map replaceMap, final String in) throws IOException {
        StringBuffer buf = new StringBuffer(in);
        Iterator it = replaceMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry elem = (Map.Entry) it.next();
            String replaceItem = "${" + elem.getKey() + "}";//NOI18N
            int index = buf.indexOf(replaceItem);
            while (index > -1) {
                String newItem = (String) elem.getValue();
                if (newItem == null) {
//                    System.out.println("no value for key=" + replaceItem);
                }
                newItem = newItem == null ? "" : newItem;//NOI18N
                buf.replace(index, index + replaceItem.length(), newItem);
                index = buf.indexOf(replaceItem);
            }
        }
        return new StringReader(buf.toString());
    }
}
