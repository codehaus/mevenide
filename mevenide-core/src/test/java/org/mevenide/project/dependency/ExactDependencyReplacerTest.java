/*
 * ExactDependencyReplacerTest.java
 * JUnit based test
 *
 * Created on October 18, 2004, 10:52 PM
 */

package org.mevenide.project.dependency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import junit.framework.*;
import org.mevenide.project.io.IContentProvider;


/**
 *
 * @author cenda
 */
public class ExactDependencyReplacerTest extends TestCase {
    
    private TestContentProvider provider = new TestContentProvider();
    
    public ExactDependencyReplacerTest(String testName) {
        super(testName);
    }

    protected void setUp() throws java.lang.Exception {
         provider.setValue("artifactId", "testartifact");
         provider.setValue("groupId", "testGroup");
         provider.setValue("version", "1.1");
    }

    protected void tearDown() throws java.lang.Exception {
    }

    /**
     * Test of replace method, of class org.mevenide.project.dependency.ExactDependencyReplacer.
     */
    public void testReplace() {
        // should match..
         ExactDependencyReplacer replacer = new ExactDependencyReplacer("testartifact", "testGroup", "1.1",
                                                          "n1", "n2", "n3");
         IContentProvider result = replacer.replace(provider);
         assertEquals("n1", result.getValue("artifactId"));
         assertEquals("n2", result.getValue("groupId"));
         assertEquals("n3", result.getValue("version"));
         
         // wrong version, should not match and replace..
         replacer = new ExactDependencyReplacer("testartifact", "testGroup", "1.2-SNAPSHOT",
                                                "n1", "n2", "n3");
         result = replacer.replace(provider);
        
         assertEquals("testartifact", result.getValue("artifactId"));
         assertEquals("testGroup", result.getValue("groupId"));
         assertEquals("1.1", result.getValue("version"));
    }
    
    private class TestContentProvider implements IContentProvider {
        
        private List props = new ArrayList();
        private HashMap valueLists = new HashMap();
        private HashMap values = new HashMap();
        private HashMap subcontentLists = new HashMap();
        private HashMap subcontents = new HashMap();
        
        public void setValueList(String parent, List list) {
            valueLists.put(parent, list);
        }
        
        public java.util.List getValueList(String parentKey, String childKey) {
            return (List)valueLists.get(parentKey);
        }

        public void setValue(String key, String value) {
            values.put(key, value);
        }
        
        public String getValue(String key) {
            return (String)values.get(key);
        }

        public void setSubContentProviderList(String parentKey, List list) {
            subcontentLists.put(parentKey, list);
        }
        
        public java.util.List getSubContentProviderList(String parentKey, String childKey) {
            return (List)subcontentLists.get(parentKey);
        }

        public void setSubContentProvider(String key, IContentProvider prov) {
            subcontents.put(key, prov);
        }
        
        public IContentProvider getSubContentProvider(String key) {
            return (IContentProvider)subcontents.get(key);
        }

        public java.util.List getProperties() {
            return props;
        }
        
        public void setProperties(List properties) {
            props = properties;
        }
        
    }
    
}
