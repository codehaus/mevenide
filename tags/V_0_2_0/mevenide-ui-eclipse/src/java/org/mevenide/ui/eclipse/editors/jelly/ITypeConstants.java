/*
 * Created on 13.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
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
