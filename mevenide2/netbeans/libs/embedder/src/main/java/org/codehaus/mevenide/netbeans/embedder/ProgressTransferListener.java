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

package org.codehaus.mevenide.netbeans.embedder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.resource.Resource;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

/**
 *
 * @author mkleint
 */
public class ProgressTransferListener implements TransferListener {
    
    private long lenght = 0;
    private int count = 0;
    private ProgressHandle handle;
    /** Creates a new instance of ProgressTransferListener */
    public ProgressTransferListener() {
    }
    
    public void transferInitiated(TransferEvent transferEvent) {
        Resource res = transferEvent.getResource();
        File fil = transferEvent.getLocalFile();
        int lastSlash = res.getName().lastIndexOf("/");
        String resName = lastSlash > -1 ? res.getName().substring(lastSlash + 1) : res.getName();
        String name = (transferEvent.getRequestType() == TransferEvent.REQUEST_GET ? 
                          "Downloading " : "Uploading ") 
                          + resName;
        handle = ProgressHandleFactory.createHandle(name);
    }
    
    public void transferStarted(TransferEvent transferEvent) {
        Resource res = transferEvent.getResource();
        int total = (int)Math.min((long)Integer.MAX_VALUE, res.getContentLength());
        handle.start(total);
        lenght = total;
        count = 0;
        handle.progress("Transfer Started...");
    }
    
    public void transferProgress(TransferEvent transferEvent, byte[] b, int i) {
        count = (int)Math.min((long)Integer.MAX_VALUE, (long)count + i);
        handle.progress("Transferred " + count, count);
    }
    
    public void transferCompleted(TransferEvent transferEvent) {
        handle.finish();
    }
    
    public void transferError(TransferEvent transferEvent) {
        transferCompleted(transferEvent);
    }
    
    public void debug(String string) {
    }
    
}
