package org.mevenide.idea.module;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.util.ui.Tree;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.idea.util.ui.tree.GoalTreeNode;
import org.mevenide.idea.util.ui.tree.PluginTreeNode;
import org.mevenide.idea.util.ui.tree.SimpleGoalsTreeModel;
import org.mevenide.idea.util.ui.tree.GoalsTreeCellRenderer;
import org.mevenide.idea.util.goals.GoalsHelper;
import org.mevenide.idea.util.goals.grabber.CustomGoalsGrabber;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * A user interface component for displaying module Maven settings to the user.
 *
 * <p>The user can select the POM for the module, the favorite goals, etc.</p>
 *
 * @author Arik
 */
public class ModuleSettingsPanel extends JPanel {
    /**
     * Resources.
     */
    private static final Res RES = Res.getInstance(ModuleSettingsPanel.class);

    /**
     * The text field for selecting (or browsing) the POM file.
     */
    private final TextFieldWithBrowseButton pomFileField = new TextFieldWithBrowseButton();

    /**
     * The global maven goals tree. We need a reference to it to retrieve the current selection when the "Add"
     * button is pressed.
     */
    private final Tree mavenGoalsTree = new Tree();

    /**
     * The goals grabber used for storing the selected favorite goals.
     */
    private CustomGoalsGrabber favoriteGoalsGrabber;

    /**
     * The favorite goals list.
     */
    private final Tree favoriteGoalsTree = new Tree();

    /**
     * The model for the selected goals.
     */
    private SimpleGoalsTreeModel favoriteGoalsModel;

    /**
     * Creates an instance.
     */
    public ModuleSettingsPanel() {
        init();
    }

    /**
     * Creates an instance using (or not using) double buffering.
     *
     * @param pDoubleBuffered whether to use double buffering or not
     */
    public ModuleSettingsPanel(final boolean pDoubleBuffered) {
        super(pDoubleBuffered);
        init();
    }

    /**
     * Initializes the panel by creating the required components and laying them out on the panel.
     *
     * <p>If overriding, make sure you call this super method first.</p>
     */
    private void init() {
        GridBagConstraints c;
        setLayout(new GridBagLayout());

        //
        //create POM file selection panel
        //
        final JPanel pomFilePanel = new JPanel(new BorderLayout());
        pomFilePanel.add(new JLabel(RES.get("pom.file.label")), BorderLayout.LINE_START);
        pomFileField.addBrowseFolderListener(RES.get("choose.pom.file"),
                                             RES.get("choose.pom.file.desc"),
                                             null,
                                             new PomFileChooser());
        pomFilePanel.add(pomFileField, BorderLayout.CENTER);
        c = new GridBagConstraints();
        c.gridwidth = 3;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        add(pomFilePanel, c);

        //
        //create maven global goals tree
        //
        mavenGoalsTree.setCellRenderer(new GoalsTreeCellRenderer());
        mavenGoalsTree.setShowsRootHandles(true);
        mavenGoalsTree.setRootVisible(false);
        c = new GridBagConstraints();
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(5, 5, 5, 5);
        add(new JScrollPane(mavenGoalsTree), c);

        //
        //create selection buttons area
        //TODO: convert this to an ActionToolbar
        //
        final JButton addButton = new JButton(Icons.FORWARD);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent pEvent) {
                addSelection();
            }
        });
        final JButton removeButton = new JButton(Icons.BACK);
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent pEvent) {
                removeSelection();
            }
        });

        final Box buttonBox = Box.createVerticalBox();
        buttonBox.add(Box.createVerticalGlue());
        buttonBox.add(addButton);
        buttonBox.add(removeButton);
        buttonBox.add(Box.createVerticalGlue());
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        add(buttonBox, c);

        //
        //create favorite goals list
        //
        favoriteGoalsTree.setCellRenderer(new GoalsTreeCellRenderer());
        favoriteGoalsTree.setShowsRootHandles(true);
        favoriteGoalsTree.setRootVisible(false);
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.insets = new Insets(5, 5, 5, 5);
        add(new JScrollPane(favoriteGoalsTree), c);
    }

    public void setPomFile(final File pPomFile) {
        pomFileField.setText(pPomFile == null ? null : pPomFile.getAbsolutePath());
    }

    public File getPomFile() {
        final String text = pomFileField.getText();
        if (text == null)
            return null;
        else if (text.trim().length() == 0)
            return null;
        else
            return new File(text).getAbsoluteFile();
    }

    public void setMavenGoals(final IGoalsGrabber pGoalsProvider) {
        if (pGoalsProvider == null)
            mavenGoalsTree.setModel(null);
        else
            mavenGoalsTree.setModel(new SimpleGoalsTreeModel(pGoalsProvider));
    }

    public void setFavoriteGoals(final IGoalsGrabber pGoalsProvider) {
        favoriteGoalsGrabber = new CustomGoalsGrabber(pGoalsProvider.getName(),
                                                      pGoalsProvider);
        favoriteGoalsModel = new SimpleGoalsTreeModel(pGoalsProvider);
        favoriteGoalsTree.setModel(favoriteGoalsModel);
    }

    public IGoalsGrabber getFavoriteGoals() {
        return favoriteGoalsGrabber;
    }

    protected void addGoalSelection(final GoalTreeNode pGoalNode) {
        final PluginTreeNode pluginNode = (PluginTreeNode) pGoalNode.getParent();
        final String plugin = pluginNode.getPlugin();
        final String goal = pGoalNode.getGoal();
        final String fqName = GoalsHelper.buildFullyQualifiedName(plugin, goal);

        final String props =
                StringUtils.defaultString(pGoalNode.getDescription()) +
                ">" +
                StringUtils.join(pGoalNode.getPrereqs(), ',');
        favoriteGoalsGrabber.registerGoal(fqName, props);

        favoriteGoalsModel.addGoal(plugin,
                                   goal,
                                   pGoalNode.getDescription(),
                                   pGoalNode.getPrereqs());
    }

    protected void addSelection() {
        final TreePath[] selections = mavenGoalsTree.getSelectionPaths();
        if (selections == null || selections.length == 0)
            return;

        for(final TreePath path : selections) {
            final Object lastPathComponent = path.getLastPathComponent();
            if (lastPathComponent instanceof TreeNode) {
                final TreeNode node = (TreeNode) lastPathComponent;

                if (node instanceof GoalTreeNode)
                    addGoalSelection((GoalTreeNode) node);
                else if (node instanceof PluginTreeNode) {
                    final int childCount = node.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        final TreeNode goalNode = node.getChildAt(i);
                        addGoalSelection((GoalTreeNode) goalNode);
                    }
                }
            }
        }
    }

    protected void removeGoalSelection(final GoalTreeNode pGoalNode) {
        if(pGoalNode == null)
            return;

        final PluginTreeNode pluginNode = (PluginTreeNode) pGoalNode.getParent();
        final String plugin = pluginNode.getPlugin();
        final String goal = pGoalNode.getGoal();

        final String fqName = GoalsHelper.buildFullyQualifiedName(plugin, goal);
        favoriteGoalsGrabber.unregisterGoal(fqName);

        if (pluginNode.getChildCount() == 1) {
            final TreeNode pluginParentNode = pluginNode.getParent();
            final int index = pluginParentNode.getIndex(pluginNode);
            pluginNode.removeFromParent();
            favoriteGoalsModel.nodesWereRemoved(pluginParentNode,
                                                new int[]{index},
                                                new Object[]{pluginNode});
        }
        else {
            final int index = pluginNode.getIndex(pGoalNode);
            pGoalNode.removeFromParent();
            favoriteGoalsModel.nodesWereRemoved(pluginNode,
                                                new int[]{index},
                                                new Object[]{pGoalNode});
        }
    }

    protected void removeSelection() {
        final TreePath[] selections = favoriteGoalsTree.getSelectionPaths();
        for(final TreePath path : selections) {
            final TreeNode node = (TreeNode) path.getLastPathComponent();
            if(node instanceof GoalTreeNode) {
                final GoalTreeNode goalNode = (GoalTreeNode) node;
                removeGoalSelection(goalNode);
            }
            else if(node instanceof PluginTreeNode) {
                final PluginTreeNode pluginNode = (PluginTreeNode) node.getParent();
                final int childCount = pluginNode.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    final TreeNode tempNode = pluginNode.getChildAt(i);
                    final GoalTreeNode goalNode = (GoalTreeNode) tempNode;
                    removeGoalSelection(goalNode);
                }
            }
        }
    }

    private class PomFileChooser extends FileChooserDescriptor {
        public PomFileChooser() {
            super(true,    //prevent file-selection
                  false,   //allow folder-selection
                  false,   //prevent jar selection
                  false,   //prevent jar file selection
                  false,   //prevent jar content selection
                  false    //prevent multiple selection
            );
        }
    }
}

