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

package org.mevenide.netbeans.grammar;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.grammar.AttrCompletionProvider;
import org.mevenide.grammar.AttributeCompletion;
import org.mevenide.grammar.TagLib;
import org.mevenide.grammar.TagLibProvider;
import org.mevenide.grammar.impl.EmptyAttributeCompletionImpl;
import org.mevenide.grammar.impl.GoalsAttributeCompletionImpl;
import org.mevenide.grammar.impl.MavenTagLibProvider;
import org.mevenide.grammar.impl.PluginDefaultsCompletionImpl;
import org.mevenide.grammar.impl.StaticTagLibImpl;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

/**
 * Netbeans provider of TagLib instances.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */

public class NbTagLibProvider implements TagLibProvider, AttrCompletionProvider {
    
    private static Log logger = LogFactory.getLog(NbTagLibProvider.class);
    
    private File dynaTagFile;
    private MavenTagLibProvider mavenProvider;
    /** Creates a new instance of NbTagLibProvider */
    public NbTagLibProvider() {
        dynaTagFile = new File(ConfigUtils.getDefaultLocationFinder().getMavenPluginsDir(), "dynatag.cache");
        mavenProvider = new MavenTagLibProvider(dynaTagFile) {
			protected ClassLoader getMavenClassLoader() {
				return MavenGrammarModule.getMavenClassLoader();
			}
        };
    }
    
    public String[] getAvailableTags() {
        // retrieve the tags anew each time, maybe can be cached to improve performance.
        FileObject tagLibFolder = Repository.getDefault().getDefaultFileSystem().findResource("Plugins/Mevenide-Grammar");
        FileObject[] libFOs = tagLibFolder.getChildren();
        Collection toReturn = new HashSet();
        if (libFOs != null)
        {
            for (int i = 0; i < libFOs.length; i++)
            {
                if (libFOs[i].getExt().equals("xml") 
                 && !libFOs[i].getName().startsWith("default")) { //kind of hack not to show the default code completion..
                    // kind of weird condition however for now it's ok.. maybe we can live without any condition or make it mimetype one.
                     
                     //additional hack replacing - with : in name.
                    toReturn.add(libFOs[i].getName().replace('-',':'));
                }
            }
        }
        String[] mavenTags = mavenProvider.getAvailableTags();
        for (int i = 0; i < mavenTags.length; i++) 
        {
            toReturn.add(mavenTags[i]);
        }
        String[] str = new String[toReturn.size()];
        str = (String[])toReturn.toArray(str);
        return str;
    }
    
    public TagLib retrieveTagLib(String name) {
        TagLib toReturn = null;
        toReturn = mavenProvider.retrieveTagLib(name);
        if (toReturn != null) {
            return toReturn;
        }
        name = name.replace(':', '-');
        FileObject tagLib = Repository.getDefault().getDefaultFileSystem().findResource("Plugins/Mevenide-Grammar/" + name + ".xml");
        if (tagLib == null) {
                logger.error("cannot find taglib with name=" + name + "  (no fileobject found)");
                return null;
        }
        try {
            toReturn = new StaticTagLibImpl(tagLib.getInputStream());
        } catch (Exception exc) {
            logger.error("cannot retrieve the taglibrary=" + name, exc);
        }
        return toReturn;
        
    }
    
    public AttributeCompletion retrieveAttributeCompletion(String name)
    {
        AttributeCompletion completion = null;
        if ("goal".equals(name)) {
            try {
                completion = new GoalsAttributeCompletionImpl();
            } catch (Exception exc) {
                logger.error("Cannot create new instance of GoalsAttributeCompletionImpl", exc);
            }
        }
        if ("pluginDefaults".equals(name)) {
            completion = new PluginDefaultsCompletionImpl();
        }
        if (completion == null) {
            // fallback implementation.
            logger.warn("AttributeCompletion: using a fallback implementation, no impl for type=" + name);
            completion = new EmptyAttributeCompletionImpl(name);
        }
        return completion;
    }
    
}
