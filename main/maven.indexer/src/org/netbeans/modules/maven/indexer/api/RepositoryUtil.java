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
package org.netbeans.modules.maven.indexer.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.InvalidProjectModelException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.netbeans.modules.maven.embedder.EmbedderFactory;
import org.codehaus.plexus.util.IOUtil;

/**
 *
 * @author Anuradha G
 */
public final class RepositoryUtil {

    private RepositoryUtil() {
    }

    public static Artifact createArtifact(NBVersionInfo info) {
        return createArtifact(info, null);
    }

    public static Artifact createJavadocArtifact(NBVersionInfo info) {
        return createArtifact(info, "javadoc"); //NOI18N
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

    public static String calculateMD5Checksum(File file) throws IOException {
        byte[] buffer = readFile(file);
        String md5sum = DigestUtils.md5Hex(buffer);
        return md5sum;
    }
    
    static String calculateSHA1Checksum(File file) throws IOException {
        byte[] buffer = readFile(file);
        String sha1sum = DigestUtils.shaHex(buffer);
        return sha1sum;
    }

    static byte[] readFile(File file) throws IOException {

        InputStream is = null; 
        byte[] bytes = new byte[(int) file.length()];
        try {
            is = new FileInputStream(file);


        int offset = 0;
        int numRead = 0;

        while (offset < bytes.length &&
                (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {

            offset += numRead;
        }
        } finally {
            IOUtil.close(is);
        }

        return bytes;
    }

    public static MavenProject readMavenProject(String grId, String artId, String ver, ArtifactRepository repository) {
        MavenProject mavenProject = null;
        try {
            // we need to use the online embedder as the project one never
            // puts anything in the local repository, thus not resolving dependencies.
            //mkleint: this is somewhat strange thing to do for indexing remote repositories
            // via the maven-repo-utils CLI tool..
            ArtifactFactory artifactFactory = (ArtifactFactory) EmbedderFactory.getOnlineEmbedder().getPlexusContainer().lookup(ArtifactFactory.class);
            Artifact projectArtifact = artifactFactory.createProjectArtifact(
                    grId,
                    artId,
                    ver,
                    null);

            MavenProjectBuilder builder = (MavenProjectBuilder) EmbedderFactory.getOnlineEmbedder().getPlexusContainer().lookup(MavenProjectBuilder.class);
            mavenProject = builder.buildFromRepository(projectArtifact, new ArrayList(), repository);

        } catch (InvalidProjectModelException ex) {
            //ignore nexus is falling ???
            ex.printStackTrace();
        } catch (ProjectBuildingException ex) {
            ex.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return mavenProject;
    }
}
