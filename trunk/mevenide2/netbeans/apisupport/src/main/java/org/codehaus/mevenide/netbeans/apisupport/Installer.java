package org.codehaus.mevenide.netbeans.apisupport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import org.codehaus.mevenide.indexer.api.RepositoryInfo;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences;
import org.openide.modules.ModuleInstall;

/**
 * hack!!
 * 
 * @author mkleint
 */
public class Installer extends ModuleInstall {
    
    /**
     * screw friend dependency.
     */ 
    @Override
    public void validate() throws IllegalStateException {
        try {
            java.lang.Class main = java.lang.Class.forName("org.netbeans.core.startup.Main", false,  //NOI18N
                    Thread.currentThread().getContextClassLoader());
            Method meth = main.getMethod("getModuleSystem", new Class[0]); //NOI18N
            Object moduleSystem = meth.invoke(null, new Object[0]);
            meth = moduleSystem.getClass().getMethod("getManager", new Class[0]); //NOI18N
            Object mm = meth.invoke(moduleSystem, new Object[0]);
            Method moduleMeth = mm.getClass().getMethod("get", new Class[] {String.class}); //NOI18N
            Object persistence = moduleMeth.invoke(mm, "org.netbeans.core.startup"); //NOI18N
            if (persistence != null) {
                Field frField = persistence.getClass().getSuperclass().getDeclaredField("friendNames"); //NOI18N
                frField.setAccessible(true);
                Set friends = (Set)frField.get(persistence);
                friends.add("org.codehaus.mevenide.netbeans.apisupport"); //NOI18N
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            new IllegalStateException("Cannot fix dependencies for org.codehaus.mevenide.netbeans.apisupport. " + //NOI18N
                    "Please log a report at http://jira.codehaus.org/browse/MEVENIDE"); //NOI18N
        }
    }

    @Override
    public void restored() {
        super.restored();
        RepositoryInfo NETBEANS = new RepositoryInfo("netbeans", RepositoryPreferences.TYPE_NEXUS, "Netbeans Repository",null,
                "http://deadlock.netbeans.org/maven2/", //NOI18N
                "http://deadlock.netbeans.org/maven2/.index/netbeans/");//NOI18N
        RepositoryPreferences.getInstance().addDefaultRepositoryInfo(NETBEANS);

    }
    
}
