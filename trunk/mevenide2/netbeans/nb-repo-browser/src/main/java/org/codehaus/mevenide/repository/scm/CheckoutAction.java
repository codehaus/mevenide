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
import java.io.File;
import javax.swing.AbstractAction;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.extension.ExtensionScanningException;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.mevenide.netbeans.api.execute.RunUtils;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.repository.RepositoryUtils;
import org.codehaus.mevenide.repository.scm.ui.CheckoutUI;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class CheckoutAction extends AbstractAction {

    private StandardArtifactIndexRecord record;

    public CheckoutAction(StandardArtifactIndexRecord record) {
        putValue(NAME, NbBundle.getMessage(CheckoutAction.class, "LBL_Checkout"));
        this.record = record;


    }

    public void actionPerformed(ActionEvent e) {


        CheckoutUI checkoutUI = new CheckoutUI(record, getScm(record));
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

   static  Scm getScm(StandardArtifactIndexRecord record) {
        Scm scm = null;
        Artifact artifact = RepositoryUtils.createArtifact(record,
                EmbedderFactory.getProjectEmbedder().getLocalRepository());

        String absolutePath = artifact.getFile().getAbsolutePath();
        String extension = artifact.getArtifactHandler().getExtension();

        String pomPath = absolutePath.substring(0, absolutePath.length() - extension.length());
        pomPath += "pom";//NOI18N
        File file = new File(pomPath);
        if (file.exists()) {
            try {
               
                MavenProject readProject = EmbedderFactory.getProjectEmbedder().
                        readProject(file);
               scm= readProject.getScm();
            } catch (ProjectBuildingException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ExtensionScanningException ex) {
                Exceptions.printStackTrace(ex);
            }

        }
        return scm;
    }

    @Override
    public boolean isEnabled() {
        return getScm(record) != null;
    }
}
