package org.mevenide.idea.synchronize;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.mevenide.idea.synchronize.inspections.dependencies.DependencyNotDownloadedInspector;
import org.mevenide.idea.synchronize.inspections.dependencies.IdeaLibs2POMInspector;
import org.mevenide.idea.synchronize.inspections.dependencies.POM2IdeaLibsInspector;
import org.mevenide.idea.util.components.AbstractProjectComponent;

/**
 * @todo find an appropriate place to register the Sync action (in the menu)
 *
 * @author Arik
 */
public class InspectionsManager extends AbstractProjectComponent {
    /**
     * Registered inspections.
     */
    private final Set<ProblemInspector> inspections = new HashSet<ProblemInspector>(10);

    /**
     * Creates an instance.
     *
     * @param pProject the component project
     */
    public InspectionsManager(final Project pProject) {
        super(pProject);
    }

    /**
     * Initializes the component.
     */
    @Override
    public void initComponent() {
        registerSyncAction();

        //TODO: somehow dynamically locate all inspectors (allow customization...)
        inspections.add(new DependencyNotDownloadedInspector());
        inspections.add(new IdeaLibs2POMInspector());
        inspections.add(new POM2IdeaLibsInspector());
    }

    private void registerSyncAction() {
        final ActionManager mgr = ActionManager.getInstance();
        final AnAction inspectAction = mgr.getAction(InspectProjectAction.ID);
        if (inspectAction == null) {
            LOG.warn("Could not find action " + InspectProjectAction.ID);
            return;
        }

        final AnAction exToolsGrp = mgr.getAction(IdeActions.GROUP_FILE);
        if (exToolsGrp instanceof DefaultActionGroup) {
            final DefaultActionGroup toolsGrp = (DefaultActionGroup) exToolsGrp;
            toolsGrp.add(inspectAction);
        }
        else
            LOG.warn("The action group " + IdeActions.GROUP_EXTERNAL_TOOLS + " could not be found (" + exToolsGrp + ")");
    }

    /**
     * Returns registered inspectors.
     *
     * @return unmodifiable set
     */
    public Set<ProblemInspector> getInspections() {
        return Collections.unmodifiableSet(inspections);
    }

    /**
     * Inspects the project for problems, and returns them.
     *
     * <p>Module inspectors are handed all project modules.</p>
     *
     * @return problems found, or an empty array
     */
    public ProblemInfo[] inspect() {
        final ModuleManager moduleMgr = ModuleManager.getInstance(project);

        final Set<ProblemInfo> problems = new HashSet<ProblemInfo>(10);
        for (ProblemInspector inspector : inspections) {
            if(inspector instanceof ModuleProblemInspector) {
                final ModuleProblemInspector modInsp = (ModuleProblemInspector) inspector;
                final Module[] modules = moduleMgr.getModules();
                for (Module module : modules) {
                    final ProblemInfo[] probs = modInsp.inspect(module);
                    for (ProblemInfo problemInfo : probs)
                        problems.add(problemInfo);
                }
            }
            else if(inspector == null)
                throw new IllegalStateException("null inspector encountered.");
            else
                throw new UnsupportedOperationException("Unknown inspector type - " + inspector.getClass().getName());
        }

        return problems.toArray(new ProblemInfo[problems.size()]);
    }

    public static InspectionsManager getInstance(final Project pProject) {
        return pProject.getComponent(InspectionsManager.class);
    }
}
