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
public class CommentTest extends TestCase {

    private Comment comment;
    
    protected void setUp() throws Exception {
        comment = new Comment();
    }

    protected void tearDown() throws Exception {
        comment = null;
    }
  
    public void testSetComment() {
        comment.addToComment("test my test");
        assertEquals("test my test", comment.toString());
    }
    
    public void testAddToComment() {
        comment.addToComment("test my test");
        assertEquals("test my test", comment.toString());
        
        comment.setComment("my other test");
        comment.addToComment("another comment addition");
        assertEquals("my other testanother comment addition", comment.toString());
    }



}
