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
package org.codehaus.mevenide.netbeans.execute;

import java.io.*;
import org.apache.maven.BuildFailureException;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.lifecycle.LifecycleExecutionException;
import org.apache.maven.monitor.event.EventMonitor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.DuplicateProjectException;
import org.apache.maven.wagon.events.TransferListener;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.plexus.util.dag.CycleDetectedException;
import org.openide.ErrorManager;
import org.openide.util.Cancellable;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;



/**
 * support for executing maven, from the ide using embedder
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class MavenJavaExecutor implements Runnable, Cancellable {
    
    private static final RequestProcessor PROCESSOR = new RequestProcessor("Maven2 execution", 3);
    
    private RunConfig config;
    private InputOutput io;
    
    public MavenJavaExecutor(RunConfig conf) {
        config = conf;
    }
    
    private InputOutput createInputOutput() {
        InputOutput newio = IOProvider.getDefault().getIO(config.getExecutionName(), false);
        newio.setErrSeparated(false);
        try {
            newio.getOut().reset();
        } catch (IOException exc) {
            ErrorManager.getDefault().notify(exc);
        }
        return newio;
    }
    
    /**
     * not to be called directrly.. use execute();
     */
    public void run() {
        InputOutput ioput = getInputOutput();
        try {
            MavenEmbedder embedder;
            embedder = EmbedderFactory.createExecuteEmbedder();
            OutputHandler out = new OutputHandler(ioput, config.getProject());
            embedder.setLogger(out);
            embedder.execute(config.getProject().getOriginalMavenProject(), config.getGoals(), out, out, null, config.getExecutionDirectory());
        } catch (CycleDetectedException ex) {
            ex.printStackTrace();
        } catch (DuplicateProjectException ex) {
            ex.printStackTrace();
        } catch (BuildFailureException ex) {
            ex.printStackTrace();
        } catch (LifecycleExecutionException ex) {
            ex.printStackTrace();
        } catch (MavenEmbedderException ex) {
            ex.printStackTrace();
        } catch (ThreadDeath death) {
            cancel();
            throw death;
        } finally {
            ioput.getOut().close();
            ioput.getErr().close();
        }
    }
    
    public boolean cancel() {
//        if (proces != null) {
//            // this system out prints to output window..
//            System.err.println("**User cancelled execution**");
//            proces.destroy();
//        }
        return true;
    }
    
    public InputOutput getInputOutput() {
        if (io == null) {
            io = createInputOutput();
        }
        return io;
    }

}
