/*
 *  Copyright 2007 Anuradha.
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
package org.codehaus.mevenide.repository.dependency;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.AbstractAction;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.embedder.writer.WriterUtils;
import org.codehaus.mevenide.repository.VersionNode;
import org.codehaus.mevenide.repository.dependency.ui.AddDependencyUI;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G (theanuradha-at-netbeans.org)
 */
public class AddAsDependencyAction extends AbstractAction {

    private StandardArtifactIndexRecord record;

    public AddAsDependencyAction(StandardArtifactIndexRecord record) {
        putValue(NAME, NbBundle.getMessage(AddAsDependencyAction.class, "LBL_Add_As_Dependency"));
        this.record = record;
    }

    public void actionPerformed(ActionEvent e) {
         StringBuffer buffer = new StringBuffer();
         buffer.append("<b>");
         buffer.append(record.getArtifactId());
         buffer.append("</b>");
         buffer.append(":");
         buffer.append("<b>");
         buffer.append(record.getVersion().toString());
         buffer.append("</b>");

        AddDependencyUI adui = new AddDependencyUI(buffer.toString());
        DialogDescriptor dd = new DialogDescriptor(adui, NbBundle.getMessage(AddAsDependencyAction.class, "TIT_Add_Dependency"));
        dd.setClosingOptions(new Object[]{
            adui.getAddButton(),
            DialogDescriptor.CANCEL_OPTION
        });
        dd.setOptions(new Object[]{
            adui.getAddButton(),
            DialogDescriptor.CANCEL_OPTION
        });
        Object ret = DialogDisplayer.getDefault().notify(dd);
        if (adui.getAddButton() == ret) {
            List<NbMavenProject> nmps = adui.getSelectedMavenProjects();
            for (NbMavenProject project : nmps) {
                addDependency(project, record.getGroupId(), record.getArtifactId(),
                        record.getVersion(), record.getType(), null, null);
            }

        }

    }

    /*
     *Copyed from org.codehaus.mevenide.netbeans.nodes.DependenciesNode
     * 
     *  this method should  provided as API. (mkleint)?
     */
    public static void addDependency(NbMavenProject project,
            String group,
            String artifact,
            String version,
            String type,
            String scope,
            String classifier) {
        FileObject fo = project.getProjectDirectory().getFileObject("pom.xml"); //NOI18N
        Model model = WriterUtils.loadModel(fo);
        if (model != null) {
            Dependency dep = PluginPropertyUtils.checkModelDependency(model, group, artifact, true);
            dep.setVersion(version);
            if (scope != null) {
                dep.setScope(scope);
            }
            if (type != null) {
                dep.setType(type);
            }
            if (classifier != null) {
                dep.setClassifier(classifier);
            }
            try {
                WriterUtils.writePomModel(fo, model);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
