/*
 * NetbeansBuildActionXpp3Reader.java
 * JUnit based test
 *
 * Created on January 16, 2006, 7:15 AM
 */

package org.codehaus.mevenide.netbeans.execute.model.io.xpp3;

import java.io.StringReader;
import junit.framework.*;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;

/**
 *
 * @author mkleint
 */
public class NetbeansBuildActionXpp3ReaderTest extends TestCase {
    
    public NetbeansBuildActionXpp3ReaderTest(String testName) {
        super(testName);
    }

    public void testReader() throws Exception {
        String string1 = 
                "<action>\n" +
                   "<goals><goal>hello</goal></goals>\n" + 
                   "<properties><helloprop>value1</helloprop></properties>\n" +
                "</action>";
        StringReader sr = new StringReader(string1);
        NetbeansBuildActionXpp3Reader reader = new NetbeansBuildActionXpp3Reader();
        NetbeansActionMapping act = reader.read(sr);
        assertEquals(act.getGoals().size(), 1);
        assertEquals(act.getProperties().getProperty("helloprop"), "value1");
        assertEquals(act.getPlugins().size(), 0);
        
    }

}
