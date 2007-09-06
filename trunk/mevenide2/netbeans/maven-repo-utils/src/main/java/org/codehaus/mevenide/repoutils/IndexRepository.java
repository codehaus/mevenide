/*
 * IndexRepository.java
 *
 * Created on Sep 4, 2007, 11:21:24 AM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codehaus.mevenide.repoutils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.archiva.discoverer.ArtifactDiscoverer;
import org.apache.maven.archiva.discoverer.filter.AcceptAllArtifactFilter;
import org.apache.maven.archiva.indexer.RepositoryArtifactIndex;
import org.apache.maven.archiva.indexer.RepositoryArtifactIndexFactory;
import org.apache.maven.archiva.indexer.record.RepositoryIndexRecordFactory;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.InvalidArtifactRTException;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.embed.Embedder;


/**
 *
 * @author mkleint
 */
public class IndexRepository {

    /**
     * @param args
     * 0 - repository index directory
     * 1 - repository root directory
     */
    public static void main(String[] args) {

        try {
            Embedder embedder = new Embedder();
            RepositoryArtifactIndexFactory indexFactory;
            RepositoryArtifactIndex defaultIndex;
            RepositoryIndexRecordFactory recordFactory;
            ArtifactDiscoverer discoverer;
            ArtifactFactory artifactFactory;

            embedder.start();
            indexFactory = (RepositoryArtifactIndexFactory) embedder.lookup(RepositoryArtifactIndexFactory.ROLE, "lucene"); //NOI18N
            discoverer = (ArtifactDiscoverer) embedder.lookup(ArtifactDiscoverer.ROLE, "default" ); //NOI18N
            recordFactory = (RepositoryIndexRecordFactory) embedder.lookup(RepositoryIndexRecordFactory.ROLE, "standard"); //NOI18N
            artifactFactory = (ArtifactFactory) embedder.lookup(ArtifactFactory.ROLE);
            File basedir = new File(args[0]);
            if (basedir.exists()) {
                throw new IllegalArgumentException(args[0] + " folder already exist");
            }
            basedir.mkdirs();
            defaultIndex = indexFactory.createStandardIndex(new File(basedir, ".index")); //NOI18N
            File repodir = new File(args[1]);
            if (!repodir.exists()) {
                throw new IllegalArgumentException(args[1] + " folder doesn't exist");
            }

            //discover artifacts
            ArtifactFilter filter = new AcceptAllArtifactFilter();
            ArtifactRepository repository = new DefaultArtifactRepository("central", "file://" + repodir.getAbsolutePath(), new DefaultRepositoryLayout());
            final List artifacts = discoverer.discoverArtifacts(repository, null, filter);

            //now index..
            int size = artifacts.size();
            System.out.println("Number of Artifacts: " + size);
            int count = 0;
            Collection records = new ArrayList(250);
            for (Iterator i = artifacts.iterator(); i.hasNext();) {
                Artifact artifact = (Artifact) i.next();
                i.remove();
                count++;
                try {
                    records.add(recordFactory.createRecord(artifact));
                } catch (InvalidArtifactRTException e) {
                    //TODO.. some better handling..
                    e.printStackTrace();
                }
                if (count % 200 == 0) {
                    defaultIndex.indexRecords(records);
                    records.clear();
                    System.out.println("... processed " + count);
                }
                if (count % 800 == 0) {
                    System.gc();
                }
            }
            defaultIndex.indexRecords(records);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}