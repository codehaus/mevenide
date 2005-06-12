package org.mevenide.idea.synchronize.inspections.dependencies;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.context.IProjectContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.idea.Res;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.idea.synchronize.ProblemInfo;
import org.mevenide.idea.synchronize.ProblemInspector;
import org.mevenide.idea.synchronize.inspections.AbstractInspector;

/**
 * @author Arik
 */
public class LibrariesInspector extends AbstractInspector {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(LibrariesInspector.class);

    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(LibrariesInspector.class);

    public LibrariesInspector() {
        super(RES.get("lib.inspector.name"), RES.get("lib.inspector.desc"));
    }

    public ProblemInfo[] inspect(final Module pModule) {
        if(pModule == null)
            return new ProblemInfo[0];

        //the project library table
        final ModuleRootManager rootMgr = ModuleRootManager.getInstance(pModule);
        final LibraryTable libTable = LibraryTablesRegistrar.getInstance().getLibraryTable(pModule.getProject());

        //make sure this module has a POM at all
        final ModuleSettings settings = ModuleSettings.getInstance(pModule);
        final IQueryContext ctx = settings.getQueryContext();
        if(ctx == null)
            return new ProblemInfo[0];

        final String localRepo = ctx.getResolver().getResolvedValue("maven.repo.local");
        if(localRepo == null) {
            LOG.warn("Could not find local repository for module " + pModule.getName());
            return new ProblemInfo[0];
        }

        final IProjectContext pomContext = ctx.getPOMContext();
        final Project project = pomContext.getFinalProject();

        //noinspection UNCHECKED_WARNING
        @SuppressWarnings("unchecked")
        final List<Dependency> deps = project.getDependencies();

        final Set<ProblemInfo> problems = new HashSet<ProblemInfo>(deps.size());
        final StringBuilder buf = new StringBuilder();
        for (final Dependency dependency : deps) {
            if(!dependency.isAddedToClasspath())
                continue;

            buf.replace(0, buf.length(), localRepo);
            if(!(buf.charAt(buf.length() - 1) == '/'))
                buf.append('/');
            buf.append(dependency.getArtifactDirectory()).append("/");
            buf.append(dependency.getType()).append("s/");
            buf.append(dependency.getArtifact());

            final String relPath = buf.toString();

            boolean found = false;
            final OrderEntry[] orderEntries = rootMgr.getOrderEntries();
            for (OrderEntry orderEntry : orderEntries) {
                final VirtualFile[] files = orderEntry.getFiles(OrderRootType.CLASSES);
                for (VirtualFile file : files) {
                }
            }

            //TODO: search module jars, project and application libraries
            final Library lib = libTable.getLibraryByName(dependency.getArtifact());
            if(lib == null) {
                final ProblemInfo prob;
                prob = new DependencyMissingInIPR(pModule, dependency);
                problems.add(prob);
            }
        }

        return problems.toArray(new ProblemInfo[problems.size()]);
    }

    private class DependencyMissingInIPR implements ProblemInfo {
        protected final String description;
        private final Module module;
        private final Dependency dependency;
        protected final com.intellij.openapi.project.Project project;

        public DependencyMissingInIPR(final Module pModule, final Dependency pDependency) {
            module = pModule;
            dependency = pDependency;
            description = RES.get("dep.missing.in.ipr.desc", dependency.getArtifact());
            project = module.getProject();
        }

        public ProblemInspector getInspector() {
            return LibrariesInspector.this;
        }

        public String getDescription() {
            return description;
        }

        public boolean isValid() {
            final LibraryTablesRegistrar libTblReg = LibraryTablesRegistrar.getInstance();
            final LibraryTable libTable = libTblReg.getLibraryTable(project);
            return libTable.getLibraryByName(dependency.getArtifact()) == null;
        }

        public boolean canBeFixed() {
            return true;
        }

        /**
         * @todo add the dependency to the project library table, and use it in the module
         */
        public void fix() {
            LOG.info("Fixing " + description);
        }

        public Module getModule() {
            return module;
        }
    }
}
