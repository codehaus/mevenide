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


import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.vfs.VirtualFile;


/**
 * A group that represents all of the actions that are related to Maven 2 processing.
 *
 * @author bkate
 */
public class MavenPopupGroup extends DefaultActionGroup {

    private static final String POM_NAME = "pom.xml";


    /**
     * Default constructor.
     */
    public MavenPopupGroup() {
        super();
    }


    /**
     * More involved constructor.
     *
     * @param shortName The short name of the group.
     * @param popup True if the group is a popup group.
     */
    public MavenPopupGroup(String shortName, boolean popup) {
        super(shortName, popup);
    }


    /**
     * {@inheritDoc}
     *
     * Turns the group off if it is not a Maven 2 POM file.
     */
    public void update(AnActionEvent e) {

        super.update(e);

        boolean enabled = isPomFile((VirtualFile)e.getDataContext().getData(DataConstants.VIRTUAL_FILE));

        e.getPresentation().setEnabled(enabled);
        e.getPresentation().setVisible(enabled);
    }


    /**
     * Determines if the group is being called on a Maven 2 POM file.
     *
     * @param file The file being acted on in this group.
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

}

