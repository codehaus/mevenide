/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.environment.CustomLocationFinder;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.grammar.impl.MavenTagLibProvider;
import org.mevenide.grammar.impl.StaticTagLibImpl;
import org.mevenide.grammar.TagLib;
import org.mevenide.grammar.TagLibProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

/**
 * Netbeans provider of TagLib instances.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */

public class NbTagLibProvider implements TagLibProvider {
    
    private static Log logger = LogFactory.getLog(NbTagLibProvider.class);
    
    private File dynaTagFile;
    private MavenTagLibProvider mavenProvider;
    /** Creates a new instance of NbTagLibProvider */
    public NbTagLibProvider() {
        dynaTagFile = new File(getLocFinder().getMavenPluginsDir(), "dynatag.cache");
        mavenProvider = new MavenTagLibProvider(dynaTagFile);
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
    
    private static ILocationFinder aggregator = null;
    
    /**
     * kind of temporary, not sure if it should be made effective dir aware or not.
     * Maybe there also should be some kind of singleton instance - better instantiated maybe.
     */
    private ILocationFinder getLocFinder() {
        if (aggregator == null) {
            CustomLocationFinder finder = new CustomLocationFinder();
            String userHome = System.getProperty("user.home");
            File userHomeFile = new File(userHome);
            finder.setMavenLocalHome(new File(userHomeFile, ".maven").getAbsolutePath());
            finder.setMavenPluginsDir(new File(finder.getMavenLocalHome(), "plugins").getAbsolutePath());
            aggregator = new LocationFinderAggregator();
            ((LocationFinderAggregator)aggregator).setCustomLocationFinder(finder);
        }
        return aggregator;
    }
    
    private String getPluginDir() {
        return getLocFinder().getMavenPluginsDir();
    }    
    
}
