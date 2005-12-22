/*
 * ProgressTransferListener.java
 *
 * Created on December 22, 2005, 4:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.embedder;

import java.io.File;
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
        System.out.println("res=" + res.getContentLength());
        int total = (int)Math.min((long)Integer.MAX_VALUE, res.getContentLength());
        handle.start(total);
    }

    public void transferStarted(TransferEvent transferEvent) {
        Resource res = transferEvent.getResource();
        System.out.println("res2=" + res.getContentLength());
        File fil = transferEvent.getLocalFile();
        ProgressHandle handle;
        synchronized (LOCK) {
            handle = (ProgressHandle)map.get(fil);
        }
        if (handle != null) {
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
