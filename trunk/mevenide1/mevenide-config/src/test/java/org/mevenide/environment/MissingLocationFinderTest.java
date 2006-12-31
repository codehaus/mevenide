/* ==========================================================================
 * Copyright 2003-2005 Mevenide Team
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

import junit.framework.TestCase;

/**
 * Ensures that MissingLocationFinder follows the <tt>Null Object Pattern</tt>.
 * @author fdutton
 */
public class MissingLocationFinderTest extends TestCase {
    private ILocationFinder finder;

    protected void setUp() throws Exception {
        this.finder = new MissingLocationFinder();
    }

    protected void tearDown() throws Exception {
        this.finder = null;
    }

    public void testGetMavenHome() {
        assertNull(this.finder.getMavenHome());
    }

    public void testGetJavaHome() {
        assertNull(this.finder.getJavaHome());
    }

    public void testGetUserHome() {
        assertNull(this.finder.getUserHome());
    }

    public void testGetMavenLocalHome() {
        assertNull(this.finder.getMavenLocalHome());
    }

    public void testGetMavenLocalRepository() {
        assertNull(this.finder.getMavenLocalRepository());
    }

    public void testGetMavenPluginsDir() {
        assertNull(this.finder.getMavenPluginsDir());
    }

    public void testGetPluginJarsDir() {
        assertNull(this.finder.getPluginJarsDir());
    }

    public void testGetUserPluginsDir() {
        assertNull(this.finder.getUserPluginsDir());
    }

    public void testGetConfigurationFileLocation() {
        assertNull(this.finder.getConfigurationFileLocation());
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MissingLocationFinderTest.class);
    }

}
