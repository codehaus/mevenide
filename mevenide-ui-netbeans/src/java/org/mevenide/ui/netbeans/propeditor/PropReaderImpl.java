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
package org.mevenide.ui.netbeans.propeditor;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.openide.nodes.Node;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class PropReaderImpl implements PropReader
{
    
    private PropReader override;
    private PropertyResourceBundle bundle;
    private String layer;
    /** Creates a new instance of PropReader */
    public PropReaderImpl(String layer, InputStream stream) throws IOException
    {
        if (stream != null) {
            bundle = new PropertyResourceBundle(stream);
        }
        this.layer = layer;
    }
    
    public PropReaderImpl(String layer, InputStream stream, PropReader override) throws IOException
    {
        this(layer, stream);
        this.override = override;
    }
    
    public Node[] createNodeStructure()
    {
        Collection vals = getProperties().values();
        Node[] toReturn = new Node[vals.size()];
        Iterator it = vals.iterator();
        int index = 0;
        while (it.hasNext())
        {
            PropValue val = (PropValue)it.next();
            toReturn[index] = new PropertyNode(val);
            index = index + 1;
        }
        return toReturn;
    }
    
    public Map getProperties() {
        Map toReturn = new HashMap(30);
        if (override != null) {
            toReturn.putAll(override.getProperties());
        }
        if (bundle != null)
        {
            Enumeration en = bundle.getKeys();
            while (en.hasMoreElements())
            {
                String key = (String)en.nextElement();
                PropValue previous = null;
                if (toReturn.containsKey(key))
                {
                    previous = (PropValue)toReturn.get(key);
                }
                PropValue toAdd = new PropValue(layer, key, bundle.getString(key), previous);
                toReturn.put(key, toAdd);
            }
        }
        return toReturn;
    }
    
    
}
