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
package org.mevenide.idea.execute;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.ExceptionFilter;
import com.intellij.execution.filters.RegexpFilter;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.MavenHomeNotDefinedException;
import org.mevenide.idea.PomNotDefinedException;
import org.mevenide.idea.util.ui.UIUtils;

/**
 * Executes Maven processes. This is a non-instantiable class, used only as a
 * services provider for actions, tool windows, etc.
 *
 * @author Arik
 */
public final class MavenRunner {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(MavenRunner.class);

    /**
     * Private ctor preventing instantiation.
     */
    private MavenRunner() {
    }

    /**
     * Executes a Maven process for the given module and goals.
     *
     * <p>The reason you need to provide a module, is to locate the appropriate
     * Maven POM and working directory.</p>
     *
     * @param pModule the module to execute the goals for - providing the working dir and POM.
     * @param pGoals the goals to execute - these must be fully qualified goal names (no '(default)' suffixes)
     * @param pContext the context with which to execute
     */
    public static void execute(final Module pModule,
                               final String[] pGoals,
                               final DataContext pContext) {
        //
        //always save all modified files before invoking maven
        //
        ApplicationManager.getApplication().saveAll();

        try {
            //
            //create the process descriptor
            //
            MavenJavaParameters p = new MavenJavaParameters(pModule, pGoals);

            //
            //provide filters which allow linking compilation errors to source files
            //
            Filter[] filters = new Filter[]{
                new ExceptionFilter(pModule.getProject()),
                new RegexpFilter(pModule.getProject(), MavenJavaParameters.COMPILE_REGEXP)
            };

            //
            //executes the process, creating a console window for it
            //
            ExecutionManager.getInstance(pModule.getProject()).execute(
                    p, StringUtils.join(pGoals, ' '), pContext, filters);
        }
        catch (MavenHomeNotDefinedException e) {
            UIUtils.showError(pModule, e);
            LOG.trace(e.getMessage(), e);
        }
        catch (PomNotDefinedException e) {
            UIUtils.showError(pModule, e);
            LOG.trace(e.getMessage(), e);
        }
        catch (CantRunException e) {
            UIUtils.showError(pModule, e);
            LOG.trace(e.getMessage(), e);
        }
        catch (ExecutionException e) {
            UIUtils.showError(pModule, e);
            LOG.trace(e.getMessage(), e);
        }
    }
}
