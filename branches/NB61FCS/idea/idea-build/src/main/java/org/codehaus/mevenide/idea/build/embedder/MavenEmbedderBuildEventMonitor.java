/* ==========================================================================
 * Copyright 2006 Mevenide Team
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



package org.codehaus.mevenide.idea.build.embedder;

import com.intellij.openapi.progress.ProgressIndicator;

import org.apache.maven.monitor.event.EventMonitor;

import org.codehaus.mevenide.idea.build.AbstractMavenBuildTask;
import org.codehaus.mevenide.idea.build.MavenBuildCancelledException;
import org.codehaus.mevenide.idea.build.util.BuildConstants;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenEmbedderBuildEventMonitor implements EventMonitor {
    private ProgressIndicator progressIndicator;
    private AbstractMavenBuildTask buildTask;

    public MavenEmbedderBuildEventMonitor(ProgressIndicator progressIndicator, AbstractMavenBuildTask buildTask) {
        this.progressIndicator = progressIndicator;
        this.progressIndicator.setIndeterminate(true);
        this.progressIndicator.setFraction(0.5);
        this.buildTask = buildTask;
    }

    public void startEvent(String eventName, String target, long timestamp) {
        if (progressIndicator.isCanceled()) {
            buildTask.cancel();

            throw new MavenBuildCancelledException(System.getProperty("line.separator")
                    + BuildConstants.MESSAGE_EMBEDDED_MAVENBUILD_ABORTED);
        }

        if (buildTask.isStopped()) {
            throw new MavenBuildCancelledException(System.getProperty("line.separator")
                    + BuildConstants.MESSAGE_EMBEDDED_MAVENBUILD_ABORTED);
        }

        if (eventName.equals("mojo-execute")) {
            progressIndicator.setText(target);
        }
    }

    public void endEvent(String string, String string1, long l) {}

    public void errorEvent(String string, String string1, long l, Throwable throwable) {}
}
