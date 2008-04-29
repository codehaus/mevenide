/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codehaus.mevenide.hints;

import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.modules.java.hints.spi.AbstractHint;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Fix;

/**
 *
 * @author Anuradha
 */
public class TestMavenHint extends AbstractHint {

    private static final List<Fix> NO_FIXES = Collections.<Fix>emptyList();
    private static final Set<Tree.Kind> TREE_KINDS =
            EnumSet.<Tree.Kind>of(Tree.Kind.METHOD_INVOCATION);

    public TestMavenHint() {
        super(true, true, AbstractHint.HintSeverity.WARNING);
    }

    public Set<Kind> getTreeKinds() {

        return TREE_KINDS;
    }

    public List<ErrorDescription> run(CompilationInfo info, TreePath treePath) {

        Tree t = treePath.getLeaf();
       
        Element el = info.getTrees().getElement(treePath);
        String name = el.getSimpleName().toString();

        if (name.equals("getOnlineEmbedder")) {
            return Collections.<ErrorDescription>singletonList(
                    ErrorDescriptionFactory.createErrorDescription(
                    getSeverity().toEditorSeverity(),
                    getDisplayName(),
                    NO_FIXES,
                    info.getFileObject(),
                    (int) info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), t),
                    (int) info.getTrees().getSourcePositions().getEndPosition(info.getCompilationUnit(), t)));

        }

        return null;
    }

    public void cancel() {
        // Does nothing
    }

    public String getId() {
        return "MAVEN_TEST"; // NOI18N

    }

    public String getDisplayName() {
        return "Maven test hint :using getOnlineEmbedder";
    }

    public String getDescription() {
        return "You are using MavenEmbedder from EmbedderFactory :-)";
    }
}