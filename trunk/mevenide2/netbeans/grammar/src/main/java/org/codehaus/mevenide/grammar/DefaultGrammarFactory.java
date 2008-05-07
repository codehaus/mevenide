package org.codehaus.mevenide.grammar;

import java.io.File;
import java.net.URI;
import org.codehaus.mevenide.netbeans.api.FileUtilities;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.GrammarQuery;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

public class DefaultGrammarFactory extends GrammarFactory {

    public GrammarQuery isSupported(GrammarEnvironment env) {
        FileObject fo = env.getFileObject();
        if (fo.getNameExt().equals("settings.xml") && fo.getParent() != null && ".m2".equalsIgnoreCase(fo.getParent().getNameExt())) {
            return new MavenSettingsGrammar(env);
        }
        Project owner = FileOwnerQuery.getOwner(fo);
        if (owner == null) {
            //#107511
            return null;
        }
        if (fo.getNameExt().equals("pom.xml") && owner.getProjectDirectory().equals(fo.getParent())) {
            return new MavenProjectGrammar(env);
        }
        if (fo.getNameExt().equals("profiles.xml") && owner.getProjectDirectory().equals(fo.getParent())) {
            return new MavenProfilesGrammar(env);
        }
        File file = FileUtil.toFile(fo);
        if (owner.getLookup().lookup(ProjectURLWatcher.class) != null) {
            if ("src/main/resources/META-INF/archetype.xml".equals(FileUtil.getRelativePath(owner.getProjectDirectory(), env.getFileObject()))) {
                return new MavenArchetypeGrammar(env);
            }
            String desc = PluginPropertyUtils.getPluginProperty(owner, "org.apache.maven.plugins", "maven-assembly-plugin", "descriptor", "assembly");
            //NOI18N
            if (desc == null) {
                desc = PluginPropertyUtils.getPluginProperty(owner, "org.apache.maven.plugins", "maven-assembly-plugin", "descriptor", "directory");
            }
            if (desc != null) {
                URI uri = FileUtilities.getDirURI(owner.getProjectDirectory(), desc);
                if (uri != null && new File(uri).equals(file)) {
                    return new MavenAssemblyGrammar(env);
                }
            }
            desc = PluginPropertyUtils.getPluginProperty(owner, "org.codehaus.mojo", "nbm-maven-plugin", "descriptor", "jar");
            //NOI18N
            if (desc == null) {
                desc = PluginPropertyUtils.getPluginProperty(owner, "org.codehaus.mevenide.plugins", "maven-nbm-plugin", "descriptor", "jar");
            }
            if (desc != null) {
                URI uri = FileUtilities.getDirURI(owner.getProjectDirectory(), desc);
                if (uri != null && new File(uri).equals(file)) {
                    return new MavenNbmGrammar(env);
                }
            }
        }
        return null;
    }
}
