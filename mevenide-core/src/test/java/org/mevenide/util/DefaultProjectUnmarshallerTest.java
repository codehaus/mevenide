/*
 * Created on 26 juil. 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.mevenide.util;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;

/**
 * @author gdodinet
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DefaultProjectUnmarshallerTest extends TestCase {

	private DefaultProjectUnmarshaller unmarshaller;
	private FileReader reader;
	
	protected void setUp() throws Exception {
		File pom = new File(DefaultProjectUnmarshallerTest.class.getResource("/project.xml").getFile());
		reader = new FileReader(pom);
		unmarshaller = new DefaultProjectUnmarshaller(); 
	}

	
	protected void tearDown() throws Exception {
		unmarshaller = null;
	}
	
	public void testUnmarshallProperties() throws Exception {
		Project project = unmarshaller.parse(reader);
		List deps = project.getDependencies();
		for (int i = 0; i < deps.size(); i++) {
			Dependency d = (Dependency) deps.get(i);
			if ( "maven".equals(d.getGroupId()) && "maven".equals(d.getArtifactId() ) ) {
				assertEquals(2, d.getProperties().size());
				assertEquals("true", d.getProperty("test.prop"));
				assertEquals("it worked", d.getProperty("anotherProp"));
			}
		}	
	}
	
}
