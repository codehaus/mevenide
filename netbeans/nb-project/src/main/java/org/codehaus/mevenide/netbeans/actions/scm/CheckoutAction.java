/*
 *  Copyright 2005-2008 Mevenide Team.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.codehaus.mevenide.netbeans.actions.scm;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.actions.ActionsUtil;
import org.codehaus.mevenide.netbeans.actions.scm.ui.CheckoutUI;
import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
import org.codehaus.mevenide.netbeans.api.execute.RunUtils;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author Anuradha G (anuradha@codehaus.org)
 */
public class CheckoutAction extends AbstractAction {

    private Artifact artifact;

    public CheckoutAction(Artifact artifact) {
        putValue(NAME, NbBundle.getMessage(CheckoutAction.class, "LBL_Checkout"));
        this.artifact = artifact;


    }

    public void actionPerformed(ActionEvent e) {
        MavenProject readMavenProject = ActionsUtil.readMavenProject(artifact);

        CheckoutUI checkoutUI = new CheckoutUI(artifact, readMavenProject.getScm());
        DialogDescriptor dd = new DialogDescriptor(checkoutUI,  NbBundle.getMessage(CheckoutAction.class, "LBL_Checkout"));
        dd.setClosingOptions(new Object[]{
            checkoutUI.getCheckoutButton(),
            DialogDescriptor.CANCEL_OPTION
        });
        dd.setOptions(new Object[]{
            checkoutUI.getCheckoutButton(),
            DialogDescriptor.CANCEL_OPTION
        });
        Object ret = DialogDisplayer.getDefault().notify(dd);
        if (checkoutUI.getCheckoutButton() == ret) {
            final RunConfig rc = checkoutUI.getRunConfig();
            ExecutorTask task = RunUtils.executeMaven(rc);
            task.addTaskListener(new TaskListener() {
                public void taskFinished(Task task) {
                    FileObject fo = FileUtil.toFileObject(rc.getExecutionDirectory());
                    if (fo != null) {
                        try {
                            Project prj = ProjectManager.getDefault().findProject(fo);
                            if (prj != null) {
                                OpenProjects.getDefault().open(new Project[] {prj}, false);
                            }
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (IllegalArgumentException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            });
        }
    }

  

    @Override
    public boolean isEnabled() {
         MavenProject readMavenProject = ActionsUtil.readMavenProject(artifact);
        return readMavenProject!=null && readMavenProject.getScm() != null;
    }
}
