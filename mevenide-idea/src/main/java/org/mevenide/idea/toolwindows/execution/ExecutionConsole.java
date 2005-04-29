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
package org.mevenide.idea.toolwindows.execution;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.filters.ExceptionFilter;
import com.intellij.execution.filters.RegexpFilter;
import com.intellij.execution.filters.TextConsoleBuidlerFactory;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.actions.AbstractAnAction;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.idea.util.ui.UIUtils;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Container;

/**
 * @author Arik
 */
public class ExecutionConsole extends JPanel {
    private static final Res RES = Res.getInstance(ExecutionConsole.class);
    private static final Log LOG = LogFactory.getLog(ExecutionConsole.class);

    private static final String COMPILE_REGEXP =
    RegexpFilter.FILE_PATH_MACROS + ":" + RegexpFilter.LINE_MACROS;

    private final Project project;
    private final ProcessHandler runner;
    private       ConsoleView    console;

    public ExecutionConsole(final Project pProject,
                            final ProcessHandler pRunner) {
        project = pProject;
        runner = pRunner;
        init();
    }

    public ExecutionConsole(final Project pProject,
                            final ProcessHandler pRunner,
                            final boolean pDoubleBuffered) {
        super(pDoubleBuffered);
        project = pProject;
        runner = pRunner;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());

        //
        //create a console view
        //
        final TextConsoleBuidlerFactory factory = TextConsoleBuidlerFactory.getInstance();
        final TextConsoleBuilder builder = factory.createBuilder(project);
        console = builder.getConsole();
        console.attachToProcess(runner);
        console.addMessageFilter(new ExceptionFilter(project));
        console.addMessageFilter(new RegexpFilter(project, COMPILE_REGEXP));

        //
        //tell the process handler to start sending notification for UI updates
        //to the console
        //
        runner.startNotify();

        //
        //add the console view to the panel
        //
        add(console.getComponent(), BorderLayout.CENTER);

        //
        //create the toolbar
        //
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new DefaultExecutionResult.StopAction(runner));
        actionGroup.add(new PauseAction());
        actionGroup.add(new CloseAction());

        //
        //add the toolbar to the panel
        //
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(
                        ExecutionConsole.class.getName(),
                        actionGroup,
                        false);
        add(toolbar.getComponent(), BorderLayout.LINE_START);
    }

    private class CloseAction extends AbstractAnAction {
        public CloseAction() {
            super(RES.get("close.action.text"),
                  RES.get("close.action.desc"),
                  Icons.CANCEL);
        }

        public void actionPerformed(AnActionEvent pEvent) {
            try {
                //
                //stop the process first
                //
                runner.destroyProcess();

                //
                //remove the console
                //
                final Container parent = ExecutionConsole.this.getParent();
                parent.remove(ExecutionConsole.this);

                //
                //if this is the last console, hide execution panel container
                //
                if (parent.getComponentCount() == 0) {
                    final ToolWindow toolWindow = ExecutionToolWindowUI.getToolWindow(project);
                    toolWindow.setAvailable(false, null);
                }
            }
            catch (Exception e) {
                UIUtils.showError(project, e);
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private class PauseAction extends ToggleAction {
        public PauseAction() {
            super(RES.get("pause.action.text"),
                  RES.get("pause.action.desc"),
                  Icons.PAUSE);
        }

        public boolean isSelected(AnActionEvent e) {
            return console.isOutputPaused();
        }

        public void setSelected(AnActionEvent e, boolean state) {
            console.setOutputPaused(state);
        }
    }
}
