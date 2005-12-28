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
	/**
	 * The minimum value for the JVM's max heap size parameter (-Xmx).
	 * <p>
	 * Sun specifies (for their JVM) that the minimum must be greater
	 * than 2MB in increments of 1KB. Since we only deal with MB here,
	 * the next value greater than 2MB is 3MB.
	 * </p>
	 * <p>Value: {@value}MB</p> 
	 */
	public static final int XMX_MIN = 3;

	/**
	 * The maximum value for the JVM's max heap size parameter (-Xmx).
	 * <p>
	 * Sun specifies (for their JVM) that the maximum is approximately
	 * 2000MB for Windows and Linux, and 4000MB for Solaris. Independent
	 * analysis shows that the garbage collector has issues when dealing
	 * with heap sizes greater than 1.5GB. Therefore, we set the maximum
	 * value at 1MB less than 1.5GB.
	 * </p>
	 * <p>Value: {@value}MB</p> 
	 */
	public static final int XMX_MAX = 1535;

	/**
	 * The default value for the JVM's max heap size parameter (-Xmx).
	 * <p>
	 * Sun specifies (for their JVM) that the default is 64MB. We override
	 * this value for some reason lost in history.
	 * </p>
	 * <p>Value: {@value}MB</p> 
	 */
	public static final int XMX_DEFAULT = 160;

	/**
	 * The value for the JVM's max heap size parameter (-Xmx).
	 */
	private static int heapSize = XMX_DEFAULT;
    
    private static ILocationFinder defaultLocFinder;
    private static Object LOCK = new Object();
    
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
	 * Set the JVM's maximum heap size.
	 * <p>
	 * This value is passed as a JVM argument (Xmx) when launching Maven.
	 * <p>
	 * <p>
	 * This value must be between <tt>XMX_MIN</tt> (inclusive) and <tt>XMX_MAX</tt>
	 * (inclusive). Attempting to set this value less than <tt>XMX_MIN</tt> will
	 * force the value to <tt>XMX_MIN</tt>. Likewise, attempting to set this
	 * value greater than <tt>XMX_MAX</tt> will force the value to <tt>XMX_MAX</tt>.
	 * </p>
	 * 
     * @param hSize The maximum size (in MB) of the JVM's heap space.
     */
    public static void setHeapSize(int hSize) {
    	if (hSize < XMX_MIN) {
    		hSize = XMX_MIN;
    	} else if (hSize > XMX_MAX) {
    		hSize = XMX_MAX;
    	}
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
