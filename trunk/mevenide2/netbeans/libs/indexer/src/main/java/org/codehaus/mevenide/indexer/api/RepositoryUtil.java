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
package org.codehaus.mevenide.indexer.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import javax.swing.event.ChangeListener;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.codehaus.mevenide.indexer.NexusRepositoryIndexserImpl;
import org.codehaus.mevenide.indexer.api.NBVersionInfo;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha G
 */
public final class RepositoryUtil {

    private RepositoryUtil() {
    }

    public static RepositoryIndexer getDefaultRepositoryIndexer() {
        RepositoryIndexer lookup = Lookup.getDefault().lookup(RepositoryIndexer.class);

        return lookup != null ? lookup : new NexusRepositoryIndexserImpl();//default
    }

    public static Artifact createArtifact(NBVersionInfo info) {
        return createArtifact(info,  null);
    }

    public static Artifact createJavadocArtifact(NBVersionInfo info) {
        return createArtifact(info,  "javadoc"); //NOI18N
    }

    private static Artifact createArtifact(NBVersionInfo info, String classifier) {
        Artifact art;

        if (info.getClassifier() != null || classifier != null) {
            art = EmbedderFactory.getOnlineEmbedder().createArtifactWithClassifier(info.getGroupId(),
                    info.getArtifactId(),
                    info.getVersion(),
                    info.getType(),
                    classifier == null ? info.getClassifier() : classifier);
        } else {
            art = EmbedderFactory.getOnlineEmbedder().createArtifact(info.getGroupId(),
                    info.getArtifactId(),
                    info.getVersion(),
                    null,
                    info.getType());
        }
        ArtifactRepository repo = EmbedderFactory.getOnlineEmbedder().getLocalRepository();
        String localPath = repo.pathOf(art);
        art.setFile(new File(repo.getBasedir(), localPath));

        return art;
    }

    public static String calculateChecksum(File file) throws NoSuchAlgorithmException, IOException {
        byte[] buffer = readFile(file);

            String md5sum = DigestUtils.md5Hex(buffer);
            
            return md5sum;
    }
    public static void addIndexChangeListener(ChangeListener cl){
      
    }
     static byte[] readFile(File file) throws IOException {

        InputStream is = new FileInputStream(file);

        byte[] bytes = new byte[(int)file.length()];

        int offset = 0;
        int numRead = 0;

        while (offset < bytes.length &&
            (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {

            offset += numRead;
        }

        is.close();

        return bytes;
    }
}
