/*
 * DefaultPluginInfoTest.java
 * JUnit based test
 *
 * Created on November 27, 2004, 5:20 PM
 */

package org.mevenide.plugins;

import junit.framework.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author cenda
 */
public class DefaultPluginInfoTest extends TestCase {
    
    public DefaultPluginInfoTest(String testName) {
        super(testName);
    }

    protected void setUp() throws java.lang.Exception {
    }

    protected void tearDown() throws java.lang.Exception {
    }


    /**
     * Test of readProjectValues method, of class org.mevenide.plugins.DefaultPluginInfo.
     */
    public void testReadProjectValues() {
        File fil = new File(this.getClass().getResource("/project.xml").getFile());
        
        DefaultPluginInfo info = new DefaultPluginInfo(new File("cachefilewhatever"));
        info.readProjectValues(fil);
        assertEquals("Goals Grabber", info.getLongName());
        assertEquals("Gets all the available goals", info.getDescription());
        
    }
    
}
