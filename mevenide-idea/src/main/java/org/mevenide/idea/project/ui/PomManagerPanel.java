package org.mevenide.idea.project.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.Tree;
import java.awt.*;
import javax.swing.*;
import org.mevenide.idea.Res;

/**
 * @author Arik
 */
public class PomManagerPanel extends JPanel implements Disposable {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(PomManagerPanel.class);

    /**
     * Tool window name.
     */
    public static final String TITLE = RES.get("pom.manager.name");

    /**
     * The project this instance is registered for.
     */
    private final Project project;

    /**
     * The Maven tree model.
     */
    private final PomTreeModel model;

    /**
     * The Maven tree.
     */
    private final JTree tree;

    /**
     * Creates an instance for the given project.
     *
     * @param pProject the project this instance will be registered to
     */
    public PomManagerPanel(final Project pProject) {
        super(new BorderLayout());

        project = pProject;
        model = new PomTreeModel(project);
        tree = new Tree(model);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new PomManagerTreeCellRenderer());

        add(ScrollPaneFactory.createScrollPane(tree), BorderLayout.CENTER);
    }

    public void dispose() {
        model.dispose();
    }
}
