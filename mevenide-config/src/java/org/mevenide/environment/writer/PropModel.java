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
package org.mevenide.environment.writer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Model of properties file.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class PropModel
{
    private List list;
    /** Creates a new instance of PropModel */
    public PropModel()
    {
        list = new LinkedList();
    }
    /**
     * Adds an element to the endo of the file/model.
     */
    public void addElement(Element el)
    {
        list.add(el);
    }
    /**
     * removes the element from model.
     */
    public void removeElement(Element el)
    {
        list.remove(el);
    }
    
    /**
     * returns a copy of the model, is not live.
     */
    public List getList()
    {
        return new ArrayList(list);
    }
    
    public int getSize()
    {
        return list.size();
    }
    
    /**
     * inserts element at designated position.
     */
    public boolean insertAt(int index, Element el)
    {
        if (index >= 0 && index < list.size())
        {
            list.add(index, el);
            return true;
        }
        return false;
    }

    /**
     * based on the key from Properties, will look up an item in the model.
     */
    public KeyValuePair findByKey(String key)
    {
        Iterator it = list.iterator();
        while (it.hasNext())
        {
            Object el = it.next();
            if (el instanceof KeyValuePair)
            {
                KeyValuePair kp = (KeyValuePair)el;
                if (kp.getKey().equals(key))
                {
                    return kp;
                }
            }
        }
        return null;
    }
    

    public static Comment createComment()
    {
        return new Comment();
    }
    
    public static KeyValuePair createKeyValuePair(String key, char keyseparator)
    {
        return new KeyValuePair(key, keyseparator);
    }
    
    
    /**
     * just a marker class so that all have common ground.
     */
    public static class Element
    {
        protected Element()
        {
        }
    }
    
    public static class Comment extends Element
    {
        StringBuffer buf;
        private Comment()
        {
            buf = new StringBuffer(100);
        }
        
        public void addToComment(String add)
        {
            buf.append(add);
        }
        
        public void setComment(String comment)
        {
            buf = new StringBuffer(comment);
        }
        
        public String toString()
        {
            return buf.toString();
        }
    }

    public static class KeyValuePair extends Element
    {
        private String key;
        private StringBuffer buf;
        private char sepChar;
        
        private KeyValuePair(String newKey, char separator)
        {
            key = newKey;
            buf = new StringBuffer(100);
            sepChar = separator;
        }
        
        public void addToValue(String value) 
        {
            buf.append(value);
        }
        
        public void setValue(String value)
        {
            buf = new StringBuffer(value);
        }

        /**
         * for comparisons, trim the leaning and trailing whitespace
         */
        public String getKey()
        {
            return key.trim();
        }
        
        public String toString()
        {
            return key + sepChar + buf.toString();
        }
    }
    
}
