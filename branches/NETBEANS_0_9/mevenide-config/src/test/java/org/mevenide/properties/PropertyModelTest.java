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
package org.mevenide.properties;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PropertyModelTest extends TestCase {

    private PropertyModel model;
    private Comment comment ;

    protected void setUp() throws Exception {
        model = new PropertyModel();
    }

    protected void tearDown() throws Exception {
        model = null;
    }

    public void testAddElement() {
        addEmptyComment();
        assertEquals(1, model.getList().size());
    }

    private void addEmptyComment() {
        comment = new Comment();
        model.addElement(comment);
    }

    public void testRemoveElement() {
        addEmptyComment();
        
        Comment anotherComment = new Comment();
        model.removeElement(anotherComment);
        assertEquals(1, model.getList().size());
        
        model.removeElement(comment);
        assertEquals(0, model.getList().size());
    }

    public void testGetSize() {
        addEmptyComment();
        assertEquals(1, model.getSize());
    }

    public void testInsertAt() {
        Comment comment_0 = new Comment();
        Comment comment_1 = new Comment();
        Comment comment_2 = new Comment();
        model.addElement(comment_0);
        model.addElement(comment_2);
        model.insertAt(1, comment_1);
        assertEquals(comment_1, model.getList().get(1));
    }

    public void testFindByKey() {
    	addEmptyComment();
		assertNull(model.findByKey("myKey"));
		
		KeyValuePair keyValuePair = new KeyValuePair("myKey", '=');
		model.addElement(keyValuePair);
		assertEquals(keyValuePair, model.findByKey("myKey"));
    }

    public void testNewKeyPair() {
		model.newKeyPair("myKey", '=', "myValue");
		assertNotNull(model.findByKey("myKey"));
		assertEquals("myValue", model.findByKey("myKey").getValue());
    }

    public void testAddToComment() {
		addEmptyComment();
		model.addToComment(comment, "myCommentLine");
		assertEquals("myCommentLine", comment.getValue());
    }


}
