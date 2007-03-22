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



package org.codehaus.mevenide.idea.model;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.codehaus.mevenide.idea.xml.ProjectDocument;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public interface MavenProjectDocument extends Comparable {

    public boolean isValid ();

    public PsiFile getPsiFile();

    public VirtualFile getPomFile();

    public ProjectDocument.Project getProject();

    public Iterable<? extends MavenPluginDocument> getPlugins();

    public void addPlugin ( MavenPluginDocument plugin );

    public void removePlugin ( MavenPluginDocument plugin );

    void reparse();
}
