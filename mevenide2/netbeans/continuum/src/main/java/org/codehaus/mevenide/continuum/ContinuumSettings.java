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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * continuum related settings
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class ContinuumSettings extends SystemOption {
    public static final String PROP_SERVERS = "servers"; // NOI18N
    public static final String PROP_OUTPUTS = "outputs"; //NOI18N
    
    private static final long serialVersionUID = -4857548488373437L;
    
    protected void initialize() {
        super.initialize();
        setServers(new String[] {
            "http://maven.zones.apache.org:8000",
            "http://ci.codehaus.org:8000",
            "http://localhost:8000"        
        });
        setOutputs(new String[] {
            "http://maven.zones.apache.org:8080/continuum/build-output-directory",
            "http://ci.codehaus.org:8080/continuum/build-output-directory",        
            "http://localhost:8080/continuum/build-output-directory"        
            
        });
    }
    
    public String displayName() {
        return NbBundle.getMessage(ContinuumSettings.class, "LBL_Settings"); //NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public static ContinuumSettings getDefault() {
        return (ContinuumSettings) findObject(ContinuumSettings.class, true);
    }
    
    public String[] getServers() {
        return (String[])getProperty(PROP_SERVERS);
    }
    
    public void setServers(String[] repos) {
        putProperty(PROP_SERVERS, repos, true);
    }    
    
    public String[] getOutputs() {
        return (String[])getProperty(PROP_OUTPUTS);
    }
    
    public void setOutputs(String[] repos) {
        putProperty(PROP_OUTPUTS, repos, true);
    }    
    
    public String getOutputForServer(String server) {
        String[] sers = getServers();
        int index = -1;
        for (int i = 0; i < sers.length; i++) {
            if (server.equals(sers[i])) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            String[] outs = getOutputs();
            if (index < outs.length) {
                return outs[index];
            }
        }
        return null;
    }
    
}
