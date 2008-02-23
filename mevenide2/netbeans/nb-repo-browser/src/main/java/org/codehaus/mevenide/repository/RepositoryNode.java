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
package org.codehaus.mevenide.repository;

import java.awt.Image;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.codehaus.mevenide.indexer.api.RepositoryIndexer;
import org.codehaus.mevenide.indexer.api.RepositoryInfo;
import org.codehaus.mevenide.indexer.api.RepositoryUtil;
import org.openide.nodes.AbstractNode;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class RepositoryNode extends AbstractNode {

    private RepositoryInfo info;

    public RepositoryNode(RepositoryInfo info) {
        super(new GroupListChildren(info));
        this.info = info;
        setName(info.getId());
        setDisplayName(info.getName());

    }

    @Override
    public Image getIcon(int arg0) {
        if (info.isRemote()) {
            return Utilities.loadImage("org/codehaus/mevenide/repository/remoterepo.png", true); //NOI18N
        }
        return Utilities.loadImage("org/codehaus/mevenide/repository/localrepo.png", true); //NOI18N
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }

    @Override
    public String getShortDescription() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("<html>");//NOI18N

        buffer.append(NbBundle.getMessage(RepositoryNode.class,
                "LBL_REPO_ID", info.getId()));//NOI18N

        buffer.append(NbBundle.getMessage(RepositoryNode.class,
                "LBL_REPO_Name", info.getName()));//NOI18N

        //show repo url if available
        if (info.getRepositoryUrl() != null) {
            buffer.append(NbBundle.getMessage(RepositoryNode.class,
                    "LBL_REPO_Url", info.getRepositoryUrl()));//NOI18N
        }
        //show index url if available
        if (info.getIndexUpdateUrl() != null) {
            buffer.append(NbBundle.getMessage(RepositoryNode.class,
                    "LBL_REPO_Index_Url", info.getIndexUpdateUrl()));//NOI18N
        }
        buffer.append("</html>");//NOI18N

        return buffer.toString();
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[]{new RefreshIndexAction()};
    }

    public class RefreshIndexAction extends AbstractAction {

        public RefreshIndexAction() {
            putValue(NAME, NbBundle.getMessage(RepositoryNode.class,
                    "LBL_REPO_Update_Index"));//NOI18N
        }

        public void actionPerformed(ActionEvent e) {
            setEnabled(false);
            RequestProcessor.getDefault().post(new Runnable() {

                public void run() {
                    RepositoryIndexer.indexRepo(info);
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            RefreshIndexAction.this.setEnabled(true);
                        }
                    });
                }
            });
        }
    }
}
