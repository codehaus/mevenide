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
package org.mevenide.idea.editor.pom.ui.layer;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import org.jdom.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;

import javax.swing.*;

/**
 * @author Arik
 */
public abstract class AbstractPomLayerPanel extends JPanel {
    protected final Log LOG;
    protected final Res RES;
    protected final Project project;
    protected final Document editorDocument;

    protected AbstractPomLayerPanel(final Project pProject,
                                    final Document pPomDocument) {

        LOG = LogFactory.getLog(this.getClass());
        RES = Res.getInstance(this.getClass());

        project = pProject;
        editorDocument = pPomDocument;
    }
}
