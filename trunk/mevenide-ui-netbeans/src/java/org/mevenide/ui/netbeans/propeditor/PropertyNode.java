/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Milos Kleint (ca206216@tiscali.cz).  All rights
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.ErrorManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

/**
 *
 * @author  cenda
 */
public class PropertyNode extends AbstractNode
{
    private static Log log = LogFactory.getLog(PropertyNode.class);
    private PropValue value;
    /** Creates a new instance of PropertyNode */
    public PropertyNode(PropValue value)
    {
        super(value.getOverride() == null ? Children.LEAF : new PropertyNodeChildren(value));
        setName(value.getKey());
        this.value = value;
        createProperties();
    }
    
    private void createProperties()
    {
        try
        {
            Node.Property prop1 = new PropertySupport.Reflection(value, String.class, "getValue", null);
            prop1.setName("value");
            Node.Property prop2 = new PropertySupport.Reflection(value, String.class, "getLayerDesc", null);
            prop2.setName("location");
            Sheet.Set set = getSheet().get(Sheet.PROPERTIES);
            if (set == null) {
                set = getSheet().createPropertiesSet();
                getSheet().put(set);
            }
            set.put(prop1);
            set.put(prop2);
        } catch (Exception exc)
        {
            //TODO catch only thrown exceptions.
            ErrorManager.getDefault().notify(exc);
            log.debug("Cannot create properties", exc); //NOI18N
        }
    }
    
    
    private static class PropertyNodeChildren extends Children.Keys
    {
        public PropertyNodeChildren(PropValue parent)
        {
            setKeys(new Object[]
            { parent.getOverride() });
        }
        
        protected Node[] createNodes(Object obj)
        {
            if (obj != null && obj instanceof PropValue)
            {
                return new Node[]
                {new PropertyNode((PropValue)obj)};
            }
            return new Node[0];
        }
    }
}
