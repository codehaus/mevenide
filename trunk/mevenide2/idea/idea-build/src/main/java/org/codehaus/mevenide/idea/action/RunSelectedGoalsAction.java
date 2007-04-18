package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.codehaus.mevenide.idea.component.PomTreeStructure;
import org.codehaus.mevenide.idea.gui.PomTreeView;
import org.codehaus.mevenide.idea.helper.ActionContext;
import org.codehaus.mevenide.idea.helper.BuildContext;
import org.codehaus.mevenide.idea.model.MavenProjectDocument;
import org.codehaus.mevenide.idea.util.IdeaMavenPluginException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RunSelectedGoalsAction extends PomTreeAction {
    public void update(final AnActionEvent e) {
        boolean enabled = false;
        PomTreeView pomTreeView = getView(e);
        if ( pomTreeView != null ) {
            Collection<PomTreeStructure.GoalNode> goalNodes = pomTreeView.getSelectedNodes(PomTreeStructure.GoalNode.class, false);
            PomTreeStructure.PomNode pomNode = getCommonParent(goalNodes);
            enabled = pomNode != null;
        }
        e.getPresentation().setEnabled(enabled);
    }

    public void actionPerformed(AnActionEvent e) {
        PomTreeView view = getView(e);
        if (view != null) {
            Collection<PomTreeStructure.GoalNode> nodes = view.getSelectedNodes(PomTreeStructure.GoalNode.class, false);
            runSelectedGoals(getActionContext(e), nodes);
        }
    }

    public static void runSelectedGoals(ActionContext context, Collection<PomTreeStructure.GoalNode> goalNodes)
            /*throws IdeaMavenPluginException*/ {
        PomTreeStructure.PomNode pomNode = getCommonParent(goalNodes);
        if ( pomNode == null ) {
            return;
        }

        BuildContext buildContext = new BuildContext();

        buildContext.setActionContext(context);

        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        Document[] documents = fileDocumentManager.getUnsavedDocuments();

        if ((documents != null) && (documents.length > 0)) {
            fileDocumentManager.saveAllDocuments();
        }

        MavenProjectDocument mavenProject = pomNode.getDocument();

        List<String> goalList = new ArrayList<String>();

        for (PomTreeStructure.GoalNode node : goalNodes) {
            String goalName = node.getName();
            goalList.add(goalName);
        }

        VirtualFile pomFile = mavenProject.getPomFile();

        buildContext.setPomFile(pomFile.getPath());

        if (pomFile.getParent() != null) {
            buildContext.setWorkingDir(pomFile.getParent().getPath());
        }

        buildContext.setGoals(goalList);

        MavenRunner runner = new MavenRunner(buildContext);

        try {
            runner.execute();
        } catch (IdeaMavenPluginException e) {
            e.printStackTrace();
        }
    }

    static PomTreeStructure.PomNode getCommonParent (Collection<PomTreeStructure.GoalNode> goalNodes) {
        PomTreeStructure.PomNode parent = null;
        for (PomTreeStructure.GoalNode goalNode : goalNodes) {
            PomTreeStructure.PomNode nextParent = goalNode.getParent(PomTreeStructure.PomNode.class);
            if ( parent == null ) {
                parent = nextParent;
            } else if ( parent != nextParent ) {
                return null;
            }
        }
        return parent;
    }
}
