/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.ui.eclipse.editors.entries;

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
		if (text != null ) {
			textbox.setText(text);
		}
		else {
			textbox.setText("");
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
		if (textbox != null) {
			return textbox.setFocus();
		}
		return false;
    }

    public void setEnabled(boolean enable) {
		if (textbox != null) {
			textbox.setEnabled(enable);
		}
    }
    
    public Composite getParent() {
    	return textbox.getParent();
    }
}
