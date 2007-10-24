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
package org.mevenide.environment.sysenv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import java.util.logging.Logger;

/**
 * Default implementation of SysEnvProvider, will execute an external process and
 * get the system environment variables. Is operating system dependent.
 * @author Milos Kleint
 *
 */
public class DefaultSysEnvProvider implements SysEnvProvider
{
    private static Logger LOGGER = Logger.getLogger(DefaultSysEnvProvider.class.getName());
    
    private Map<String, String> envMap;
    
    private boolean loaded;
    private Object LOCK = new Object();
    
    public DefaultSysEnvProvider()
    {
        loaded = false;
    }
    
    private void loadEnvironment()
    {
        try {
            envMap = System.getenv();
        } catch (Exception exc) {
            LOGGER.log(Level.SEVERE,"Problem while looking for env. variables.", exc);
            envMap = new HashMap<String, String>();
        }
    }
    
    public String getProperty(String name)
    {
        if (!loaded)
        {
            synchronized(LOCK)
            {
                if (!loaded)
                {
                    loadEnvironment();
                    loaded = true;
                }
            }
        }
        return envMap.get(name);
    }
}
