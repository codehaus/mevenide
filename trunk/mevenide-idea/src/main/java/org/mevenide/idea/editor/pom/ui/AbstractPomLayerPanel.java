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
package org.mevenide.idea.editor.pom.ui;

import javax.swing.*;
import org.apache.commons.logging.Log;

/**
 * A base class for all POM editing panels.
 *
 * <p>Automatically keeps track of the current focusable component. This is a bug fix for
 * IDEA - when a FileEditor has a panel with multiple fields, switching to another editor
 * and returning returns the focus back to the first component. This class fixes
 * that.</p>
 *
 * <p>Also, this class provides a {@link Log} instance, the correct resource bundle and
 * the project and document this panel edits.</p>
 *
 * @author Arik
 */
public abstract class AbstractPomLayerPanel extends JPanel {
}
