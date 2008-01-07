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
package org.codehaus.mevenide.netbeans.actions.usages;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.maven.artifact.Artifact;
import org.codehaus.mevenide.netbeans.actions.usages.ui.UsagesUI;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G (anuradha@codehaus.org)
 */
public class FindArtifactUsages extends AbstractAction {

    private Artifact artifact;

    public FindArtifactUsages(Artifact artifact) {
        this.artifact = artifact;
        putValue(Action.NAME, NbBundle.getMessage(FindArtifactUsages.class, "LBL_FindartifactUsages"));

    }

    public void actionPerformed(ActionEvent event) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<b>");
        buffer.append(artifact.getArtifactId());
        buffer.append("</b>");
        buffer.append(":");
        buffer.append("<b>");
        buffer.append(artifact.getVersion().toString());
        buffer.append("</b>");

        UsagesUI uI = new UsagesUI(buffer.toString(), artifact);

        DialogDescriptor dd = new DialogDescriptor(uI, NbBundle.getMessage(
                FindArtifactUsages.class, "LBL_FindartifactUsages"));
        dd.setClosingOptions(new Object[]{
            DialogDescriptor.CLOSED_OPTION
        });
        dd.setOptions(new Object[]{
            DialogDescriptor.CLOSED_OPTION
        });
        DialogDisplayer.getDefault().notify(dd);
    }
}
