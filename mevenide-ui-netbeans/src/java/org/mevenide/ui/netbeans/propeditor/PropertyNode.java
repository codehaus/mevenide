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
 * @author  Milos Kleint (ca206216@tiscali.cz)
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
