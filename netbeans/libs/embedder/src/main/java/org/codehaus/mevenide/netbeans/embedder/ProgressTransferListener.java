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
    
    private Map map;
    private Object LOCK = new Object();
    /** Creates a new instance of ProgressTransferListener */
    public ProgressTransferListener() {
        map = new HashMap();
    }

    public void transferInitiated(TransferEvent transferEvent) {
        File fil = transferEvent.getLocalFile();
        String name = (transferEvent.getRequestType() == TransferEvent.REQUEST_GET ? "Downloading " : "Uploading ")
                     + fil.getName();
                
        ProgressHandle handle = ProgressHandleFactory.createHandle(name);
        synchronized (LOCK) {
            map.put(fil, handle);
        }
        Resource res = transferEvent.getResource();
    }

    public void transferStarted(TransferEvent transferEvent) {
        Resource res = transferEvent.getResource();
        File fil = transferEvent.getLocalFile();
        ProgressHandle handle;
        synchronized (LOCK) {
            handle = (ProgressHandle)map.get(fil);
        }
        if (handle != null) {
            int total = (int)Math.min((long)Integer.MAX_VALUE, res.getContentLength());
            handle.start(total);
            handle.progress("Transfer Started...");
        }
    }

    public void transferProgress(TransferEvent transferEvent, byte[] b, int i) {
        File fil = transferEvent.getLocalFile();
        ProgressHandle handle;
        synchronized (LOCK) {
            handle = (ProgressHandle)map.get(fil);
        }
        if (handle != null) {
            handle.progress("Transferred " + i, i);
        }
    }

    public void transferCompleted(TransferEvent transferEvent) {
        File fil = transferEvent.getLocalFile();
        ProgressHandle handle;
        synchronized (LOCK) {
            handle = (ProgressHandle)map.get(fil);
        }
        if (handle != null) {
            handle.finish();
        }
    }

    public void transferError(TransferEvent transferEvent) {
        transferCompleted(transferEvent);
    }

    public void debug(String string) {
    }
    
}
