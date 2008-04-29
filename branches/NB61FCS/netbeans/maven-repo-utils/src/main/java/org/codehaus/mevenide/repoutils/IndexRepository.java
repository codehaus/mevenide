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
import org.codehaus.mevenide.indexer.NexusRepositoryIndexserImpl;


/**
 *
 * @author mkleint
 */
public class IndexRepository {

    /**
     * @param args
     * 0 - repository index directory
     * 1 - repository root directory
     * 2 - repository id
     */
    public static void main(String[] args) {
            if (args.length < 2) {
                throw new IllegalArgumentException("Must have 2 parameters [repo index directory] [repo root directory] [repo id]");
            }
            File basedir = new File(args[0]);
            if (basedir.exists()) {
                throw new IllegalArgumentException(args[0] + " folder already exist");
            }
            basedir.mkdirs();
            File repodir = new File(args[1]);
            if (!repodir.exists()) {
                throw new IllegalArgumentException(args[1] + " folder doesn't exist");
            }
            NexusRepositoryIndexserImpl index = new NexusRepositoryIndexserImpl();
            index.indexRepo(args[2], repodir, basedir);
    }
}