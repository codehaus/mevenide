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