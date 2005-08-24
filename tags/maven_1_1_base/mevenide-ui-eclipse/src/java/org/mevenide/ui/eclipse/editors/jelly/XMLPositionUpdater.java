/*
 * Created on 01.06.2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.mevenide.ui.eclipse.editors.jelly;

import org.eclipse.jface.text.DefaultPositionUpdater;


/**
 * @author jll
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class XMLPositionUpdater extends DefaultPositionUpdater {

    public XMLPositionUpdater(String category) {
        super(category);
    }

    protected void adaptToReplace() {
        super.adaptToReplace();
        int offset = super.fOriginalPosition.offset;
        int length = super.fOriginalPosition.length;
        if (super.fOffset >= offset && super.fOffset <= offset + length) {
            ((XMLNode) super.fPosition).setModified(true);
            if (super.fPosition.length == 0) {
                super.fPosition.delete();
                notDeleted();
            }
        }
    }

    protected void adaptToInsert() {
        super.adaptToInsert();
        if (super.fPosition.length == 0) {
            super.fPosition.delete();
            notDeleted();
        }
    }

    protected void adaptToRemove() {
        super.adaptToRemove();
        if (super.fPosition.length == 0) {
            super.fPosition.delete();
            notDeleted();
        }
    }
}