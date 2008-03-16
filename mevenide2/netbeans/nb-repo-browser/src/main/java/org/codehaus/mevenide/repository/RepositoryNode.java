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
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.codehaus.mevenide.indexer.api.RepositoryIndexer;
import org.codehaus.mevenide.indexer.api.RepositoryInfo;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences;
import org.codehaus.mevenide.repository.register.RepositoryRegisterUI;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
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
        if (info.isRemoteDownloadable()) {
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
    public void destroy() throws IOException {
        RepositoryPreferences.getInstance().removeRepositoryInfo(info);
        super.destroy();
    }

    @Override
    public boolean canDestroy() {
        return !info.isLocal();
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[]{
            new RefreshIndexAction(),
            new EditAction(),
            DeleteAction.get(DeleteAction.class),
            null,
            PropertiesAction.get(PropertiesAction.class)
        };
    }
    
    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set basicProps = sheet.get(Sheet.PROPERTIES);
        try {
            PropertySupport.Reflection id = new PropertySupport.Reflection<String>(info, String.class, "getId", null); //NOI18N
            id.setName("Id"); //NOI18N
            id.setDisplayName("Id");
            id.setShortDescription(""); //NOI18N
            PropertySupport.Reflection name = new PropertySupport.Reflection<String>(info, String.class, "getName", null); //NOI18N
            name.setName("name"); //NOI18N
            name.setDisplayName("Name");
            name.setShortDescription(""); //NOI18N
            PropertySupport.Reflection type = new PropertySupport.Reflection<String>(info, String.class, "getType", null); //NOI18N
            type.setName("type"); //NOI18N
            type.setDisplayName("Repository Manager Type");
            PropertySupport.Reflection local = new PropertySupport.Reflection<Boolean>(info, Boolean.TYPE, "isLocal", null); //NOI18N
            local.setName("local"); //NOI18N
            local.setDisplayName("Local");
            local.setShortDescription("");
            PropertySupport.Reflection localRepoLocation = new PropertySupport.Reflection<String>(info, String.class, "getRepositoryPath", null); //NOI18N
            localRepoLocation.setName("repositoryPath"); //NOI18N
            localRepoLocation.setDisplayName("Local repository path");
            PropertySupport.Reflection remoteDownloadable = new PropertySupport.Reflection<Boolean>(info, Boolean.TYPE, "isRemoteDownloadable", null); //NOI18N
            remoteDownloadable.setName("remoteDownloadable"); //NOI18N
            remoteDownloadable.setDisplayName("Remote Index Downloadable");
            PropertySupport.Reflection repoURL = new PropertySupport.Reflection<String>(info, String.class, "getRepositoryUrl", null); //NOI18N
            repoURL.setName("repositoryUrl"); //NOI18N
            repoURL.setDisplayName("Remote Repository URL");
            PropertySupport.Reflection indexURL = new PropertySupport.Reflection<String>(info, String.class, "getIndexUpdateUrl", null); //NOI18N
            indexURL.setName("indexUpdateUrl"); //NOI18N
            indexURL.setDisplayName("Remote Index URL");
            basicProps.put(new Node.Property[] {
                id, name, type, local, localRepoLocation, remoteDownloadable, repoURL, indexURL
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            });
        } catch (NoSuchMethodException exc) {
            exc.printStackTrace();
        }
        return sheet;
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
                    ((GroupListChildren)getChildren()).refreshGroups();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            RefreshIndexAction.this.setEnabled(true);
                        }
                    });
                }
            });
        }
    }
    
    private class EditAction extends AbstractAction {
        public EditAction() {
            putValue(NAME, "Edit...");
        }

        public void actionPerformed(ActionEvent e) {
            final RepositoryRegisterUI rrui = new RepositoryRegisterUI();
            rrui.modify(RepositoryNode.this.info);
            DialogDescriptor dd = new DialogDescriptor(rrui, NbBundle.getMessage(RepositoryRegisterUI.class, "LBL_Repo_ADD"));
            dd.setClosingOptions(new Object[]{
                        rrui.getButton(),
                        DialogDescriptor.CANCEL_OPTION
                    });
            dd.setOptions(new Object[]{
                        rrui.getButton(),
                        DialogDescriptor.CANCEL_OPTION
                    });
            Object ret = DialogDisplayer.getDefault().notify(dd);
            if (rrui.getButton() == ret) {
                RepositoryInfo info = rrui.getRepositoryInfo();
                RepositoryPreferences.getInstance().addOrModifyRepositoryInfo(info);
                RepositoryNode.this.info = info;
                setDisplayName(info.getName());
                fireIconChange();
                fireOpenedIconChange();
                ((GroupListChildren)getChildren()).refreshGroups();
            }

        }
    }
}
