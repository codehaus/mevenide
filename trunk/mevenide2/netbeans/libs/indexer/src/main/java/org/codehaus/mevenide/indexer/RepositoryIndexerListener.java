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
public class RepositoryIndexerListener implements ArtifactScanningListener
    {
        private final IndexingContext indexingContext;

        private final NexusIndexer nexusIndexer;




        private long tstart;

        private long ts = System.currentTimeMillis();

        private int count;

       public  RepositoryIndexerListener( NexusIndexer nexusIndexer, IndexingContext indexingContext)
        {
            this.indexingContext = indexingContext;
            this.nexusIndexer = nexusIndexer;
          
         
        }

        public void scanningStarted( IndexingContext ctx )
        {
            System.err.println( "Scanning started" );
            tstart = System.currentTimeMillis();
        }

      
        public void artifactDiscovered( ArtifactContext ac )
        {
            count++;

            long t = System.currentTimeMillis();

            ArtifactInfo ai = ac.getArtifactInfo();

            if ( "maven-plugin".equals( ai.packaging ) )
            {
                System.err.printf( "Plugin: %s:%s:%s - %s %s\n", //
                    ai.groupId,
                    ai.artifactId,
                    ai.version,
                    ai.prefix,
                    "" + ai.goals );
            }

            if ( ( t - ts ) > 500L )
            {
                // ArtifactInfo ai = ac.getArtifactInfo();
                System.err.printf( "  %6d %s\n", count, formatFile( ac.getPom() ) );
                ts = t;
            }
        }

        
        public void artifactError( ArtifactContext ac, Exception e )
        {
            System.err.printf( "! %6d %s - %s\n", count, formatFile( ac.getPom() ), e.getMessage() );

            System.err.printf( "         %s\n", formatFile( ac.getArtifact() ) );

//            if ( debug )
//            {
//                e.printStackTrace();
//            }

            ts = System.currentTimeMillis();
        }

        private String formatFile( File file )
        {
            return file.getAbsolutePath().substring( indexingContext.getRepository().getAbsolutePath().length() + 1 );
        }

   
        public void scanningFinished( IndexingContext ctx, ScanningResult result )
        {
            if ( result.hasExceptions() )
            {
                System.err.printf( "Total scanning errors: %s\n", result.getExceptions().size() );
            }

            System.err.printf( "Total files scanned: %s\n", result.getTotalFiles() );

            long t = System.currentTimeMillis() - tstart;

            long s = t / 1000L;

            if ( t > 60 * 1000 )
            {
                long m = t / 1000L / 60L;

                System.err.printf( "Total time: %d min %d sec\n", m, s - ( m * 60 ) );
            }
            else
            {
                System.err.printf( "Total time: %d sec\n", s );

            }

           
        }
}

       

