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
import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * continuum related settings
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class ContinuumSettings2 extends SystemOption {
    
    public static final String PROP_SERVERS = "servers"; // NOI18N
    
    private List<ServerInfo> serverInfos;
    
    private static final long serialVersionUID = -4857548488373437L;
    
    protected void initialize() {
        super.initialize();
        List<ServerInfo> servers = new ArrayList<ServerInfo>();
        servers.add(new ServerInfo("maven.zones.apache.org",8000,8080));
//        servers.add(new ServerInfo("localhost",8000,8080));
        setServers(servers);
    }
    
    public String displayName() {
        return NbBundle.getMessage(ContinuumSettings2.class, "LBL_Settings"); //NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public static ContinuumSettings2 getDefault() {
        return (ContinuumSettings2) findObject(ContinuumSettings2.class, true);
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
        List<ServerInfo> toReturn = new ArrayList<ServerInfo>();
        String[] servers = (String[])getProperty(PROP_SERVERS);
        for (String rawServerInfo :  servers ) {
            toReturn.add(new ServerInfo(rawServerInfo));
        }
        return toReturn;
    }
    
    public void setServers(List<ServerInfo> serverInfos) {
        String[] servers = new String[serverInfos.size()];
        int i =0;
        for (ServerInfo serverInfo : serverInfos) {
            servers[i] = serverInfo.toString();
            i++;
        }
        putProperty(PROP_SERVERS, servers, true);
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
}
