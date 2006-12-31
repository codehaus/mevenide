/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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

package org.mevenide.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.maven.project.Dependency;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.LocationFinderAggregator;

/**
 *
 * @author  <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 */
public final class PluginInfoManager {
    
    private IQueryContext context;
    private PluginInfoParser parser;
    private String oldCacheDir = null;
    private LocationFinderAggregator finder;
    /** Creates a new instance of PluginInfoManager */
    PluginInfoManager(IQueryContext queryContext) {
        context = queryContext;
        finder = new LocationFinderAggregator(context);
    }
    
    private synchronized void findParser() {
        String cacheDir = finder.getMavenPluginsDir();
        if (oldCacheDir == null || !oldCacheDir.equals(cacheDir)) {
            oldCacheDir = cacheDir;
            parser = PluginInfoFactory.getInstance().getParser(new File(cacheDir));
        }
    }
    
    public IPluginInfo[] getCurrentPlugins() {
        findParser();
        IPluginInfo[] cachedInfos = parser.getInfos();
        List projectInfos = loadProjectDependentPlugins();
        if (projectInfos.size() > 0) {
            // take the plugins defined as dependency into account.
            // these have precedence over the cached ones..
            Iterator it = projectInfos.iterator();
            String list = "";
            List toRet = new ArrayList();
            while (it.hasNext()) {
                IPluginInfo projectInfo = (IPluginInfo)it.next();
                list = list + projectInfo.getArtifactId() + ",";
                toRet.add(projectInfo);
            }
            for (int i = 0; i < cachedInfos.length; i++) {
                String toMatch = cachedInfos[i].getArtifactId() + ",";
                if (list.indexOf(toMatch) == -1) {
                    toRet.add(cachedInfos[i]);
                }
            }
            return (IPluginInfo[])toRet.toArray(new IPluginInfo[toRet.size()]);
        }

        return cachedInfos;
    }
    
    private List loadProjectDependentPlugins() {
        List prjPlugins = new ArrayList();
        List deps = context.getPOMContext().getFinalProject().getDependencies();
        if (deps != null) {
            Iterator it = deps.iterator();
            while (it.hasNext()) {
                Dependency dep = (Dependency)it.next();
                if (dep.isPlugin()) {
                    File repoLocal =  new File(finder.getMavenLocalRepository());
                    String grId = dep.getGroupId() != null ? dep.getGroupId() : dep.getId();
                    String artId = dep.getArtifactId() != null ? dep.getArtifactId() : dep.getId();
                    String relPath = grId + File.separator + "plugins" + File.separator + artId + "-" + dep.getVersion() + ".jar";
                    File depLocal = new File(repoLocal,  relPath);
                    if (depLocal.exists()) {
                        JarPluginInfo info = new JarPluginInfo(depLocal);
                        info.setArtifactId(artId);
                        info.setVersion(dep.getVersion());
                        // we are not setting name here because it's not easily available, do it lazily in DefaultPluginInfo's getName() method
                        prjPlugins.add(info);
                    }
                }
            }
        }
        return prjPlugins;
    }
    
    /**
     * is somewhat slow.
     */
    public boolean isUsedByProject(IPluginInfo info) {
        Set keys = new HashSet();
        keys.addAll(context.getUserPropertyKeys());
        int depth = context.getPOMContext().getProjectDepth();
        for (int i = 1; i <= depth; i++) {
            keys.addAll(context.getPropertyKeysAt(i * 10 + IQueryContext.BUILD_PROPS_OFFSET));
            keys.addAll(context.getPropertyKeysAt(i * 10 + IQueryContext.PROJECT_PROPS_OFFSET));
        }
        Iterator it = info.getPropertyKeys().iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            if (keys.contains(key)) {
                return true;
            }
        }
        return false;
    }
}
