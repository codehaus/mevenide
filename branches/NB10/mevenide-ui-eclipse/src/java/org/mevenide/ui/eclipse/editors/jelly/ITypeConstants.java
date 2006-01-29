/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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

package org.mevenide.ui.eclipse.editors.jelly;

import org.eclipse.jface.text.IDocument;

/**
 * @author jll
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface ITypeConstants {
    
    public final String TAG = "TAG";
    public final String TEXT = "TEXT";
    public final String PI = "PI";
    public final String DECL = "DECL";
	public final String START_DECL = "STARTDECL";
	public final String END_DECL = "ENDDECL";
    public final String COMMENT = "COMMENT";
    public final String ENDTAG = "ENDTAG";
    public final String ATTR = "ATTR";
    public final String EMPTYTAG = "EMPTYTAG";

    public final String[] TYPES = {
        IDocument.DEFAULT_CONTENT_TYPE,
        TAG, TEXT, PI, START_DECL, END_DECL, DECL, COMMENT, ENDTAG, EMPTYTAG
    };
    

}
