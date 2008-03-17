/*
 *  Copyright 2005-2008 Mevenide Team.
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
import org.codehaus.mevenide.indexer.api.RepositoryInfo;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.NbBundle;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 *
 * @author Anuradha G
 */
public class RemoteIndexTransferListener implements TransferListener {

    private ProgressHandle handle;
    private RepositoryInfo info;
    private int lastunit;/*last work unit*/
    /*Debug*/

    private boolean debug;
    private InputOutput io;
    private OutputWriter writer;

    public RemoteIndexTransferListener(RepositoryInfo info) {

        this.info = info;


        if (debug) {
            io = IOProvider.getDefault().getIO(NbBundle.getMessage(RemoteIndexTransferListener.class, "LBL_Transfer_TAG")//NII18N
                    + (info.getName()), true);
            writer = io.getOut();
        }
    }

    public void transferInitiated(TransferEvent arg0) {/*EMPTY*/

    }

    public void transferStarted(TransferEvent arg0) {
        long contentLength = arg0.getResource().getContentLength();
        this.handle = ProgressHandleFactory.createHandle(NbBundle.getMessage(RemoteIndexTransferListener.class, "LBL_Transfer_TAG")//NII18N
                + info.getName());
        handle.start((int) contentLength / 1024);
        if (debug) {
            writer.println("File Size :" + (int) contentLength / 1024);//NII18N

        }
    }

    public void transferProgress(TransferEvent arg0, byte[] arg1, int arg2) {
        int work = arg2 / 1024;
        if (handle != null) {
            handle.progress(lastunit += work);
        }
        if (debug) {
            writer.println("Units completed :" + lastunit);//NII18N

        }
    }

    public void transferCompleted(TransferEvent arg0) {
        if (handle != null) {
            handle.finish();
        }
        if (debug) {
            writer.println("Completed");//NII18N

        }
    }

    public void transferError(TransferEvent arg0) {

        if (debug) {
            writer.println("Finish with Errors");//NII18N

        }
    }

    public void debug(String arg0) {
        if (debug) {
            writer.println(arg0);
        }
    }
}
