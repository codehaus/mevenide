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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.grammar.StaticTagLibImpl;
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
    
    private Map taglibs;
    /** Creates a new instance of NbTagLibProvider */
    public NbTagLibProvider() {
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
        String[] str = new String[toReturn.size()];
        str = (String[])toReturn.toArray(str);
        return str;
    }
    
    public TagLib retrieveTagLib(String name) {
        name = name.replace(':', '-');
        FileObject tagLib = Repository.getDefault().getDefaultFileSystem().findResource("Plugins/Mevenide-Grammar/" + name + ".xml");
        if (tagLib == null) {
            logger.error("cannot find taglib with name=" + name + "  (no fileobject found)");
        }
        TagLib toReturn = null;
        try {
            toReturn = new StaticTagLibImpl(tagLib.getInputStream());
        } catch (Exception exc) {
            logger.error("cannot retrieve the taglibrary=" + name, exc);
        }
        return toReturn;
        
    }
    
}
