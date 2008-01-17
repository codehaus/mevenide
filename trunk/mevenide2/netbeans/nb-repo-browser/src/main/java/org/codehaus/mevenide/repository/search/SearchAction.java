package org.codehaus.mevenide.repository.search;

import java.util.List;

import org.codehaus.mevenide.repository.GroupIdNode;
import org.codehaus.mevenide.repository.M2RepositoryBrowserTopComponent;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class SearchAction extends CallableSystemAction {
    
    public void performAction() {
        // TODO implement action body
        SearchPanel panel = new SearchPanel();
        DialogDescriptor dd = new DialogDescriptor(panel, "Find in Maven Repository");
        performAction(dd, panel);
    }
    
    public void performAction(DialogDescriptor dd, SearchPanel panel) {
//        Object ret = DialogDisplayer.getDefault().notify(dd);
//        if (ret == DialogDescriptor.OK_OPTION) {
//
//                M2RepositoryBrowserTopComponent m2comp = M2RepositoryBrowserTopComponent.findInstance();
//                m2comp.setSearchDialogCache(dd, panel);
//            final List<GroupInfo> groupInfo = CustomQueries.getGroupInfo(panel.getFindQuery());
//               Children repoChildren = new Children.Keys<GroupInfo>() {
//
//            @Override
//            protected Node[] createNodes(GroupInfo ug) {
//                return new Node[]{new GroupIdNode(ug)};
//            }
//
//            @Override
//            protected void addNotify() {
//                super.addNotify();
//                setKeys(groupInfo);
//            }
//        };
//                m2comp.showSearchResults(new AbstractNode(repoChildren));
//                m2comp.open();
//                m2comp.requestActive();
//
//        }
        
    }
    
    public String getName() {
        return NbBundle.getMessage(SearchAction.class, "CTL_SearchAction");
    }
    
    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    @Override
    protected boolean asynchronous() {
        return false;
    }
    
}
