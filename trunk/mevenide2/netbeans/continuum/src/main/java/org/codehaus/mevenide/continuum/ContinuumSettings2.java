/* ==========================================================================
 * Copyright 2005 Mevenide Team
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

package org.codehaus.mevenide.continuum;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;


/**
 * continuum related settings
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class ContinuumSettings2  {
    
    public static final String PROP_SERVER = "server"; // NOI18N
    
    private static ContinuumSettings2 INSTANCE = new ContinuumSettings2();
    private Preferences preferences;
//    private List<ServerInfo> serverInfos;
    
//    protected void initialize() {
//        super.initialize();
//        List<ServerInfo> servers = new ArrayList<ServerInfo>();
//        servers.add(new ServerInfo("maven.zones.apache.org",8000,8080));
////        servers.add(new ServerInfo("localhost",8000,8080));
//        setServers(servers);
//    }
    
    public String displayName() {
        return NbBundle.getMessage(ContinuumSettings2.class, "LBL_Settings"); //NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public static ContinuumSettings2 getDefault() {
        return INSTANCE;
    }
    
    public String[] getServerArray() {
        List<ServerInfo> servers = getServers();
        String[] toReturn = new String[servers.size()];
        for (int i = 0; i < servers.size(); i++) {
            toReturn[i] =  servers.get(i).toString();
        }
        return toReturn;
    }
    
    public List<ServerInfo> getServers() {
        try {
        List<ServerInfo> toReturn = new ArrayList<ServerInfo>();
            String[] keys = getPreferences().keys();
            for (int i = 0; i < keys.length; i++) {
                String rawServerInfo = getPreferences().get(keys[i], null);
            toReturn.add(new ServerInfo(rawServerInfo));
        }
        return toReturn;
        } catch (BackingStoreException ex) {
            throw new RuntimeException(ex);
    }
    }
    
    public void setServers(List<ServerInfo> serverInfos) {
        try {
        String[] servers = new String[serverInfos.size()];
            int i = 0;
        for (ServerInfo serverInfo : serverInfos) {
            servers[i] = serverInfo.toString();
            i++;
        }
            getPreferences().clear();
            for (int j = 0; j < servers.length; j++) {
//                IOProvider.getDefault().getStdOut().println("getPreferences().put("+PROP_SERVER + j+", "+servers[j]+")");
                getPreferences().put(PROP_SERVER + j, servers[j]);
    }
        } catch (BackingStoreException ex) {
           throw new RuntimeException(ex);
        }
    }
    
    
    public void removeServer(String serverRawInfo) {
        removeServer(new ServerInfo(serverRawInfo));
    }
    
    public void addServer(ServerInfo serverInfo) {
        List<ServerInfo> servers = getServers();
        if (!servers.contains(serverInfo)) {
            servers.add(serverInfo);
            setServers(servers);
        }
    }
    
    public void removeServer(ServerInfo serverInfo) {
        List<ServerInfo> servers = getServers();
        servers.remove(serverInfo);
        setServers(servers);
    }
    
    public Preferences getPreferences() {
        if (preferences == null) preferences = NbPreferences.forModule(ContinuumSettings2.class);
        return preferences;
}
    
}
