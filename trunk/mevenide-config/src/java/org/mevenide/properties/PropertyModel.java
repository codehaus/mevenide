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
package org.mevenide.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Model of properties file.
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 * @author  <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 */
public class PropertyModel
{
    //private static final Log log = LogFactory.getLog(PropertyModel.class);
    
    private List list;

    /** Creates a new instance of PropModel */
    PropertyModel()
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
        //needed because we want to iterate all keys in case some are overriden
        KeyValuePair result = null;
        while (it.hasNext())
        {
            Object el = it.next();
            if (el instanceof KeyValuePair)
            {
                KeyValuePair kp = (KeyValuePair)el;
                if (kp.getKey().equals(key))
                {
                    result = kp;
                }
            }
        }
        return result;
    }
    
	public KeyValuePair newKeyPair(String key, char separator, String value){
	    //trim key and values for usability issues - why was there a newLine ?
	    //@warn removal of the newLine might impact CarefulProjectWriter 
	    //@todo should we remove that newLine from comment too ? 
        KeyValuePair pair = new KeyValuePair(key.trim(), separator);
        pair.addToValue(value.trim());
        addElement(pair);
        return pair;
    }

	public void addToComment(Comment comment, String line) {
        if (comment == null)
        {
            comment = ElementFactory.getFactory().createComment();
            addElement(comment);
        }
        comment.addToComment(line + "\n");
    }
}
