package org.mevenide.idea.module;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.project.Dependency;
import org.mevenide.context.IQueryContext;
import org.mevenide.idea.psi.util.PsiUtils;
import org.mevenide.idea.psi.util.XmlTagPath;
import org.mevenide.idea.util.FileUtils;

/**
 * @author Arik
 */
public abstract class ModuleUtils {

    public static XmlFile getModulePomXmlFile(final Module pModule) {
        final ModuleSettings settings = ModuleSettings.getInstance(pModule);
        final VirtualFile pomFile = settings.getPomVirtualFile();
        if(pomFile == null)
            return null;

        final Document pomDoc = FileDocumentManager.getInstance().getDocument(pomFile);
        if(pomDoc == null)
            return null;

        return PsiUtils.findXmlFile(pModule, pomDoc);
    }

    public static boolean isFileInClasspath(final Module pModule,
                                            final VirtualFile pFile) {
        final String depFilePath = pFile.getPath();
        final VirtualFile[] files = getModuleClasspath(pModule);
        for (VirtualFile file : files) {
            final String libFilePath = FileUtils.fixPath(file);
            if (pFile.equals(file) || depFilePath.equals(libFilePath))
                return true;
        }
        return false;
    }

    public static VirtualFile[] getModuleClasspath(final Module pModule) {
        return getModuleClasspath(pModule, false);
    }

    public static VirtualFile[] getModuleClasspath(final Module pModule,
                                                   final boolean pIncludeJdk) {
        final ModuleRootManager rootMgr = ModuleRootManager.getInstance(pModule);
        final ModifiableRootModel model = rootMgr.getModifiableModel();

        final Set<VirtualFile> files = new HashSet<VirtualFile>();
        final OrderEntry[] entries = model.getOrderEntries();
        for (OrderEntry entry : entries) {
            if (entry instanceof LibraryOrderEntry) {
                final LibraryOrderEntry libEntry = (LibraryOrderEntry) entry;
                final Library lib = libEntry.getLibrary();
                if(lib == null)
                    Collections.addAll(files, libEntry.getFiles(OrderRootType.CLASSES));
                else
                    Collections.addAll(files, lib.getFiles(OrderRootType.CLASSES));
            }
            else if(pIncludeJdk || !(entry instanceof JdkOrderEntry))
                Collections.addAll(files, entry.getFiles(OrderRootType.CLASSES));
        }

        return files.toArray(new VirtualFile[files.size()]);
    }

    private static Dependency[] getPomDependencies(final IQueryContext pContext,
                                                   final XmlFile pPomFile) {
        if (pPomFile == null || !pPomFile.isValid())
            return new Dependency[0];

        final Set<Dependency> deps = new HashSet<Dependency>(10);
        final XmlTagPath depsPath = new XmlTagPath(pPomFile, "project/dependencies");
        final XmlTag depsTag = depsPath.getTag();
        if (depsTag != null) {
            final XmlTag[] depTags = depsTag.findSubTags("dependency");
            for (XmlTag tag : depTags) {
                final String groupId = PsiUtils.getTagValue(tag, "groupId");
                final String artifactId = PsiUtils.getTagValue(tag, "artifactId");
                String version = PsiUtils.getTagValue(tag, "version");
                if (version == null)
                    version = "SNAPSHOT";

                String type = PsiUtils.getTagValue(tag, "type");
                if (type == null)
                    type = "jar";

                final String jar = PsiUtils.getTagValue(tag, "jar");
                final String url = PsiUtils.getTagValue(tag, "url");

                final Dependency dep = new Dependency();
                dep.setGroupId(groupId);
                dep.setArtifactId(artifactId);
                dep.setVersion(version);
                dep.setType(type);
                if(jar != null)
                    dep.setJar(jar);
                dep.setUrl(url);

                deps.add(dep);
            }
        }

        final XmlTagPath extendPath = new XmlTagPath(pPomFile, "project/extend");
        final XmlTag extendTag = extendPath.getTag();
        if (extendTag != null) {
            String extendUrl = PsiUtils.getTagValue(extendTag);
            if (extendUrl.indexOf('$') > 0 && pContext != null)
                extendUrl = pContext.getResolver().resolveString(extendUrl);

            final VirtualFile pomVirtualFile = pPomFile.getVirtualFile();
            final VirtualFile pomVirtualDir = pomVirtualFile.getParent();
            final VirtualFile parentPom = pomVirtualDir.findFileByRelativePath(extendUrl);
            if (parentPom != null) {
                final FileDocumentManager docMgr = FileDocumentManager.getInstance();
                final Document doc = docMgr.getDocument(parentPom);
                final PsiDocumentManager psiMgr = PsiDocumentManager.getInstance(pPomFile.getProject());
                PsiFile psiFile = psiMgr.getCachedPsiFile(doc);
                if(psiFile == null)
                    psiFile = psiMgr.getPsiFile(doc);

                final XmlFile xmlFile = (XmlFile) psiFile;
                Collections.addAll(deps, getPomDependencies(pContext, xmlFile));
            }
        }

        return deps.toArray(new Dependency[deps.size()]);
    }

    public static Dependency[] getModulePomDependencies(final Module pModule) {
        final ModuleSettings settings = ModuleSettings.getInstance(pModule);
        final IQueryContext ctx = settings.getQueryContext();
        return getPomDependencies(ctx, getModulePomXmlFile(pModule));
    }
}
