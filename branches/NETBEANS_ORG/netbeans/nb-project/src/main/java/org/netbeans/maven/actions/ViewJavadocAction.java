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
package org.netbeans.maven.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.maven.artifact.Artifact;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import org.openide.filesystems.URLMapper;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G (anuradha@codehaus.org)
 */
public class ViewJavadocAction extends AbstractAction {

    private Artifact artifact;

    public ViewJavadocAction(Artifact artifact) {
        this.artifact = artifact;
        putValue(Action.NAME, NbBundle.getMessage(ViewJavadocAction.class, "LBL_View_Javadoc"));

    }

    public void actionPerformed(ActionEvent event) {
        File javadocFile = getJavadocFile();
        FileObject fo = FileUtil.toFileObject(javadocFile);
        if (fo != null) {
            FileObject jarfo = FileUtil.getArchiveRoot(fo);
            if (jarfo != null) {
                FileObject index = jarfo.getFileObject("apidocs/index.html"); //NOI18N
                if (index == null) {
                    index = jarfo.getFileObject("index.html"); //NOI18N
                }
                if (index == null) {
                    index = jarfo;
                }
                URL link = URLMapper.findURL(index, URLMapper.EXTERNAL);
                HtmlBrowser.URLDisplayer.getDefault().showURL(link);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return hasJavadocInRepository();
    }

    public boolean hasJavadocInRepository() {
        return (!Artifact.SCOPE_SYSTEM.equals(artifact.getScope())) && getJavadocFile().exists();
    }

    public File getJavadocFile() {
        return getJavadocFile(artifact.getFile());
    }

    private static File getJavadocFile(File artifact) {
        String version = artifact.getParentFile().getName();
        String artifactId = artifact.getParentFile().getParentFile().getName();
        return new File(artifact.getParentFile(), artifactId + "-" + version + "-javadoc.jar"); //NOI18N
    }
}
