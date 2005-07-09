package org.mevenide.idea.synchronize;

import org.mevenide.idea.repository.Artifact;

/**
 * @author Arik Kfir
 */
public interface ArtifactProblemInfo extends ProblemInfo {

    Artifact getArtifact();

}
