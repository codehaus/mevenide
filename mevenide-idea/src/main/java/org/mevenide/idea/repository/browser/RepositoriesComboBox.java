package org.mevenide.idea.repository.browser;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import java.awt.*;
import java.net.URI;
import javax.swing.*;
import org.mevenide.idea.repository.PomRepoManager;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.repository.IRepositoryReader;

/**
 * @author Arik
 * @todo refresh repository list when user changes the 'maven.repo.remote' properties
 */
public class RepositoriesComboBox extends ComboBox {
    public RepositoriesComboBox(final Project pProject, final String pPomUrl) {
        this(PomRepoManager.getInstance(pProject).getRepositoryReaders(pPomUrl));
    }

    public RepositoriesComboBox(final Project pProject) {
        this(PomRepoManager.getInstance(pProject).getRepositoryReaders());
    }

    public RepositoriesComboBox(final IRepositoryReader... pRepos) {
        setModel(new RepositoriesModel(pRepos));
        setRenderer(new RepositoriesRenderer());
    }

    private static IRepositoryReader[] appendNull(final IRepositoryReader... pRepos) {
        final IRepositoryReader[] repos = new IRepositoryReader[pRepos.length + 1];
        System.arraycopy(pRepos, 0, repos, 0, pRepos.length);
        repos[repos.length - 1] = null;
        return repos;
    }

    private class RepositoriesModel extends DefaultComboBoxModel {
        public RepositoriesModel(final IRepositoryReader... pRepos) {
            super(appendNull(pRepos));
        }

        public IRepositoryReader getSelectedItem() {
            return (IRepositoryReader) super.getSelectedItem();
        }

        public IRepositoryReader getElementAt(int index) {
            return (IRepositoryReader) super.getElementAt(index);
        }
    }

    private class RepositoriesRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            final Component c = super.getListCellRendererComponent(list,
                                                                   value,
                                                                   index,
                                                                   isSelected,
                                                                   cellHasFocus);
            if (c instanceof JLabel) {
                final JLabel label = (JLabel) c;

                if (value instanceof IRepositoryReader) {
                    final IRepositoryReader repo = (IRepositoryReader) value;
                    final URI uri = repo.getRootURI();
                    final String scheme = uri.getScheme();
                    if (scheme != null)
                        if (scheme.startsWith("file"))
                            label.setIcon(Icons.REPO_LOCAL);
                        else if (scheme.startsWith("http"))
                            label.setIcon(Icons.WEB_SERVER);
                        else
                            label.setIcon(Icons.REPO_TYPE_JAVADOC_JAR_OPEN);

                    label.setText(repo.getRootURI().toString());
                }
                else {
                    label.setText("(None)");
                    label.setIcon(null);
                }
            }

            return c;
        }
    }
}
