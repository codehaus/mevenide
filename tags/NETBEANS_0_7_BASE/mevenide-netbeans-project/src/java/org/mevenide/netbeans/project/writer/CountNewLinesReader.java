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

package org.mevenide.netbeans.project.writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;


/**
 * a reader that will count different kinds of newlines and
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class CountNewLinesReader extends Reader {
    Reader reader;
    /**  \n */
    static final byte LN_N = 0;
    
    /**  \r */
    static final byte LN_R = 1;
    
    /** \r\n */
    static final byte LN_RN = 2;
    /** Character buffer */
    char[] charBuff;
    char[] readBuff;
    int remain;
    
    /** The position at the current line. */
    int position;
    
    /** The count of types new line delimiters used in the file */
    final int[] newLinesCounts;
    
    CountNewLinesReader(InputStream is) throws IOException {
        this(is, null);
    }
    
    CountNewLinesReader(InputStream is, String encoding) throws IOException {
        if (encoding == null)
            reader = new InputStreamReader(is);
        else
            reader = new InputStreamReader(is, encoding);
        position = 0;
        newLinesCounts = new int[] { 0, 0, 0 };
    }
    
   /** Close underlayed writer. */
    public void close() throws IOException {
        reader.close();
    }
    
    /** Read the array of chars */
    public int read(char[] cbuf, int off, int len) throws IOException {
        
        if (charBuff == null) {
            readCharBuff();
            translateToCharBuff();
        }
        
        if (remain <= 0) {
            return -1;
        } else {
            int min = Math.min(len, remain);
            System.arraycopy(charBuff, position, cbuf, off, min);
            remain -= min;
            position += min;
            return min;
        }
    }
    
    /** Reads readBuff */
    private final void readCharBuff() throws IOException {
        
        char[] tmp = new char[2048];
        int read;
        ArrayList buffs = new ArrayList(20);
        
        for (;;) {
            read = readFully(tmp);
            buffs.add(tmp);
            if (read < 2048) {
                break;
            } else {
                tmp = new char[2048];
            }
        }
        
        int listsize = buffs.size() - 1;
        int size = listsize * 2048 + read;
        readBuff = new char[size];
        charBuff = new char[size];
        int copy = 0;
        
        for (int i = 0; i < listsize; i++) {
            char[] tmp2 = (char[]) buffs.get(i);
            System.arraycopy(tmp2, 0, readBuff, copy, 2048);
            copy += 2048;
        }
        System.arraycopy(tmp, 0, readBuff, copy, read);
    }
    
    /** reads fully given buffer */
    private final int readFully(final char[] buff) throws IOException {
        int read = 0;
        int sum = 0;
        
        do {
            read = reader.read(buff, sum, buff.length - sum);
            sum += read;
        } while ((sum < buff.length) && (read > 0));
        
        return sum + 1;
    }
    
    /** Called after raw filling from an underlying reader */
    final void translateToCharBuff() {
        position = 0;
        
        // points to first unused cell in charBuff
        int charBuffPtr = 0;
        int stop = readBuff.length - 1;
        
        int c;
        // ptr to first not processed char in readBuff
        int i = 0;
        
        //process newlines so only '\n' appears in the charBuff
        //count all kinds of newlines - most used will be used on save
        while (i < stop) {
            c = readBuff[i];
            switch (c) {
                case (int)'\n':
                    newLinesCounts[LN_N] = newLinesCounts[LN_N] + 1;
                    charBuff[charBuffPtr++] = '\n';
                    i++;
                    break;
                case (int)'\r':
                    int c2 = readBuff[i + 1];
                    if (c2 != (int)'\n') {
                        newLinesCounts[LN_R] = newLinesCounts[LN_R] + 1;
                        i++;
                    } else {
                        i +=2;
                        newLinesCounts[LN_RN] = newLinesCounts[LN_RN] + 1;
                    }
                    charBuff[charBuffPtr++] = '\n';
                    break;
                    
                default:
                    charBuff[charBuffPtr++] = readBuff[i++];
            }
            
        }
        
        if (i == stop) {
            c = readBuff[i];
            switch (c) {
                case (int)'\n':
                    newLinesCounts[LN_N]++;
                    charBuff[charBuffPtr++] = '\n';
                    break;
                case (int)'\r':
                    newLinesCounts[LN_R]++;
                    charBuff[charBuffPtr++] = '\n';
                    break;
                    
                default:
                    charBuff[charBuffPtr++] = readBuff[i++];
            }
        }
        
        remain = charBuffPtr;
        readBuff = null;
    }
    
    /** Searches for newline from i */
    final int toNewLine(int i) {
        int chr;
        int counter = i;
        final int len = readBuff.length;
        while (counter < len) {
            chr = readBuff[counter++];
            if (chr == '\r' || chr == '\n') {
                counter = counter - 1;
                break;
            }
        }
        return counter - i;
    }
    
    public String getNewLineString() {
        if (newLinesCounts[0] == newLinesCounts[1] &&
                newLinesCounts[1] == newLinesCounts[2]) {
            String s = System.getProperty("line.separator"); //NOI18N
            return s;
        }
        if (newLinesCounts[0] > newLinesCounts[1]) {
            return (newLinesCounts[0] > newLinesCounts[2]) ? "\n" : "\r\n"; //NOI18N
        } else {
            return (newLinesCounts[1] > newLinesCounts[2]) ? "\r" : "\r\n"; //NOI18N
        }
    }
    
 
}