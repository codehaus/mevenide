package org.mevenide.idea.module;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.util.ui.Tree;
import org.mevenide.idea.GoalsProvider;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.idea.util.ui.tree.GoalTreeNode;
import org.mevenide.idea.util.ui.tree.PluginTreeNode;
import org.mevenide.idea.util.ui.tree.SimpleGoalsTreeModel;

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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

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
     * The favorite goals list.
     */
    private final JList favoriteGoalsList = new JList();

    /**
     * The favorite goals list model - used when the "Add" or "Remove" buttons are pressed to add/remove
     * goals.
     */
    private final DefaultListModel favoriteGoalsModel = new DefaultListModel();

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
        mavenGoalsTree.setShowsRootHandles(true);
        mavenGoalsTree.setRootVisible(false);
        c = new GridBagConstraints();
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
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
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.insets = new Insets(5, 5, 5, 5);
        favoriteGoalsList.setModel(favoriteGoalsModel);
        add(new JScrollPane(favoriteGoalsList), c);
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

    public void loadMavenGoals(final GoalsProvider pGoalsProvider) {
        if (pGoalsProvider == null)
            mavenGoalsTree.setModel(null);
        else
            mavenGoalsTree.setModel(new SimpleGoalsTreeModel(pGoalsProvider));
    }

    public void setFavoriteGoals(final Collection pFavoriteGoals) {
        favoriteGoalsModel.clear();
        final Iterator goalIterator = pFavoriteGoals.iterator();
        while (goalIterator.hasNext())
            favoriteGoalsModel.addElement(goalIterator.next());
    }

    public Collection getFavoriteGoals() {
        final Collection favoriteSet = new HashSet(favoriteGoalsModel.getSize());
        final Object[] favorites = favoriteGoalsModel.toArray();
        for (int i = 0; i < favorites.length; i++)
            favoriteSet.add(favorites[i]);

        return favoriteSet;
    }

    protected void addSelection() {
        final TreePath[] selections = mavenGoalsTree.getSelectionPaths();
        if (selections == null || selections.length == 0)
            return;

        for (int i = 0; i < selections.length; i++) {
            final TreePath path = selections[i];
            final Object lastPathComponent = path.getLastPathComponent();
            if (lastPathComponent instanceof TreeNode) {
                final TreeNode node = (TreeNode) lastPathComponent;
                final TreeNode parent = node.getParent();

                if (node instanceof GoalTreeNode) {
                    final String plugin;
                    if (parent instanceof PluginTreeNode)
                        plugin = ((PluginTreeNode) parent).getPlugin() + ":";
                    else
                        plugin = "";

                    String goalName = plugin + ((GoalTreeNode) node).getGoal();

                    if (!favoriteGoalsModel.contains(goalName))
                        favoriteGoalsModel.addElement(goalName);
                }
                else if (node instanceof PluginTreeNode) {
                    final String plugin = ((PluginTreeNode) node).getPlugin() + ":";
                    final int childCount = node.getChildCount();
                    for (int j = 0; j < childCount; j++) {
                        final TreeNode goalNode = node.getChildAt(j);
                        if (goalNode instanceof GoalTreeNode) {
                            String goalName = plugin + ((GoalTreeNode) goalNode).getGoal();
                            if (!favoriteGoalsModel.contains(goalName))
                                favoriteGoalsModel.addElement(goalName);
                        }
                    }
                }
            }
        }
    }

    protected void removeSelection() {
        final int[] selections = favoriteGoalsList.getSelectedIndices();
        final Object[] data = new Object[selections.length];
        for (int i = 0; i < selections.length; i++)
            data[i] = favoriteGoalsModel.get(selections[i]);

        for (int i = 0; i < data.length; i++)
            favoriteGoalsModel.removeElement(data[i]);
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

