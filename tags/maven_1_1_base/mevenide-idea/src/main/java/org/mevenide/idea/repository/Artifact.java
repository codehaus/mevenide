package org.mevenide.idea.repository;

import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik Kfir
 */
public class Artifact {
    private String groupId;
    private String artifactId;
    private String type;
    private String version;
    private String ext;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(final String pGroupId) {
        groupId = neverNull(pGroupId);
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(final String pArtifactId) {
        artifactId = neverNull(pArtifactId);
    }

    public String getType() {
        return type;
    }

    public void setType(final String pType) {
        type = neverNull(pType);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String pVersion) {
        version = neverNull(pVersion);
    }

    public String getExtension() {
        return ext;
    }

    public void setExtension(final String pExtension) {
        ext = neverNull(pExtension);
    }

    public boolean isComplete() {
        return groupId != null && groupId.trim().length() > 0 &&
                artifactId != null && artifactId.trim().length() > 0 &&
                type != null && type.trim().length() > 0 &&
                version != null && version.trim().length() > 0;
    }

    public Artifact getCompleteArtifact() {
        if (groupId == null || groupId.trim().length() == 0)
            throw new IllegalStateException("Artifact has no group ID");

        if (artifactId == null || artifactId.trim().length() == 0)
            throw new IllegalStateException("Artifact has no artifact ID");

        final Artifact artifact = new Artifact();
        artifact.setGroupId(groupId);
        artifact.setArtifactId(artifactId);
        if (type == null)
            artifact.setType("jar");
        else
            artifact.setType(type);

        if (version == null)
            artifact.setVersion("SNAPSHOT");
        else
            artifact.setVersion(version);

        if (ext == null)
            artifact.setExtension(artifact.getType());
        else
            artifact.setExtension(ext);

        return artifact;
    }

    public String getRelativePath() {
        return getRelativePath(true);
    }

    public String getRelativePath(final boolean pGuessMissingItems) {
        return convertToRelativePath(this, pGuessMissingItems);
    }

    public RepoPathElement toRepoPathElement(final IRepositoryReader pRepo) {
        return new RepoPathElement(pRepo,
                                   null,
                                   groupId,
                                   type,
                                   version,
                                   artifactId,
                                   ext);
    }

    public static Artifact fromRepoPathElement(final RepoPathElement pElt) {
        final Artifact a = new Artifact();
        a.setGroupId(pElt.getGroupId());
        a.setArtifactId(pElt.getArtifactId());
        a.setType(pElt.getType());
        a.setVersion(pElt.getVersion());
        a.setExtension(pElt.getExtension());
        return a;
    }

    private static String convertToRelativePath(final Artifact pArtifact,
                                                final boolean pGuessMissingItems) {
        final Artifact a;
        if (pGuessMissingItems)
            a = pArtifact.getCompleteArtifact();
        else
            a = pArtifact;

        final String groupId = a.getGroupId();
        final String artifactId = a.getArtifactId();
        final String type = a.getType();
        final String version = a.getVersion();
        final String ext = a.getExtension() == null ? type : a.getExtension();

        final StringBuilder buf = new StringBuilder(100);
        buf.append(groupId).append('/').
                append(type).append('s').append('/').
                append(artifactId).append('-').append(version).append('.').append(ext);
        return buf.toString();
    }

    private static String neverNull(final String pValue) {
        if (pValue != null && pValue.trim().length() == 0)
            return null;
        else
            return pValue;
    }

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Artifact artifact = (Artifact) o;

        if (artifactId != null ? !artifactId.equals(artifact.artifactId) : artifact.artifactId != null) return false;
        if (groupId != null ? !groupId.equals(artifact.groupId) : artifact.groupId != null) return false;
        if (type != null ? !type.equals(artifact.type) : artifact.type != null) return false;
        if (version != null ? !version.equals(artifact.version) : artifact.version != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (groupId != null ? groupId.hashCode() : 0);
        result = 29 * result + (artifactId != null ? artifactId.hashCode() : 0);
        result = 29 * result + (type != null ? type.hashCode() : 0);
        result = 29 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder(100);
        final String ver = version == null || version.trim().length() == 0 ? "SNAPSHOT" : version;
        final String t = type == null || type.trim().length() == 0 ? "jar" : type;
        final String ex = ext == null || ext.trim().length() == 0 ? t : ext;
        buf.append(artifactId).append('-').append(ver).append('.').append(ex);
        return buf.toString();
    }
}
