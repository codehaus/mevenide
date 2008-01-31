/*
 *  Copyright 2008 Anuradha.
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

package org.codehaus.mevenide.indexer;

import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences.RepositoryInfo;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.NbBundle;



/**
 *
 * @author Anuradha G
 */
public class RemoteIndexTransferListener implements TransferListener{

   private ProgressHandle handle;
   private RepositoryInfo info;
   private int lastunit;
    public RemoteIndexTransferListener( RepositoryInfo info) {
        this.handle =   ProgressHandleFactory.createHandle(NbBundle.getMessage(RemoteIndexTransferListener.class, "LBL_Transfer_TAG", new Object[] {})+ info.getName());
        this.info = info;
    }
   
   
    public void transferInitiated(TransferEvent arg0) {
    
      
    }

    public void transferStarted(TransferEvent arg0) {
        long contentLength = arg0.getResource().getContentLength();
        handle.start( (int) contentLength/1024);
    }

    public void transferProgress(TransferEvent arg0, byte[] arg1, int arg2) {
        int work = arg2/1024;
        
        handle.progress(lastunit+=work);
        
    }

    public void transferCompleted(TransferEvent arg0) {
       handle.finish();
    }

    public void transferError(TransferEvent arg0) {
         //todo add Error to handler
    }

    public void debug(String arg0) {
          //todo add debug info to handler
    }

    
   

}
