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

package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.log4j.Logger;
import org.codehaus.mevenide.idea.form.MavenCoreConfigurationForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Action class for browsing and setting the local maven repository to use.
 * A FileChooser is invoked to let the user pick the desired directory.
 *
 * @author Ralf Quebbemann (ralfq@codehaus.org)
 */
public class CoreConfigurationActionListener implements ActionListener {
    private static final Logger LOG = Logger.getLogger(CoreConfigurationActionListener.class);
    private Project project;
    private MavenCoreConfigurationForm form;

    public CoreConfigurationActionListener(Project project,
                                           MavenCoreConfigurationForm form) {
        this.project = project;
        this.form = form;
    }


    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals(MavenCoreConfigurationForm.ACTION_COMMAND_SET_LOCAL_REPOSITORY)) {

            FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
            VirtualFile[] localRepository =
                    FileChooser.chooseFiles(project, descriptor);

            if (localRepository != null) {
                LOG.debug("Chosen repository is: " + localRepository[0].getPath());
                form.setTextFieldLocalRepositoryData(localRepository[0].getPath());
            }
        }
    }
}
