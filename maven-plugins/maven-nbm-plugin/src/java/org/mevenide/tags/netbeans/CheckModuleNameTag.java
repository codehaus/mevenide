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
package org.mevenide.tags.netbeans;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

import org.apache.commons.jelly.XMLOutput;
import org.mevenide.tags.AbstractNbMevenideTag;

/**
 * A tag that will try normalize the modulename for use as maven.nbm.final name for the jar file.
 * @author <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 *
 */
public class CheckModuleNameTag extends AbstractNbMevenideTag {
    
    private String value;
    
    String finalName;
    String finalNameVar;
    
    public void doTag(XMLOutput arg0) throws MissingAttributeException, JellyTagException {
        
        checkAttribute(value, "value");
        resetExamination();
        processValue(value);
        setContextVars();
    }

    void resetExamination() {
        finalName = null;
    }
    
    void processValue(String val) throws JellyTagException {
        if (val != null) {
            int index = val.indexOf('/');
            if (index > -1) {
                finalName = val.substring(0, index).trim();
            } else {
                finalName = val;
            }
            finalName = finalName.replace('.', '-');
        }
    }
    
    private void setContextVars() {
        if (finalNameVar != null) {
            context.setVariable(finalNameVar, finalName);
        }
    }    
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFinalNameVar() {
        return finalNameVar;
    }

    public void setFinalNameVar(String typeVar) {
        finalNameVar = typeVar;
    }

}
