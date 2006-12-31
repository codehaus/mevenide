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
package org.mevenide.ui.eclipse.editors.pom;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;

/**
 * Defines strategy followed when user double-clicks inside the XML document.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PomXmlDoubleClickStrategy implements ITextDoubleClickStrategy {
	
	private ITextViewer textViewer;

	public void doubleClicked(ITextViewer part) {
		int pos = part.getSelectedRange().x;
		if (pos >= 0) {
			textViewer = part;
			if (!selectComment(pos)) {
				selectWord(pos);
			}
		}
	}
	
	private boolean selectComment(int caretPos) {
		IDocument doc = textViewer.getDocument();
		int startPos, endPos;

		try {
			int pos = caretPos;
			char c = ' ';

			while (pos >= 0) {
				c = doc.getChar(pos);
				if (c == '\\') {
					pos -= 2;
					continue;
				}
				if (c == Character.LINE_SEPARATOR || c == '\"') {
					break;
				}
				--pos;
			}

			if (c != '\"') {
				return false;
			}

			startPos = pos;

			pos = caretPos;
			int length = doc.getLength();
			c = ' ';

			while (pos < length) {
				c = doc.getChar(pos);
				if (c == Character.LINE_SEPARATOR || c == '\"')
					break;
				++pos;
			}
			
			if (c != '\"') {
				return false;
			}

			endPos = pos;

			int offset = startPos + 1;
			int len = endPos - offset;
			textViewer.setSelectedRange(offset, len);
			return true;
		} catch (BadLocationException ignored) {
		}

		return false;
	}
	
	private boolean selectWord(int caretPos) {

		IDocument doc = textViewer.getDocument();
		int startPos, endPos;

		try {
			int pos = caretPos;
			char c;

			while (pos >= 0) {
				c = doc.getChar(pos);
				if (!Character.isJavaIdentifierPart(c))
					break;
				--pos;
			}

			startPos = pos;

			pos = caretPos;
			int length = doc.getLength();

			while (pos < length) {
				c = doc.getChar(pos);
				if (!Character.isJavaIdentifierPart(c))
					break;
				++pos;
			}

			endPos = pos;
			selectRange(startPos, endPos);
			return true;

		} catch (BadLocationException ignored) {
		}

		return false;
	}

	private void selectRange(int startPos, int stopPos) {
		int offset = startPos + 1;
		int length = stopPos - offset;
		textViewer.setSelectedRange(offset, length);
	}
}