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
package org.codehaus.mevenide.netbeans.actions;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G (anuradha@codehaus.org)
 */
public class ViewProjectHomeAction extends AbstractAction {

    private Artifact artifact;

    public ViewProjectHomeAction(Artifact artifact) {
        this.artifact = artifact;
        putValue(Action.NAME, NbBundle.getMessage(ViewProjectHomeAction.class, "LBL_View_ProjectHome"));
        MavenProject mp = ActionsUtil.readMavenProject(artifact);
        //enable only if url persent
        setEnabled(mp != null && mp.getUrl() != null);
    }

    public void actionPerformed(ActionEvent event) {
        MavenProject mp = ActionsUtil.readMavenProject(artifact);
        try {

            URLDisplayer.getDefault().showURL(new URL(mp.getUrl()));
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
