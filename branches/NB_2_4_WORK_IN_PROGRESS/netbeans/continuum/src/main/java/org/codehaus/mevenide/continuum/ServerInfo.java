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

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 *
 * @author laurent.foret@codehaus.org
 */
public class ServerInfo implements Serializable {
    
    private String hostname;
    private int rpcPort;
    private int webAppPort;
    
    public ServerInfo() {
    }
    
    public ServerInfo(String rawInfos) {
        StringTokenizer tokenizer = new StringTokenizer(rawInfos, ",");
        setHostname(tokenizer.nextToken());
        setRpcPort(Integer.parseInt(tokenizer.nextToken()));
        setWebAppPort(Integer.parseInt(tokenizer.nextToken()));
    }
    
    public ServerInfo(String name, int rpcPort, int webAppPort) {
        setHostname(name);
        setRpcPort(rpcPort);
        setWebAppPort(webAppPort);
    }
    
    public String getHostname() {
        return hostname;
    }
    
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    
    public int getRpcPort() {
        return rpcPort;
    }
    
    public void setRpcPort(int rpcPort) {
        this.rpcPort = rpcPort;
    }
    
    public int getWebAppPort() {
        return webAppPort;
    }
    
    public void setWebAppPort(int webAppPort) {
        this.webAppPort = webAppPort;
    }
    
    public String getRpcURL() {
        return "http://" + getHostname().trim() +":"+ getRpcPort();
    }
    
    public  String getWebAppURL() {
        return "http://" + getHostname().trim() +":"+ getWebAppPort() + "/continuum/servlet/browse";
    }
    
    public String toString() {
        return getHostname()+","+getRpcPort()+","+getWebAppPort();
    }
    
    public boolean equals(Object object) {
        return this.toString().equalsIgnoreCase(object.toString());
    }
    
}
