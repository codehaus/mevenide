/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */

package org.mevenide.ui.netbeans.propeditor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PropertyResourceBundle;
import org.mevenide.ui.netbeans.MavenPropertyFiles;
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
