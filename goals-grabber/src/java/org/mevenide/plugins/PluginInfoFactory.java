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
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import org.mevenide.context.IQueryContext;

/**
 *
 * @author  <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 */
public class PluginInfoFactory {
    
    private static PluginInfoFactory instance;
    private HashSet set = new HashSet(); 
    /** Creates a new instance of PluginInfoFactory */
    PluginInfoFactory() {
    }
    
    public static PluginInfoFactory getInstance() {
        synchronized (PluginInfoFactory.class) {
            if (instance == null) {
                instance = new PluginInfoFactory();
            }
        }
        return instance;
    }
    
    public PluginInfoManager createManager(IQueryContext context) {
        return new PluginInfoManager(context);
    }
    
    synchronized PluginInfoParser getParser(File cacheDir) {
        Iterator it = set.iterator();
        while (it.hasNext()) {
            WeakReference ref = (WeakReference)it.next();
            PluginInfoParser pars = (PluginInfoParser)ref.get();
            if (pars == null) {
                it.remove();
            } else {
                if (pars.getCachedDir().equals(cacheDir)) {
                    return pars;
                }
            }
        }
        PluginInfoParser parser = new PluginInfoParser(cacheDir);
        set.add(new WeakReference(parser));
        return parser;
    }
}
