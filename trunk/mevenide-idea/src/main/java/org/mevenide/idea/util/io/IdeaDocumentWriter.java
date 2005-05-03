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
package org.mevenide.idea.util.io;

import com.intellij.openapi.editor.Document;

import java.io.Writer;
import java.io.IOException;

/**
 * @author Arik
 */
public class IdeaDocumentWriter extends Writer {

    private final Document document;

    public IdeaDocumentWriter(final Document pDocument) {
        document = pDocument;
    }

    public IdeaDocumentWriter(final Object lock, final Document pDocument) {
        super(lock);
        document = pDocument;
    }

    public void close() throws IOException {
    }

    public void flush() throws IOException {
    }

    public void write(char cbuf[], int off, int len) throws IOException {
        final String text = new String(cbuf, off, len).replace("\r", "");
        document.insertString(document.getTextLength(), text);
    }


}
