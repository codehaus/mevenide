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

import org.mevenide.ui.netbeans.MavenPropertyFiles;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class PropValue
{
    
    private String layer;
    private String value;
    private PropValue override;
    private String key;
    
    public PropValue(String layer, String key, String value, PropValue override)
    {
        this.layer = layer;
        this.value = value;
        this.override = override;
        this.key = key;
    }
    
    public PropValue getOverride()
    {
        return override;
    }
    
    public String getValue()
    {
        return value;
    }
    public String getLayer()
    {
        return layer;
    }
    public String getLayerDesc()
    {
        String toReturn;
        if (MavenPropertyFiles.PROP_PROJECT.equals(layer))
        {
            toReturn = "Project";
        } else if (MavenPropertyFiles.PROP_PROJECT_BUILD.equals(layer))
        {
            toReturn = "Project Build";
        } else if (MavenPropertyFiles.PROP_USER_BUILD.equals(layer))
        {
            toReturn = "User build";
        } else
        {
            toReturn = layer;
        }
        return toReturn;
    }
    public String getKey()
    {
        return key;
    }
    
}
