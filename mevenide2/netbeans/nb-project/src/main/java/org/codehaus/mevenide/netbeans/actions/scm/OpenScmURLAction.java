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
package org.codehaus.mevenide.netbeans.actions.scm;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.AbstractAction;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;

import org.codehaus.mevenide.netbeans.actions.ActionsUtil;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G (anuradha@codehaus.org)
 */
public class OpenScmURLAction extends AbstractAction {

    private Artifact artifact;

    public OpenScmURLAction(Artifact artifact) {
        putValue(NAME, NbBundle.getMessage(OpenScmURLAction.class, "LBL_OpenURL"));
        this.artifact = artifact;


    }

    public void actionPerformed(ActionEvent e) {
        Scm scm = ActionsUtil.readMavenProject(artifact).getScm();
        try {

            URLDisplayer.getDefault().showURL(new URL(scm.getUrl()));
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public boolean isEnabled() {
        MavenProject readMavenProject = ActionsUtil.readMavenProject(artifact);
        return readMavenProject != null && readMavenProject.getScm() != null && readMavenProject.getScm().getUrl() != null;
    }
}
