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
package org.mevenide.properties;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Model of properties file.
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 * @author  <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 */
public class PropertyModel
{
    private static final Logger LOGGER = Logger.getLogger(PropertyModel.class.getName());
    
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
        String lineSeparator = new String(new byte[]{ Character.LINE_SEPARATOR});
        if ( result != null ) {
            result.setValue(result.getValue().trim().replaceAll("\\\\r\\\\n", lineSeparator));
            result.setValue(result.getValue().trim().replaceAll("\\\\r", lineSeparator));
            result.setValue(result.getValue().trim().replaceAll("\\\\n", lineSeparator));
        }
        return result;
    }
    
	public KeyValuePair newKeyPair(String key, char separator, String value){
	    //trim key and values for usability issues - why was there a newLine ?
	    //@warn removal of the newLine might impact CarefulProjectWriter 
	    //@todo should we remove that newLine from comment too ? 
		KeyValuePair pair = findByKey(key.trim());
		if ( pair == null ) {
	        pair = new KeyValuePair(key.trim(), separator);
	        pair.addToValue(value.trim());
	        addElement(pair);
		}
		else {
			pair.setValue(value.trim());
		}
		return pair;
    }

	public void store(OutputStream stream) throws IOException {
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(stream);
		    Iterator it = getList().iterator();
		    while (it.hasNext()) {
		        writer.write(it.next().toString());
		        writer.write('\n');
		    }
		}
		finally {
			if ( writer != null ) {
			    writer.close();
			}
		}
	}
	
	
    public String toString() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String modelToString = null;
        try {
            store(bos);
            modelToString = "PropertyModel = " + new String(bos.toByteArray());
            
        }
        catch (IOException e) {
            String message = "unable to write model to Stream"; 
            LOGGER.log(Level.SEVERE, message, e);
        }
        finally {
            if ( bos != null ) {
                try {
                    bos.close();
                }
                catch (IOException e1) {
                    String message = "Cannot close stream"; 
                    LOGGER.log(Level.SEVERE, message, e1);
                }
            }
        }
        return modelToString;
    }
	
	public void addToComment(Comment comment, String line) {
        if (comment == null)
        {
            comment = ElementFactory.getFactory().createComment();
            addElement(comment);
        }
        comment.addToComment(line);
    }
}
