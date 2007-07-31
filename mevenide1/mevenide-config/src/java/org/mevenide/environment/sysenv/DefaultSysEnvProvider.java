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
    
    private Properties envProperties;
    
    private boolean loaded;
    private Object LOCK = new Object();
    
    public DefaultSysEnvProvider()
    {
        loaded = false;
    }
    
    private void loadEnvironment()
    {
        try {
            envProperties = getEnvVars();
        } catch (Exception exc) {
            LOGGER.log(Level.SEVERE,"Problem while looking for env. variables.", exc);
            envProperties = new Properties();
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
        return envProperties.getProperty(name);
    }
    
    private static Properties getEnvVars() throws IOException
    {
        Process p = null;
        Properties envVars = new Properties();
        Runtime r = Runtime.getRuntime();
        String os = System.getProperty("os.name").toLowerCase();
        // System.out.println(OS);
        if (os.indexOf("windows")  > -1) {
            if (os.indexOf("windows 9") > -1)
            {
                // old Win9X stuff..
                p = r.exec( "command.com /c set" );
            }
            else 
            {
                // for everything else (XP, 2000, NT
                p = r.exec( "cmd.exe /c set" );
            }
        } else 
        {
            // let's assume what is not windows is unix..
            p = r.exec( "sh -c env" );
        }
        BufferedReader br = new BufferedReader
                                    ( new InputStreamReader( p.getInputStream() ) );
        String line;
        while( (line = br.readLine()) != null )
        {
            int idx = line.indexOf( '=' );
            if (idx > 0) {
                String key = line.substring( 0, idx );
                String value = line.substring( idx+1 );
                envVars.setProperty( key, value );
            } else {
                //what now? wrong line format?
            }
        }
        return envVars;
    }
}
