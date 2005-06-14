package org.mevenide.idea.util;

import org.apache.maven.project.Dependency;
import org.mevenide.idea.module.ModuleUtils;
import com.intellij.openapi.module.Module;

/**
 * @author Arik
 */
public abstract class MavenUtils {

    public static boolean dependenciesEqual(final Dependency pFirst,
                                            final Dependency pSecond) {
        String d1GroupId = pFirst.getGroupId();
        String d1ArtifactId = pFirst.getArtifactId();
        String d1Version = pFirst.getVersion();
        if (d1Version == null || d1Version.trim().length() == 0)
            d1Version = "SNAPSHOT";

        String d1Type = pFirst.getType();
        if (d1Type == null || d1Type.trim().length() == 0)
            d1Type = "jar";

        String d2GroupId = pSecond.getGroupId();
        String d2ArtifactId = pSecond.getArtifactId();
        String d2Version = pSecond.getVersion();
        if (d2Version == null || d2Version.trim().length() == 0)
            d2Version = "SNAPSHOT";

        String d2Type = pSecond.getType();
        if (d2Type == null || d2Type.trim().length() == 0)
            d2Type = "jar";

        return d1GroupId.equals(d2GroupId) &&
            d1ArtifactId.equals(d2ArtifactId) &&
            d1Version.equalsIgnoreCase(d2Version) &&
            d1Type.equalsIgnoreCase(d2Type);
    }

    public static boolean isDependencyDeclared(final Module pModule,
                                               final Dependency pDependency) {
        final Dependency[] deps = ModuleUtils.getModulePomDependencies(pModule);
        for (Dependency dependency : deps)
            if (MavenUtils.dependenciesEqual(pDependency, dependency))
                return true;

        return false;
    }

}
