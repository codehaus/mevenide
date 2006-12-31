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
package org.mevenide.idea.editor.pom;

import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import java.awt.*;

/**
 * @author Arik
 */
public class PomFileEditorState implements FileEditorState {
    private int selectedTabIndex = 0;
    private transient Component currentField = null;

    public PomFileEditorState() {
    }

    public PomFileEditorState(final int pSelectedTabIndex,
                              final Component pCurrentField) {
        selectedTabIndex = pSelectedTabIndex;
        currentField = pCurrentField;
    }

    public int getSelectedTabIndex() {
        return selectedTabIndex;
    }

    public void setSelectedTabIndex(final int pSelectedTabIndex) {
        selectedTabIndex = pSelectedTabIndex;
    }

    public Component getCurrentField() {
        return currentField;
    }

    public void setCurrentField(final Component pCurrentField) {
        currentField = pCurrentField;
    }

    public boolean canBeMergedWith(final FileEditorState otherState,
                                   final FileEditorStateLevel level) {
        return false;
    }
}
