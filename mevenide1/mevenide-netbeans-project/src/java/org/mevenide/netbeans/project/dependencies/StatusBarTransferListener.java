/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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

package org.mevenide.netbeans.project.dependencies;

import java.util.logging.Logger;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;
import org.openide.awt.StatusDisplayer;

/**
 * DownloadMeter that puts events into status bar. To be replaced when netbeans
 * gets a proper progress bar.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
class StatusBarTransferListener implements TransferListener {

    private static final Logger LOGGER = Logger.getLogger(StatusBarTransferListener.class.getName());

    private long complete;

    public StatusBarTransferListener() {
    }

    public void transferInitiated(TransferEvent transferEvent) {
    }

    public void transferStarted(TransferEvent transferEvent) {
        complete = 0;
    }

    public void transferProgress(TransferEvent transferEvent, byte[] buffer, int length) {
        long total = transferEvent.getResource().getContentLength();
        complete += length;
        StatusDisplayer.getDefault().setStatusText(transferEvent.getResource().getName() + " downloaded " + complete + " of " + total);
    }

    public void transferCompleted(TransferEvent transferEvent) {
        StatusDisplayer.getDefault().setStatusText(transferEvent.getResource().getName() + " downloaded.");
    }

    public void transferError(TransferEvent transferEvent) {
        StatusDisplayer.getDefault().setStatusText(transferEvent.getException().getMessage());
    }

    public void debug(String message) {
        LOGGER.fine(message);
    }
}    