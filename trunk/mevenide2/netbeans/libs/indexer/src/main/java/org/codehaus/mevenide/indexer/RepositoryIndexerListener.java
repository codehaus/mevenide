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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences.RepositoryInfo;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import org.sonatype.nexus.index.ArtifactContext;
import org.sonatype.nexus.index.ArtifactInfo;
import org.sonatype.nexus.index.ArtifactScanningListener;
import org.sonatype.nexus.index.NexusIndexer;
import org.sonatype.nexus.index.context.IndexingContext;
import org.sonatype.nexus.index.scan.ScanningResult;

/**
 *
 * @author Anuradha G
 */
public class RepositoryIndexerListener implements ArtifactScanningListener {

    private final IndexingContext indexingContext;
    private final NexusIndexer nexusIndexer;
    private long tstart;

    private int count;
    private InputOutput io;
    OutputWriter writer;
    RepositoryInfo repositoryInfoById;
    ProgressHandle handle;
    public RepositoryIndexerListener(NexusIndexer nexusIndexer, IndexingContext indexingContext) {
        this.indexingContext = indexingContext;
        this.nexusIndexer = nexusIndexer;
        repositoryInfoById = RepositoryPreferences.getRepositoryInfoById(indexingContext.getId());
        io = IOProvider.getDefault().getIO("Indexing " +repositoryInfoById.getName(), true);
        writer = io.getOut();
        
    }

    public void scanningStarted(IndexingContext ctx) {
         handle = ProgressHandleFactory.createHandle("Indexing Repo   : " + repositoryInfoById.getName());
        writer.println("Indexing Repo   : " + repositoryInfoById.getName());
        writer.println("Index Directory : " + ctx.getIndexDirectory().toString());
        writer.println("--------------------------------------------------------");
        writer.println("Scanning started at " + SimpleDateFormat.getInstance().format(new Date()));
        handle.start();
        handle.switchToIndeterminate();
        tstart = System.currentTimeMillis();
    }

    public void artifactDiscovered(ArtifactContext ac) {
        count++;

        long t = System.currentTimeMillis();

        ArtifactInfo ai = ac.getArtifactInfo();

        if ("maven-plugin".equals(ai.packaging)) {
            writer.printf("Plugin: %s:%s:%s - %s %s\n", //
                    ai.groupId,
                    ai.artifactId,
                    ai.version,
                    ai.prefix,
                    "" + ai.goals);
        }

        
            // ArtifactInfo ai = ac.getArtifactInfo();
            writer.printf("  %6d %s\n", count, formatFile(ac.getPom()));
            handle.progress(ac.getArtifactInfo().toString());
        
    }

    public void artifactError(ArtifactContext ac, Exception e) {
        writer.printf("! %6d %s - %s\n", count, formatFile(ac.getPom()), e.getMessage());

        writer.printf("         %s\n", formatFile(ac.getArtifact()));
        e.printStackTrace(writer);

       
    }

    private String formatFile(File file) {
        return file.getAbsolutePath().substring(indexingContext.getRepository().getAbsolutePath().length() + 1);
    }

    public void scanningFinished(IndexingContext ctx, ScanningResult result) {
        writer.println("Scanning ended at " + SimpleDateFormat.getInstance().format(new Date()));
       
        if (result.hasExceptions()) {
            writer.printf("Total scanning errors: %s\n", result.getExceptions().size());
        }

        writer.printf("Total files scanned: %s\n", result.getTotalFiles());

        long t = System.currentTimeMillis() - tstart;

        long s = t / 1000L;

        if (t > 60 * 1000) {
            long m = t / 1000L / 60L;

            writer.printf("Total time: %d min %d sec\n", m, s - (m * 60));
        } else {
            writer.printf("Total time: %d sec\n", s);

        }
        handle.finish();

    }
}

       

