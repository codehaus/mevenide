/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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
package org.mevenide.tags.eclipse;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.jelly.JellyContext;
import org.apache.maven.project.Dependency;
import org.apache.maven.repository.Artifact;
import org.apache.maven.repository.DefaultArtifactFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.mevenide.tags.InvalidDirectoryException;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class UpdatePluginLibsTagTest extends TestCase {
    private static class MockJellyContext extends JellyContext {
        private Map variables;
        public MockJellyContext() {
            variables = new HashMap();
        }
        public void addExpectedVariable(String key, Object value) {
            variables.put(key, value);   
        }
        public Object findVariable(String strg) {
            return variables.get(strg);
        }
        public Object getVariable(String strg) {
            return findVariable(strg);
        }
		public void removeVariable(String strg) {
            variables.remove(strg);
        }
    }
    private static class MockUpdatePluginLibsTag extends UpdatePluginLibsTag {
        private int addRuntimeLibraryCalls = 0;
		private int expectedAddRuntimeLibraryCalls = 0;
        void setExpectedAddRuntimeLibraryCalls(int ncalls) {
			expectedAddRuntimeLibraryCalls = ncalls;
		}
        void addRuntimeLibrary(Artifact artifact) {
         	addRuntimeLibraryCalls++;   
        }
        
		private int addRequiresPluginCalls = 0;
		private int expectedAddRequiresPluginCalls = 0;
		void setExpectedAddRequiresPluginCalls(int ncalls) {
			expectedAddRequiresPluginCalls = ncalls;
		}
		void addRequiresPlugin(Artifact artifact) {
            addRequiresPluginCalls++;
        }

		void verify() {
		    assertEquals(expectedAddRuntimeLibraryCalls, addRuntimeLibraryCalls);
			assertEquals(expectedAddRequiresPluginCalls, addRequiresPluginCalls);
		} 
    }
    
    private UpdatePluginLibsTag tag = null;
    private Artifact artifact;
    
    private Document doc = null;
    
    protected void setUp() throws Exception {
        setupTag();
        setupArtifact();
        setupDoc();
    }

    private void setupTag() throws Exception {
        tag = new UpdatePluginLibsTag();
        MockJellyContext context = new MockJellyContext();
        context.addExpectedVariable("maven.eclipse.plugin.src.dir", new File(UpdatePluginLibsTagTest.class.getResource("/").getFile(), "full-descriptor").getAbsolutePath());
        tag.setContext(context);
    }
    
    private void setupArtifact() {
        Dependency d = new Dependency();
        d.setGroupId("theGroup");
        d.setArtifactId("theArtifact");
        d.setVersion("theVersion");
        artifact = DefaultArtifactFactory.createArtifact(d);
    }
    
    private void setupDoc() {
        doc = new Document();
        Element root = new Element("plugin");
        root.setAttribute("version", "1.0.0");
        root.setAttribute("id", "org.mevenide.test");
        root.setAttribute("name", "Mevenide Unit Tests");
        doc.setRootElement(root);
    }
    
    protected void tearDown() throws Exception {
        tag = null;
    }
    
    public void testSetUpDescriptor() throws Exception {
        tag.setUpDescriptor();
        Element root = tag.getDescriptor().getRootElement();
        assertEquals(2, root.getChildren().size());
        assertEquals("plugin", root.getName());
        assertEquals("1.0.0", root.getAttributeValue("version"));
        assertEquals("Mevenide Unit Tests", root.getAttributeValue("name"));
        assertEquals("org.mevenide.test", root.getAttributeValue("id"));
    }
    
    public void testGetBundleMode() {
        ((MockJellyContext) tag.getContext()).addExpectedVariable("maven.eclipse.plugin.build.mode", null);
        assertEquals("", tag.getBundleMode());
        
        ((MockJellyContext) tag.getContext()).addExpectedVariable("maven.eclipse.plugin.build.mode", "");
        assertEquals("", tag.getBundleMode());
        
        ((MockJellyContext) tag.getContext()).addExpectedVariable("maven.eclipse.plugin.build.mode", "bundle");
        assertEquals("bundle", tag.getBundleMode());
        
        ((MockJellyContext) tag.getContext()).addExpectedVariable("maven.eclipse.plugin.build.mode", "bundle  ");
        assertEquals("bundle", tag.getBundleMode());
    }
    
    public void testGetPluginName() {
        assertEquals("theGroup", tag.getPluginName(artifact));
    }
    
    public void testAssertRuntimePresent() throws Exception {
        tag.setUpDescriptor();
        tag.assertRuntimePresent();
        assertTrue(tag.getDescriptor().getRootElement().getChild("runtime") != null);
        
        MockJellyContext context = new MockJellyContext();
        context.addExpectedVariable("maven.eclipse.plugin.src.dir", new File(UpdatePluginLibsTagTest.class.getResource("/").getFile(), "empty-descriptor").getAbsolutePath());
        tag.setContext(context);
        tag.setUpDescriptor();
        tag.assertRuntimePresent();
        assertTrue(tag.getDescriptor().getRootElement().getChild("runtime") != null);
    }

    public void testAssertRequiresPresent() throws Exception {
        tag.setUpDescriptor();
        tag.assertRequiresPresent();
        assertTrue(tag.getDescriptor().getRootElement().getChild("requires") != null);
        
        MockJellyContext context = new MockJellyContext();
        context.addExpectedVariable("maven.eclipse.plugin.src.dir", new File(UpdatePluginLibsTagTest.class.getResource("/").getFile(), "empty-descriptor").getAbsolutePath());
        tag.setContext(context);
        tag.setUpDescriptor();
        tag.assertRequiresPresent();
        assertTrue(tag.getDescriptor().getRootElement().getChild("requires") != null);
    }
    
    public void testIsRequiredPluginDeclared() throws Exception {
        tag.setUpDescriptor();
        assertFalse(tag.isRequiredPluginDeclared(artifact));
        
        artifact.getDependency().setGroupId("org.mevenide.test");
        assertTrue(tag.isRequiredPluginDeclared(artifact));
    }

    public void testIsLibraryDeclared() throws Exception {
        tag.setUpDescriptor();
        assertFalse(tag.isLibraryDeclared(artifact));
        
        artifact.getDependency().setArtifactId("myDep");
        artifact.getDependency().setVersion("1.0");
        assertTrue(tag.isLibraryDeclared(artifact));
    }
    
    public void testAddRequiresPlugin() throws Exception {
        tag.setUpDescriptor();
        assertFalse(tag.isRequiredPluginDeclared(artifact));
        
        artifact.getDependency().setGroupId("org.mevenide.test");
        tag.addRequiresPlugin(artifact);
        assertTrue(tag.isRequiredPluginDeclared(artifact));
        
        int previousChildrenLength = tag.getDescriptor().getRootElement().getChild("requires").getChildren().size();
        tag.addRequiresPlugin(artifact);
        assertEquals(previousChildrenLength + 1, tag.getDescriptor().getRootElement().getChild("requires").getChildren().size());
        
    }

    public void testAddRuntimeLibrary() throws Exception {
        tag.setUpDescriptor();
        assertFalse(tag.isLibraryDeclared(artifact));
        
        artifact.getDependency().setArtifactId("myDep");
        artifact.getDependency().setVersion("1.0");
        tag.addRuntimeLibrary(artifact);
        assertTrue(tag.isLibraryDeclared(artifact));
        
        int previousChildrenLength = tag.getDescriptor().getRootElement().getChild("runtime").getChildren().size();
        tag.addRequiresPlugin(artifact);
        assertEquals(previousChildrenLength, tag.getDescriptor().getRootElement().getChild("runtime").getChildren().size());
    }
		    
    public void testUpdateRuntime() throws Exception {
        MockUpdatePluginLibsTag mockTag = new MockUpdatePluginLibsTag(); 
        mockTag.setExpectedAddRuntimeLibraryCalls(1);

		MockJellyContext context = new MockJellyContext();
        context.addExpectedVariable("maven.eclipse.plugin.src.dir", new File(UpdatePluginLibsTagTest.class.getResource("/").getFile(), "full-descriptor").getAbsolutePath());
        context.addExpectedVariable("maven.eclipse.plugin.bundle.lib.dir", "lib");
		mockTag.setContext(context);
        mockTag.setUpDescriptor();

	    mockTag.updateRuntime(artifact);

        mockTag.verify();

		context.addExpectedVariable("maven.eclipse.plugin.bundle.lib.dir", UpdatePluginLibsTagTest.class.getResource("/").getFile());
		mockTag.setContext(context);
		mockTag.setUpDescriptor();

		try {
            mockTag.updateRuntime(artifact);
			fail("Expected InvalidDirectory Exception : bundled libraries dir is expected to be a subdirectory of ${basedir} (created if non existent)");
        }
        catch (InvalidDirectoryException e) { }
		
    }
    
    public void testUpdateRequires() throws Exception {
		MockUpdatePluginLibsTag mockTag = new MockUpdatePluginLibsTag(); 
        mockTag.setExpectedAddRequiresPluginCalls(1);

		MockJellyContext context = new MockJellyContext();
        context.addExpectedVariable("maven.eclipse.plugin.src.dir", new File(UpdatePluginLibsTagTest.class.getResource("/").getFile(), "full-descriptor").getAbsolutePath());
		mockTag.setContext(context);
        mockTag.setUpDescriptor();

        mockTag.updateRequires(artifact);
        mockTag.verify();
    }
    
    public void testUpdateDescriptor() throws Exception {
		artifact.getDependency().resolvedProperties().put("eclipse.plugin.bundle", "true");
		MockUpdatePluginLibsTag mockTag = new MockUpdatePluginLibsTag();
		mockTag.setExpectedAddRequiresPluginCalls(1);
			
		MockJellyContext context = new MockJellyContext();
		context.addExpectedVariable("maven.eclipse.plugin.build.mode", "dist");
		context.addExpectedVariable("maven.eclipse.plugin.src.dir", new File(UpdatePluginLibsTagTest.class.getResource("/").getFile(), "full-descriptor").getAbsolutePath());
		mockTag.setContext(context);
		mockTag.setUpDescriptor();

		mockTag.updateDescriptor(artifact);
		mockTag.verify();

		mockTag = new MockUpdatePluginLibsTag();
		context = new MockJellyContext();
		mockTag.setExpectedAddRuntimeLibraryCalls(1);
		context.addExpectedVariable("maven.eclipse.plugin.build.mode", "bundle");
		context.addExpectedVariable("maven.eclipse.plugin.bundle.lib.dir", "lib");
		context.addExpectedVariable("maven.eclipse.plugin.src.dir", new File(UpdatePluginLibsTagTest.class.getResource("/").getFile(), "full-descriptor").getAbsolutePath());
		mockTag.setContext(context);
		mockTag.setUpDescriptor();

		mockTag.updateDescriptor(artifact);
		mockTag.verify();
    }


    //@TODO
    public void testShouldExport() {
    }

    public void testGetPackagesPrefixes() {
    }

    public void testOutputDescriptor() {
    }

    public void testValidateDescriptor() {
    }

}
