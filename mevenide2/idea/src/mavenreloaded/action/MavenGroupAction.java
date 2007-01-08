/*
 * Copyright (c) 2006 Bryan Kate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package mavenreloaded.action;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.project.Project;
import mavenreloaded.PluginPomManager;


/**
 * An abstract base class for all actions in the Maven group.
 *
 * @author bkate
 */
public abstract class MavenGroupAction extends AnAction {

    private static final String POM_NAME = "pom.xml";


    /**
     * {@inheritDoc}
     *
     * Turns the action off if it is not a Maven 2 POM file.
     */
    public void update(AnActionEvent e) {

        super.update(e);

        e.getPresentation().setEnabled(isActionEnabled(e));
    }


    /**
     * Determines if the action is being called on a Maven 2 POM file.
     *
     * @param file The file being acted on in this action.
     *
     * @return True if it is a POM file, false otherwise.
     */
    protected boolean isPomFile(VirtualFile file) {

        // only enable the action for pom files
        if (file == null) {
            return false;
        }

        return file.getName().equalsIgnoreCase(POM_NAME);
    }


    /**
     * Determines if this action should be enabled.
     *
     * @param e The action event triggering this call.
     *
     * @return True if the event context supports enabling this action.
     */
    protected boolean isActionEnabled(AnActionEvent e) {

        Project project = (Project)e.getDataContext().getData(DataConstants.PROJECT);
        VirtualFile thisFile = (VirtualFile)e.getDataContext().getData(DataConstants.VIRTUAL_FILE);

        return isPomFile(thisFile) && PluginPomManager.getInstance(project).isPomEnabled(thisFile);
    }

}

