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



package org.codehaus.mevenide.idea.build;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public abstract class AbstractMavenBuildTask implements Runnable {
    protected IBuildEnvironment buildEnvironment;
    private boolean stopped = false;
    private boolean cancelled = false;

    protected AbstractMavenBuildTask(IBuildEnvironment buildEnvironment) {
        this.buildEnvironment = buildEnvironment;
    }

    public abstract void run();

    public abstract String getCaption ();

    protected int stop() {
        stopped = true;

        return 0;
    }

    public boolean isStopped() {
        return stopped;
    }

    protected void setStopped(boolean stop) {
        stopped = stop;
    }

    protected boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
        stop();
    }

    public IMavenBuildLogger getLogger() {
        return buildEnvironment.getLogger();
    }
}
