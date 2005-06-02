package org.mevenide.idea.repository;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik
 */
public class RepositoryTreeCellRenderer extends DefaultTreeCellRenderer {
    protected static final String ROOT_TEXT = "Repository Browser";

    @Override public Component getTreeCellRendererComponent(JTree tree,
                                                            Object value,
                                                            boolean sel,
                                                            boolean expanded,
                                                            boolean leaf,
                                                            int row,
                                                            boolean hasFocus) {

        final String text;
        if (value instanceof RepoPathElement)
            text = convertRepoPathElementToString((RepoPathElement) value);
        else
            text = value == null ? "" : value.toString();

        return super.getTreeCellRendererComponent(tree,
                                                  text,
                                                  sel,
                                                  expanded,
                                                  leaf,
                                                  row,
                                                  hasFocus);
    }

    protected String convertRepoPathElementToString(final RepoPathElement pNode) {
        final String text;
        switch (pNode.getLevel()) {
            case RepoPathElement.LEVEL_ARTIFACT:
                text = pNode.getArtifactId();
                break;
            case RepoPathElement.LEVEL_GROUP:
                text = pNode.getGroupId();
                break;
            case RepoPathElement.LEVEL_ROOT:
                text = ROOT_TEXT;
                break;
            case RepoPathElement.LEVEL_TYPE:
                text = pNode.getType();
                break;
            case RepoPathElement.LEVEL_VERSION:
                final String ext = pNode.getExtension();
                if(ext != null && ext.trim().length() > 0)
                    text = pNode.getArtifactId() + "-" + pNode.getVersion() + "." + ext;
                else
                    text = pNode.getArtifactId() + "-" + pNode.getVersion();
                break;
            default:
                text = pNode.toString();
        }
        return text;
    }
}
