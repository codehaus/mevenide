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

package org.codehaus.mevenide.netbeans.embedder.exec;

import java.io.File;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.resource.Resource;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.openide.util.NbBundle;

/**
 *
 * @author mkleint
 */
public class ProgressTransferListener implements TransferListener {
    
    private static ThreadLocal<Integer> lengthRef = new ThreadLocal<Integer>();
    private static ThreadLocal<Integer> countRef = new ThreadLocal<Integer>();
    private static ThreadLocal<ProgressContributor> contribRef = new ThreadLocal<ProgressContributor>();
    private static ThreadLocal<AggregateProgressHandle> handleRef = new ThreadLocal<AggregateProgressHandle>();
    /** Creates a new instance of ProgressTransferListener */
    public ProgressTransferListener() {
    }
    
    public static void setAggregateHandle(AggregateProgressHandle hndl) {
        handleRef.set(hndl);
    }
    
    private AggregateProgressHandle getHandle() {
        if (handleRef.get() == null) {
            handleRef.set(AggregateProgressFactory.createHandle("Fallback", new ProgressContributor[0], null, null));
            handleRef.get().start();
            //TODO just a fallback.. shall not happen..
        }
        return handleRef.get();
    }
    
    public static void clearAggregateHandle() {
        handleRef.remove();
        contribRef.remove();
    }
    
    public void transferInitiated(TransferEvent transferEvent) {
        Resource res = transferEvent.getResource();
        File fil = transferEvent.getLocalFile();
        int lastSlash = res.getName().lastIndexOf("/"); //NOI18N
        String resName = lastSlash > -1 ? res.getName().substring(lastSlash + 1) : res.getName();
        if (!resName.endsWith(".pom")) { //NOI18N
            String name = (transferEvent.getRequestType() == TransferEvent.REQUEST_GET
                              ? NbBundle.getMessage(ProgressTransferListener.class, "TXT_Download", resName) 
                              : NbBundle.getMessage(ProgressTransferListener.class, "TXT_Uploading", resName));
            contribRef.set(AggregateProgressFactory.createProgressContributor(name));
        }
    }
    
    public void transferStarted(TransferEvent transferEvent) {
        String smer = transferEvent.getRequestType() == TransferEvent.REQUEST_GET ? 
                              "Downloading: " : "Uploading: "; //NOI18N - ends up in the maven output. 
        System.out.println(smer + transferEvent.getWagon().getRepository().getUrl() + "/" + transferEvent.getResource().getName()); //NOI18N
        if (contribRef.get() == null) {
            return;
        }
        Resource res = transferEvent.getResource();
        int total = (int)Math.min((long)Integer.MAX_VALUE, res.getContentLength());
        handleRef.get().addContributor(contribRef.get());
        if (total < 0) {
            contribRef.get().start(0);
        } else {
            contribRef.get().start(total);
        }
        lengthRef.set(total);
        countRef.set(0);
        contribRef.get().progress(org.openide.util.NbBundle.getMessage(ProgressTransferListener.class, "TXT_Started")); 
    }
    
    public void transferProgress(TransferEvent transferEvent, byte[] b, int i) {
        if (contribRef.get() == null) {
            return;
        }
        countRef.set((int)Math.min((long)Integer.MAX_VALUE, (long)countRef.get() + i));
        if (lengthRef.get() < 0) {
            contribRef.get().progress(NbBundle.getMessage(ProgressTransferListener.class, "TXT_Transferring")); 
        } else {
            contribRef.get().progress(NbBundle.getMessage(ProgressTransferListener.class, "TXT_Transferred", countRef.get()), countRef.get()); 
        }
    }
    
    public void transferCompleted(TransferEvent transferEvent) {
        if (contribRef.get() == null) {
            return;
        }
        contribRef.get().finish();
        contribRef.remove();
    }
    
    public void transferError(TransferEvent transferEvent) {
        transferCompleted(transferEvent);
        //TODO some reporting??
    }
    
    public void debug(String string) {
    }
    
}
