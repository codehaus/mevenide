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

package org.mevenide.netbeans.project;
import java.lang.reflect.Method;
import org.openide.util.Lookup;
import org.openide.util.SharedClassObject;

/**
 * utilities for proxy settings and artifact downloads.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public final class ProxyUtilities {
    
    private static final String PROXY_HOST = "http.proxyHost"; //NOI18N
    private static final String PROXY_PORT = "http.proxyPort"; //NOI18N
    
    private static Method mGetProxyPort;
    private static Method mGetProxyHost;
    private static SharedClassObject settingsInst;
    private static boolean tryReflection = true;
    /** Creates a new instance of ProxyUtilities */
    private ProxyUtilities() {
    }
    
    
    private static synchronized void reflectIDESettings() {
        try {
            ClassLoader l = (ClassLoader)Lookup.getDefault().lookup(ClassLoader.class);
            Class clazz = l.loadClass("org.netbeans.core.IDESettings"); // NOI18N
            settingsInst = SharedClassObject.findObject(clazz, true);
            mGetProxyHost = clazz.getMethod("getUserProxyHost", (Class[])null); // NOI18N
            mGetProxyPort = clazz.getMethod("getUserProxyPort", (Class[])null); // NOI18N
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tryReflection = false;
        }
    } 
    
    public static String getProxyHost() {
        if (tryReflection) {
            reflectIDESettings();
        }
        String toRet = reflValue(mGetProxyHost);
        return toRet != null ? toRet : System.getProperty(PROXY_HOST);
    }
    
    public static String getProxyPort() {
        if (tryReflection) {
            reflectIDESettings();
        }
        String toRet = reflValue(mGetProxyPort);
        return toRet != null ? toRet : System.getProperty(PROXY_PORT);
    }
    
    private static String reflValue(Method method) {
        if (method != null) {
            try {
                return (String)method.invoke(settingsInst, new Object[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    

}
