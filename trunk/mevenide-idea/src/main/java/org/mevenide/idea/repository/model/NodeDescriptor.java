package org.mevenide.idea.repository.model;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.mevenide.idea.Res;
import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik
 */
public class NodeDescriptor implements Comparable {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(NodeDescriptor.class);

    /**
     * The text to display for the root node, if displayed.
     */
    protected static final String ROOT_TEXT = RES.get("repo.tree.root.label");

    private final int level;
    private final String groupId;
    private final String type;
    private final String artifactId;
    private final String version;
    private final String extension;

    public NodeDescriptor() {
        this(RepoPathElement.LEVEL_ROOT, null);
    }

    public NodeDescriptor(final int pLevel,
                          final String pGroupId) {
        this(pLevel, pGroupId, null);
    }

    public NodeDescriptor(final int pLevel,
                          final String pGroupId,
                          final String pType) {
        this(pLevel, pGroupId, pType, null);
    }

    public NodeDescriptor(final int pLevel,
                          final String pGroupId,
                          final String pType,
                          final String pArtifact) {
        this(pLevel, pGroupId, pType, pArtifact, null);
    }

    public NodeDescriptor(final int pLevel,
                          final String pGroupId,
                          final String pType,
                          final String pArtifact,
                          final String pVersion) {
        this(pLevel, pGroupId, pType, pArtifact, pVersion, null);
    }

    public NodeDescriptor(final int pLevel,
                          final String pGroupId,
                          final String pType,
                          final String pArtifact,
                          final String pVersion,
                          final String pExtension) {
        level = pLevel;
        groupId = pGroupId;
        type = pType;
        artifactId = pArtifact;
        version = pVersion;
        extension = pExtension;
    }

    public int getLevel() {
        return level;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getExtension() {
        return extension;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    public int compareTo(final Object o) {
        if (this == o) return 0;

        final NodeDescriptor that = (NodeDescriptor) o;

        return new CompareToBuilder()
            .append(level, that.level)
            .append(groupId, that.groupId)
            .append(type, that.type)
            .append(artifactId, that.artifactId)
            .append(version, that.version)
            .append(extension, that.extension).toComparison();
    }

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final NodeDescriptor that = (NodeDescriptor) o;

        if (artifactId != null ? !artifactId.equals(that.artifactId) : that.artifactId != null) return false;
        if (extension != null ? !extension.equals(that.extension) : that.extension != null) return false;
        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (groupId != null ? groupId.hashCode() : 0);
        result = 29 * result + (type != null ? type.hashCode() : 0);
        result = 29 * result + (artifactId != null ? artifactId.hashCode() : 0);
        result = 29 * result + (version != null ? version.hashCode() : 0);
        result = 29 * result + (extension != null ? extension.hashCode() : 0);
        return result;
    }

    public String toDisplayString() {
        switch (getLevel()) {
            case RepoPathElement.LEVEL_ARTIFACT:
                return getArtifactId();

            case RepoPathElement.LEVEL_GROUP:
                return getGroupId();

            case RepoPathElement.LEVEL_ROOT:
                return ROOT_TEXT;

            case RepoPathElement.LEVEL_TYPE:
                return getType();

            case RepoPathElement.LEVEL_VERSION:
                final String ext = getExtension();
                if (ext != null && ext.trim().length() > 0)
                    return getArtifactId() + "-" + getVersion() + "." + ext;
                else
                    return getArtifactId() + "-" + getVersion();

            default:
                return toString();
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
