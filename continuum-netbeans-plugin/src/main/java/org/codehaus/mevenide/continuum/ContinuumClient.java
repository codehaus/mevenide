/*
 *  Copyright 2008 Mevenide Team.
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
package org.codehaus.mevenide.continuum;

import org.apache.maven.continuum.xmlrpc.client.ContinuumXmlRpcClient;
import org.openide.util.RequestProcessor;

/** 
 * Facade of the Continuum client.
 * @author Lo
 */
public class ContinuumClient {

    private ServerInfo serverInfo;
    private ContinuumXmlRpcClient xmlRpcClient;
    private RequestProcessor queue;

    public ContinuumClient(String serverRawInfo) {
        this.serverInfo = new ServerInfo(serverRawInfo);
        this.queue = new RequestProcessor("Continuum server processor", 1);
        this.xmlRpcClient = new ContinuumXmlRpcClient(serverInfo.getXmlRpcUrl(), serverInfo.getUser(), serverInfo.getPassword());
    }

    public ContinuumXmlRpcClient getXmlRpcClient() {
        return xmlRpcClient;
    }

    public RequestProcessor getQueue() {
        return queue;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }
}
