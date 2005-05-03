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
package org.mevenide.idea.util.ui;

import com.jgoodies.forms.factories.ComponentFactory;
import com.jgoodies.forms.factories.DefaultComponentFactory;

import javax.swing.*;
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;

/**
 * @author Arik
 */
public class CustomFormsComponentFactory implements ComponentFactory {

    private final ComponentFactory delegate = DefaultComponentFactory.getInstance();

    public JLabel createLabel(String textWithMnemonic) {
        return delegate.createLabel(textWithMnemonic);
    }

    public JComponent createSeparator(String text, int alignment) {
        final JComponent separator = delegate.createSeparator(text, alignment);
        for(final Component c : separator.getComponents())
            if(c instanceof JLabel)
                patchTitleLabel((JLabel) c);

        return separator;
    }

    public JLabel createTitle(String textWithMnemonic) {
        final JLabel title = delegate.createTitle(textWithMnemonic);
        patchTitleLabel(title);
        return title;
    }

    private void patchTitleLabel(final JLabel pTitle) {
        final Font font = pTitle.getFont();
        pTitle.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
        pTitle.setForeground(new Color(0, 0, 128));
    }
}
