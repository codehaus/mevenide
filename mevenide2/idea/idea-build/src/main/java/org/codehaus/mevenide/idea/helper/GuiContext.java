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



package org.codehaus.mevenide.idea.helper;

import com.intellij.execution.ui.ConsoleView;

import javax.swing.*;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class GuiContext {
    private ConsoleView outputConsoleView;
    private JTextField mavenQuickCommandLine;
    private IForm mavenToolWindowForm;
    private IForm mavenOutputWindowForm;
    private IForm projectConfigurationForm;
    private IForm applicationConfigurationForm;

    public GuiContext() {}

    public IForm getApplicationConfigurationForm() {
        return applicationConfigurationForm;
    }

    public void setApplicationConfigurationForm(IForm applicationConfigurationForm) {
        this.applicationConfigurationForm = applicationConfigurationForm;
    }

    public IForm getProjectConfigurationForm() {
        return projectConfigurationForm;
    }

    public void setProjectConfigurationForm(IForm projectConfigurationForm) {
        this.projectConfigurationForm = projectConfigurationForm;
    }

    public ConsoleView getOutputConsoleView() {
        return outputConsoleView;
    }

    public void setOutputConsoleView(ConsoleView outputConsoleView) {
        this.outputConsoleView = outputConsoleView;
    }

    public JTextField getMavenQuickCommandLine() {
        return mavenQuickCommandLine;
    }

    public void setMavenQuickCommandLine(JTextField mavenQuickCommandLine) {
        this.mavenQuickCommandLine = mavenQuickCommandLine;
    }

    public IForm getMavenToolWindowForm() {
        return mavenToolWindowForm;
    }

    public void setMavenToolWindowForm(IForm mavenToolWindowForm) {
        this.mavenToolWindowForm = mavenToolWindowForm;
    }

    public IForm getMavenOutputWindowForm() {
        return mavenOutputWindowForm;
    }

    public void setMavenOutputWindowForm(IForm mavenOutputWindowForm) {
        this.mavenOutputWindowForm = mavenOutputWindowForm;
    }
}
