/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.ui.eclipse.sync.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.Resource;

import junit.framework.TestCase;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: DirectoryMappingNodeContainerTest.java,v 1.1 24 mars 2004 Exp gdodinet 
 * 
 */
public class DirectoryMappingNodeContainerTest extends TestCase {
	
	private DirectoryMappingNodeContainer container;
	
	private Resource r;
	private DirectoryMappingNode nodeR;
	
	private Directory d;
	private DirectoryMappingNode nodeD;

	private Directory d2;
	
	protected void setUp() throws Exception {
		container = new DirectoryMappingNodeContainer();
		r = new Resource();
		r.setDirectory("C:\\test\\fake");
		d = new Directory();
		d.setPath("C:/test/fake/");
		
		DirectoryMappingNode[] nodes = new DirectoryMappingNode[3];
		nodeR = new DirectoryMappingNode();
		nodeR.setArtifact(r);
		nodes[0] = nodeR;
		
		nodeD = new DirectoryMappingNode();
		nodeD.setArtifact(d);
		nodes[1] = nodeD;
		
		DirectoryMappingNode node = new DirectoryMappingNode();
		d2 = new Directory();
		d2.setPath("/tmp/dir");
		node.setResolvedDirectory(d2);
		nodes[2] = node;
		
		container.setNodes(nodes);
	}
	
	protected void tearDown() throws Exception {
		container = null;
	}
	
	public void testLowMatch() {
		
		assertTrue(container.lowMatch(r, d));
		
		d.setPath("C:/test/fake/");
		assertTrue(container.lowMatch(r, d));
	}
	
	public void testHaveSamePath() {
		assertTrue(container.haveSamePath(nodeR, nodeD));
	}
	
	public void testRemoveEquivalentItems() {
		List list = new ArrayList();
		list.add(r); 
		list.add(d); 
		list.add(d2);
		container.removeEquivalentItems(list);
		assertEquals("initial size was 3. Expected size : 2", 2, list.size());
	}
	
	public void testRemoveDuplicate() {
		List list = new ArrayList();
		list.add(r); 
		list.add(d2);
		
		DirectoryMappingNode node = new DirectoryMappingNode();
		Directory d3 = new Directory();
		d3.setPath("/tmp/dir2");
		node.setResolvedDirectory(d3);
		
		list.add(d3);
		
		container.removeDuplicate(list);
		assertEquals("initial size was 3. Expected size : 1", 1, list.size());
	}
}
