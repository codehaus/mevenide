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

import org.apache.commons.jelly.XMLOutput;
import org.mevenide.tags.AbstractNbMevenideTag;

/**
 * A tag that will try to convert the maven version number to a Netbeans friendly version number.
 * @author <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 *
 */
public class CheckDependencyTypeTag extends AbstractNbMevenideTag {
    
    private static final String TYPE_SPECIFICATION = "spec"; //NOI18N
    private static final String TYPE_IMPLEMENTATION = "impl"; //NOI18N
    private static final String TYPE_LOOSE = "loose"; //NOI18N
    
    private String value;
    private String typeVar;
    private String dependencyValueVar;
    private String completeVar;
    
    String type;
    String dependencyValue;
    boolean complete;
    
    public void doTag(XMLOutput arg0) throws JellyTagException {
        
        checkAttribute(value, "value");
        resetExamination();
        processValue(value);
        setContextVars();
    }

    void resetExamination() {
        type = null;
        dependencyValue = null;
        complete = false;
    }
    
    void processValue(String val) throws JellyTagException {
        if (val != null) {
            int index = val.indexOf('=');
            if (index > -1) {
                type = val.substring(0, index).trim();
                dependencyValue = val.substring(index + 1).trim();
                int greaterIndex = dependencyValue.indexOf("&gt;");
                if (greaterIndex > -1) {
                    dependencyValue = dependencyValue.substring(0, greaterIndex) 
                               + ">" + dependencyValue.substring(greaterIndex + "&gt;".length());
                }
                String important = null;
                if (TYPE_IMPLEMENTATION.equals(type)) {
                    important = "=";
                }
                if (TYPE_SPECIFICATION.equals(type)) {
                    important = ">";
                }
                complete =  important == null || dependencyValue.indexOf(important) > -1;
            } else {
                type = val.trim();
            }
            if (!TYPE_SPECIFICATION.equals(type) 
                && !TYPE_IMPLEMENTATION.equals(type) 
                && !TYPE_LOOSE.equals(type)) {
                    throw new JellyTagException("Dependency type has to be one of 'loose', 'spec', 'impl'");
            }
        }
    }
    
    private void setContextVars() {
        if (dependencyValueVar != null) {
            context.setVariable(dependencyValueVar, dependencyValue);
        }
        if (typeVar != null) {
            context.setVariable(typeVar, type);
        }
        if (completeVar != null) {
            context.setVariable(completeVar, Boolean.valueOf(complete));
        }
    }    
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTypeVar() {
        return typeVar;
    }

    public void setTypeVar(String typeVar) {
        this.typeVar = typeVar;
    }

    public String getDependencyValueVar() {
        return dependencyValueVar;
    }

    public void setDependencyValueVar(String dependencyValueVar) {
        this.dependencyValueVar = dependencyValueVar;
    }

    public String getCompleteVar() {
        return completeVar;
    }

    public void setCompleteVar(String completeVar) {
        this.completeVar = completeVar;
    }
    
    
    
}
