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



package org.codehaus.mevenide.idea.gui.form;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import org.codehaus.mevenide.idea.build.LogListener;
import org.codehaus.mevenide.idea.common.util.ErrorHandler;

import java.awt.*;

import java.util.Hashtable;

import javax.swing.*;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenBuildProjectOutputForm extends AbstractForm implements LogListener {
    private final JPanel panel = new JPanel();
    private ConsoleView consoleView;
    private static Hashtable<Integer, ConsoleViewContentType> outputTypeMap = new Hashtable<Integer,
                                                                                  ConsoleViewContentType>();

    public MavenBuildProjectOutputForm(ConsoleView view) {
        this.consoleView = view;

        if (outputTypeMap.isEmpty()) {
            outputTypeMap.put(LogListener.OUTPUT_TYPE_ERROR, ConsoleViewContentType.ERROR_OUTPUT);
            outputTypeMap.put(LogListener.OUTPUT_TYPE_NORMAL, ConsoleViewContentType.NORMAL_OUTPUT);
            outputTypeMap.put(LogListener.OUTPUT_TYPE_SYSTEM, ConsoleViewContentType.SYSTEM_OUTPUT);
            outputTypeMap.put(LogListener.OUTPUT_TYPE_USER, ConsoleViewContentType.USER_INPUT);
        }

        panel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(view.getComponent(),
                  new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                                      null, null, null));
    }

    public void printMessage(String message, String stackTrace, int outputType) {
        if (consoleView != null) {
            consoleView.print(message, outputTypeMap.get(outputType));

            if (stackTrace != null) {
                consoleView.print(stackTrace, ConsoleViewContentType.ERROR_OUTPUT);
            }
        }
    }

    public void raiseFatalErrorMessage(String message, String throwable) {
        ErrorHandler.showErrorMessage(message);
    }

    public JComponent getRootComponent() {
        return panel;
    }
}
