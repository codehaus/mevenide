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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import org.openide.util.Exceptions;

/**
 *
 * @author laurent.foret@codehaus.org
 */
@SuppressWarnings("serial")
public class ServerInfo implements Serializable {

    private URL xmlRpcUrl;
    private URL webUrl;
    private String user;
    private String password;

    public ServerInfo() {
    }

    public ServerInfo(String rawInfos) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(rawInfos, ",");
            setXmlRpcUrl(new URL(tokenizer.nextToken()));
            setWebUrl(new URL(tokenizer.nextToken()));
            if (tokenizer.hasMoreTokens()) {
                setUser(tokenizer.nextToken());
                setPassword(tokenizer.nextToken());
            } else {
                setUser(null);
                setPassword(null);
            }
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public ServerInfo(URL xmlRpcUrl, URL webUrl, String user, String password) {
        setXmlRpcUrl(xmlRpcUrl);
        setWebUrl(webUrl);
        setUser(user);
        setPassword(password);
    }

    public URL getXmlRpcUrl() {
        return xmlRpcUrl;
    }

    public void setXmlRpcUrl(URL url) {
        this.xmlRpcUrl = url;
    }

    public URL getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(URL url) {
        this.webUrl = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return xmlRpcUrl + "," + webUrl + "," + (user == null ? "" : user) + "," + (password == null ? "" : password);
    }

    @Override
    public boolean equals(Object object) {
        return this.toString().equalsIgnoreCase(object.toString());
    }
}
