package org.netbeans.modules.maven.apisupport;

import org.netbeans.modules.maven.indexer.api.RepositoryInfo;
import org.netbeans.modules.maven.indexer.api.RepositoryPreferences;
import org.openide.modules.ModuleInstall;

/**
 * 
 * @author mkleint
 */
public class Installer extends ModuleInstall {
    
    @Override
    public void restored() {
        super.restored();
        RepositoryInfo NETBEANS = new RepositoryInfo("netbeans", RepositoryPreferences.TYPE_NEXUS, "Netbeans Repository",null,
                "http://deadlock.netbeans.org/maven2/", //NOI18N
                "http://deadlock.netbeans.org/maven2/.index/netbeans/");//NOI18N
        RepositoryPreferences.getInstance().addDefaultRepositoryInfo(NETBEANS);

    }
    
}
