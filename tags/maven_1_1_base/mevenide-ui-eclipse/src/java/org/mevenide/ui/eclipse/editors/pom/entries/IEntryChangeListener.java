/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
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

/**
 * Implement to listen for changes to a page entry.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public interface IEntryChangeListener {

    /**
     * The user altered the entry (e.g., keystrokes, selections, etc.).
     * @param entry the altered entry
     */
    void entryDirty(PageEntry entry);

    /**
     * The user committed the changes to the entry.
     * @param entry the saved entry
     */
    void entryChanged(PageEntry entry);
}
