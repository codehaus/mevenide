/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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

package org.mevenide.netbeans.project.exec;

import org.netbeans.api.debugger.jpda.DebuggerStartException;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.RequestProcessor;

/**
 * Custom line based filter for maven executor output when running the application.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class AttachDebuggerOutputFilter implements OutputFilter {
    private int timeout;
    private String host;
    private int port;
    
    public AttachDebuggerOutputFilter(int delay, String hostname, int p) {
        timeout = delay;
        host = hostname;
        port = p;
    }
    public String filterLine(String line) {
        if (line.indexOf("[mevenide-debug-start]") != -1) { //NOI18N
            System.out.println("tstarting thread..");
            RequestProcessor.postRequest(new Runnable() {
                public void run() {
                    try {
                        System.out.println("starting jpda debugger..");
                        JPDADebugger debug = JPDADebugger.attach(host, port, new Object[0]);
                    } catch (DebuggerStartException exc) {
                        System.out.println("exception while runnign debugger.");
                        exc.printStackTrace();
                        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Cannot attach debugger.", NotifyDescriptor.ERROR_MESSAGE));
                    }
                }
            }, timeout);
        }
        return line;
    }
}
