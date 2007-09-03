package org.codehaus.mevenide.repository.search;

import java.util.List;
import org.apache.lucene.queryParser.ParseException;
import org.apache.maven.archiva.indexer.RepositoryIndexSearchException;
import org.codehaus.mevenide.indexer.LocalRepositoryIndexer;
import org.codehaus.mevenide.repository.M2RepositoryBrowserTopComponent;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.nodes.AbstractNode;
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
        Object ret = DialogDisplayer.getDefault().notify(dd);
        if (ret == DialogDescriptor.OK_OPTION) {
            try {
                M2RepositoryBrowserTopComponent m2comp = M2RepositoryBrowserTopComponent.findInstance();
                m2comp.setSearchDialogCache(dd, panel);
                List lst = LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), panel.createLuceneQuery());
                m2comp.showSearchResults(new AbstractNode(new SearchResultChildren(lst)));
                m2comp.open();
                m2comp.requestActive();
            } catch (RepositoryIndexSearchException ex) {
                ex.printStackTrace();
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        
    }
    
    public String getName() {
        return NbBundle.getMessage(SearchAction.class, "CTL_SearchAction");
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}
