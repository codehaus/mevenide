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
package org.mevenide.ui.eclipse.editors.pom.entries;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Wrapper class for Text widgets in the POM Editor framework.  Handles
 * change event notification, etc.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class TextEntry extends PageEntry {
	
	protected Text textbox;
	private String value;
	
	public TextEntry(Text text) {
		textbox = text;
		value = textbox.getText();
		initializeEntryListenerRouters();
	}
	
	private void initializeEntryListenerRouters() {
		// modification of text box constitutes a dirty entry event
		textbox.addModifyListener(
			new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setDirty(true);
					fireEntryDirtyEvent();
				}
			}
		);
		
		// change of focus, if the text box was previously changed,
		// constitutes a changed entry event
		textbox.addFocusListener(
			new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					if (isDirty()) {
						value = textbox.getText();
						fireEntryChangeEvent();
					}
				}
			}
		);
	}

	public String getText() {
		return textbox.getText();
	}

	public void setText(String text) {
		value = text;
		if ( !textbox.isDisposed() ) {
			if (text != null ) {
				textbox.setText(text);
			}
			else {
				textbox.setText(""); //$NON-NLS-1$
			}
		}
	}

	public void setText(String text, boolean disableNotification) {
		this.disableNotification = disableNotification;
		setText(text);
		this.disableNotification = false;
	}

    public Object getValue() {
        return value;
    }

    public Object getAdaptor(Class clazz) {
    	if (clazz == Text.class) {
    		return textbox;
    	}
        return null;
    }

    public void setLayoutData(Object data) {
    	if (textbox != null) textbox.setLayoutData(data);
    }
    
    public boolean setFocus() {
		if (textbox != null && !textbox.isDisposed()) {
			return textbox.setFocus();
		}
		return false;
    }

    public void setEnabled(boolean enable) {
		if (textbox != null && !textbox.isDisposed()) {
			textbox.setEnabled(enable);
		}
    }
    
    public Composite getParent() {
    	return textbox.getParent();
    }
}
