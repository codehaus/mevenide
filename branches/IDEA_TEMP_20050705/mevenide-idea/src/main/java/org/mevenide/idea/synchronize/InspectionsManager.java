package org.mevenide.idea.synchronize;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.mevenide.idea.synchronize.inspections.dependencies.DependencyDiffInspector;
import org.mevenide.idea.synchronize.inspections.dependencies.DependencyNotDownloadedInspector;
import org.mevenide.idea.synchronize.ui.SynchronizationResultsPanel;
import org.mevenide.idea.util.components.AbstractProjectComponent;
import org.mevenide.idea.util.ui.images.Icons;

/**
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
        //TODO: somehow dynamically locate all inspectors (allow customization...)
        inspections.add(new DependencyNotDownloadedInspector());
        inspections.add(new DependencyDiffInspector());
    }

    @Override
    public void projectOpened() {
        final ToolWindowManager toolMgr = ToolWindowManager.getInstance(project);
        final String twName = SynchronizationResultsPanel.NAME;

        final SynchronizationResultsPanel ui = new SynchronizationResultsPanel(project);
        final ToolWindowAnchor anchor = ToolWindowAnchor.BOTTOM;
        final ToolWindow tw = toolMgr.registerToolWindow(twName, ui, anchor);

        tw.setIcon(Icons.SYNC);
        tw.setAvailable(false, null);
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
            if (inspector instanceof ModuleProblemInspector) {
                final ModuleProblemInspector modInsp = (ModuleProblemInspector) inspector;
                final Module[] modules = moduleMgr.getModules();
                for (Module module : modules) {
                    final ProblemInfo[] probs = modInsp.inspect(module);
                    for (ProblemInfo problemInfo : probs)
                        problems.add(problemInfo);
                }
            }
            else if (inspector == null)
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
