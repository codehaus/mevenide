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

package org.mevenide.netbeans.project.customizer.ui;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import org.mevenide.properties.IPropertyLocator;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class OriginChangeCellEditor extends DefaultCellEditor {
    
    /** Creates a new instance of OriginChangeCellEditor */
    public OriginChangeCellEditor(final OriginChange change) {
        super(new JTextField());
        super.editorComponent = change.getComponent();
        super.delegate = new EditorDelegate() {
            public void setValue(Object value) { 
            	int integer = IPropertyLocator.LOCATION_NOT_DEFINED;
		if (value instanceof Integer) {
		    integer = ((Integer)value).intValue();
		}
		change.setSelectedLocationID(integer);
            }

	    public Object getCellEditorValue() {
		return new Integer(change.getSelectedLocationID());
	    }            
        };
        super.clickCountToStart = 1;
        change.setChangeObserver(new OriginChange.ChangeObserver() {
             public void actionSelected(String action) {
                 OriginChangeCellEditor.this.stopCellEditing();
             }
        });
    }
    
}
