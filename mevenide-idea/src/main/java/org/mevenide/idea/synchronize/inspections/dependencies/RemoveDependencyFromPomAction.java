package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.mevenide.idea.Res;
import org.mevenide.idea.psi.util.PsiUtils;
import org.mevenide.idea.psi.util.XmlTagPath;
import org.mevenide.idea.module.ModuleUtils;
import org.mevenide.idea.synchronize.AbstractFixAction;
import org.mevenide.idea.synchronize.ProblemInfo;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.idea.util.IDEUtils;

/**
 * @author Arik
 */
public class RemoveDependencyFromPomAction extends AbstractFixAction {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(RemoveDependencyFromPomAction.class);

    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RemoveDependencyFromPomAction.class);

    private final Module module;
    private final Dependency dependency;
    private final RemoveDependencyRunnable removeDepRunnable = new RemoveDependencyRunnable();

    public RemoveDependencyFromPomAction(final ProblemInfo pProblem,
                                         final Module pModule,
                                         final Dependency pDependency) {
        super(RES.get("remove.dep.from.pom.action.name", pDependency.getArtifact()),
              RES.get("remove.dep.from.pom.action.desc", pDependency.getArtifact()),
              Icons.FIX_PROBLEMS,
              pProblem);
        module = pModule;
        dependency = pDependency;
    }

    public Module getModule() {
        return module;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        IDEUtils.runCommand(module, removeDepRunnable);
    }

    private class RemoveDependencyRunnable implements Runnable {
        public void run() {
            final XmlFile xmlFile = ModuleUtils.getModulePomXmlFile(module);
            final XmlTagPath path = new XmlTagPath(xmlFile, "project/dependencies");
            final XmlTag depsTag = path.getTag();
            if(depsTag == null)
                return;

            final XmlTag[] depTags = depsTag.findSubTags("dependency");
            for (XmlTag tag : depTags) {
                final String groupId = PsiUtils.getTagValue(tag, "groupId");
                final String artifactId = PsiUtils.getTagValue(tag, "artifactId");
                final String version = PsiUtils.getTagValue(tag, "version");
                String type = PsiUtils.getTagValue(tag, "type");
                if(type == null || type.trim().length() == 0)
                    type = "jar";

                if(StringUtils.equals(groupId, dependency.getGroupId()) &&
                    StringUtils.equals(artifactId, dependency.getArtifactId()) &&
                    StringUtils.equals(version, dependency.getVersion()) &&
                    StringUtils.equals(type, dependency.getType())) {

                    try {
                        tag.delete();
                        ApplicationManager.getApplication().saveAll();
                    }
                    catch (IncorrectOperationException e) {
                        LOG.error(e.getMessage(), e);
                        UIUtils.showError(module, e);
                    }
                    return;
                }
            }

            //
            //if we got here, we could not find the dependency tag
            //which means that the dependency was defined in a parent
            //POM - therefor we will simply notify the user, for now
            //(future versions might modify the parent POM)
            //
            UIUtils.showError(
                module,
                RES.get("dep.defined.in.parent.pom", dependency.getArtifact()));
        }
    }
}
