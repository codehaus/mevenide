/*
 *  Copyright 2008 Anuradha.
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
package org.codehaus.mevenide.repository.scm;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.api.execute.RunUtils;
import org.codehaus.mevenide.repository.RepositoryUtils;
import org.codehaus.mevenide.repository.scm.ui.CheckoutUI;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G (anuradha@codehaus.org)
 */
public class CheckoutAction extends AbstractAction {

    private StandardArtifactIndexRecord record;

    public CheckoutAction(StandardArtifactIndexRecord record) {
        putValue(NAME, NbBundle.getMessage(CheckoutAction.class, "LBL_Checkout"));
        this.record = record;


    }

    public void actionPerformed(ActionEvent e) {
        MavenProject readMavenProject = RepositoryUtils.readMavenProject(record);

        CheckoutUI checkoutUI = new CheckoutUI(record, readMavenProject.getScm());
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
            RunUtils.executeMaven(checkoutUI.getRunConfig());

        }
    }

  

    @Override
    public boolean isEnabled() {
         MavenProject readMavenProject = RepositoryUtils.readMavenProject(record);
        return readMavenProject!=null && readMavenProject.getScm() != null;
    }
}
