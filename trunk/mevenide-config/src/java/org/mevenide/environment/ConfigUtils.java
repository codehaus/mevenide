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
package org.mevenide.environment;

import java.io.File;

/**
 * Utility class.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public final class ConfigUtils {

    private static ILocationFinder defaultLocFinder;
    private static Object LOCK = new Object();
	/** heap size */
	private static int heapSize = 160;
    
    
    /** Creates a new instance of Utils */
    private ConfigUtils() {
    }
    
    /**
     * a default location finder, non-project based, just takes the system environment
     * and userdir based values into account.
     * @deprecated don't use, doesn't take the project's properties into account. Instead
     * create and keep an instance of LocatioFinderAgrregator created with IQueryContext of the project.
     */
    public static ILocationFinder getDefaultLocationFinder() {
        if (defaultLocFinder == null) {
            synchronized (LOCK) {
                if (defaultLocFinder == null) {
                    defaultLocFinder = new LocationFinderAggregator();
                }
            }
        }
        return defaultLocFinder;
    }
    
    /**
     * @return maximum java heap size - passed as vm argument (Xmx) when launching maven
     */
    public static int getHeapSize() {
        return heapSize;
    }
	/**
	 * set maximum java heap size - passed as vm argument (Xmx) when launching maven
	 */
    public static void setHeapSize(int hSize) {
        heapSize = hSize;
    }    

    /**
     * constructs the endorsedDirs property needed for Maven execution
     * @return "JAVA_HOME/lib/endorsed:MAVEN_HOME/lib/endorsed"
     */
	public static String getEndorsedDirs() {
		return getDefaultLocationFinder().getJavaHome() + File.separatorChar 
		          + "lib" + File.separatorChar + "endorsed"
		          + File.pathSeparator + getDefaultLocationFinder().getMavenHome()
		          + File.separator + "lib" + File.separator + "endorsed";
	}

   /**
     * @return the configuration file. for now the value of forehead.conf.file 
     */
	public static String getConfigurationFile() {
        return new File(new File(getDefaultLocationFinder().getMavenHome(), "bin"), "forehead.conf").getAbsolutePath();
	}    
    
}
