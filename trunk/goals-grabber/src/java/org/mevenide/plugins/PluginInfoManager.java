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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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
    
    private void findParser() {
        String cacheDir = finder.getMavenPluginsDir();
        if (oldCacheDir == null || !oldCacheDir.equals(cacheDir)) {
            oldCacheDir = cacheDir;
            parser = PluginInfoFactory.getInstance().getParser(new File(cacheDir));
        }
    }
    
    public IPluginInfo[] getCurrentPlugins() {
        findParser();
        return parser.getInfos();
    }
    
    /**
     * is possibly somewhat slow.
     */
    public boolean isUsedByProject(IPluginInfo info) {
        Set keys = new HashSet(context.getBuildPropertyKeys());
        keys.addAll(context.getParentBuildPropertyKeys());
        keys.addAll(context.getParentProjectPropertyKeys());
        keys.addAll(context.getProjectPropertyKeys());
        keys.addAll(context.getUserPropertyKeys());
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
