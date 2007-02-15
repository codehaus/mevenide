/* ==========================================================================
 * Copyright 2006 Mevenide Team
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



package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;

import org.apache.log4j.Logger;

import org.codehaus.mevenide.idea.model.MavenProjectDocument;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class PomDocumentListener extends DocumentAdapter {
    private static final Logger LOG = Logger.getLogger(PomDocumentListener.class);
    private MavenProjectDocument mavenProjectDocument;

    public PomDocumentListener(MavenProjectDocument mavenProjectDocument) {
        super();
        this.mavenProjectDocument = mavenProjectDocument;
    }

    public void documentChanged(DocumentEvent e) {

//      LOG.info("Document changed: " + mavenProjectDocument.getPomFile().getPath());
    }
}
