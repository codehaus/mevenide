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
package org.mevenide.netbeans.project.exec;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.util.StringTokenizer;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class GoalsListPropEditor extends PropertyEditorSupport
{
    private final static String GOAL_SEPARATOR = "/"; //NOI18N
    /** Creates new Goal */
    public GoalsListPropEditor()
    {
    }
    
    public Component getCustomEditor()
    {
        return new GoalCustomEditor(this);
    }
    
    public boolean supportsCustomEditor()
    {
        return true;
    }
    
    public void setAsText(String text) throws java.lang.IllegalArgumentException
    {
        StringTokenizer tok = new StringTokenizer(text, GOAL_SEPARATOR, false);
        String[] set = new String[tok.countTokens()];
        int index = 0;
        while (tok.hasMoreTokens())
        {
            set[index] = tok.nextToken();
            index++;
        }
        setValue(set);
    }
    
    public String getAsText()
    {
        String[] str = (String[])getValue();
        StringBuffer buf = new StringBuffer(100);
        for (int i =0; i < str.length; i++)
        {
            buf.append(str[i]);
            if (i < str.length - 1)
            {
                buf.append(GOAL_SEPARATOR);
            }
        }
        return buf.toString();
    }

    
}
