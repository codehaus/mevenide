package org.mevenide.idea.synchronize;

/**
 * @author Arik Kfir
 */
public interface ArtifactProblemInfo extends ProblemInfo {

    String getGroupId();
    String getArtifactId();
    String getType();
    String getVersion();
    String getExtension();

}
