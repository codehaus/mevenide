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
package org.mevenide.properties.writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.mevenide.properties.Element;
import org.mevenide.properties.ElementFactory;
import org.mevenide.properties.KeyValuePair;
import org.mevenide.properties.PropertyModel;
import org.mevenide.properties.PropertyModelFactory;

/**
 *
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a> 
 */
public class CarefulPropertiesWriter implements IPropertiesWriter
{   
    private static Logger LOGGER = Logger.getLogger(CarefulPropertiesWriter.class.getName());
    
    /** Creates a new instance of CarefulPropertiesWriter */
    public CarefulPropertiesWriter()
    {
    }
    
    public void marshall(OutputStream output, Properties props, InputStream currentContent) throws IOException
    {
        PropertyModel model = PropertyModelFactory.getFactory().newPropertyModel(currentContent);
        List newOnes = new ArrayList();
        List replacedOnes = new ArrayList();
        
        Enumeration en = props.propertyNames();
        while (en.hasMoreElements()) 
        {
            String key = (String)en.nextElement();
            KeyValuePair kp = model.findByKey(key);
            if (kp != null)
            {
                kp.setValue(saveConvert(props.getProperty(key), false));
                replacedOnes.add(kp);
            } else {
                kp = ElementFactory.getFactory().createKeyValuePair(key, '=');
                kp.setValue(saveConvert(props.getProperty(key), false));
                newOnes.add(kp);
                //TODO for now append to the model, later have some better positioning heuristics
                model.addElement(kp);
            }
        }
        // now remove theobsolete ones
        List modelList = model.getList();
        Iterator it = modelList.iterator();
        while (it.hasNext())
        {
            Element obj = (Element)it.next();
            if (obj instanceof KeyValuePair)
            {
                if (!newOnes.contains(obj) && !replacedOnes.contains(obj))
                {
                    model.removeElement(obj);
                }
            }
        }
        marshallModel(model, output);
    }
    
    private void marshallModel(PropertyModel model, OutputStream stream) throws IOException
    {
        model.store(stream);
    }
    
    /*
     * Converts unicodes to encoded &#92;uxxxx
     * and writes out any of the characters in specialSaveChars
     * with a preceding slash
     * Milos Kleint -= Copied from java.util.Properties.java
     *                it's a pity it's private in Properties.. :(
     */
    private static final String specialSaveChars = "=: \t\r\n\f#!";
    
    private String saveConvert(String theString, boolean escapeSpace) {
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len*2);

        for(int x=0; x<len; x++) {
            char aChar = theString.charAt(x);
            switch(aChar) {
        		case ' ':
                    if (x == 0 || escapeSpace) outBuffer.append('\\'); outBuffer.append(' ');
                          break;
                case '\\':outBuffer.append('\\'); outBuffer.append('\\');
                          break;
                case '\t':outBuffer.append('\\'); outBuffer.append('t');
                          break;
                case '\n':outBuffer.append('\\'); outBuffer.append('n');
                          break;
                case '\r':outBuffer.append('\\'); outBuffer.append('r');
                          break;
                case '\f':outBuffer.append('\\'); outBuffer.append('f');
                          break;
                default:
                    if ((aChar < 0x0020) || (aChar > 0x007e)) {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex((aChar >> 12) & 0xF));
                        outBuffer.append(toHex((aChar >>  8) & 0xF));
                        outBuffer.append(toHex((aChar >>  4) & 0xF));
                        outBuffer.append(toHex( aChar        & 0xF));
                    } else {
                        if (specialSaveChars.indexOf(aChar) != -1)
                            outBuffer.append('\\');
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }    
    
   /**
     * Convert a number to a hex character
     */
    private static char toHex(int number) 
    {
    	return hexDigit[(number & 0xF)];
    }

    /** A table of hex digits */
    private static final char[] hexDigit = {
    	'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };    
}

