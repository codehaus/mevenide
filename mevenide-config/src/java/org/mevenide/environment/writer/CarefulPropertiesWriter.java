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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class CarefulPropertiesWriter implements IPropertiesWriter
{
    
    private static Log logger = LogFactory.getLog(CarefulPropertiesWriter.class);
    
    private static final String whiteSpaceChars = " \t\r\n\f";
    
    /** Creates a new instance of CarefulPropertiesWriter */
    public CarefulPropertiesWriter()
    {
    }
    
    public void marshall(OutputStream output, Properties props, InputStream currentContent) throws IOException
    {
        PropModel model = createModel(currentContent);
        List newOnes = new ArrayList();
        List replacedOnes = new ArrayList();
        
        Enumeration enum = props.propertyNames();
        while (enum.hasMoreElements()) 
        {
            String key = (String)enum.nextElement();
            PropModel.KeyValuePair kp = model.findByKey(key);
            if (kp != null)
            {
                kp.setValue(saveConvert(props.getProperty(key), false));
                replacedOnes.add(kp);
            } else {
                kp = model.createKeyValuePair(key, '=');
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
            PropModel.Element obj = (PropModel.Element)it.next();
            if (obj instanceof PropModel.KeyValuePair)
            {
                if (!newOnes.contains(obj) && !replacedOnes.contains(obj))
                {
                    model.removeElement(obj);
                }
            }
        }
        marshallModel(model, output);
    }
    
    private void marshallModel(PropModel model, OutputStream stream) throws IOException
    {
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        Iterator it = model.getList().iterator();
        while (it.hasNext())
        {
            writer.write(it.next().toString());
        }
        writer.close();
    }
    
    
    private PropModel createModel(InputStream stream) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "8859_1"));
        // according to Properties.java comment, it has to be 8859_1
        String line;
        PropModel model = new PropModel();
        PropModel.Comment currComment = null;
        PropModel.KeyValuePair currKeyPair = null;
        boolean appendingMode = false;
        while ((line = reader.readLine()) != null)
        {
            // Find start of key first..
            // apparently doens't have to be the first in line..
            int len = line.length();
            int keyStart;
            for (keyStart=0; keyStart<len; keyStart++)
            {
                if (whiteSpaceChars.indexOf(line.charAt(keyStart)) == -1)
                    break;
            }
            if (appendingMode)
            {
                currComment = null;
                addToKeyPair(currKeyPair, line);
                appendingMode = continueLine(line);
                continue;
            }
            
            // Blank lines are added to comment (unless it's being added to the previous value??
            if (keyStart == len)
            {
                currKeyPair = null;
                addToComment(model, currComment, line);
                continue;
            }
            
            // Continue lines that end in slashes if they are not comments
            char firstChar = line.charAt(keyStart);
            if ((firstChar == '#') || (firstChar == '!'))
            {
                currKeyPair = null;
                addToComment(model, currComment, line);
                continue;
            }
            else
            {
                int sepIndex = Math.min(line.indexOf('='), line.indexOf(':'));
                if (sepIndex > 0)
                {
                    String key = line.substring(0, sepIndex - 1);
                    String value = line.substring(sepIndex + 1);
                    currComment = null;
                    if (key.trim().length() == 0)
                    {
                        logger.warn("strange line - key is whitespace");
                        continue;
                    }
                    currKeyPair = createKeyPair(model, key, line.charAt(sepIndex), value);
                    appendingMode = continueLine(line);
                } else
                {
                    logger.warn("A non-comment non key-pair line encountered:'" + line + "'");
                }
            }
        }
        return model;
    }
    
    
    private void addToComment(PropModel model, PropModel.Comment comment, String line)
    {
        if (comment == null)
        {
            comment = model.createComment();
            model.addElement(comment);
        }
        comment.addToComment(line + "\n");
    }

    private void addToKeyPair(PropModel.KeyValuePair pair, String line)
    {
        pair.addToValue(line + "\n");
    }
    
    private PropModel.KeyValuePair createKeyPair(PropModel model, String key, char separator, String value)
    {
        PropModel.KeyValuePair pair = model.createKeyValuePair(key, separator);
        pair.addToValue(value + "\n");
        model.addElement(pair);
        return pair;
    }
        
    
    /*
     * Returns true if the next line should be appended to this one..
     */
    private boolean continueLine(String line) {
        int slashCount = 0;
        int index = line.length() - 1;
        while ((index >= 0) && (line.charAt(index--) == '\\'))
            slashCount++;
        return (slashCount % 2 == 1);
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

